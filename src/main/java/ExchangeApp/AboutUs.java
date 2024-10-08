package ExchangeApp;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller class for the About Us page.
 * Handles hyperlink clicks to open URLs in the default web browser.
 */
public class AboutUs extends Menu implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(AboutUs.class.getName());

    @FXML
    private Hyperlink quera;
    @FXML
    private Hyperlink github;
    @FXML
    private Hyperlink linkedin;

    /**
     * Opens the Quera profile URL in the default web browser.
     */
    @FXML
    public void openQueraLink() {
        openLink("https://quera.org/profile/fadisheiweb1383");
    }

    /**
     * Opens the GitHub profile URL in the default web browser.
     */
    @FXML
    public void openGithubLink() {
        openLink("https://github.com/FadisheiWeb");
    }

    /**
     * Opens the LinkedIn profile URL in the default web browser.
     */
    @FXML
    public void openLinkedinLink() {
        openLink("https://ir.linkedin.com/in/amirali-fadishei-b3b5a81b9");
    }

    /**
     * Opens the specified URL in the default web browser.
     *
     * @param url the URL to open
     */
    private void openLink(String url) {
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(new URI(url));
            } catch (IOException | URISyntaxException e) {
                LOGGER.log(Level.SEVERE, "Failed to open link: " + url, e);
            }
        } else {
            LOGGER.log(Level.WARNING, "Desktop is not supported on this system.");
        }
    }

    @Override
    public void stop() {

    }
}
