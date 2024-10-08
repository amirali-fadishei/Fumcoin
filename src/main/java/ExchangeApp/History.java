package ExchangeApp;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.ResourceBundle;

/**
 * This class handles the history of transactions in the ExchangeApp.
 */
public class History extends Menu implements Initializable {

    @FXML
    private TableView<Object[]> historyTable;

    @FXML
    private Button printBtn;

    private List<Object[]> billRecords;

    /**
     * Initializes the controller class.
     * Sets up the table columns and loads the history data.
     *
     * @param url the location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resourceBundle the resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
        initializeTableColumns();
        loadHistoryData();

        Thread dataUpdateThread = new Thread(() -> {
            while (true) {
                try {
                    loadHistoryData();
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt(); // Ensure proper thread interruption handling
                }
            }
        });
        dataUpdateThread.setDaemon(true);
        dataUpdateThread.start(); // Start thread to update data periodically
    }

    @Override
    public void stop() {
        // Implement necessary stop functionality if required
    }

    /**
     * Initializes the columns of the history table.
     */
    private void initializeTableColumns() {
        TableColumn<Object[], String> statusColumn = createTableColumn("وضعیت", 0);
        TableColumn<Object[], String> typeColumn = createTableColumn("خرید/فروش", 1);
        TableColumn<Object[], String> tokenColumn = createTableColumn("ارز", 2);
        TableColumn<Object[], Double> amountColumn = createTableColumn("تعداد", 3);
        TableColumn<Object[], Double> valueColumn = createTableColumn("قیمت نهایی", 4);

        historyTable.getColumns().addAll(statusColumn, typeColumn, tokenColumn, amountColumn, valueColumn);
    }

    /**
     * Creates a new table column for the history table.
     *
     * @param title the title of the column
     * @param index the index of the data in the array to display in this column
     * @param <T> the type of the data in the column
     * @return the created table column
     */
    private <T> TableColumn<Object[], T> createTableColumn(String title, int index) {
        TableColumn<Object[], T> column = new TableColumn<>(title);
        column.setCellValueFactory(cellData -> new SimpleObjectProperty<>((T) cellData.getValue()[index]));
        return column;
    }

    /**
     * Prints the history data to a CSV file.
     *
     */
    public void print() {
        try (FileOutputStream fos = new FileOutputStream("bills.csv");
             OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
             PrintWriter printWriter = new PrintWriter(osw)) {

            // Adding BOM
            printWriter.write('\uFEFF');

            printWriter.println("status,type,token,amount,value");

            for (Object[] bill : billRecords) {
                printWriter.printf("%s,%s,%s,%f,%f%n",
                        bill[0], bill[1], bill[2], bill[3], bill[4]);
            }
        } catch (IOException e) {
            System.err.println("Error writing to CSV file: " + e.getMessage());
        }
    }

    /**
     * Loads the history data from the database and updates the table view.
     */
    private void loadHistoryData() {
        try {
            List<Object[]> newBillRecords = Database.showBills(User.getUser().getUserShow());
            Platform.runLater(() -> {
                billRecords = newBillRecords;
                ObservableList<Object[]> data = FXCollections.observableArrayList(billRecords);
                historyTable.setItems(data);
            });
        } catch (Exception e) {
            Platform.runLater(() -> Database.showAlert(Alert.AlertType.ERROR, "خطا", "خطایی در بارگذاری داده‌ها رخ داده است."));
            e.printStackTrace();
        }
    }
}
