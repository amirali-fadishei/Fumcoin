package ExchangeApp;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main application class for the ExchangeApp. This class handles the application startup and main stage management.
 */
public class Main extends Application {

    private static final Logger logger = Logger.getLogger(Main.class.getName());
    private static final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private static Date appStartTime;

    /**
     * Changes the stage based on the ActionEvent and loads the specified FXML file.
     *
     * @param event   the ActionEvent triggered by a user action
     * @param fxmlFile the FXML file to load
     */
    public static void stageChanger(ActionEvent event, String fxmlFile) {
        changeStage(event.getSource(), fxmlFile);
    }

    /**
     * Changes the stage based on the MouseEvent and loads the specified FXML file.
     *
     * @param mouseEvent the MouseEvent triggered by a user action
     * @param fxmlFile   the FXML file to load
     */
    public static void stageChanger(MouseEvent mouseEvent, String fxmlFile) {
        changeStage(mouseEvent.getSource(), fxmlFile);
    }

    private static void changeStage(Object eventSource, String fxmlFile) {
        Platform.runLater(() -> {
            try {
                Parent root = FXMLLoader.load(Objects.requireNonNull(Main.class.getResource(fxmlFile)));
                Scene scene = new Scene(root);
                Stage stage = (Stage) ((Node) eventSource).getScene().getWindow();
                stage.setScene(scene);
                stage.show();
                FadeTransition fadeIn = new FadeTransition(Duration.millis(500), scene.getRoot());
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Failed to load FXML file: " + fxmlFile, e);
            }
        });
    }

    /**
     * Schedules a periodic update task to run every 10 seconds.
     */
    public static void immediateUpdate() {
        executorService.scheduleAtFixedRate(() -> Platform.runLater(Database::periodicUpdate), 0, 10, TimeUnit.SECONDS);
    }

    public static void main(String[] args) {
        appStartTime = new Date();
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Login.fxml")));
        Scene loginPanel = new Scene(root);

        stage.setTitle("Fum Coin");
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("images/icon.png"))));
        stage.setScene(loginPanel);
        stage.setResizable(false);
        stage.show();

        stage.setOnCloseRequest(t -> {
            shutdownExecutorService();
            Platform.exit();
            System.exit(0);
        });

        appStartTime = new Date();
    }

    /**
     * Shuts down the executor service gracefully.
     */
    private void shutdownExecutorService() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
                if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    logger.log(Level.SEVERE, "Executor service did not terminate");
                }
            }
        } catch (InterruptedException ie) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
