package com.tokioschool.ratingapp.core.exceptions;

import java.util.Map;

public class ValidacionException extends RuntimeException {
  private Map<String, String> errors;

  public ValidacionException(String message, Map<String, String> errors) {
    super(message);
    this.errors = errors;
  }

  public Map<String, String> getErrors() {
    return errors;
  }

}
