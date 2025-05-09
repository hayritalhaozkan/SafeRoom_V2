package com.saferoom.email;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.*;
import java.util.Properties;

import com.saferoom.db.DBManager;
import com.saferoom.log.Logger;

public class EmailSender {

    private static final String CONFIG_FILE = "src/main/resources/emailconfig.properties";
    private static final String ICON_RESOURCE_NAME = "Verificate.png";
    private static String HOST;
    private static String PORT;
    private static String USERNAME;
    private static String PASSWORD;

    public static Logger LOGGER = Logger.getLogger(EmailSender.class);

    static {
        try {
            loadEmailConfig();
        } catch (Exception e) {
            try {
                LOGGER.error("Email Config loading failed: " + e.getMessage());
                throw new RuntimeException("Email Config File Unreadable", e);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    private static void loadEmailConfig() throws Exception {
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            Properties props = new Properties();
            props.load(fis);
            HOST = props.getProperty("smtp.host");
            PORT = props.getProperty("smtp.port");
            USERNAME = props.getProperty("smtp.user");
            PASSWORD = props.getProperty("smtp.password");
            LOGGER.info("Email config loaded successfully.");
        } catch (FileNotFoundException e) {
            LOGGER.error("Config file not found: " + e.getMessage());
            throw new IOException("Email Config file not found.", e);
        } catch (IOException e) {
            LOGGER.error("Failed to load email config: " + e.getMessage());
            throw new IOException("Failed to load email config.", e);
        }
    }

    // Geriye uyumlu yapı (4 parametreli)
    public static boolean sendEmail(String toEmail, String subject, String body, String attachmentPath) throws Exception {
        return sendEmail(toEmail, subject, body); // attachmentPath parametresi sadece görsel olduğu için artık kullanılmıyor
    }

    // Yeni 3 parametreli yapı – classpath üzerinden PNG ekliyor
    public static boolean sendEmail(String toEmail, String subject, String body) throws Exception {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", HOST);
        props.put("mail.smtp.port", PORT);

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(USERNAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);

            Multipart multipart = new MimeMultipart();

            // Gövde
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(body);
            multipart.addBodyPart(textPart);

            // PNG'yi classpath'ten oku
            InputStream imageStream = EmailSender.class.getClassLoader().getResourceAsStream(ICON_RESOURCE_NAME);
            if (imageStream != null) {
                File tempFile = File.createTempFile("verificate", ".png");
                try (OutputStream os = new FileOutputStream(tempFile)) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = imageStream.read(buffer)) != -1) {
                        os.write(buffer, 0, bytesRead);
                    }
                }

                MimeBodyPart filePart = new MimeBodyPart();
                filePart.attachFile(tempFile);
                multipart.addBodyPart(filePart);
            } else {
                LOGGER.warn("Verificate.png bulunamadı, ikon eklentisi yapılmadı.");
            }

            message.setContent(multipart);
            Transport.send(message);
            System.out.println("Email başarıyla gönderildi.");
            return true;

        } catch (MessagingException | IOException e) {
            LOGGER.error("Email gönderme hatası: " + e.getMessage());
            throw new RuntimeException("Email gönderme hatası: " + e.getMessage(), e);
        }
    }

    public static void notifyAccountLock(String username) throws Exception {
        String userEmail = DBManager.getEmailByUsername(username);
        String subject = "Urgent: Your SafeRoom Account has been Locked";
        String message = "Dear " + username + ",\n\n"
                + "Due to multiple incorrect verification attempts, your account has been temporarily locked.\n"
                + "If this was not you, please contact SafeRoom Security Team immediately.\n\n"
                + "Regards,\nSafeRoom Security Team";
        sendEmail(userEmail, subject, message);
    }
}
