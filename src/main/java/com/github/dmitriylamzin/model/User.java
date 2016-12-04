package com.github.dmitriylamzin.model;


import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.Pattern;

@Entity
public class User {

  @Id
  @Email
  @NotEmpty
  private String email;

  @Pattern(regexp = ".*!.*\\d.*\\d.*|.*\\d.*\\d.*!.*|.*\\d.*!.*\\d.*")
  private String password;

  private boolean isConfirmed;

  public User() {
  }

  /**
   * Construct user instance with full data about user.
   *
   * @param email an user email.
   * @param password an user password.
   * @param isConfirmed defines if the user has confirmed his registration.
   * */
  public User(String email, String password, boolean isConfirmed) {
    this.email = email;
    this.password = password;
    this.isConfirmed = isConfirmed;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public boolean isConfirmed() {
    return isConfirmed;
  }

  public void setIsConfirmed(boolean isConfirmed) {
    this.isConfirmed = isConfirmed;
  }

  @Override
  public String toString() {
    return "User{"
            + "email='" + email + '\''
            + ", password='" + password + '\''
            + ", isConfirmed=" + isConfirmed
            + '}';
  }
}
