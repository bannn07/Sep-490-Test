package io.fptu.sep490.utils;

import io.fptu.sep490.constant.EmailType;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Component
public record EmailUtils(JavaMailSender mailSender, TemplateEngine templateEngine) {

    public void sendEmail(String to, EmailType emailType, Map<String, Object> variables) throws MessagingException {
        Context context = new Context();
        context.setVariables(variables);

        String htmlContent = templateEngine.process(emailType.getTemplateName(), context);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(emailType.getSubject());
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }
}
