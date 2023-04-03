package vn.com.loyalty.core.orchestration;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import vn.com.loyalty.core.constant.Constants;
import vn.com.loyalty.core.exception.OrchestrationException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
public record Orchestration(List<OrchestrationStep> orchestrationPool) {

    public Orchestration asyncProcessOrchestration() throws OrchestrationException {

        CompletableFuture.allOf(orchestrationPool.stream()
                        .map(step -> CompletableFuture.supplyAsync(step::process))
                        .toArray(CompletableFuture[]::new))
                .join();

        if (this.orchestrationPool.stream().anyMatch(step -> !Constants.OrchestrationStepStatus.STATUS_COMPLETED.equals(step.getStepStatus()))) {
            this.rollbackOrchestration();
            throw new OrchestrationException("Orchestration Exception");
        }
        return this;
    }

    private void rollbackOrchestration() {

        Flux.fromStream(this.orchestrationPool.stream())
                .subscribeOn(Schedulers.parallel())
                .log()
                .map(OrchestrationStep::rollback)
                .handle((response, synchronousSink) -> {
                    if (response.getStatus() != 1) {
                        // TODO save to db to manual process
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
