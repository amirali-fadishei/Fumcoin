package ExchangeApp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import net.synedra.validatorfx.Validator;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * This class handles the sending of recovery emails and the verification of recovery codes.
 */
public class SendEmail {

    private final Validator validator = new Validator();
    private final Random rand = new Random();
    @FXML
    private TextField emailTextField;
    @FXML
    private Button sendButton;
    @FXML
    private Button recoverButton;
    @FXML
    private TextField recoveryCodeTextField;
    private String recoveryCode;

    /**
     * Validates the email and sends a recovery email if the email is valid.
     */
    public void signUpApp() {
        validator.createCheck().withMethod(c -> {
            if (!Pattern.matches("[^@ \\t\\r\\n]+@[^@ \\t\\r\\n]+\\.[^@ \\t\\r\\n]+", c.get("email"))) {
                c.error("Please enter a valid email!");
            }
        }).dependsOn("email", emailTextField.textProperty()).decorates(emailTextField).immediate();

        if (validator.validate()) {
            sendRecoveryEmail();
        }
    }

    /**
     * Generates a recovery code and sends it to the provided email address.
     */
    private void sendRecoveryEmail() {
        recoveryCode = String.valueOf(rand.nextInt(9000) + 1000);

        Properties properties = new Properties();

        try {
            properties.load(new FileInputStream("config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        String host = properties.getProperty("smtp.host");
        String port = properties.getProperty("smtp.port");
        String auth = properties.getProperty("smtp.auth");
        String starttls = properties.getProperty("smtp.starttls.enable");
        final String user = properties.getProperty("email.user");
        final String password = properties.getProperty("email.password");

        Properties smtpProperties = new Properties();
        smtpProperties.put("mail.smtp.host", host);
        smtpProperties.put("mail.smtp.auth", auth);
        smtpProperties.put("mail.smtp.starttls.enable", starttls);
        smtpProperties.put("mail.smtp.port", port);

        Session session = Session.getDefaultInstance(smtpProperties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(user));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(emailTextField.getText()));
            message.setSubject("Recovery Email");
            message.setText("با سلام و عرض ادب کد بازیابی حساب کاربری شما: " + recoveryCode);
            message.addHeader("FumCoin", "Recovery Email");

            Transport.send(message);

            Database.showAlert(Alert.AlertType.INFORMATION, "اطلاعات ایمیل", "ایمیل بازیابی ارسال شد!");
            toggleEmailRecoveryFields(false);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Validates the recovery code and attempts to log in the user if the code is correct.
     *
     * @param event the event that triggered the recovery attempt
     * @throws IOException if an I/O error occurs
     */
    public void recoverEmail(ActionEvent event) throws IOException {
        validator.createCheck().withMethod(c -> {
            if (!c.get("recovery").equals(recoveryCode)) {
                c.error("Invalid recovery code!");
            }
        }).dependsOn("recovery", recoveryCodeTextField.textProperty()).decorates(recoveryCodeTextField).immediate();

        if (validator.validate()) {
            Database.emailLogin(event, emailTextField.getText());
        }
    }

    /**
     * Toggles the enable state of the email and recovery fields.
     *
     * @param enable if true, enables the email fields and disables the recovery fields; if false, the opposite
     */
    private void toggleEmailRecoveryFields(boolean enable) {
        emailTextField.setDisable(!enable);
        sendButton.setDisable(!enable);
        recoverButton.setDisable(enable);
        recoveryCodeTextField.setDisable(enable);
    }
}
