package ExchangeApp;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * This class manages the user's wallet, displaying the coins and their values,
 * and updating the profit chart periodically.
 */
public class Wallet extends Menu implements Initializable {

    @FXML
    private Label profit_d;

    @FXML
    private LineChart<Number, Number> chart;

    private Double previousProfit = null;

    private ScheduledExecutorService scheduler;

    /**
     * Initializes the controller class.
     * Sets up the table columns and schedules periodic updates to the wallet data and profit chart.
     *
     * @param url the location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resourceBundle the resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        chart.getData().add(series);
        updateTable();

        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> Platform.runLater(this::checkAndUpdateProfit), 0, 10, TimeUnit.SECONDS);
    }

    /**
     * Stops the scheduled executor service when the application is stopped.
     */
    @Override
    public void stop() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }

    /**
     * Sets up the columns for the wallet table.
     */

    /**
     * Updates the wallet table with the latest data from the user.
     */
    private void updateTable() {
        User.getUser().updateFromDatabase();
        profit_d.setText(User.getUser().getpD() + " USD");
        updateChart();
    }

    /**
     * Checks the profit and updates it if there are changes.
     */
    private void checkAndUpdateProfit() {
        ArrayList<Double> profits = User.getUser().getProfits();
        if (profits.isEmpty()) return;

        Double currentProfit = profits.getLast();

        if (previousProfit == null || !previousProfit.equals(currentProfit)) {
            previousProfit = currentProfit;
            User.getUser().updateProfit(currentProfit);
            Database.saveProfits();
            updateChart();
        }
    }

    /**
     * Updates the profit chart with the latest profit data.
     */
    private void updateChart() {
        XYChart.Series<Number, Number> series = chart.getData().getFirst();
        series.getData().clear();
        ArrayList<Double> profits = User.getUser().getProfits();
        for (int i = 0; i < profits.size(); i++) {
            series.getData().add(new XYChart.Data<>(i, profits.get(i)));
        }
    }
}
