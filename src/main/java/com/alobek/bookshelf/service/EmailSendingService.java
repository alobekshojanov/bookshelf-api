package com.alobek.bookshelf.service;

import com.alobek.bookshelf.enums.AppLanguage;
import com.alobek.bookshelf.enums.SmsType;
import com.alobek.bookshelf.exps.AppBadException;
import com.alobek.bookshelf.util.JwtUtil;
import com.alobek.bookshelf.util.RandomUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Service
public class EmailSendingService {

    private Integer emailLimit = 3;

    @Value("${spring.mail.username}")
    private String fromAccount;

    @Value("${server.domain}")
    private String serverDomain;
    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private EmailHistoryService emailHistoryService;

    public void sendRegistrationEmail(String email, Integer profileId, AppLanguage lang) {
        String subject = "Registration Confirmation";
        String body = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Title</title>\n" +
                "    <style>\n" +
                "        a{\n" +
                "            padding: 10px 30px;\n" +
                "            display: inline-block;\n" +
                "        }\n" +
                "        .button-link {\n" +
                "            text-decoration: none;\n" +
                "            color: white;\n" +
                "            background-color: indianred;\n" +
                "        }\n" +
                "        .namuna4: hover {\n" +
                "            background-color: #dd4444;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "<h1> Welcome to BookShelf – Confirm Your Email </h1>\n" +
                "<p> Hello \uD83D\uDC4B, </p>\n" +
                "<p> Welcome to BookShelf! </p>\n" +
                "<p>Thank you for signing up. To activate your account and complete your registration, </p>\n" +
                "<p>  please confirm your email address by clicking the link below: </p>\n" +
                "<p> \uD83D\uDC49 Confirm your email </p>\n" +
                "<a class = \"button-link\"\n" +
                "   href=\"%s/auth/registration/verification/%s?lang=%s\" target=\"_blank\" >Click here</a>\n" +
                "<p> Once confirmed, your account will be activated and ready to use. </p>\n" +
                "<p> If you didn’t create this account, you can safely ignore this email. </p>\n" +
                "<p>   Best regards,</p>\n" +
                "<p>   BookShelf Team</p>\n" +
                "</body>\n" +
                "</html>";
        body = String.format(body, serverDomain, JwtUtil.encode1(profileId), lang.name());
        System.out.println(JwtUtil.encode1(profileId));

        /*String body = "Welcome to Bookshelf Registration Service.
         Please click the link to confirm your registration: http://localhost:8080/auth/registration/verification/" + profileId;*/
        sendMimeEmail(email, subject, body);

    }

    public void sendResetPasswordEmail(String email){
        String subject = "Reset Password Confirmation";
        String code = RandomUtil.getRandomSmsCode();
        String body = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Title</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "<h1> Welcome to BookShelf – Confirm Your Email </h1>\n" +
                "<p> Hello \uD83D\uDC4B, </p>\n" +
                "<p> We received a request to reset the password for your BookShelf account. </p>\n" +
                "<p> To continue, please enter the code below in the password reset screen: </p>\n" +
                "<p>  \uD83D\uDD10 Reset code: %s</p>\n" +
                "<p> This code is valid for 2 minutes.</p>\n" +
                "\n" +
                "<p> If you didn’t request a password reset, you can safely ignore </p>\n" +
                "<p>  this email — your account remains secure. </p>\n" +
                "<p> Need assistance? Just reply to this email and we’ll help you \uD83D\uDE42 </p>\n" +
                "<p>   Warm regards,</p>\n" +
                "<p>   BookShelf Team</p>\n" +
                "</body>\n" +
                "</html>";

        body = String .format(body, code);
        checkAndSendMimeEmail(email, subject, body, code);
    }

    public void checkAndSendMimeEmail(String email, String subject, String body, String code){
        // checking
        Long count = emailHistoryService.getEmailCount(email);
        if (count >= emailLimit) {
            System.out.println("Email Limit Exceeded: " + email);
            throw new AppBadException("Email Limit Exceeded");
        }
        // send
        sendMimeEmail(email, subject, body);
        // create
        emailHistoryService.create(email, code, SmsType.CONFIRM_RESET_PASSWORD);

    }

    public void sendMimeEmail(String email, String subject, String body) {
        MimeMessage msg = javaMailSender.createMimeMessage();
        try {

            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
            helper.setFrom(fromAccount);
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(body, true); // html enabled

            javaMailSender.send(msg);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendSimpleEmail(String email, String subject, String body) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(fromAccount);
        msg.setTo(email);
        msg.setSubject(subject);
        msg.setText(body);
        javaMailSender.send(msg);
    }

    public void sendChangeUsernameEmail(String email){
        String subject = "Change Username";
        //generate special code to confirm ...
        String code = RandomUtil.getRandomSmsCode();
        String body =  "\"<!DOCTYPE html>\\n\" +\n" +
                "                \"<html lang=\\\"en\\\">\\n\" +\n" +
                "                \"<head>\\n\" +\n" +
                "                \"    <meta charset=\\\"UTF-8\\\">\\n\" +\n" +
                "                \"    <title>Title</title>\\n\" +\n" +
                "                \"</head>\\n\" +\n" +
                "                \"<body>\\n\" +\n" +
                "                \"<h1> Welcome to BookShelf – Confirm Your Email </h1>\\n\" +\n" +
                "                \"<p> Hello \\uD83D\\uDC4B, </p>\\n\" +\n" +
                "                \"<p> We received a request to change the username on your BookShelf account. </p>\\n\" +\n" +
                "                \"<p> To confirm this change, please enter the confirmation code below in the application: </p>\\n\" +\n" +
                "                \"<p>  \\uD83D\\uDD10 Confirmation code: %s</p>\\n\" +\n" +
                "                \"<p> This code is valid for 2 minutes.</p>\\n\" +\n" +
                "                \"\\n\" +\n" +
                "                \"<p> If you did not request a username change, please ignore this email — no changes will be </p>\\n\" +\n" +
                "                \"<p>  made to your account. </p>\\n\" +\n" +
                "                \"<p> If you need help, feel free to contact our support team. </p>\\n\" +\n" +
                "                \"<p>   Best regards,</p>\\n\" +\n" +
                "                \"<p>   BookShelf Team</p>\\n\" +\n" +
                "                \"</body>\\n\" +\n" +
                "                \"</html>\";\n";

        body = String.format(body,code);
        checkAndSendMimeEmail(email, subject, body, code);
    }

   /* private void checkAndSendMimeEmail(String email, String subject, String body, String code ) {
        // check ..
        Long count = emailHistoryService.getEmailCount(email);
        if (count >= emailLimit) {
            System.out.println("Email Limit Exceeded: " + email);
            throw new AppBadException("Email Limit Exceeded");
        }
        // send ...
        sendMimeEmail(email, subject, body);

        // create ...
        emailHistoryService.create(email, code, SmsType.RESET_PASSWORD);
    }*/

}
