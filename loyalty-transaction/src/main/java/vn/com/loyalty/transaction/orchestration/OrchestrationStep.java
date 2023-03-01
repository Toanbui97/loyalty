package vn.com.loyalty.transaction.orchestration;

import lombok.extern.slf4j.Slf4j;
import vn.com.loyalty.core.constant.Constants;
import vn.com.loyalty.core.constant.enums.ResponseStatusCode;
import vn.com.loyalty.core.dto.request.BodyRequest;
import vn.com.loyalty.core.utils.factory.response.BodyResponse;

@Slf4j
public abstract class OrchestrationStep {


    private String stepStatus = Constants.OrchestrationStepStatus.STATUS_PENDING;

    String getStepStatus() {
        return this.stepStatus;
    }

    <T, V> BodyResponse<V> process(BodyRequest<T> request) {

        try {
            BodyResponse<V> response = this.handleProcess(request);
            if (ResponseStatusCode.SUCCESS.getCode().equals(response.getCode())) {
                this.stepStatus = Constants.OrchestrationStepStatus.STATUS_COMPLETED;
            } else {
                this.stepStatus = Constants.OrchestrationStepStatus.STATUS_FAILED;
            }
            log.info(response.getData() + " done");
            return response;
        } catch (Exception e) {
            this.stepStatus = Constants.OrchestrationStepStatus.STATUS_FAILED;
            return BodyResponse.<V>builder().code(ResponseStatusCode.INTERNAL_SERVER_ERROR.getCode()).build();
        }

    }

    <T, V> BodyResponse<V> rollback(BodyRequest<T> request) {
        try {
            BodyResponse<V> response = this.handleRollback(request);
            if (ResponseStatusCode.SUCCESS.getCode().equals(response.getCode())) {
                this.stepStatus = Constants.OrchestrationStepStatus.STATUS_ROLLBACK;
            } else {
                this.stepStatus = Constants.OrchestrationStepStatus.STATUS_FAILED;
            }
            return response;
        } catch (Exception e) {
            this.stepStatus = Constants.OrchestrationStepStatus.STATUS_FAILED;
            return BodyResponse.<V>builder().code(ResponseStatusCode.INTERNAL_SERVER_ERROR.getCode()).build();
        }
    }


    abstract <T, V> BodyResponse<V> handleProcess(BodyRequest<T> request);

    abstract <T, V> BodyResponse<V> handleRollback(BodyRequest<T> request);


}
