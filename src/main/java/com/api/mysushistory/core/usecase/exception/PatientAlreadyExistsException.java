package com.api.mysushistory.core.usecase.exception;

import static java.lang.String.format;

public class PatientAlreadyExistsException extends BusinessException {

  private static final String ERROR_CODE = "already_exists";
  private static final String MESSAGE = "Patient with cpf=[%s] already exists.";

  public PatientAlreadyExistsException(final String cpf) {
    super(format(MESSAGE, cpf), ERROR_CODE);
  }
}
