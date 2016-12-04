package com.github.dmitriylamzin.model;

public class Email {

  private String from;

  private String to;

  private String subject;

  private String body;

  public Email() {
  }

  /**
   * Construct email with full data about email.
   *
   * @param from an address of email producer.
   * @param to an address of email consumer.
   * @param subject an email subject field.
   * @param body the main section of the email.
   * */
  public Email(String from, String to, String subject, String body) {
    this.from = from;
    this.to = to;
    this.subject = subject;
    this.body = body;
  }

  public String getFrom() {
    return from;
  }

  public void setFrom(String from) {
    this.from = from;
  }

  public String getTo() {
    return to;
  }

  public void setTo(String to) {
    this.to = to;
  }

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  @Override
  public String toString() {
    return "Email{"
            + "from='" + from + '\''
            + ", to='" + to + '\''
            + ", subject='" + subject + '\''
            + ", body='" + body + '\''
            + '}';
  }
}