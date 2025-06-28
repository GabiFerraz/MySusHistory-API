package com.api.mysushistory.core.domain.exception;

public record ErrorDetail(String field, String errorMessage, Object rejectedValue) {}
