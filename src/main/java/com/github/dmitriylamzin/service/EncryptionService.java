package com.github.dmitriylamzin.service;

public interface EncryptionService {

  String encrypt(String data);

  String decrypt(String encryptedData);
}
