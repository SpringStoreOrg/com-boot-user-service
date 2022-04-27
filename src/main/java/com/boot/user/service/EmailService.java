package com.boot.user.service;

import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import com.boot.user.config.AppConfig;
import com.boot.user.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;

import com.boot.services.model.Email;
import com.boot.services.model.User;
import com.boot.user.model.ConfirmationToken;
import com.boot.user.model.PasswordResetToken;

@Slf4j
@Service
public class EmailService {

    private static final String CONFIMATION_EMAIL_TEMPLATE = "/templates/email-template.vm";

    private static final String RESET_PASSWORD_EMAIL_TEMPLATE = "/templates/reset-password-email-template.vm";

    @Autowired
    AppConfig appConfig;

    @Autowired
    JavaMailSender emailSender;

    @Autowired
    VelocityEngine velocityEngine;

    public void sendConfirmationEmail(User user, ConfirmationToken confirmationToken) {

        Email email = new Email();

        Map<String, Object> model = new HashMap<String, Object>();
        model.put("firstName", user.getFirstName());
        model.put("lastName", user.getLastName());
        model.put("path", appConfig.userServiceUrl + Constants.CONFIRM_USER_ACCOUNT);
        model.put("confirmationToken", confirmationToken.getConfirmationToken());
        model.put("signature", "www.springStore.com");
        email.setModel(model);

        MimeMessage mimeMessage = emailSender.createMimeMessage();

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

            mimeMessageHelper.setSubject("SpringStore confirmation Email");
            mimeMessageHelper.setFrom("noreply@springwebstore.com");
            mimeMessageHelper.setTo(user.getEmail());
            email.setEmailContent(geContentFromTemplate(email.getModel(), CONFIMATION_EMAIL_TEMPLATE));
            mimeMessageHelper.setText(email.getEmailContent(), true);

            emailSender.send(mimeMessageHelper.getMimeMessage());
        } catch (MessagingException e) {
            log.info("Exception by sending confirmation email: {}", e.getStackTrace());
        }
    }

    public void sendPasswordResetEmail(User user, PasswordResetToken passwordResetToken) {

        Email email = new Email();

        Map<String, Object> model = new HashMap<String, Object>();
        model.put("firstName", user.getFirstName());
        model.put("lastName", user.getLastName());
        model.put("path", appConfig.userServiceUrl + Constants.PASSWORD_RESET_EMAIL);
        model.put("passwordResetToken", passwordResetToken.getResetToken());
        model.put("location", "Cluj");
        model.put("signature", "www.springStore.com");
        email.setModel(model);

        MimeMessage mimeMessage = emailSender.createMimeMessage();

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

            mimeMessageHelper.setSubject("Password Reset Request");
            mimeMessageHelper.setFrom("noreply@springwebstore.com");
            mimeMessageHelper.setTo(user.getEmail());
            email.setEmailContent(geContentFromTemplate(email.getModel(), RESET_PASSWORD_EMAIL_TEMPLATE));
            mimeMessageHelper.setText(email.getEmailContent(), true);

            emailSender.send(mimeMessageHelper.getMimeMessage());

        } catch (MessagingException e) {

            log.info("Exception by trying to send password reset email: {}", e.getStackTrace());
        }
    }

    public String geContentFromTemplate(Map<String, Object> model, String templatePath) {
        StringBuffer content = new StringBuffer();
        try {
            content.append(VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, templatePath, model));
        } catch (Exception e) {
            log.info("Exception by getting content from the email template: {}", e.getStackTrace());
        }
        return content.toString();
    }

}
