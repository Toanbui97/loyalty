package vn.com.loyalty.cms.config;

import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class SchedulerConfiguration {

    private final DataSource dataSource;

    @Bean
    public LockProvider lockProvider() {
        return new JdbcTemplateLockProvider(dataSource, "cms.shedlock");
    }
}
