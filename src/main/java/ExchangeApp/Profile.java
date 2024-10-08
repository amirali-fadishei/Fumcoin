package ExchangeApp;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.synedra.validatorfx.Validator;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

/**
 * The Profile class represents a user profile interface allowing users to view and edit their information.
 * It implements Initializable for JavaFX initialization.
 */
public class Profile extends Menu implements Initializable {

    private final FileChooser fileChooser = new FileChooser();
    private final Validator validator = new Validator();
    private File selectedFile;
    @FXML
    private Button editBtn;
    @FXML
    private Button stopBtn;
    @FXML
    private TextField signFName;
    @FXML
    private TextField signLName;
    @FXML
    private TextField signPhone;
    @FXML
    private TextField signEmail;
    @FXML
    private PasswordField signPass;
    @FXML
    private ImageView profileImage;
    private boolean isEditing = false;
    private Stage stage;
    private String path = User.getUser().getUserImage();

    /**
     * Toggles the edit mode for the user information form.
     * Validates the inputs before updating the user information.
     */
    @FXML
    public void editInfo() {
        setupValidation();

        if (!isEditing) {
            enableEditing(true);
            editBtn.setText("ثبت اطلاعات کاربری");
        } else {
            if (validator.validate()) {
                Platform.runLater(() -> {
                    Database.updateInfo(signFName.getText(), signLName.getText(), signPass.getText(), signEmail.getText(), signPhone.getText(), path);
                    User.getUser().setUserFirstName(signFName.getText());
                    User.getUser().setUserLastName(signLName.getText());
                    User.getUser().setUserPassword(signPass.getText());
                    User.getUser().setUserEmail(signEmail.getText());
                    User.getUser().setUserPhone(signPhone.getText());
                    User.getUser().setUserImage(path);
                    enableEditing(false);
                    editBtn.setText("ویرایش اطلاعات کاربری");
                });

            }
        }

        isEditing = !isEditing;
    }

    /**
     * Sets up the validation rules for the user input fields.
     */
    private void setupValidation() {
        validator.createCheck().withMethod(c -> {
            if (!Pattern.matches("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$ %^&*-]).{8,20}$", c.get("password"))) {
                c.error("please enter valid password!");
            }
        }).dependsOn("password", signPass.textProperty()).decorates(signPass).immediate();

        validator.createCheck().withMethod(c -> {
            if (!Pattern.matches("^([\\u0600-\\u06FF\\s])+$", c.get("firstname"))) {
                c.error("please enter valid firstname!");
            }
        }).dependsOn("firstname", signFName.textProperty()).decorates(signFName).immediate();

        validator.createCheck().withMethod(c -> {
            if (!Pattern.matches("^([\\u0600-\\u06FF\\s])+$", c.get("lastname"))) {
                c.error("please enter valid lastname!");
            }
        }).dependsOn("lastname", signLName.textProperty()).decorates(signLName).immediate();

        validator.createCheck().withMethod(c -> {
            if (!Pattern.matches("[^@ \\t\\r\\n]+@[^@ \\t\\r\\n]+\\.[^@ \\t\\r\\n]+", c.get("email"))) {
                c.error("please enter valid email!");
            }
        }).dependsOn("email", signEmail.textProperty()).decorates(signEmail).immediate();

        validator.createCheck().withMethod(c -> {
            if (!Pattern.matches("^(?:(?:\\+?|00)(98)|(0))?((?:90|91|92|93|99)[0-9]{8})$", c.get("phone number"))) {
                c.error("please enter valid phone number!");
            }
        }).dependsOn("phone number", signPhone.textProperty()).decorates(signPhone).immediate();
    }

    /**
     * Enables or disables the editing of user information fields.
     *
     * @param enable true to enable editing, false to disable.
     */
    private void enableEditing(boolean enable) {
        signFName.setDisable(!enable);
        signLName.setDisable(!enable);
        signEmail.setDisable(!enable);
        signPass.setDisable(!enable);
        signPhone.setDisable(!enable);
        profileImage.setDisable(!enable);
    }

    /**
     * Toggles the state of the store's buying functionality.
     */
    public void stopStore() {
        Exchange.buying = !Exchange.buying;
    }

    /**
     * Opens a file chooser dialog to select a new profile image.
     * Updates the path of the selected image file.
     */
    public void changeProfile() {
        selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            path = selectedFile.getAbsolutePath();
        }
    }

    /**
     * Initializes the profile view with the user's current information.
     * Sets up a thread to update the profile image periodically.
     *
     * @param url            the location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resourceBundle the resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
        profileImage.setImage(new Image("file:" + User.getUser().getUserImage()));
        signFName.setText(User.getUser().getUserFirstName());
        signLName.setText(User.getUser().getUserLastName());
        signPass.setText(User.getUser().getUserPassword());
        signEmail.setText(User.getUser().getUserEmail());
        signPhone.setText(User.getUser().getUserPhone());

        if (User.getUser().getUserShow().equals("admin2024")) {
            stopBtn.setVisible(true);
        }

        double radius = Math.min(profileImage.getFitWidth(), profileImage.getFitHeight()) / 2;
        Circle clip = new Circle(radius, radius, radius);
        profileImage.setClip(clip);

        Thread dataUpdateThread = new Thread(() -> {
            while (true) {
                try {
                    Platform.runLater(this::updateUIImage);
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }
            }
        });
        dataUpdateThread.setDaemon(true);
        dataUpdateThread.start();
    }

    /**
     * Updates the profile image with the current user's image.
     */
    public void updateUIImage() {
        profileImage.setImage(new Image("file:" + User.getUser().getUserImage()));
    }

    @Override
    public void stop() {

    }
}
