# RegistrationService

Simple registration service with two supported Locales (english and russian).

There is one "sign up" button on the `/registration` page.

After clicking on this button the registration form will be loaded.
Then typing our email and password(contains at least two digits and
one exclamation point.)

After submitting the form you receive an email with confirmation link.

After confirmation you get access to the secret page.

Stack of technology:
* `maven` for building and dependency management;
* `jacoco` ensures that code is covered by tests;
* `checkstyle` for code accuracy;
* `spring boot` for fast start up;
* `hibernate validator` for form fields validation;
* `hsqldb` embedded database;
* `activeMQ` - message broker with SpringJMS support to send emails;
* `skeleton` - css framework;
* `Thymeleafe` as view template;
* `travic-ci` for continuous integration.