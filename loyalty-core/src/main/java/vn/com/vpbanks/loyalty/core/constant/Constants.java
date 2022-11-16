package vn.com.vpbanks.loyalty.core.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

public class Constants {

    public static final String QUEUE_INIT = "QUEUE_INIT";
    public static final int RESPONSE_STATUS_SUCCESS = 1;
    public static final int RESPONSE_STATUS_FAIL = 0;
    public static final Integer INIT_VERSION_APPROVED = 0;
    public static final String AUTH_HEADER = "Authorization";
    public static final String REGEX_PATH_URI = "(\\{[^\\}]*\\})";

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class ServiceConfig {

        /* ================== Kafka ==================== */

        public static final String DEPOSITORY_NAME = "depository";

        public static final String KAFKA_PROPERTIES_PREFIX = "spring.kafka";
        public static final String KAFKA_POOL_PROPERTIES_PREFIX = "spring.kafka.poll";
    }

}
