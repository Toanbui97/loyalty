package vn.com.loyalty.transaction.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.com.loyalty.core.constant.Constants;
import vn.com.loyalty.core.dto.message.OrchestrationMessage;
import vn.com.loyalty.core.dto.request.BodyRequest;
import vn.com.loyalty.core.entity.transaction.TransactionMessageEntity;
import vn.com.loyalty.core.entity.voucher.VoucherEntity;
import vn.com.loyalty.core.orchestration.Orchestration;
import vn.com.loyalty.core.orchestration.OrchestrationStep;
import vn.com.loyalty.core.repository.TransactionMessageRepository;
import vn.com.loyalty.core.service.internal.RedisOperation;
import vn.com.loyalty.core.service.internal.WebClientService;
import vn.com.loyalty.core.utils.factory.response.BodyResponse;
import vn.com.loyalty.transaction.dto.VoucherMessage;
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

    @Override
    @Transactional
    public OrchestrationMessage processVoucherOrchestration(VoucherMessage voucherMessage) throws JsonProcessingException {

        voucherMessage.setTransactionId(UUID.randomUUID().toString());

        //save orchestration
        transactionMessageRepository.save(
                TransactionMessageEntity.builder().messageReceived(objectMapper.writeValueAsString(voucherMessage)).build());

        VoucherEntity voucherEntity = redisOperation.getValue(Constants.RedisConstants.VOUCHER_DIR + voucherMessage.getVoucherCode(), VoucherEntity.class);
        BigDecimal usedPoint = voucherEntity.getPrice().multiply(voucherMessage.getNumberVoucher());
        BigDecimal customerEpoint = redisOperation.getValue(redisOperation.genEpointKey(voucherMessage.getCustomerCode()), BigDecimal.class);

        if (customerEpoint.compareTo(usedPoint) < 0) {
            throw new RuntimeException();
        }

        redisOperation.setValue(redisOperation.genEpointKey(voucherMessage.getCustomerCode()), customerEpoint.subtract(usedPoint));

        this.buildOrchestration(voucherMessage).asyncProcessOrchestration();

        return null;
    }

    private Orchestration buildOrchestration(OrchestrationMessage voucherOrchestrationMessage) {

        return Orchestration.ofSteps(new OrchestrationStep(voucherOrchestrationMessage) {
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
        });
    }

}
