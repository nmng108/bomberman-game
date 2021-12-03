package uet.oop.bomberman;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import uet.oop.bomberman.Base.GameMap;
import uet.oop.bomberman.entities.*;
import uet.oop.bomberman.entities.Motion.Movement;
import uet.oop.bomberman.entities.MovableEntities.Bomber;
import uet.oop.bomberman.entities.StillEntities.BreakableStillObject;
import uet.oop.bomberman.entities.MovableEntities.MovableEntity;
import uet.oop.bomberman.graphics.Sprite;

public class BombermanGame extends Application {
    
    public static final int WIDTH = 31;
    public static final int HEIGHT = 13;

    private GraphicsContext gc;
    private Canvas canvas;
    private Group root;
    private Scene scene;
    Bomber bomber;

    public static int TIME_COUNT_MAX_BASE = 10;
    public static double TIME_COUNT_MAX_EXPONENT = 2;
    public static double TIME_COUNT_MAX = Math.pow(TIME_COUNT_MAX_BASE, TIME_COUNT_MAX_EXPONENT);
    private static double time = 0;
    private static double start_time = TIME_COUNT_MAX;

    private boolean ingame = true;
    private boolean appIsRunning = true;

    public void end() {
        ingame = false;
    }

    public static void main(String[] args) {
        Application.launch(BombermanGame.class);
    }

    @Override
    public void start(Stage stage) {
        // Tao Canvas
        canvas = new Canvas(Sprite.SCALED_SIZE * WIDTH, Sprite.SCALED_SIZE * HEIGHT);
        gc = canvas.getGraphicsContext2D();

        // Tao root container
        root = new Group(canvas);

        // Tao scene
        scene = new Scene(root);
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                switch (keyEvent.getCode()) {
                    case LEFT -> {
                        bomber.setDirection(Movement.LEFT);
                    }
                    case RIGHT -> {
                        bomber.setDirection(Movement.RIGHT);
                    }
                    case UP -> {
                        bomber.setDirection(Movement.UP);
                    }
                    case DOWN -> {
                        bomber.setDirection(Movement.DOWN);
                    }
                    case SPACE -> {
                        bomber.spawnBomb();
                    }
                    default -> {
                        bomber.setDirection(Movement.FREEZE);
                    }
                }
            }
        });
        scene.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent key) -> {

            switch (key.getCode()) {
                case UP, DOWN, LEFT, RIGHT -> bomber.setDirection(Movement.FREEZE);
            }
        });

        // Them scene vao stage
        stage.setResizable(false);
        stage.setTitle("Bomberman");
        stage.setScene(scene);
        stage.show();
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                try {
                    time = takeSeconds(l);

                    if (start_time == TIME_COUNT_MAX) start_time = time;

                    if (getTime() % 1 <= 0.012) System.out.println(getTime());

                    render();
                    update();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                end();
            }
        };
        timer.start();

        createMap();

    }

    public void createMap() {
        bomber = GameMap.create();
        System.out.println("Map created." + bomber.getX() + " " + bomber.getY());
    }

    public void update() {
        GameMap.updateBomb();
        GameMap.updateMovableEntities();
        GameMap.updateStillObjects();
    }

    public void render() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        GameMap.render(gc);
    }

//    public void handleEvent() {
//
//    }

    private double takeSeconds(long l) {
        l %= Math.pow(10, 9 + TIME_COUNT_MAX_EXPONENT);
        double result = (double) l / Math.pow(TIME_COUNT_MAX_BASE, 9);
        return Double.parseDouble(String.format("%.9f", result));
    }

    public static double getTime() {
        if (time < start_time) {
            start_time = time;
        }

        return Double.parseDouble(String.format("%.9f", Math.abs(time - start_time)));
    }
}
