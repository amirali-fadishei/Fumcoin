package ExchangeApp;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

import static javafx.collections.FXCollections.observableArrayList;

public class Swap extends Menu implements Initializable {
    @FXML
    private ComboBox<String> firstToken;
    @FXML
    private ComboBox<String> secondToken;
    @FXML
    private TextField firstinput;
    @FXML
    private TextField secondinput;

    private String firstcoin = "Ethereum";
    private String secondcoin = "Ethereum";
    private Double firstprice = 0.0;
    private Double secondprice = 0.0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
        firstToken.setItems(observableArrayList("Ethereum", "Doge coin", "Bitcoin", "Tether"));
        secondToken.setItems(observableArrayList("Ethereum", "Doge coin", "Bitcoin", "Tether"));

        updatePrices();

        firstToken.setOnAction(event -> {
            firstcoin = firstToken.getValue();
            updatePrices();
        });

        secondToken.setOnAction(event -> {
            secondcoin = secondToken.getValue();
            updatePrices();
        });
    }

    @Override
    public void stop() {

    }

    private void updatePrices() {
        firstinput.setPromptText(String.valueOf(firstprice));
        secondinput.setPromptText(String.valueOf(secondprice));
    }

    public void convert() {
        if (!validateInputs()) {
            Database.showAlert(Alert.AlertType.ERROR, "خطا", "لطفا فیلدها را به درستی پر نمایید!");
            return;
        }

        try {
            double firstAmount = Double.parseDouble(firstinput.getText());
            double secondAmount = (firstAmount * firstprice) / secondprice;
            secondinput.setText(String.valueOf(secondAmount));

            showConfirmationAlert(firstAmount, secondAmount);
        } catch (NumberFormatException e) {
            Database.showAlert(Alert.AlertType.ERROR, "خطا", "لطفاً مقادیر معتبر وارد کنید.");
        }
    }

    private boolean validateInputs() {
        return firstToken.getValue() != null && secondToken.getValue() != null && !firstinput.getText().isEmpty() && isNumeric(firstinput.getText());
    }

    private boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void showConfirmationAlert(double firstAmount, double secondAmount) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("تبادل ارز");
        alert.setHeaderText("تایید عملیات");
        alert.setContentText("کارمزد عملیات شما 10 دلار می باشد! آیا مایل به ادامه هستید؟");

        alert.showAndWait().ifPresent(buttonType -> {
            if (buttonType == ButtonType.OK) {
                Platform.runLater(() -> {
                    if (User.getUser().getpD() >= 10) {
                        performSwap(firstAmount, secondAmount);
                    } else {
                        Database.showAlert(Alert.AlertType.ERROR, "خطا", "موجودی حساب کافی نیست!");
                    }
                });
            }
        });
    }

    private void performSwap(double firstAmount, double secondAmount) {
        if (hasSufficientBalance(firstcoin, firstAmount)) {
            deductFirstCoinBalance(firstAmount);
            addSecondCoinBalance(secondAmount);
            User.getUser().updateProfit(User.getUser().getpD() - 10);
            User.getUser().setpD(User.getUser().getpD() - 10);
            System.out.println(User.getUser().getpD());
            Database.update("admin2024", "profit", 10);
            Database.showAlert(Alert.AlertType.INFORMATION, "تایید", "عملیات موفقیت انجام شد!");
        } else {
            Database.showAlert(Alert.AlertType.ERROR, "خطا", "موجودی ارز کافی نیست!");
        }
    }

    private boolean hasSufficientBalance(String coin, double amount) {
        return switch (coin) {
            case "Ethereum" -> User.getUser().getEth() >= amount;
            case "Doge coin" -> User.getUser().getDog() >= amount;
            case "Bitcoin" -> User.getUser().getBit() >= amount;
            case "Tether" -> User.getUser().getTet() >= amount;
            default -> false;
        };
    }

    private void deductFirstCoinBalance(double amount) {
        switch (firstcoin) {
            case "Ethereum":
                User.getUser().setEth(User.getUser().getEth() - amount);
                break;
            case "Doge coin":
                User.getUser().setDog(User.getUser().getDog() - amount);
                break;
            case "Bitcoin":
                User.getUser().setBit(User.getUser().getBit() - amount);
                break;
            case "Tether":
                User.getUser().setTet(User.getUser().getBit() - amount);
                break;
        }
        Database.update(User.getUser().getUserShow(), firstcoin.toLowerCase(), -amount);
    }

    private void addSecondCoinBalance(double amount) {
        switch (secondcoin) {
            case "Ethereum":
                User.getUser().setEth(User.getUser().getEth() + amount);
                break;
            case "Doge coin":
                User.getUser().setDog(User.getUser().getDog() + amount);
                break;
            case "Bitcoin":
                User.getUser().setBit(User.getUser().getBit() + amount);
                break;
            case "Tether":
                User.getUser().setTet(User.getUser().getTet() + amount);
                break;
        }
        Database.update(User.getUser().getUserShow(), secondcoin.toLowerCase(), amount);
    }
}
