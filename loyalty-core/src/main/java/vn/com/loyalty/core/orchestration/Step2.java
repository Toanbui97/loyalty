package vn.com.loyalty.core.orchestration;

import vn.com.loyalty.core.constant.enums.ResponseStatusCode;
import vn.com.loyalty.core.dto.request.BodyRequest;
import vn.com.loyalty.core.utils.factory.response.BodyResponse;
@SuppressWarnings("unchecked")
public class Step2 extends OrchestrationStep {

    @Override
    <T, V> BodyResponse<V> handleProcess(BodyRequest<T> request) {
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
        return (BodyResponse<V>) BodyResponse.<String>builder().code(ResponseStatusCode.SUCCESS.getCode()).data("process 2").build();

    }

    @Override
    @SuppressWarnings("unchecked")
    <T, V> BodyResponse<V> handleRollback(BodyRequest<T> request) {
        return (BodyResponse<V>) BodyResponse.<String>builder().code(ResponseStatusCode.SUCCESS.getCode()).data("rollback 2").build();
    }
}
