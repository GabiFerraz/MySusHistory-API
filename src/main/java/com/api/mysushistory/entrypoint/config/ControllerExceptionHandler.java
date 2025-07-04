package com.api.mysushistory.entrypoint.config;

import com.api.mysushistory.core.domain.exception.DomainException;
import com.api.mysushistory.core.usecase.exception.BusinessException;
import com.api.mysushistory.core.usecase.exception.PatientNotFoundException;
import com.api.mysushistory.core.usecase.exception.ShareTokenNotFoundException;
import com.api.mysushistory.infra.gateway.exception.GatewayException;
import jakarta.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

  protected static final String INVALID_PARAMS = "INVALID_PARAMS";

  @ExceptionHandler({BusinessException.class})
  public ResponseEntity<ErrorResponse> handlerBusinessException(final BusinessException ex) {
    log.error(ex.getMessage(), ex);
    final var errorResponse = new ErrorResponse(ex.getMessage(), ex.getErrorCode(), null);

    return ResponseEntity.badRequest().body(errorResponse);
  }

  @ExceptionHandler({GatewayException.class})
  public ResponseEntity<ErrorResponse> handlerGatewayException(final GatewayException ex) {
    log.error(ex.getMessage(), ex);
    final var errorResponse = new ErrorResponse(ex.getMessage(), ex.getCode(), null);

    return ResponseEntity.internalServerError().body(errorResponse);
  }

  @ExceptionHandler({PatientNotFoundException.class})
  public ResponseEntity<ErrorResponse> handlerClientNotFoundException(
      final PatientNotFoundException ex) {
    log.error(ex.getMessage(), ex);
    final var errorResponse = new ErrorResponse(ex.getMessage(), ex.getErrorCode(), null);

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }

  @ExceptionHandler({ShareTokenNotFoundException.class})
  public ResponseEntity<ErrorResponse> handlerClientNotFoundException(
      final ShareTokenNotFoundException ex) {
    log.error(ex.getMessage(), ex);
    final var errorResponse = new ErrorResponse(ex.getMessage(), ex.getErrorCode(), null);

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }

  @ExceptionHandler({DomainException.class})
  public ResponseEntity<ErrorResponse> handlerDomainException(final DomainException ex) {
    log.error(ex.getMessage(), ex);
    final var errorResponse = new ErrorResponse(ex.getMessage(), "domain_exception", null);

    return ResponseEntity.internalServerError().body(errorResponse);
  }

  @Override
  protected @NotNull ResponseEntity<Object> handleMethodArgumentNotValid(
      final MethodArgumentNotValidException ex,
      final @NotNull HttpHeaders headers,
      final @NotNull HttpStatusCode status,
      final @NotNull WebRequest request) {
    log.error("Validation error: {}", ex.getMessage());

    final List<FieldError> errors = ex.getBindingResult().getFieldErrors();

    final Set<Violation> violations =
        errors.stream()
            .map(it -> new Violation(it.getField(), it.getDefaultMessage()))
            .collect(Collectors.toUnmodifiableSet());

    final var errorResponse = new ViolationErrorResponse(INVALID_PARAMS, violations);

    return ResponseEntity.badRequest().body(errorResponse);
  }

  @Override
  protected @NotNull ResponseEntity<Object> handleMissingServletRequestParameter(
      final MissingServletRequestParameterException ex,
      final @NotNull HttpHeaders headers,
      final @NotNull HttpStatusCode status,
      final @NotNull WebRequest request) {

    final var violation = Set.of(new Violation(ex.getParameterName(), ex.getBody().getDetail()));
    final var errorResponse = new ViolationErrorResponse(INVALID_PARAMS, violation);

    return ResponseEntity.badRequest().body(errorResponse);
  }

  @Override
  public ResponseEntity<Object> handleHandlerMethodValidationException(
      final HandlerMethodValidationException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    log.error(ex.getMessage());

    final List<String> violations =
        Arrays.stream(ex.getDetailMessageArguments())
            .map(Object::toString)
            .map(it -> it.split(", and"))
            .flatMap(Arrays::stream)
            .map(String::trim)
            .collect(Collectors.toList());

    final var errorResponse = new ErrorMethodValidationResponse(INVALID_PARAMS, violations);

    return ResponseEntity.badRequest().body(errorResponse);
  }

  @ExceptionHandler({Exception.class})
  public ResponseEntity<ErrorResponse> handlerException(final Exception ex) {
    log.error("Exception={}", ex, ex);

    return ResponseEntity.internalServerError().build();
  }
}
