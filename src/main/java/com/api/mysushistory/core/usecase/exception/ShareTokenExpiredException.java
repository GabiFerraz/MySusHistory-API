package com.api.mysushistory.core.usecase.exception;

import static java.lang.String.format;

public class ShareTokenExpiredException extends BusinessException {

  private static final String ERROR_CODE = "share_token_expired";
  private static final String MESSAGE = "Share token [%s] has expired.";

  public ShareTokenExpiredException(final String token) {
    super(format(MESSAGE, token), ERROR_CODE);
  }
}
