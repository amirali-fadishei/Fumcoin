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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.synedra.validatorfx.Validator;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

/**
 * Controller class for the sign-up functionality of the ExchangeApp.
 */
public class SignUp implements Initializable {

    private final Validator validator = new Validator();
    private final Config customConfig = new Config();
    private final Captcha captcha = new Captcha(customConfig);
    private final FileChooser fileChooser = new FileChooser();
    private File selectedFile;

    @FXML
    private TextField signName;
    @FXML
    private TextField signFirstName;
    @FXML
    private TextField signLastName;
    @FXML
    private TextField signEmail;
    @FXML
    private TextField signPhone;
    @FXML
    private PasswordField signPassword;
    @FXML
    private PasswordField signRepeatPassword;
    @FXML
    private TextField captchaCodeField;
    @FXML
    private ImageView captchaImageView;

    private Stage stage;
    private String generatedCaptchaCode;
    private String selectedFilePath;

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
        generatedCaptchaCode = generatedCaptcha.getCode();
        Image captchaImg = SwingFXUtils.toFXImage(captchaImage, null);
        captchaImageView.setImage(captchaImg);
    }

    /**
     * Opens a file chooser dialog to select a file.
     */
    @FXML
    public void chooseFile() {
        selectedFile = fileChooser.showOpenDialog(stage);
        selectedFilePath = selectedFile.getAbsolutePath();
    }

    /**
     * Handles the sign-up action by validating the input fields and attempting to register the user.
     *
     * @param event the ActionEvent triggered by the sign-up button click
     * @throws IOException if there is an error during sign-up
     */
    @FXML
    public void signUpApp(ActionEvent event) throws IOException {
        setupValidators();
        if (validator.validate()) {
            Database.signUpDB(event, signFirstName.getText(), signLastName.getText(), signName.getText(), signPassword.getText(), signEmail.getText(), signPhone.getText(), selectedFilePath);
        }
    }

    /**
     * Navigates to the login page.
     *
     * @param event the MouseEvent triggered by clicking the login link
     */
    @FXML
    public void navigateToLoginPage(MouseEvent event) {
        Main.stageChanger(event, "Login.fxml");
    }

    /**
     * Sets up the validators for the sign-up form fields.
     */
    private void setupValidators() {
        validator.createCheck().withMethod(c -> {
            String password = c.get("password");
            String repeatPassword = c.get("repeat password");
            if (password == null || !Pattern.matches("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$ %^&*-]).{8,20}$", password) || !password.equals(repeatPassword)) {
                c.error("Please enter a valid password!");
            }
        }).dependsOn("password", signPassword.textProperty()).dependsOn("repeat password", signRepeatPassword.textProperty()).decorates(signPassword).decorates(signRepeatPassword).immediate();

        validator.createCheck().withMethod(c -> {
            String username = c.get("username");
            if (username == null || !Pattern.matches("^[a-z0-9_-]{3,15}$", username)) {
                c.error("Please enter a valid username!");
            }
        }).dependsOn("username", signName.textProperty()).decorates(signName).immediate();

        validator.createCheck().withMethod(c -> {
            String firstName = c.get("first name");
            if (firstName == null || !Pattern.matches("^([\\u0600-\\u06FF\\s])+$", firstName)) {
                c.error("Please enter a valid first name!");
            }
        }).dependsOn("first name", signFirstName.textProperty()).decorates(signFirstName).immediate();

        validator.createCheck().withMethod(c -> {
            String lastName = c.get("last name");
            if (lastName == null || !Pattern.matches("^([\\u0600-\\u06FF\\s])+$", lastName)) {
                c.error("Please enter a valid last name!");
            }
        }).dependsOn("last name", signLastName.textProperty()).decorates(signLastName).immediate();

        validator.createCheck().withMethod(c -> {
            String email = c.get("email");
            if (email == null || !Pattern.matches("[^@ \\t\\r\\n]+@[^@ \\t\\r\\n]+\\.[^@ \\t\\r\\n]+", email)) {
                c.error("Please enter a valid email!");
            }
        }).dependsOn("email", signEmail.textProperty()).decorates(signEmail).immediate();

        validator.createCheck().withMethod(c -> {
            String phoneNumber = c.get("phone number");
            if (phoneNumber == null || !Pattern.matches("^(?:(?:\\\\+?|00)(98)|(0))?((?:90|91|92|93|99)[0-9]{8})$", phoneNumber)) {
                c.error("Please enter a valid phone number!");
            }
        }).dependsOn("phone number", signPhone.textProperty()).decorates(signPhone).immediate();

        validator.createCheck().withMethod(c -> {
            String captchaInput = c.get("captcha");
            if (captchaInput == null || !captchaInput.equals(generatedCaptchaCode)) {
                c.error("Please enter the correct captcha!");
            }
        }).dependsOn("captcha", captchaCodeField.textProperty()).decorates(captchaCodeField).immediate();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        generateCaptcha();
    }
}
