package com.boot.user.service;

import com.boot.user.model.ConfirmationToken;
import com.boot.user.model.Email;
import com.boot.user.model.PasswordResetToken;
import com.boot.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private static final String CONFIRMATION_EMAIL_TEMPLATE = "/templates/email-template.vm";

    private static final String RESET_PASSWORD_EMAIL_TEMPLATE = "/templates/reset-password-email-template.vm";

    @Value("${user.service.url}")
    private String userServiceUrl;

    private final JavaMailSender emailSender;

    private final VelocityEngine velocityEngine;

    public void sendConfirmationEmail(@NotNull User user, @NotNull ConfirmationToken confirmationToken) {
        Email email = new Email();

        Map<String, Object> model = new HashMap<>();
        model.put("firstName", user.getFirstName());
        model.put("lastName", user.getLastName());
        model.put("path", "http://localhost:3000/account-confirmation/");
        model.put("confirmationToken", confirmationToken.getToken());
        model.put("signature", "www.fractalStories.com");
        email.setModel(model);

        MimeMessage mimeMessage = emailSender.createMimeMessage();

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

            mimeMessageHelper.setSubject("Fractal Wood Stories confirmare email");
            mimeMessageHelper.setFrom("noreply@springwebstore.com");
            mimeMessageHelper.setTo(user.getEmail());
            email.setEmailContent(geContentFromTemplate(email.getModel(), CONFIRMATION_EMAIL_TEMPLATE));
            mimeMessageHelper.setText(email.getEmailContent(), true);

            emailSender.send(mimeMessageHelper.getMimeMessage());
        } catch (MessagingException e) {
            log.error("Exception by sending confirmation email", e);
        }
    }

    public void sendPasswordResetEmail(@NotNull User user, @NotNull PasswordResetToken passwordResetToken) {

        Email email = new Email();

        Map<String, Object> model = new HashMap<>();
        model.put("firstName", user.getFirstName());
        model.put("lastName", user.getLastName());
        model.put("path", "http://localhost:3000/new-password/");
        model.put("passwordResetToken", passwordResetToken.getResetToken());
        model.put("location", "Cluj");
        model.put("signature", "www.fractalStories.com");
        email.setModel(model);

        MimeMessage mimeMessage = emailSender.createMimeMessage();

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

            mimeMessageHelper.setSubject("Fractal Wood Stories reseteaza parola");
            mimeMessageHelper.setFrom("noreply@springwebstore.com");
            mimeMessageHelper.setTo(user.getEmail());
            email.setEmailContent(geContentFromTemplate(email.getModel(), RESET_PASSWORD_EMAIL_TEMPLATE));
            mimeMessageHelper.setText(email.getEmailContent(), true);

            emailSender.send(mimeMessageHelper.getMimeMessage());

        } catch (MessagingException e) {
            log.error("Exception by trying to send password reset email:", e);
        }
    }

    public String geContentFromTemplate(Map<String, Object> model, String templatePath) {
        StringBuilder content = new StringBuilder();
        try {
            content.append(VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, templatePath, model));
        } catch (Exception e) {
            log.error("Exception by getting content from the email template", e);
        }
        return content.toString();
    }

}
