package vn.com.loyalty.core.dto.kafka;

import lombok.Builder;

@Builder
public record Reply(String key, String message) {}
