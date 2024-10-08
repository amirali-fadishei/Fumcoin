package ExchangeApp;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Abstract base class for the application's menu. Provides common functionality for navigating between different screens.
 */
public abstract class Menu implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(Menu.class.getName());

    @FXML
    private Text clock;
    @FXML
    private Button userShowButton;

    /**
     * Handles the exit button action. Changes the stage to the login screen.
     *
     * @param event the ActionEvent triggered by the button click
     * @throws IOException if there is an error changing the stage
     */
    @FXML
    public synchronized void exit(ActionEvent event) throws IOException {
        Main.stageChanger(event, "Login.fxml");
    }

    /**
     * Handles the token button action. Changes the stage to the token screen.
     *
     * @param event the MouseEvent triggered by the button click
     * @throws IOException if there is an error changing the stage
     */
    @FXML
    public synchronized void openTokenScreen(MouseEvent event) throws IOException {
        Main.stageChanger(event, "Token.fxml");
    }

    /**
     * Handles the swap button action. Changes the stage to the swap screen.
     *
     * @param event the ActionEvent triggered by the button click
     */
    @FXML
    public synchronized void openSwapScreen(ActionEvent event) {
        Main.stageChanger(event, "Swap.fxml");
    }

    /**
     * Handles the exchange button action. Changes the stage to the exchange screen.
     *
     * @param event the ActionEvent triggered by the button click
     */
    @FXML
    public synchronized void openExchangeScreen(ActionEvent event) {
        Main.stageChanger(event, "Exchange.fxml");
    }

    /**
     * Handles the wallet button action. Changes the stage to the wallet screen.
     *
     * @param event the ActionEvent triggered by the button click
     */
    @FXML
    public synchronized void openWalletScreen(ActionEvent event) {
        Main.stageChanger(event, "Wallet.fxml");
    }

    /**
     * Handles the history button action. Changes the stage to the history screen.
     *
     * @param event the ActionEvent triggered by the button click
     */
    @FXML
    public synchronized void openHistoryScreen(ActionEvent event) {
        Main.stageChanger(event, "History.fxml");
    }

    /**
     * Handles the profile button action. Changes the stage to the profile screen.
     *
     * @param event the ActionEvent triggered by the button click
     */
    @FXML
    public synchronized void openProfileScreen(ActionEvent event) {
        Main.stageChanger(event, "Profile.fxml");
    }

    /**
     * Handles the main page button action. Changes the stage to the main page screen.
     *
     * @param event the ActionEvent triggered by the button click
     */
    @FXML
    public synchronized void openMainPageScreen(ActionEvent event) {
        Main.stageChanger(event, "MainPage.fxml");
    }

    /**
     * Handles the transfer button action. Changes the stage to the transfer screen.
     *
     * @param event the ActionEvent triggered by the button click
     */
    @FXML
    public synchronized void openTransferScreen(ActionEvent event) {
        Main.stageChanger(event, "Transfer.fxml");
    }

    /**
     * Handles the about us button action. Changes the stage to the about us screen.
     *
     * @param event the ActionEvent triggered by the button click
     */
    @FXML
    public synchronized void openAboutUsScreen(ActionEvent event) {
        Main.stageChanger(event, "AboutUs.fxml");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> userShowButton.setText(User.getUser().getUserShow()));
        startClock();
    }

    /**
     * Starts the clock that updates the clock Text control every second.
     */
    private void startClock() {
        final SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss a");
        Thread clockThread = new Thread(() -> {
            while (true) {
                try {
                    Platform.runLater(() -> clock.setText(format.format(new Date())));
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    LOGGER.log(Level.SEVERE, "Clock thread interrupted", e);
                    Thread.currentThread().interrupt();
                }
            }
        });

        clockThread.setDaemon(true);
        clockThread.start();
    }

    /**
     * Abstract method to be implemented by subclasses to handle any necessary cleanup.
     */
    public abstract void stop();
}
