package vn.com.loyalty.core.orchestration;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import vn.com.loyalty.core.constant.Constants;
import vn.com.loyalty.core.constant.enums.ResponseStatusCode;
import vn.com.loyalty.core.dto.message.OrchestrationMessage;
import vn.com.loyalty.core.dto.request.BodyRequest;
import vn.com.loyalty.core.utils.factory.response.BodyResponse;

@Slf4j
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class OrchestrationStep {

    private String stepStatus = Constants.OrchestrationStepStatus.STATUS_PENDING;
    private String orchestrationId;
    private OrchestrationMessage message;

    protected OrchestrationStep(OrchestrationMessage message) {
        this.setStepStatus(Constants.OrchestrationStepStatus.STATUS_PENDING);
        this.message = message;
    }

    String getStepStatus() {
        return this.stepStatus;
    }

    BodyResponse<OrchestrationMessage> process() {

        try {
            BodyResponse<OrchestrationMessage> response = this.sendProcess(BodyRequest.of(this.orchestrationId, message));
            if (ResponseStatusCode.SUCCESS.getCode().equals(response.getCode())) {
                this.stepStatus = Constants.OrchestrationStepStatus.STATUS_COMPLETED;
            } else {
                this.stepStatus = Constants.OrchestrationStepStatus.STATUS_FAILED;
            }
            return response;
        } catch (Exception e) {
            this.stepStatus = Constants.OrchestrationStepStatus.STATUS_FAILED;
            return BodyResponse.<OrchestrationMessage>builder().code(ResponseStatusCode.INTERNAL_SERVER_ERROR.getCode()).build();
        }
    }

    BodyResponse<OrchestrationMessage> rollback() {
        try {
            BodyResponse<OrchestrationMessage> response = this.sendRollback(BodyRequest.of(this.orchestrationId,message));
            if (ResponseStatusCode.SUCCESS.getCode().equals(response.getCode())) {
                this.stepStatus = Constants.OrchestrationStepStatus.STATUS_ROLLBACK;
            } else {
                this.stepStatus = Constants.OrchestrationStepStatus.STATUS_FAILED;
            }
            return response;
        } catch (Exception e) {
            this.stepStatus = Constants.OrchestrationStepStatus.STATUS_FAILED;
            return BodyResponse.<OrchestrationMessage>builder().code(ResponseStatusCode.INTERNAL_SERVER_ERROR.getCode()).build();
        }
    }

    public abstract BodyResponse<OrchestrationMessage> sendProcess(BodyRequest<OrchestrationMessage> request);

    public abstract BodyResponse<OrchestrationMessage> sendRollback(BodyRequest<OrchestrationMessage> request);
}