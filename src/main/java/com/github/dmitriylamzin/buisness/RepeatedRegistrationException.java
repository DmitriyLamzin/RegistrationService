package com.github.dmitriylamzin.buisness;

public class RepeatedRegistrationException extends RuntimeException {
  public RepeatedRegistrationException(String message) {
    super(message);
  }

  public RepeatedRegistrationException(String message, Throwable cause) {
    super(message, cause);
  }


}
