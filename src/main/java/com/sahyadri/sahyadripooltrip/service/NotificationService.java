package com.sahyadri.sahyadripooltrip.service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import com.sahyadri.sahyadripooltrip.entity.ContactMessage;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final JavaMailSender mailSender;
    private final String mailFrom;
    private final String twilioSid;
    private final String twilioToken;
    private final String twilioFrom;
    private final boolean otpDevMode;

    public NotificationService(
            JavaMailSender mailSender,
            @Value("${app.mail.from:}") String mailFrom,
            @Value("${app.twilio.account-sid:}") String twilioSid,
            @Value("${app.twilio.auth-token:}") String twilioToken,
            @Value("${app.twilio.from:}") String twilioFrom,
            @Value("${app.otp.dev-mode:true}") boolean otpDevMode) {

        this.mailSender = mailSender;
        this.mailFrom = mailFrom;
        this.twilioSid = twilioSid;
        this.twilioToken = twilioToken;
        this.twilioFrom = twilioFrom;
        this.otpDevMode = otpDevMode;
    }

    public void sendEmailOtp(String to, String otp) {

        if (otpDevMode) {
            System.out.println();
            System.out.println("=================================================");
            System.out.println("        SAHYADRI POOL & TRIP - DEV OTP");
            System.out.println("=================================================");
            System.out.println("Channel : EMAIL");
            System.out.println("To      : " + to);
            System.out.println("OTP     : " + otp);
            System.out.println("Valid   : 10 minutes");
            System.out.println("=================================================");
            System.out.println();

            return;
        }

        if (mailFrom.isBlank()) {
            throw new IllegalStateException(
                    "Email OTP is not configured. Set MAIL_FROM and SMTP settings.");
        }

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(mailFrom);
        message.setTo(to);
        message.setSubject(
                "Sahyadri Pool & Trip - Password Reset OTP");

        message.setText(
                "Your password reset OTP is: " + otp
                        + "\n\nThis OTP expires in 10 minutes."
                        + "\nIf you did not request this, ignore this email.");

        mailSender.send(message);
    }

    public void sendSmsOtp(String phone, String otp) {

        if (otpDevMode) {
            System.out.println();
            System.out.println("=================================================");
            System.out.println("        SAHYADRI POOL & TRIP - DEV OTP");
            System.out.println("=================================================");
            System.out.println("Channel : SMS");
            System.out.println("To      : " + phone);
            System.out.println("OTP     : " + otp);
            System.out.println("Valid   : 10 minutes");
            System.out.println("=================================================");
            System.out.println();

            return;
        }

        if (twilioSid.isBlank()
                || twilioToken.isBlank()
                || twilioFrom.isBlank()) {

            throw new IllegalStateException(
                    "SMS OTP is not configured. Set TWILIO_ACCOUNT_SID, TWILIO_AUTH_TOKEN and TWILIO_FROM.");
        }

        try {

            String to = phone.startsWith("+")
                    ? phone
                    : "+91" + phone;

            String form = "To=" + enc(to)
                    + "&From=" + enc(twilioFrom)
                    + "&Body=" + enc(
                            "Sahyadri Pool & Trip password reset OTP: "
                                    + otp
                                    + ". Valid for 10 minutes.");

            String auth = Base64.getEncoder().encodeToString(
                    (twilioSid + ":" + twilioToken)
                            .getBytes(StandardCharsets.UTF_8));

            HttpRequest request = HttpRequest.newBuilder(
                    URI.create(
                            "https://api.twilio.com/2010-04-01/Accounts/"
                                    + twilioSid
                                    + "/Messages.json"))
                    .header("Authorization", "Basic " + auth)
                    .header(
                            "Content-Type",
                            "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(form))
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(
                            request,
                            HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200
                    || response.statusCode() >= 300) {

                throw new IllegalStateException(
                        "SMS provider rejected the OTP request");
            }

        } catch (Exception e) {

            if (e instanceof IllegalStateException) {
                throw (IllegalStateException) e;
            }

            throw new IllegalStateException(
                    "Unable to send SMS OTP", e);
        }
    }

    public void sendContactMessage(String to, ContactMessage contact) {
        if (mailFrom.isBlank()) {
            throw new IllegalStateException("Email is not configured. Set MAIL_FROM and SMTP settings.");
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailFrom);
        message.setTo(to);
        message.setReplyTo(contact.getEmail());
        message.setSubject("[Sahyadri Contact] " + contact.getSubject());
        message.setText(
            "New website contact message\n\n" +
            "Name: " + contact.getName() + "\n" +
            "Email: " + contact.getEmail() + "\n" +
            "Phone: " + (contact.getPhone() == null ? "" : contact.getPhone()) + "\n" +
            "Subject: " + contact.getSubject() + "\n\n" +
            contact.getMessage()
        );
        mailSender.send(message);
    }

    private static String enc(String value) {
        return URLEncoder.encode(
                value,
                StandardCharsets.UTF_8);
    }
}