package com.microservices.spring.notificationservice;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import io.micrometer.observation.annotation.Observed;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Observed(name = "mail-service")
public class MailService {

  @Value("${spring.mail.username}")
  private String emailUsername;

  private final JavaMailSender mailSender;
  private final FreeMarkerConfigurer freeMarker;

  public MailService(JavaMailSender mailSender, FreeMarkerConfigurer freeMarker) {
    this.mailSender = mailSender;
    this.freeMarker = freeMarker;
  }

  public void sendSimpleTextMail(String toEmail, String subject, String body) {
    try {
      SimpleMailMessage mail = new SimpleMailMessage();
      mail.setTo(toEmail);
      mail.setSubject(subject);
      mail.setFrom(getFromEmail());
      mail.setText(body);

      mailSender.send(mail);
    } catch (Exception e) {
      log.error("Exception caught while trying to send an email", e);
    }
  }

  public void sendSimpleHtmlMail(String toEmail, String subject, String body) {
    try {
      MimeMessage mail = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(mail, true, "UTF-8");

      helper.setTo(toEmail);
      helper.setSubject(subject);
      helper.setFrom(getFromEmail());
      helper.setText(body, true);

      mailSender.send(mail);
    } catch (Exception e) {
      log.error("Exception caught while trying to send an email", e);
    }
  }

  public void sendOrderPlacedEmail(String toEmail, UUID orderNumber) {
    String subject = "Your new order was registered!";

    Map<String, Object> model = new HashMap<>();
    model.put("orderNumber", orderNumber.toString());

    sendSimpleHtmlMail(toEmail, subject, geContentFromTemplate("order-placed-email.flth", model));
  }

  private String geContentFromTemplate(String templatePath, Map<String, Object> model) {
    StringBuffer content = new StringBuffer();

    try {
      content.append(FreeMarkerTemplateUtils
          .processTemplateIntoString(freeMarker.getConfiguration().getTemplate(templatePath), model));
    } catch (Exception e) {
      log.error(
          String.format("Exception caught while trying to get content from email template: %s", templatePath), e);
    }

    return content.toString();
  }

  private String getFromEmail() {
    return String.format("%s <%s>", emailUsername, emailUsername);
  }

}
