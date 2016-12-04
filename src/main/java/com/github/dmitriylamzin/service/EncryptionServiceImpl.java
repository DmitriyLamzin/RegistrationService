package com.github.dmitriylamzin.service;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


@Service
public class EncryptionServiceImpl implements EncryptionService {

  private Logger logger = LoggerFactory.getLogger(this.getClass());

  private String key;

  private IvParameterSpec ivSpec;

  private static SecretKeySpec secretKey ;

  @Autowired
  public EncryptionServiceImpl(@Value("${aes.key}")String key) {

    setSecretKey(key);
  }

  /**
   * Sets up secret key and iv based on value passed to the method.
   *
   * @param myKey the key consists of 16 bytes.
   * */
  public void setSecretKey(String myKey) {
    logger.debug("key: " + myKey);

    byte[] iv = new byte[16];
    new Random().nextBytes(iv);
    ivSpec = new IvParameterSpec(iv);

    byte[] key ;
    MessageDigest sha;

    try {
      key = myKey.getBytes("UTF-8");
      sha = MessageDigest.getInstance("SHA-1");
      key = sha.digest(key);
      key = Arrays.copyOf(key, 16); // use only first 128 bit

      secretKey = new SecretKeySpec(key, "AES");

    } catch (NoSuchAlgorithmException ex) {
      logger.error("Algorithm was not find", ex);

    } catch (UnsupportedEncodingException ex) {
      logger.error("Encoding type is unsupported", ex);
    }
  }

  /**
   * Encrypts a string passed to the method.
   *
   * @param strToEncrypt the string to be encrypted.
   *
   * @return string encrypted with AES/CBC algorithm
   * */
  public String encrypt(String strToEncrypt) {

    try {
      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

      cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);

      String encryptedData = Base64.encodeBase64URLSafeString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));

      logger.debug("encrypted: " + encryptedData);
      return encryptedData;
    } catch (Exception exc) {
      throw new CommonEncryptionException("Cannot encrypt a string", exc);
    }

  }

  /**
   * Decrypts a string passed to the method.
   *
   * @param strToDecrypt the string to be decrypted.
   *
   * @return string decrypted with AES/CBC algorithm
   * */
  public String decrypt(String strToDecrypt) {

    try {
      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

      cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);

      String decryptedData = new String(cipher.doFinal(Base64.decodeBase64(strToDecrypt)));

      logger.debug("decrypted: " + decryptedData);

      return decryptedData;
    } catch (Exception exc) {
      throw new CommonEncryptionException("cannot decrypt a string", exc);
    }
  }
}
