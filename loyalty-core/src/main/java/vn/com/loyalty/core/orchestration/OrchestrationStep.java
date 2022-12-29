package vn.com.loyalty.core.orchestration;

import vn.com.loyalty.core.constant.Constants;
import vn.com.loyalty.core.constant.enums.ResponseStatusCode;
import vn.com.loyalty.core.dto.request.BodyRequest;
import vn.com.loyalty.core.utils.factory.response.BodyResponse;

public abstract class OrchestrationStep {


    private String stepStatus = Constants.OrchestrationStepStatus.STATUS_PENDING;

    String getStepStatus() {
        return this.stepStatus;
    }

    <T, S> BodyResponse<T> process(BodyRequest<S> request) {

        try {
            BodyResponse<T> response = this.handleProcess(request);
            if (ResponseStatusCode.SUCCESS.getCode().equals(response.getCode())) {
                this.stepStatus = Constants.OrchestrationStepStatus.STATUS_COMPLETE;
            } else {
                this.stepStatus = Constants.OrchestrationStepStatus.STATUS_FAILED;
            }
            return response;
        } catch (Exception e) {
            this.stepStatus = Constants.OrchestrationStepStatus.STATUS_FAILED;
            throw e;
        }

    }

    void rollback(BodyRequest<?> request) {
        try {
            this.handleRollback(request);
            this.stepStatus = Constants.OrchestrationStepStatus.STATUS_COMPLETE;
        } catch (Exception e) {
            this.stepStatus = Constants.OrchestrationStepStatus.STATUS_FAILED;
        }

    }
    abstract <T, S> BodyResponse<S> handleProcess(BodyRequest<T> request);

    abstract <T, S> BodyResponse<S> handleRollback(BodyRequest<T> request);


}
