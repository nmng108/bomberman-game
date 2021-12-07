package uet.oop.bomberman;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.io.IOException;

public class PauseScreen {
    private static boolean paused = false;
    private static boolean backedToMenu = false;

    @FXML
    private Button continueButton;

    @FXML
    private Button backToMenuButton;

    @FXML
    private void run() {
        paused = false;
        BombermanGame.setRootToGameWindow();
    }

    @FXML
    private void openMenu() throws IOException {
        paused = false;
        backedToMenu = true;
        BombermanGame.setRoot("Menu");
    }

    @FXML
    private void initialize() throws IOException {
        paused = true;
        backedToMenu = false;
    }

    public static boolean hasPaused() {
        return paused;
    }

    public static boolean hasBackedToMenu() {
        if (backedToMenu) {
            backedToMenu = false;
            return true;
        }
        return false;
    }
}
