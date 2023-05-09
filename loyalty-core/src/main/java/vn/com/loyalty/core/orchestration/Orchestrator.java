package vn.com.loyalty.core.orchestration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import vn.com.loyalty.core.constant.Constants;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Getter
@Setter
@RequiredArgsConstructor
public class Orchestrator {

    private final List<OrchestrationStep> orchestrationPool;

    public void asyncProcessOrchestration() {
        this.asyncProcessOrchestration(UUID.randomUUID().toString());
    }

    public void asyncProcessOrchestration(String orchestrationId) {
        log.info("===================> Start Orchestration - OrchestrationId: {}", orchestrationId);
        log.info("===================> Process Orchestration");
        CompletableFuture.allOf(orchestrationPool.stream()
                        .map(step -> {
                            step.setOrchestrationId(orchestrationId);
                            return CompletableFuture.supplyAsync(step::process);
                        })
                        .toArray(CompletableFuture[]::new))
                .join();

        if (this.orchestrationPool.stream().anyMatch(step -> !Constants.OrchestrationStepStatus.STATUS_COMPLETED.equals(step.getStepStatus()))) {
            this.rollbackOrchestration();
        }
        log.info("===================> Complete Orchestration");
    }

    private void rollbackOrchestration() {
        log.info("===================> Rollback Orchestration");
        Flux.fromStream(this.orchestrationPool.stream().filter(step -> Constants.OrchestrationStepStatus.STATUS_COMPLETED.equals(step.getStepStatus())))
                .subscribeOn(Schedulers.parallel())
                .log()
                .map(OrchestrationStep::rollback)
                .handle((response, synchronousSink) -> {
                    if (response.getStatus() != 0) {
                        // TODO save to db to manual process
                    }
                    synchronousSink.complete();
                }).subscribe();
    }

    public static Orchestrator steps(OrchestrationStep... steps) {
        return new Orchestrator.OrchestrationBuilder().orchestrationPool(steps).build();
    }

    public static class OrchestrationBuilder {
        private List<OrchestrationStep> orchestrationPool;

        private Orchestrator.OrchestrationBuilder orchestrationPool(OrchestrationStep... steps) {
            this.orchestrationPool = Arrays.stream(steps).toList();
            return this;
        }

        private Orchestrator build() {
            if (this.orchestrationPool.isEmpty()) {
                this.orchestrationPool = new ArrayList<>();
            }
            return new Orchestrator(this.orchestrationPool);
        }
    }

    public static void main(String[] args) {
        Flux<LocalDateTime> localDateTimeFlux = Flux.interval(Duration.ofSeconds(1))
                .map(t -> LocalDateTime.now());

        localDateTimeFlux.subscribe(t -> System.out.println(t.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"))));
    }

}
