package vn.com.loyalty.core.orchestration;

import org.springframework.util.concurrent.ListenableFutureTask;
import vn.com.loyalty.core.constant.Constants;
import vn.com.loyalty.core.constant.enums.ResponseStatusCode;
import vn.com.loyalty.core.dto.request.BodyRequest;
import vn.com.loyalty.core.utils.factory.response.BodyResponse;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Orchestration {

    private final List<OrchestrationStep> orchestrationPool;

    public Orchestration(OrchestrationStep... steps) {
        this.orchestrationPool = Arrays.stream(steps).toList();
    }

    public <T> Boolean syncProcessOrchestration(T data){
        for (OrchestrationStep step : orchestrationPool) {
            step.process(BodyRequest.of(data));
            if(Constants.OrchestrationStepStatus.STATUS_FAILED.equals(step.getStepStatus())) {
                this.rollbackOrchestration(data);
                return false;
            }
        }
        return true;
    }

    public <T, S> Boolean asyncProcessOrchestration(T data) {

        ExecutorService executorService = Executors.newCachedThreadPool();
        for (OrchestrationStep step : orchestrationPool) {
            ListenableFutureTask<BodyResponse<S>> future = new ListenableFutureTask<>(() -> step.process(BodyRequest.of(data)));
            future.addCallback(
                    response -> {
                        if (response != null && !ResponseStatusCode.SUCCESS.getCode().equals(response.getCode())) {
                            step.rollback(BodyRequest.of(data));
                        }
                    }, error -> step.rollback(BodyRequest.of(data))
            );
            executorService.submit(future);
        }

        executorService.shutdown();
        return null;
    }

    private <T> void rollbackOrchestration(T data) {
        List<OrchestrationStep> rollbackPool = this.orchestrationPool.stream().filter(step -> Constants.OrchestrationStepStatus.STATUS_FAILED.equals(step.getStepStatus()))
                .collect(Collectors.toList());

        for (OrchestrationStep step : rollbackPool) {
            step.rollback(BodyRequest.of(data));
        }
    }

    List<OrchestrationStep> getOrchestrationPool() {
        return this.orchestrationPool;
    }


}
