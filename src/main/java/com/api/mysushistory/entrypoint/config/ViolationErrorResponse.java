package com.api.mysushistory.entrypoint.config;

import java.util.Set;

public record ViolationErrorResponse(String error, Set<Violation> violations) {}
