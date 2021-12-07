package uet.oop.bomberman.Base;

import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import uet.oop.bomberman.BombermanGame;
import uet.oop.bomberman.Menu;
import uet.oop.bomberman.entities.Bomb.Bomb;
import uet.oop.bomberman.graphics.Sprite;

import java.util.List;

public class GameWindow {
    public static final int INFORMATION_AREA_HEIGHT = 120;

    private final double DISPLAY_NOTIFICATION_TIME = 2;
    private double lose_start_time = 0;

    private boolean isWaiting = true;

    List<Integer> player_ID_list;
    ScoreManagement scoreManagement;


    //    Image inforBackGround = new Image();
    public GameWindow(Scene scene) {
        try {
            GameMap.clearMap();
            GameMap.create("level1");
            GameMap.setPlayer(Menu.getPlayersNumber(), scene);

            player_ID_list = GameMap.getPlayerIDs();

            scoreManagement = new ScoreManagement(player_ID_list);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update(Scene scene) {
        try {
            GameMap.updateBombs();
            GameMap.updateStillObjects();
            GameMap.updateMovableEntities();
            GameMap.updatePlayers(scene);

            scoreManagement.add(player_ID_list.get(0), GameMap.getKilledEnemies());

            if (!playersLose()) lose_start_time = BombermanGame.getTime();
            else if (BombermanGame.getTime() - lose_start_time >= DISPLAY_NOTIFICATION_TIME) {
                    isWaiting = false;
                }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void render(GraphicsContext graphicsContext) {
        try {
            graphicsContext.setFill(Color.DARKSLATEGREY);
            graphicsContext.fillRect(0, BombermanGame.HEIGHT * Sprite.SCALED_SIZE,
                    BombermanGame.WIDTH * Sprite.SCALED_SIZE, INFORMATION_AREA_HEIGHT);

            GameMap.render(graphicsContext);
            scoreManagement.render(graphicsContext);

            if (playersLose() && isWaiting) {
                graphicsContext.setFill(Color.RED);
                graphicsContext.fillText("Bạn đã thua!", 450, 500, 300);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleGamePaused() {
        GameMap.getBombs().forEach(Bomb::stopCountdown);

    }

    public boolean playersLose() {
        return GameMap.getPlayerIDs().size() == 0;
    }

    public boolean playersWin() {
        return GameMap.getMovableEntities().size() == 0;
    }

    public boolean canOpenMenu() {
        return !isWaiting;
    }
}
