package com.github.dmitriylamzin.buisness;

import com.github.dmitriylamzin.controller.ConfirmationController;
import com.github.dmitriylamzin.controller.RegistrationController;
import com.github.dmitriylamzin.model.Email;
import com.github.dmitriylamzin.model.User;
import com.github.dmitriylamzin.repository.UserRepository;
import com.github.dmitriylamzin.service.CommonEncryptionException;
import com.github.dmitriylamzin.service.EncryptionService;
import com.github.dmitriylamzin.service.MessageProducingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Locale;

@Service
public class ConfirmationServiceImpl implements ConfirmationService {

  private static final String MESSAGE_DESTINATION = "outbox";
  private static final String FROM = "RegistrationService";
  private static final String SUBJECT = "Registration Confirmation";
  private static final String DIVIDER = "&deviderSign&";
  private static final  String EMAIL_CONFIRMATION_TEMPLATE = "html/confirmationEmail";

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private UserRepository userRepository;
  private MessageProducingService messageProducingService;
  private EncryptionService encryptionService;
  private ITemplateEngine emailTemplateEngine;

  /**
   * Construct confirmation service.
   *
   * @param userRepository a repository for CRUD operation with User's instances.
   * @param messageProducingService a service for sending confirmation messages to the outbox.
   * @param encryptionService a service, which provides encryption/decryption of
   *                          personal user's data.
   * @param emailTemplateEngine an engine to resolve email templates.
   */
  @Autowired
  public ConfirmationServiceImpl(UserRepository userRepository,
                                 MessageProducingService messageProducingService,
                                 EncryptionService encryptionService,
                                 ITemplateEngine emailTemplateEngine) {
    this.userRepository = userRepository;
    this.messageProducingService = messageProducingService;
    this.encryptionService = encryptionService;
    this.emailTemplateEngine = emailTemplateEngine;
  }

  @Override
  public void requestConfirmation(User user, Locale locale) {
    logger.info("Requesting confirmation for user data " + user.toString());
    logger.debug("using locale: " + locale.toString());
    Email email = createMessage(user, locale);
    messageProducingService.sendMessage(MESSAGE_DESTINATION, email);
  }

  @Override
  public User confirmRegistration(String confirmationLink) {
    try {
      String decryptedData = encryptionService.decrypt(confirmationLink);
      String[] userDataArray = decryptedData.split(DIVIDER);
      User user = userRepository.findOne(userDataArray[0]);

      if (user == null) {
        throw new FailedToConfirmException("User.not.found");
      }
      if (!user.getPassword().equals(userDataArray[1])) {
        logger.debug("Actual user password:" + user.getPassword());
        logger.debug("Decrypted user password:" + userDataArray[1]);
        throw new FailedToConfirmException("Password.not.match");
      } else {
        user.setIsConfirmed(true);
        userRepository.save(user);
        return user;
      }
    } catch (CommonEncryptionException e) {
      throw new FailedToConfirmException("Password.not.match", e);
    }
  }

  private Email createMessage(User user, Locale locale) {
    logger.info("Creating confirmation message.");
    Email email = new Email();
    email.setFrom(FROM);
    email.setSubject(SUBJECT);
    email.setTo(user.getEmail());
    email.setBody(resolveTemplate(user, locale));
    logger.debug("Ready message: " + email.toString());
    return email;
  }

  private String resolveTemplate(User user, Locale locale) {
    logger.info("Resolving template for message.");

    final Context context = new Context(locale);
    final String hidedPassword = hidePassword(user.getPassword());
    final String confirmationUrl = createConfirmationLink(user);

    context.setVariable("emailAddress", user.getEmail());
    context.setVariable("password", hidedPassword);
    context.setVariable("confirmationUrl", confirmationUrl);

    final String htmlContent = this.emailTemplateEngine.process(EMAIL_CONFIRMATION_TEMPLATE, context);

    logger.debug("body of message: " + htmlContent);

    return htmlContent;
  }

  private String hidePassword(String password) {
    logger.info("Hiding password.");

    String hidedPassword = "*******" + password.substring(password.length() - 2, password.length());

    logger.debug("hided password: " + hidedPassword);

    return hidedPassword;
  }

  private String createConfirmationLink(User user) {
    logger.info("Getting confirmation link.");

    try {
      String encryptedConfirmationData = null;

      encryptedConfirmationData = encryptionService.encrypt(user.getEmail() + DIVIDER + user.getPassword());
      String confirmationLink = MvcUriComponentsBuilder
              .fromMethodName(ConfirmationController.class, "confirmRegistration", encryptedConfirmationData,
                      null)
              .build().toString();

      logger.debug("Ready confirmation Link: " + confirmationLink);

      return confirmationLink;
    } catch (CommonEncryptionException e) {
      throw new FailedToConfirmException("failed.creating.link", e);
    }
  }
}
