package vn.com.loyalty.transaction.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import vn.com.loyalty.core.constant.Constants;
import vn.com.loyalty.core.constant.enums.TransactionType;
import vn.com.loyalty.core.dto.message.*;
import vn.com.loyalty.core.dto.request.BodyRequest;
import vn.com.loyalty.core.entity.transaction.TransactionEntity;
import vn.com.loyalty.core.entity.transaction.TransactionMessageEntity;
import vn.com.loyalty.core.entity.voucher.VoucherEntity;
import vn.com.loyalty.core.exception.PointException;
import vn.com.loyalty.core.mapper.TransactionMapper;
import vn.com.loyalty.core.mapper.VoucherMapper;
import vn.com.loyalty.core.orchestration.Orchestrator;
import vn.com.loyalty.core.orchestration.OrchestrationStep;
import vn.com.loyalty.core.repository.TransactionMessageRepository;
import vn.com.loyalty.core.service.internal.RedisOperation;
import vn.com.loyalty.core.service.internal.TransactionService;
import vn.com.loyalty.core.service.internal.WebClientService;
import vn.com.loyalty.core.utils.RequestUtil;
import vn.com.loyalty.core.utils.factory.response.BodyResponse;
import vn.com.loyalty.core.dto.request.VoucherMessageReq;
import vn.com.loyalty.transaction.properties.EndpointProperties;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrchestrationServiceImpl implements OrchestrationService {

    private final WebClientService webClientService;
    private final EndpointProperties endPointProperties;
    private final RedisOperation redisOperation;
    private final ObjectMapper objectMapper;
    private final TransactionMessageRepository transactionMessageRepository;
    private final TransactionService transactionService;
    private final RequestUtil requestUtil;
    private final TransactionMapper transactionMapper;
    private final VoucherMapper voucherMapper;

    @Override
    @Transactional
    public TransactionMessageRes processTransactionOrchestration(TransactionMessageReq message) {

        BigDecimal epointGain = transactionService.calculateEpointGain(message);
        BigDecimal rpointGain = transactionService.calculateRpointGain(message);
        BigDecimal epointSpend = message.getData().getPointUse() != null ? message.getData().getPointUse() : BigDecimal.ZERO;

        TransactionEntity transactionEntity = TransactionEntity.builder()
                .customerCode(message.getCustomerCode())
                .transactionTime(message.getTransactionTime())
                .transactionId(message.getTransactionId())
                .transactionValue(message.getData().getTransactionValue())
                .transactionType(TransactionType.valueOf(message.getTransactionType()))
                .epointGain(epointGain)
                .rpointGain(rpointGain)
                .epointSpend(epointSpend)
                .voucherCodeList(message.getData().getVoucherCodeList())
                .build();

        transactionService.saveTransaction(transactionEntity);
        this.savePointToRedis(transactionEntity);
        this.runTransactionOrchestration(transactionMapper.entityToOrchestrationReq(transactionEntity));

        return transactionMapper.entityToDTO(transactionEntity);
    }


    @SneakyThrows
    @Override
    @Transactional
    public OrchestrationMessage processVoucherOrchestration(VoucherMessageReq req) {

        req.setTransactionId(UUID.randomUUID().toString());

        //save orchestration
        transactionMessageRepository.save(
                TransactionMessageEntity.builder().messageReceived(objectMapper.writeValueAsString(req)).build());

        VoucherEntity voucherEntity = redisOperation.getValue(Constants.RedisConstants.VOUCHER_DIR + req.getVoucherCode(), VoucherEntity.class);
        BigDecimal usePoint = voucherEntity.getPrice().multiply(req.getNumberVoucher());
        BigDecimal customerEpoint = redisOperation.getValue(redisOperation.genEpointKey(req.getCustomerCode()), BigDecimal.class);

        if (customerEpoint.compareTo(usePoint) < 0) {
            throw new PointException(req.getTransactionId(), req.getCustomerCode(), customerEpoint, usePoint);
        }

        redisOperation.setValue(redisOperation.genEpointKey(req.getCustomerCode()), customerEpoint.subtract(usePoint));
        req.setEpointSpend(usePoint);
        this.runVoucherOrchestration(voucherMapper.DTOToOrchestrationReq(req));

        return null;
    }

    private void runTransactionOrchestration(TransactionOrchestrationReq data) {
        Orchestrator.steps(new OrchestrationStep() {
            @Override
            public BodyResponse sendProcess() {
                return webClientService.postSync(endPointProperties.getCmsEndpoint().getBaseUrl(),
                        endPointProperties.getCmsEndpoint().getProcessOrchestrationTransaction(),
                        BodyRequest.of(data),
                        BodyResponse.class);
            }

            @Override
            public BodyResponse sendRollback() {
                return webClientService.postSync(endPointProperties.getCmsEndpoint().getBaseUrl(),
                        endPointProperties.getCmsEndpoint().getRollbackOrchestrationTransaction(),
                        BodyRequest.of(data),
                        BodyResponse.class);
            }
        }, new OrchestrationStep() {
            @Override
            public BodyResponse sendProcess() {
                return webClientService.postSync(endPointProperties.getVoucherEndpoint().getBaseUrl(),
                        endPointProperties.getVoucherEndpoint().getProcessOrchestrationTransaction(),
                        BodyRequest.of(data),
                        BodyResponse.class);
            }

            @Override
            public BodyResponse sendRollback() {
                return webClientService.postSync(endPointProperties.getVoucherEndpoint().getBaseUrl(),
                        endPointProperties.getVoucherEndpoint().getRollbackOrchestrationTransaction(),
                        BodyRequest.of(data),
                        BodyResponse.class);
            }
        }).asyncProcessOrchestration(requestUtil.getRequestId());
    }

    private void runVoucherOrchestration(VoucherOrchestrationReq data) {
        Orchestrator.steps(new OrchestrationStep() {
            @Override
            public BodyResponse sendProcess() {
                return webClientService.postSync(endPointProperties.getVoucherEndpoint().getBaseUrl(),
                        endPointProperties.getVoucherEndpoint().getProcessBuyVoucherOrchestration(),
                        BodyRequest.of(data),
                        BodyResponse.class);
            }

            @Override
            public BodyResponse sendRollback() {
                return webClientService.postSync(endPointProperties.getVoucherEndpoint().getBaseUrl(),
                        endPointProperties.getVoucherEndpoint().getRollbackOrchestrationTransaction(),
                        BodyRequest.of(data),
                        BodyResponse.class);
            }
        }, new OrchestrationStep() {
            @Override
            public BodyResponse sendProcess() {
                return webClientService.postSync(endPointProperties.getCmsEndpoint().getBaseUrl(),
                        endPointProperties.getCmsEndpoint().getProcessBuyVoucherOrchestration(),
                        BodyRequest.of(data),
                        BodyResponse.class);
            }

            @Override
            public BodyResponse sendRollback() {
                return webClientService.postSync(endPointProperties.getCmsEndpoint().getBaseUrl(),
                        endPointProperties.getCmsEndpoint().getRollbackBuyVoucherOrchestration(),
                        BodyRequest.of(data),
                        BodyResponse.class);
            }
        }).asyncProcessOrchestration(requestUtil.getRequestId());
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


}
