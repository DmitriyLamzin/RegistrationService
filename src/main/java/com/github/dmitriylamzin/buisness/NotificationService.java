package com.github.dmitriylamzin.buisness;


import com.github.dmitriylamzin.model.Email;

public interface NotificationService {

  boolean sendMessage(Email email);
}
