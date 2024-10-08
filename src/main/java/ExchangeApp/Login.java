package ExchangeApp;

import com.mewebstudio.captcha.Captcha;
import com.mewebstudio.captcha.Config;
import com.mewebstudio.captcha.GeneratedCaptcha;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import net.synedra.validatorfx.Validator;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

/**
 * Controller class for the login functionality of the ExchangeApp.
 */
public class Login extends Thread implements Initializable {


    @FXML
    private TextField loginName;
    @FXML
    private PasswordField loginPass;
    @FXML
    private TextField captchaCode;
    @FXML
    private ImageView loginCap;

    private final Validator validator = new Validator();
    private final Config customConfig = new Config();
    private final Captcha captcha = new Captcha(customConfig);
    private String captchaCodeText;


    /**
     * Generates a new captcha image and sets it in the ImageView.
     */
    @FXML
    public void generateCaptcha() {
        customConfig.setWidth(100);
        customConfig.setHeight(40);
        customConfig.setDark(true);
        customConfig.setLength(5);
        customConfig.setNoise(0);
        GeneratedCaptcha generatedCaptcha = captcha.generate();
        BufferedImage captchaImage = generatedCaptcha.getImage();
        captchaCodeText = generatedCaptcha.getCode();
        Image captchaImg = SwingFXUtils.toFXImage(captchaImage, null);
        loginCap.setImage(captchaImg);
    }

    /**
     * Handles the login action by validating the input fields and attempting to log in the user.
     *
     * @param event the ActionEvent triggered by the login button click
     * @throws IOException if there is an error during login
     */
    @FXML
    public void loginApp(ActionEvent event) throws IOException {
        validator.createCheck().withMethod(c -> {
            String username = c.get("username");
            if (username == null || !Pattern.matches("^[a-z0-9_-]{3,15}$", username)) {
                c.error("Please enter a valid username!");
            }
        }).dependsOn("username", loginName.textProperty()).decorates(loginName).immediate();
        validator.createCheck().withMethod(c -> {
            String password = c.get("password");
            if (password == null || !Pattern.matches("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$ %^&*-]).{8,20}$", password)) {
                c.error("Please enter a valid password!");
            }
        }).dependsOn("password", loginPass.textProperty()).decorates(loginPass).immediate();
        validator.createCheck().withMethod(c -> {
            String captchaInput = c.get("captcha");
            if (!captchaInput.equals(captchaCodeText)) {
                c.error("Please enter the correct captcha!");
            }
        }).dependsOn("captcha", captchaCode.textProperty()).decorates(captchaCode).immediate();

        if (validator.validate()) {
            Database.loginDB(event, loginName.getText(), loginPass.getText());
        }
    }

    /**
     * Navigates to the sign-up page.
     *
     * @param event the MouseEvent triggered by clicking the sign-up link
     */
    @FXML
    public void signUpPage(MouseEvent event) {
        Main.stageChanger(event, "SignUp.fxml");
    }

    /**
     * Navigates to the email recovery page.
     *
     * @param event the MouseEvent triggered by clicking the email recovery link
     */
    @FXML
    public void emailPage(MouseEvent event) {
        Main.stageChanger(event, "SendEmail.fxml");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        generateCaptcha();
    }
}
