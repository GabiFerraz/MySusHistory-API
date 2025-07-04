package com.api.mysushistory.core.usecase.exception;

public class InvalidTokenExpirationException extends BusinessException {

  private static final String ERROR_CODE = "invalid_token_expiration";
  private static final String MESSAGE = "Expiration time must be greater than zero.";

  public InvalidTokenExpirationException() {
    super(MESSAGE, ERROR_CODE);
  }
}
