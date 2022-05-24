package com.boot.user.service;

import com.boot.services.model.Email;
import com.boot.user.config.AppConfig;
import com.boot.user.model.ConfirmationToken;
import com.boot.user.model.PasswordResetToken;
import com.boot.user.model.User;
import com.boot.user.util.Constants;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.app.VelocityEngine;
import org.jetbrains.annotations.NotNull;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class EmailService {

    private static final String CONFIRMATION_EMAIL_TEMPLATE = "/templates/email-template.vm";

    private static final String RESET_PASSWORD_EMAIL_TEMPLATE = "/templates/reset-password-email-template.vm";

    AppConfig appConfig;

    JavaMailSender emailSender;

    VelocityEngine velocityEngine;

    public void sendConfirmationEmail(@NotNull User user, @NotNull ConfirmationToken confirmationToken) {

        Email email = new Email();

        Map<String, Object> model = new HashMap<>();
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
            email.setEmailContent(geContentFromTemplate(email.getModel(), CONFIRMATION_EMAIL_TEMPLATE));
            mimeMessageHelper.setText(email.getEmailContent(), true);

            emailSender.send(mimeMessageHelper.getMimeMessage());
        } catch (MessagingException e) {
            log.info("Exception by sending confirmation email: {}", (Object) e.getStackTrace());
        }
    }

    public void sendPasswordResetEmail(@NotNull User user, @NotNull PasswordResetToken passwordResetToken) {

        Email email = new Email();

        Map<String, Object> model = new HashMap<>();
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

            log.info("Exception by trying to send password reset email: {}", (Object) e.getStackTrace());
        }
    }

    public String geContentFromTemplate(Map<String, Object> model, String templatePath) {
        StringBuilder content = new StringBuilder();
        try {
            content.append(VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, templatePath, model));
        } catch (Exception e) {
            log.info("Exception by getting content from the email template: {}", (Object) e.getStackTrace());
        }
        return content.toString();
    }

}
