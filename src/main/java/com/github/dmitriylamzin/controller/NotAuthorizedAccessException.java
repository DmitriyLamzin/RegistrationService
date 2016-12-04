package com.github.dmitriylamzin.controller;

public class NotAuthorizedAccessException extends RuntimeException{
  public NotAuthorizedAccessException(String message) {
    super(message);
  }

  public NotAuthorizedAccessException(String message, Throwable cause) {
    super(message, cause);
  }
}
