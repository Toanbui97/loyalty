package vn.com.loyalty.cms;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.*;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Aspect
@Configuration
@RequiredArgsConstructor
@Slf4j
public class ApplicationAspect {


    synchronized void a() {

        // get str từ db
        // xử lý
        // update log
        // update data db db

    }


    public static void main(String[] args) {


        ExecutorService service = Executors.newFixedThreadPool(1);

    }

}
