package com.github.dmitriylamzin.buisness;

import com.github.dmitriylamzin.model.User;

import java.util.Locale;

public interface ConfirmationService {

  void requestConfirmation(User user, Locale locale);

  User confirmRegistration(String confirmationLink);

}
