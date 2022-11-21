package vn.com.loyalty.core.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WebClientConstant {

    public static final String WEBCLIENT_CONN_POOL = "webclient-conn-pool";
    public static final String WEBCLIENT_EVENT_LOOP = "webclient-event-loop";

    public static final String WEBCLIENT_HTTP_PROPERTIES_PREFIX = "webclient.http";
    public static final String WEBCLIENT_RETRY_PROPERTIES_PREFIX = "webclient.retry";
}
