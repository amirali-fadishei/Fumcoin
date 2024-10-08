package ExchangeApp;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Exchange extends Menu implements Initializable {
    public static boolean buying = true;
    @FXML
    public Button storebtn;
    @FXML
    public TextField tokennum;
    @FXML
    public TextField tokenprice;
    @FXML
    public Text finalprice;
    @FXML
    public ToggleGroup buyToggle;
    @FXML
    private ComboBox<String> tokencombo;
    @FXML
    private TableView<Object[]> exchangeTable;

    private ObservableList<Object[]> exchangeData;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
        finalprice.setText("0.0");
        tokencombo.setItems(FXCollections.observableArrayList("Ethereum", "Doge coin", "Bitcoin", "Tether"));
        tokencombo.getSelectionModel().select("Ethereum");
        if (!buying) {
            storebtn.setDisable(true);
        }
        initializeTableColumns();

        exchangeData = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
        exchangeTable.setItems(exchangeData);

        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(() -> {
            Platform.runLater(this::updateExchangeData);
        }, 0, 10, TimeUnit.SECONDS);
    }

    @Override
    public void stop() {

    }

    @FXML
    private void showPrice() {
        try {
            double numTokens = Double.parseDouble(tokennum.getText());
            double pricePerToken = Double.parseDouble(tokenprice.getText());
            double totalPrice = numTokens * pricePerToken;
            finalprice.setText(String.valueOf(totalPrice));
        } catch (NumberFormatException e) {
            Database.showAlert(Alert.AlertType.ERROR, "خطا", "لطفاً مقادیر معتبر وارد کنید.");
        }
    }

    @FXML
    private void createBills() {
        if (!validateInputs()) {
            Database.showAlert(Alert.AlertType.ERROR, "خطا", "لطفاً تمام فیلدها را به درستی پر کنید.");
            return;
        }
        ToggleButton selected = (ToggleButton) buyToggle.getSelectedToggle();
        String type = selected.getText();
        String token = tokencombo.getValue();
        double numTokens = Double.parseDouble(tokennum.getText());
        double totalPrice = Double.parseDouble(finalprice.getText());

        Database.saveBills("pending", type, User.getUser().getUserShow(), token, numTokens, totalPrice);
        double money = Database.checkBills(type, User.getUser().getUserShow(), token, numTokens, totalPrice);
        if (money != 0.0) {
            if (type.equals("خرید")) {
                User.getUser().updateProfit(User.getUser().getpD() - money);
                User.getUser().setpD(User.getUser().getpD() - money);
                switch (token) {
                    case "Ethereum":
                        User.getUser().setEth(User.getUser().getEth() + numTokens);
                        break;
                    case "Doge coin":
                        User.getUser().setDog(User.getUser().getDog() + numTokens);
                        break;
                    case "Bitcoin":
                        User.getUser().setBit(User.getUser().getBit() + numTokens);
                        break;
                    case "Tether":
                        User.getUser().setTet(User.getUser().getTet() + numTokens);
                        break;
                }
            } else {
                User.getUser().updateProfit(User.getUser().getpD() + money);
                User.getUser().setpD(User.getUser().getpD() + money);
                switch (token) {
                    case "Ethereum":
                        User.getUser().setEth(User.getUser().getEth() - numTokens);
                        break;
                    case "Doge coin":
                        User.getUser().setDog(User.getUser().getDog() - numTokens);
                        break;
                    case "Bitcoin":
                        User.getUser().setBit(User.getUser().getBit() - numTokens);
                        break;
                    case "Tether":
                        User.getUser().setTet(User.getUser().getTet() - numTokens);
                        break;
                }
            }
        }
    }

    @FXML
    private void tokenBills() {
        List<Object[]> billRecords = Database.showBills(tokencombo.getValue());
        exchangeData.clear();
        exchangeData.addAll(billRecords);
    }

    private void initializeTableColumns() {
        TableColumn<Object[], String> typeColumn = new TableColumn<>("خرید/فروش");
        TableColumn<Object[], Double> amountColumn = new TableColumn<>("تعداد");
        TableColumn<Object[], Double> valueColumn = new TableColumn<>("قیمت نهایی");

        typeColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>((String) cellData.getValue()[0]));
        amountColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>((Double) cellData.getValue()[1]));
        valueColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>((Double) cellData.getValue()[2]));

        exchangeTable.getColumns().addAll(typeColumn, amountColumn, valueColumn);
    }

    private boolean validateInputs() {
        return tokencombo.getValue() != null &&
                !tokennum.getText().isEmpty() &&
                !tokenprice.getText().isEmpty() &&
                isNumeric(tokennum.getText()) &&
                isNumeric(tokenprice.getText());
    }

    private boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void updateExchangeData() {
        List<Object[]> billRecords = Database.showBills(tokencombo.getValue());
        exchangeData.clear();
        exchangeData.addAll(billRecords);
    }
}
