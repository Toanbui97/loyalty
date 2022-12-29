package vn.com.loyalty.core.dto.message;

import lombok.Builder;

@Builder
public record Reply(String key, String message) {}
