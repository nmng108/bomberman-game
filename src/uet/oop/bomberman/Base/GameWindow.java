package uet.oop.bomberman.Base;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import uet.oop.bomberman.BombermanGame;
import uet.oop.bomberman.entities.Bomb.Bomb;
import uet.oop.bomberman.entities.MovableEntities.Bomber;
import uet.oop.bomberman.graphics.Sprite;

import java.util.List;

public class GameWindow {
    public static final int INFORMATION_AREA_HEIGHT = 120;
    private final int MAX_LEVEL = 3;
    private final double DISPLAY_NOTIFICATION_TIME = 2;
    private final double TIME_FOREACH_LEVEL = 40;

    private int level = 1;
    private double notif_start_time = 0;
    private double start_game_time;
    private double remaining_time;
    private double counted_time;

    private boolean isPausing = false;
    private boolean isWaiting = true;
    private boolean isPlayingSound = false;

    List<Integer> player_ID_list;
    ScoreManagement scoreManagement;

    GameMap gameMap;

    //    Image inforBackGround = new Image();
    public GameWindow(Scene scene) {
        try {
            gameMap = new GameMap(1, scene);

            player_ID_list = GameMap.getPlayerIDs();

            scoreManagement = new ScoreManagement(player_ID_list);

            start_game_time = BombermanGame.getTime();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update(Scene scene) {
        try {
            gameMap.updateEntities(scene);

            scoreManagement.add(gameMap.getKilledEnemies());

            if (!isWaiting && passLevel()) {
                level += 1;
                if (playersWin()) return;

                gameMap = new GameMap(level, scene);
                start_game_time = BombermanGame.getTime();

                isWaiting = true;
            }

            if (!playersLose() && !playersWin() && !passLevel()) {
                notif_start_time = BombermanGame.getTime();
            }
            else if (BombermanGame.getTime() - notif_start_time >= DISPLAY_NOTIFICATION_TIME) {
                isWaiting = false;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void render(Canvas canvas) {
        try {
            GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
            graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

            graphicsContext.setFill(Color.GREY);
            graphicsContext.fillRect(0, BombermanGame.HEIGHT * Sprite.SCALED_SIZE,
                    BombermanGame.WIDTH * Sprite.SCALED_SIZE, INFORMATION_AREA_HEIGHT);

            gameMap.render(graphicsContext);
            scoreManagement.render(graphicsContext);
            displayTimer(graphicsContext);

            graphicsContext.setFill(Color.GOLDENROD);
            graphicsContext.setFont(new Font("Bookman Old Style", 26));
            graphicsContext.fillText("Màn " + level, 460, 439, 50);

            if (playersWin() && isWaiting) {
                graphicsContext.setFill(Color.GREEN);
                graphicsContext.setFont(new Font("Segoe UI Semibold", 22));
                graphicsContext.fillText("Chúc mừng! Bạn đã thắng tất cả số màn chơi", 271, 480);
            }
            else if (passLevel() && isWaiting) {
                graphicsContext.setFill(Color.GREEN);
                graphicsContext.setFont(new Font("Segoe UI Semibold", 23));
                graphicsContext.fillText("Vượt qua màn " + level + " thành công!", 357, 480);
            }
            else if (playersLose() && isWaiting) {
                graphicsContext.setFill(Color.RED);
                graphicsContext.setFont(new Font("Segoe UI Semibold", 24));

                if (remaining_time <= 0) graphicsContext.fillText("Hết giờ, bạn đã thua!", 380, 480);
                else graphicsContext.fillText("Bạn đã thua!", 422, 480);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayTimer(GraphicsContext graphicsContext) {
        if (isPausing) {
            this.start_game_time = BombermanGame.getTime() - this.counted_time;
            isPausing = false;
        }

        this.counted_time = BombermanGame.getTime() - this.start_game_time;
        this.remaining_time = TIME_FOREACH_LEVEL - this.counted_time;
        if (this.remaining_time <= 0) return;

        graphicsContext.setFill(Color.BEIGE);
        if (this.remaining_time <= 10) {
            graphicsContext.setFill(Color.PALEVIOLETRED);
            // Play sound concurrently with time displaying.
            try {
                if (this.remaining_time >= 0 && this.remaining_time % 1 <= 0.018) {
                    new Sound("Countdown", false).play();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        graphicsContext.setFont(new Font("Arial", 17));
        graphicsContext.fillText("Thời gian còn lại: "
                        + String.format("%.0f", this.remaining_time), 415, 512);
    }

    public void handleGamePaused() {
        GameMap.getBombs().forEach(Bomb::stopCountdown);
        this.isPausing = true;

        for (Bomber player : GameMap.getPlayers()) {
            if (player != null && player.getFootstepsSound() != null) player.getFootstepsSound().stop();
        }
    }

    public boolean playersLose() {
        if (GameMap.getPlayers().size() == 0 || this.remaining_time <= 0) {
            try {
                for (Bomber player : GameMap.getPlayers()) {
                    if (player != null && player.getFootstepsSound() != null) player.getFootstepsSound().stop();
                }

                if (!isPlayingSound){
                    new Sound("Negative_tone", false).play();
                    isPlayingSound = true;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    public boolean playersWin() {
        if (this.level > MAX_LEVEL) {

            return true;
        }
        return false;
    }

    public boolean passLevel() {
//        for (BreakableStillObject object : GameMap.getBreakableStillObjects()) {
//            if (object instanceof Portal) return false;
//        }
        return GameMap.getBots().size() == 0;
    }

    public boolean canOpenMenu() {
        return !isWaiting;
    }
}
