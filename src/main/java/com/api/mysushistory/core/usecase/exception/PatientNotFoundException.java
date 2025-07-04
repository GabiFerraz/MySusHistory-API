package com.api.mysushistory.core.usecase.exception;

import static java.lang.String.format;

public class PatientNotFoundException extends BusinessException {

  private static final String ERROR_CODE = "not_found";
  private static final String MESSAGE = "Patient with identifier=[%s] not found.";

  public PatientNotFoundException(final String cpf) {
    super(format(MESSAGE, cpf), ERROR_CODE);
  }
}
