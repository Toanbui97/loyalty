package vn.com.loyalty.transaction.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import vn.com.loyalty.core.constant.Constants;
import vn.com.loyalty.core.constant.enums.TransactionType;
import vn.com.loyalty.core.dto.message.OrchestrationMessage;
import vn.com.loyalty.core.dto.message.TransactionMessage;
import vn.com.loyalty.core.dto.message.TransactionOrchestrationMessage;
import vn.com.loyalty.core.dto.request.BodyRequest;
import vn.com.loyalty.core.entity.transaction.TransactionEntity;
import vn.com.loyalty.core.entity.transaction.TransactionMessageEntity;
import vn.com.loyalty.core.entity.voucher.VoucherEntity;
import vn.com.loyalty.core.exception.PointException;
import vn.com.loyalty.core.exception.TransactionException;
import vn.com.loyalty.core.orchestration.Orchestration;
import vn.com.loyalty.core.orchestration.OrchestrationStep;
import vn.com.loyalty.core.repository.TransactionMessageRepository;
import vn.com.loyalty.core.service.internal.RedisOperation;
import vn.com.loyalty.core.service.internal.TransactionService;
import vn.com.loyalty.core.service.internal.WebClientService;
import vn.com.loyalty.core.utils.factory.response.BodyResponse;
import vn.com.loyalty.transaction.dto.VoucherMessage;
import vn.com.loyalty.transaction.properties.EndpointProperties;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class OrchestrationServiceImpl implements OrchestrationService {

    private final WebClientService webClientService;
    private final EndpointProperties endPointProperties;
    private final RedisOperation redisOperation;
    private final ObjectMapper objectMapper;
    private final TransactionMessageRepository transactionMessageRepository;
    private final TransactionService transactionService;
    private final HttpSession httpSession;

    @Override
    @Transactional
    public TransactionMessage processTransactionOrchestration(TransactionMessage message) {

        BigDecimal epointGain = transactionService.calculateEpointGain(message);
        BigDecimal rpointGain = transactionService.calculateRpointGain(message);
        BigDecimal epointSpend = message.getData().getPointUsed() != null ? message.getData().getPointUsed() : BigDecimal.ZERO;

        TransactionEntity transactionEntity = TransactionEntity.builder()
                .customerCode(message.getCustomerCode())
                .transactionTime(message.getTransactionTime())
                .transactionId(message.getTransactionId())
                .transactionValue(message.getData().getTransactionValue())
                .transactionType(TransactionType.valueOf(message.getTransactionType()))
                .epointGain(epointGain)
                .rpointGain(rpointGain)
                .epointSpend(epointSpend)
                .voucherDetailCodeList(message.getData().getVoucherDetailCodeList())
                .build();

         CompletableFuture.allOf(
                CompletableFuture.runAsync(() -> transactionService.saveTransaction(transactionEntity)),
                CompletableFuture.runAsync(() -> this.savePointToRedis(transactionEntity)),
                CompletableFuture.runAsync(() -> this.buildTransactionOrchestration(transactionEntity))
        ).exceptionally(throwable -> {
            throwable.printStackTrace();
            throw new TransactionException(throwable.getMessage());
        }).join();
         return message;
    }


    @SneakyThrows
    @Override
    @Transactional
    public OrchestrationMessage processVoucherOrchestration(VoucherMessage message) {

        message.setTransactionId(UUID.randomUUID().toString());

        //save orchestration
        transactionMessageRepository.save(
                TransactionMessageEntity.builder().messageReceived(objectMapper.writeValueAsString(message)).build());

        VoucherEntity voucherEntity = redisOperation.getValue(Constants.RedisConstants.VOUCHER_DIR + message.getVoucherCode(), VoucherEntity.class);
        BigDecimal usePoint = voucherEntity.getPrice().multiply(message.getNumberVoucher());
        BigDecimal customerEpoint = redisOperation.getValue(redisOperation.genEpointKey(message.getCustomerCode()), BigDecimal.class);

        if (customerEpoint.compareTo(usePoint) < 0) {
            throw new PointException(message.getTransactionId(), message.getCustomerCode(), customerEpoint, usePoint);
        }

        redisOperation.setValue(redisOperation.genEpointKey(message.getCustomerCode()), customerEpoint.subtract(usePoint));

        this.buildVoucherOrchestration(message);

        return null;
    }

    private void buildTransactionOrchestration(TransactionEntity transaction) {
        TransactionOrchestrationMessage transactionOrchestrationMessage = TransactionOrchestrationMessage.builder()
                .transactionId(transaction.getTransactionId())
                .customerCode(transaction.getCustomerCode())
                .rpointGain(transaction.getRpointGain())
                .epointGain(transaction.getEpointGain())
                .epointSpend(transaction.getEpointSpend())
                .voucherDetailCodeList(transaction.getVoucherDetailCodeList())
                .build();

        Orchestration.ofSteps(new OrchestrationStep(transactionOrchestrationMessage) {
            @Override
            public BodyResponse<OrchestrationMessage> sendProcess (BodyRequest<OrchestrationMessage> request) {
                return webClientService.postSync(endPointProperties.getCmsEndpoint().getBaseUrl(),
                        endPointProperties.getCmsEndpoint().getProcessOrchestrationTransaction(),
                        request,
                        BodyResponse.class);
            }

            @Override
            public BodyResponse<OrchestrationMessage> sendRollback (BodyRequest<OrchestrationMessage> request) {
                return webClientService.postSync(endPointProperties.getCmsEndpoint().getBaseUrl(),
                        endPointProperties.getCmsEndpoint().getRollbackOrchestrationTransaction(),
                        request,
                        BodyResponse.class);
            }
        },new OrchestrationStep(transactionOrchestrationMessage) {
            @Override
            public BodyResponse<OrchestrationMessage> sendProcess(BodyRequest<OrchestrationMessage> request) {
                return webClientService.postSync(endPointProperties.getVoucherEndpoint().getBaseUrl(),
                        endPointProperties.getVoucherEndpoint().getProcessOrchestrationTransaction(),
                        request,
                        BodyResponse.class);
            }

            @Override
            public BodyResponse<OrchestrationMessage> sendRollback(BodyRequest<OrchestrationMessage> request) {
                return webClientService.postSync(endPointProperties.getVoucherEndpoint().getBaseUrl(),
                        endPointProperties.getVoucherEndpoint().getRollbackOrchestrationTransaction(),
                        request,
                        BodyResponse.class);
            }
        }).asyncProcessOrchestration(this.generateOrchestrationId());
    }

    private void buildVoucherOrchestration(OrchestrationMessage voucherOrchestrationMessage) {
        Orchestration.ofSteps(new OrchestrationStep(voucherOrchestrationMessage) {
            @Override
            public BodyResponse<OrchestrationMessage> sendProcess(BodyRequest<OrchestrationMessage> request) {
                return webClientService.postSync(endPointProperties.getVoucherEndpoint().getBaseUrl(),
                        endPointProperties.getVoucherEndpoint().getProcessBuyVoucherOrchestration(),
                        request,
                        BodyResponse.class);
            }

            @Override
            public BodyResponse<OrchestrationMessage> sendRollback(BodyRequest<OrchestrationMessage> request) {
                return webClientService.postSync(endPointProperties.getVoucherEndpoint().getBaseUrl(),
                        endPointProperties.getVoucherEndpoint().getRollbackOrchestrationTransaction(),
                        request,
                        BodyResponse.class);
            }
        }, new OrchestrationStep(voucherOrchestrationMessage) {
            @Override
            public BodyResponse<OrchestrationMessage> sendProcess(BodyRequest<OrchestrationMessage> request) {
                return webClientService.postSync(endPointProperties.getCmsEndpoint().getBaseUrl(),
                        endPointProperties.getCmsEndpoint().getProcessBuyVoucherOrchestration(),
                        request,
                        BodyResponse.class);
            }

            @Override
            public BodyResponse<OrchestrationMessage> sendRollback(BodyRequest<OrchestrationMessage> request) {
                return webClientService.postSync(endPointProperties.getCmsEndpoint().getBaseUrl(),
                        endPointProperties.getCmsEndpoint().getRollbackBuyVoucherOrchestration(),
                        request,
                        BodyResponse.class);
            }
        }).asyncProcessOrchestration(this.generateOrchestrationId());
    }

    private void savePointToRedis(TransactionEntity transaction) {

        // save epoint to redis
        String epointKey = redisOperation.genEpointKey(transaction.getCustomerCode());
        BigDecimal epoint = redisOperation.hasValue(epointKey) ? redisOperation.getValue(epointKey, BigDecimal.class) : BigDecimal.ZERO;

        if (epoint.compareTo(transaction.getEpointSpend()) < 0) {
            throw new PointException(transaction.getTransactionId(), transaction.getCustomerCode(), epoint, transaction.getEpointSpend());
        }
        redisOperation.setValue(epointKey, epoint.add(transaction.getEpointGain()).subtract(transaction.getEpointSpend()));

        // save rpoint to redis
        String rpointKey = redisOperation.genRpointKey(transaction.getCustomerCode());
        if (redisOperation.hasValue(rpointKey)) {
            BigDecimal rpoint = redisOperation.getValue(rpointKey, BigDecimal.class);
            redisOperation.setValue(rpointKey, rpoint.add(transaction.getRpointGain()));
        } else {
            redisOperation.setValue(rpointKey, transaction.getRpointGain());
        }
    }

    private String generateOrchestrationId() {
        return UUID.randomUUID().toString();
    }
}
