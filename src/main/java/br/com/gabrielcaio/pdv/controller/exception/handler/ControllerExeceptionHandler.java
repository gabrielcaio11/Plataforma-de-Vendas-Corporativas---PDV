package br.com.gabrielcaio.pdv.controller.exception.handler;

import br.com.gabrielcaio.pdv.controller.exception.error.BusinessException;
import br.com.gabrielcaio.pdv.controller.exception.error.DataBaseException;
import br.com.gabrielcaio.pdv.controller.exception.error.EntityExistsException;
import br.com.gabrielcaio.pdv.controller.exception.error.ErrorType;
import br.com.gabrielcaio.pdv.controller.exception.error.ForbiddenException;
import br.com.gabrielcaio.pdv.controller.exception.error.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerExeceptionHandler {
  private static final Logger LOG = LoggerFactory.getLogger(ControllerExeceptionHandler.class);

  @ExceptionHandler(ResourceNotFoundException.class)
  public ProblemDetail handlerResourceNotFound(
      ResourceNotFoundException e, HttpServletRequest request) {
    return buildProblemDetail(
        HttpStatus.NOT_FOUND, ErrorType.RESOURCE_NOT_FOUND, e.getMessage(), request);
  }

  @ExceptionHandler(EntityExistsException.class)
  public ProblemDetail handlerEntityExists(EntityExistsException e, HttpServletRequest request) {
    return buildProblemDetail(
        HttpStatus.CONFLICT, ErrorType.ENTITY_ALREADY_EXISTS, e.getMessage(), request);
  }

  @ExceptionHandler(DataBaseException.class)
  public ProblemDetail handlerDataBase(DataBaseException e, HttpServletRequest request) {
    return buildProblemDetail(
        HttpStatus.BAD_REQUEST, ErrorType.DATABASE_ERROR, e.getMessage(), request);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ProblemDetail handlerMethodArgumentNotValid(
      MethodArgumentNotValidException e, HttpServletRequest request) {
    ProblemDetail problemDetail =
        buildProblemDetail(
            HttpStatus.UNPROCESSABLE_CONTENT,
            ErrorType.VALIDATION_ERROR,
            "Validation failed",
            request);

    List<Map<String, String>> errors =
        e.getBindingResult().getFieldErrors().stream()
            .map(
                fieldError ->
                    Map.of(
                        "field",
                        fieldError.getField(),
                        "message",
                        fieldError.getDefaultMessage() == null
                            ? "Invalid value"
                            : fieldError.getDefaultMessage()))
            .toList();
    problemDetail.setProperty("errors", errors);
    return problemDetail;
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ProblemDetail handleAccesDeniedException(
      AccessDeniedException e, HttpServletRequest request) {
    return buildProblemDetail(HttpStatus.FORBIDDEN, ErrorType.FORBIDDEN, "Access denied", request);
  }

  @ExceptionHandler(AuthenticationException.class)
  public ProblemDetail handleAuthenticationException(
      org.springframework.security.core.AuthenticationException e, HttpServletRequest request) {
    return buildProblemDetail(
        HttpStatus.UNAUTHORIZED, ErrorType.UNAUTHORIZED, "Authentication failed", request);
  }

  @ExceptionHandler(BusinessException.class)
  public ProblemDetail handleBusinessException(BusinessException e, HttpServletRequest request) {
    return buildProblemDetail(
        HttpStatus.UNPROCESSABLE_CONTENT,
        ErrorType.BUSINESS_RULE_VIOLATION,
        e.getMessage(),
        request);
  }

  @ExceptionHandler(ForbiddenException.class)
  public ProblemDetail handleForbiddenException(ForbiddenException e, HttpServletRequest request) {
    return buildProblemDetail(HttpStatus.FORBIDDEN, ErrorType.FORBIDDEN, e.getMessage(), request);
  }

  @ExceptionHandler(Exception.class)
  public ProblemDetail handleUnexpectedException(Exception e, HttpServletRequest request) {
    LOG.error("Unexpected error while processing request {}", request.getRequestURI(), e);
    return buildProblemDetail(
        HttpStatus.INTERNAL_SERVER_ERROR,
        ErrorType.INTERNAL_ERROR,
        "Unexpected error occurred",
        request);
  }

  private ProblemDetail buildProblemDetail(
      HttpStatus status, ErrorType errorType, String detail, HttpServletRequest request) {
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
    problemDetail.setTitle(errorType.getTitle());
    problemDetail.setType(URI.create(errorType.getTypeUri()));
    problemDetail.setInstance(URI.create(request.getRequestURI()));
    return problemDetail;
  }
}
