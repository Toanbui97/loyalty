package vn.com.loyalty.core.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
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

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class KafkaConstants {
        public static final String TRANSACTION_TOPIC = "loyalty_transaction_topic";
        public static final String POINT_TOPIC = "loyalty_point_topic";
        public static final String CUSTOMER_TOPIC = "loyalty_customer_topic";
        public static final String POINT_GROUP = "loyalty_point_group";
        public static final String TRANSACTION_GROUP = "loyalty_transaction_group";
        public static final String CUSTOMER_GROUP = "loyalty_customer_group";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class RedisConstants {
        public static final String EPOINT_DIR = "EPOINT:";
        public static final String RPOINT_DIR = "RPOINT:";
        public static final String RANK_DIR = "MDATA:RANK:";
        public static final String CRANK_DIR = "CRANK:";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class OrchestrationStepStatus {

        public static final String STATUS_PENDING = "PENDING";
        public static final String STATUS_COMPLETED = "COMPLETE";
        public static final String STATUS_FAILED = "FAILED";
        public static final String STATUS_ROLLBACK = "ROLLBACK";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class MasterDataKey {
        public static final String EPOINT_EXPIRE_TIME = "EPOINT_EXPIRE_TIME";
        public static final String RANK_EXPIRE_TIME = "RANk_EXPIRE_TIME";
        public static final String POINT_TO_VALUE = "POINT_TO_VALUE";
        public static final String RANK_DEFAULT = "NONE";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class SchedulerTaskName {
        public static final String DEACTIVATE_EPOINT = "DEACTIVATE_EPOINT";
        public static final String SCHEDULE_EPOINT = "SCHEDULE_EPOINT";
    }

}
