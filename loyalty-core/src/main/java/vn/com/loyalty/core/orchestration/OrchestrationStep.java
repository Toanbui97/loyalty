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

import java.io.Serializable;

@Slf4j
@Getter
@Setter
public abstract class OrchestrationStep {

    private String stepStatus = Constants.OrchestrationStepStatus.STATUS_PENDING;
    private String orchestrationId;
    protected OrchestrationStep() {
        this.setStepStatus(Constants.OrchestrationStepStatus.STATUS_PENDING);
    }

    String getStepStatus() {
        return this.stepStatus;
    }

    protected BodyResponse<?> process() {
        try {
            BodyResponse<?> response = this.sendProcess();
            if (ResponseStatusCode.SUCCESS.getCode().equals(response.getCode())) {
                this.stepStatus = Constants.OrchestrationStepStatus.STATUS_COMPLETED;
            } else {
                this.stepStatus = Constants.OrchestrationStepStatus.STATUS_FAILED;
            }
            return response;
        } catch (Exception e) {
            this.stepStatus = Constants.OrchestrationStepStatus.STATUS_FAILED;
            return BodyResponse.builder().code(ResponseStatusCode.INTERNAL_SERVER_ERROR.getCode()).build();
        }
    }

    BodyResponse<?> rollback() {
        try {
            BodyResponse<?> response = this.sendRollback();
            if (ResponseStatusCode.SUCCESS.getCode().equals(response.getCode())) {
                this.stepStatus = Constants.OrchestrationStepStatus.STATUS_ROLLBACK;
            } else {
                this.stepStatus = Constants.OrchestrationStepStatus.STATUS_FAILED;
            }
            return response;
        } catch (Exception e) {
            this.stepStatus = Constants.OrchestrationStepStatus.STATUS_FAILED;
            return BodyResponse.builder().code(ResponseStatusCode.INTERNAL_SERVER_ERROR.getCode()).build();
        }
    }

    public abstract BodyResponse<?> sendProcess();

    public abstract BodyResponse<?> sendRollback();
}