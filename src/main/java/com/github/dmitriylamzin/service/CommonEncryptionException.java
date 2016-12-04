package com.github.dmitriylamzin.service;

public class CommonEncryptionException extends RuntimeException {
  public CommonEncryptionException(String message) {
    super(message);
  }

  public CommonEncryptionException(String message, Throwable cause) {
    super(message, cause);
  }
}
