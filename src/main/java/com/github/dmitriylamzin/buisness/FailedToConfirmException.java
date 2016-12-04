package com.github.dmitriylamzin.buisness;

public class FailedToConfirmException extends RuntimeException {

  public FailedToConfirmException(String message) {
    super(message);
  }

  public FailedToConfirmException(String message, Throwable cause) {
    super(message, cause);
  }

}
