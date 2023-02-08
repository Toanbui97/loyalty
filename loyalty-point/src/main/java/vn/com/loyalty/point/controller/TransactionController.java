package vn.com.loyalty.point.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.com.loyalty.point.worker.PointScheduler;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@Slf4j
public class TransactionController {

    private final PointScheduler pointScheduler;

    @GetMapping("/startPointSchedule")
    public String startPointSchedule() {
        return null;
    }
}
