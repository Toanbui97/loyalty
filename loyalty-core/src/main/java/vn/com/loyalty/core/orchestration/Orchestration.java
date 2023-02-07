package vn.com.loyalty.core.orchestration;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vn.com.loyalty.core.constant.Constants;
import vn.com.loyalty.core.dto.request.BodyRequest;
import vn.com.loyalty.core.exception.OrchestrationException;
import vn.com.loyalty.core.utils.factory.response.BodyResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

@Slf4j
public class Orchestration {

    private final List<OrchestrationStep> orchestrationPool;

    private Orchestration(List<OrchestrationStep> orchestrationPool) {
        super();
        this.orchestrationPool = orchestrationPool;
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

    public <T> void asyncProcessOrchestration(T data) throws OrchestrationException {

        ExecutorService service = Executors.newFixedThreadPool(orchestrationPool.size());

        CompletableFuture.allOf(orchestrationPool.stream()
                        .map(step -> CompletableFuture.supplyAsync(() -> step.process(BodyRequest.of(data)), service))
                        .toArray(CompletableFuture[]::new))
                .join();

        service.shutdown();
        if (this.orchestrationPool.stream().anyMatch(step -> !Constants.OrchestrationStepStatus.STATUS_COMPLETED.equals(step.getStepStatus()))) {
            this.rollbackOrchestration(new Object());
        }
    }

    private <T> void rollbackOrchestration(T data) {

        Flux.fromStream(this.orchestrationPool.stream())
                .flatMap(step -> {
                    step.rollback(BodyRequest.of(data));
                    return Mono.just(step);
                })
                .retry(3)
                .handle((step, synchronousSink) -> {
                    if (!Constants.OrchestrationStepStatus.STATUS_ROLLBACK.equals(step.getStepStatus())) {
                        //TODO save to db to manual process
                    }
                    synchronousSink.complete();
                }).subscribe();

    }

    public static Orchestration ofSteps(OrchestrationStep... steps) {
        return new Orchestration.OrchestrationBuilder().orchestrationPool(steps).build();
    }

    public static class OrchestrationBuilder {
        private List<OrchestrationStep> orchestrationPool;

        private Orchestration.OrchestrationBuilder orchestrationPool(OrchestrationStep... steps) {
            this.orchestrationPool = Arrays.stream(steps).toList();
            return this;
        }

        private Orchestration build() {
            if (this.orchestrationPool.isEmpty()) {
                this.orchestrationPool = new ArrayList<>();
            }
            return new Orchestration(this.orchestrationPool);
        }
    }

}
