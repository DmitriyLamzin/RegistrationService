package com.github.dmitriylamzin.service;


import com.github.dmitriylamzin.model.Email;

public interface MessageProducingService {
  void sendMessage(String destination, Email email);
}
