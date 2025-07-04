package com.api.mysushistory.core.usecase.exception;

import static java.lang.String.format;

public class ShareTokenNotFoundException extends BusinessException {

  private static final String ERROR_CODE = "share_token_not_found";
  private static final String MESSAGE = "Share token [%s] not found.";

  public ShareTokenNotFoundException(final String token) {
    super(format(MESSAGE, token), ERROR_CODE);
  }
}
