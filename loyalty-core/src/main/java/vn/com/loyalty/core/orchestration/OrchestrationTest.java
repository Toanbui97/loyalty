package vn.com.loyalty.core.orchestration;

public class OrchestrationTest {

    public static void main(String[] args) {

        Step1 step1 = new Step1();
        Step2 step2 = new Step2();

        Orchestration.ofSteps(step1, step2).asyncProcessOrchestration("data");
    }
}
