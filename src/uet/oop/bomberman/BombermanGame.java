package uet.oop.bomberman;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import uet.oop.bomberman.Base.GameWindow;
import uet.oop.bomberman.graphics.Sprite;

import java.io.IOException;

public class BombermanGame extends Application {

    public static final int WIDTH = 31;
    public static final int HEIGHT = 13;

    private GraphicsContext gc;
    private Canvas canvas;
    private static Group root;
    private static Scene scene;

    private GameWindow gameWindow;

    public static int TIME_COUNT_MAX_BASE = 10;
    public static double TIME_COUNT_MAX = Math.pow(TIME_COUNT_MAX_BASE, 8);
    private static double _time = 0;
    private static double _start_time = TIME_COUNT_MAX;

    private static boolean hasStarted = false;


    public static void main(String[] args) {
        Application.launch(BombermanGame.class);
    }

    @Override
    public void start(Stage stage) {
        // Tao Canvas
        canvas = new Canvas(Sprite.SCALED_SIZE * WIDTH,
                Sprite.SCALED_SIZE * HEIGHT + GameWindow.INFORMATION_AREA_HEIGHT);
        gc = canvas.getGraphicsContext2D();

        // Tao root container
        root = new Group(canvas);

        // Tao scene
        scene = new Scene(root);

        // Press ESC to pause
        scene.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ESCAPE)) {
                try {
                    setRoot("PauseScreen");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
//        createPauseButton(root); //cause the error that we can control our character.

        // Them scene vao stage
        stage.setResizable(false);
        stage.setTitle("Bomberman");
        stage.setScene(scene);
        stage.show();
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void start() {
                super.start();
                try {
                    setRoot("Menu");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void handle(long l) {
                try {
                    _time = takeSeconds(l);
                    if (_start_time == TIME_COUNT_MAX) _start_time = _time;
//                    game_period = l;

                    if (Menu.hasClickedQuit()) {
                        Platform.exit();
                    }

                    if (!Menu.hasClickedStart()) {
                        hasStarted = false;
                        return;
                    } else if (!hasStarted) {
                        gameWindow = new GameWindow(scene);
                        scene.setRoot(root);

                        hasStarted = true;
                    }

                    if (PauseScreen.hasPaused()) {
                        gameWindow.handleGamePaused();
                        return;
                    }

                    if (PauseScreen.hasBackedToMenu()) return;

                    gameWindow.render(canvas);
                    gameWindow.update(scene);

                    if (gameWindow.playersLose()) {
                        if (gameWindow.canOpenMenu()) setRoot("Menu");
                        return;
                    }
                    if (gameWindow.playersWin()) {
                        setRoot("Menu");
                    }

//                    game_period = l - game_period;
//                    System.out.println(String.format("%.15f", game_period));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        timer.start();

    }


    // Change the root of this scene.
    public static void setRoot(String name) throws IOException {
        scene.setRoot((Parent) loadFxml(name));
    }

    private static Node loadFxml(String name) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(BombermanGame.class.getResource(name + ".fxml"));
        return fxmlLoader.load();
    }

    //Set root of this scene to this class's canvas and set hasPause to true
    public static void setRootToGameWindow() {
        scene.setRoot(root);
    }

    private double takeSeconds(long l) {
        double result = (double) l / 1e9; // l/10^9
        return Double.parseDouble(String.format("%.4f", result));
    }

    public static double getTime() {
        if (_time < _start_time) {
            _start_time = _time;
        }

        return Double.parseDouble(String.format("%.4f", Math.abs(_time - _start_time)));
    }
}
