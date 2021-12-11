package uet.oop.bomberman;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Menu {
    private static int players_number = 1;

    private static boolean clickedStart = false;
    private static boolean clickedQuit = false;

    @FXML
    private ChoiceBox<String> choiceBox;

    @FXML
    private Button startButton;

    @FXML
    private Button quitButton;

    @FXML
    void initialize() throws IOException {
        List<String> arr = Arrays.asList("1 người chơi", "2 người chơi");
        ObservableList<String> observableList = FXCollections.observableList(arr);
        choiceBox.setItems(observableList);
        choiceBox.setValue(choiceBox.getItems().get(0));

        clickedStart = false;
    }

    @FXML
    private void start() {
        clickedStart = true;

        String result = choiceBox.getValue();
        if (result.equals("2 người chơi")) players_number = 2;
        else players_number = 1;

        BombermanGame.setRootToGameWindow();
    }

    @FXML
    private void quit() {
        clickedQuit = true;
    }

    public static int getPlayersNumber() {
        return players_number;
    }

    public static boolean hasClickedStart() {
        return clickedStart;
    }

    public static boolean hasClickedQuit() {
        return clickedQuit;
    }
}
