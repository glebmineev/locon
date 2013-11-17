package ru.spb.locon

import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.mail.*
import javax.mail.MessagingException
import javax.mail.Multipart
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart

class EmailService {

  static transactional = true
  static scope = "session";
  static proxy = true

  GrailsApplication grailsApplication =
    ApplicationHolder.getApplication().getMainContext().getBean("grailsApplication") as GrailsApplication

  //Логгер
  static Logger logger = LoggerFactory.getLogger(EmailService.class)

  static final String HOST = "smtp.gmail.com"
  static final String PORT = "587"
  static final String AUTH = "true"
  static final String STARTTLS = "true"
  static final String LOGIN = "glebmin@gmail.com"
  static final String PASSWORD = "angel2011"

  /**
   * Отсыл почты с хэшем для регистрации.
   * @param to - кому шлем.
   * @param hash - хеш.
   */
  void sendConfirmationEmail(String to, String hash) {

    try {

      Session session = getSession()

      MimeMessage message = getMessage(session, to)

      Multipart mp = new MimeMultipart();

      String htmlBody =
        new Scanner(grailsApplication.mainContext.getResource("email/template.html").file, "UTF-8").useDelimiter("\\A").next();

      String replaced = htmlBody.replace("ACTIVATION_CODE", hash)

      MimeBodyPart htmlPart = new MimeBodyPart();
      htmlPart.setContent(replaced, "text/html; charset=utf-8");
      mp.addBodyPart(htmlPart);
      message.setContent(mp)

      Transport.send(message);

    } catch (MessagingException e) {
      logger.error(e.getMessage())
    }
  }

  /**
   * Сброс пароля пользователя.
   * @param to - почта пользователя, куда слать новый пароль
   */
  void sendPassEmail(String to, String new_pass){
    Session session = getSession()
    MimeMessage message = getMessage(session, to)

    Multipart mp = new MimeMultipart();

    String htmlBody =
      new Scanner(grailsApplication.mainContext.getResource("email/newPass_template.html").file, "UTF-8").useDelimiter("\\A").next();

    String replaced = htmlBody.replace("NEW_PASS", new_pass)

    MimeBodyPart htmlPart = new MimeBodyPart();
    htmlPart.setContent(replaced, "text/html; charset=utf-8");
    mp.addBodyPart(htmlPart);
    message.setContent(mp)

    Transport.send(message);
  }

  /**
   * Создание сессии для отправки эл. почты.
   * @return сессия.
   */
  private Session getSession() {
    Properties props = new Properties();
    props.put("mail.smtp.host", HOST);
    props.put("mail.smtp.auth", AUTH);
    props.put("mail.smtp.port", PORT);
    props.put("mail.smtp.starttls.enable", STARTTLS);

    return Session.getInstance(props,
        new Authenticator() {
          @Override
          protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(LOGIN, PASSWORD);
          }
        });
  }

  /**
   * Формирвание сообщения по email.
   * @param session - сессия
   * @param to - кому слать(эл. ящик)
   * @return сообщение.
   */
  private MimeMessage getMessage(Session session, String to) {
    Message message = new MimeMessage(session);
    message.setFrom(new InternetAddress("glebsis"));
    message.setRecipients(Message.RecipientType.TO,
        InternetAddress.parse(to));
    message.setSubject("test", "UTF-8");
    message
  }

}
