package vn.com.loyalty.core.orchestration;

import vn.com.loyalty.core.constant.enums.ResponseStatusCode;
import vn.com.loyalty.core.dto.request.BodyRequest;
import vn.com.loyalty.core.dto.response.cms.CustomerResponse;
import vn.com.loyalty.core.utils.factory.response.BodyResponse;


@SuppressWarnings("unchecked")
public class Step1 extends OrchestrationStep {

    @Override
    <T, V> BodyResponse<V> handleProcess(BodyRequest<T> request) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
        return (BodyResponse<V>) BodyResponse.builder().code(ResponseStatusCode.SUCCESS.getCode())
                .data(CustomerResponse.builder().customerCode("213123")
                        .activeVoucher(2L)
                        .customerName("1standard")
                        .build())
                .build();
    }

    @Override
    <T, V> BodyResponse<V> handleRollback(BodyRequest<T> request) {
        return (BodyResponse<V>) BodyResponse.<String>builder().code(ResponseStatusCode.SUCCESS.getCode()).data("rollback 1").build();
    }
}
