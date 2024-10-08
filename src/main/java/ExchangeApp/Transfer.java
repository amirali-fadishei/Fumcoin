package ExchangeApp;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

import static javafx.collections.FXCollections.observableArrayList;

public class Transfer extends Menu implements Initializable {
    @FXML
    private ComboBox<String> tokens;
    @FXML
    private TextField destinationId;
    @FXML
    private TextField tokenValue;
    @FXML
    private TextField Comment;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
        tokens.setItems(observableArrayList("Ethereum", "Doge coin", "Bitcoin", "Tether", "$"));
    }

    @Override
    public void stop() {

    }

    public void transferToken() {
        if (!validateInputs()) {
            Platform.runLater(() -> Database.showAlert(Alert.AlertType.ERROR, "خطا", "لطفا فیلدها را به درستی پر نمایید!"));
            return;
        }

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("انتقال ارز");
        confirmationAlert.setHeaderText("تایید عملیات");
        confirmationAlert.setContentText("آیا از انجام عملیات اطمینان دارید؟");

        confirmationAlert.showAndWait().ifPresent(buttonType -> {
            if (User.getUser().getpD() >= 10 || tokens.getValue().equals("$")) {
                performTransfer();
            } else {
                Platform.runLater(() -> Database.showAlert(Alert.AlertType.ERROR, "خطا", "موجودی حساب کافی نیست!"));
            }
        });
    }

    private boolean validateInputs() {
        return tokens.getValue() != null && !destinationId.getText().isEmpty() && !tokenValue.getText().isEmpty() && isNumeric(tokenValue.getText());
    }

    private boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void performTransfer() {
        double tokenAmount = Double.parseDouble(tokenValue.getText());
        String selectedToken = tokens.getValue();

        if (hasSufficientBalance(selectedToken, tokenAmount)) {
            updateDatabase(selectedToken, tokenAmount);
            deductBalance(selectedToken, tokenAmount);
            Database.update("admin2024", "profit", 10);
            Platform.runLater(() -> Database.showAlert(Alert.AlertType.INFORMATION, "تایید", "عملیات با موفقیت انجام شد"));
        } else {
            Platform.runLater(() -> Database.showAlert(Alert.AlertType.ERROR, "خطا", "موجودی ارز کافی نیست!"));
        }
    }

    private boolean hasSufficientBalance(String token, double amount) {
        return switch (token) {
            case "Ethereum" -> User.getUser().getEth() >= amount;
            case "Doge coin" -> User.getUser().getDog() >= amount;
            case "Bitcoin" -> User.getUser().getBit() >= amount;
            case "Tether" -> User.getUser().getTet() >= amount;
            case "$" -> true;
            default -> false;
        };
    }

    private void updateDatabase(String token, double amount) {
        if (token.equals("$")) {
            Database.update(destinationId.getText(), "profit", amount - 10.0);
        } else {
            Database.update(destinationId.getText(), token, amount);
        }
    }

    private void deductBalance(String token, double amount) {
        switch (token) {
            case "Ethereum":
                User.getUser().setEth(User.getUser().getEth() - amount);
                User.getUser().updateProfit(User.getUser().getpD() - 10);
                User.getUser().setpD(User.getUser().getpD() - 10);
                break;
            case "Doge coin":
                User.getUser().setDog(User.getUser().getDog() - amount);
                User.getUser().updateProfit(User.getUser().getpD() - 10);
                User.getUser().setpD(User.getUser().getpD() - 10);
                break;
            case "Bitcoin":
                User.getUser().setBit(User.getUser().getBit() - amount);
                User.getUser().updateProfit(User.getUser().getpD() - 10);
                User.getUser().setpD(User.getUser().getpD() - 10);
                break;
            case "Tether":
                User.getUser().setTet(User.getUser().getTet() - amount);
                User.getUser().updateProfit(User.getUser().getpD() - 10);
                User.getUser().setpD(User.getUser().getpD() - 10);
                break;
            case "$":
                User.getUser().updateProfit(User.getUser().getpD() + amount - 10.0);
                User.getUser().setpD(User.getUser().getpD() + amount - 10.0);
                break;
        }
    }
}
