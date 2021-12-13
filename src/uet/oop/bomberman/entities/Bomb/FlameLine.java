package uet.oop.bomberman.entities.Bomb;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import uet.oop.bomberman.BombermanGame;
import uet.oop.bomberman.Base.Point;
import uet.oop.bomberman.entities.Entity;
import uet.oop.bomberman.entities.MovableEntities.MovableEntity;

import java.util.ArrayList;
import java.util.List;

public abstract class FlameLine extends Entity {
    protected Point lastFlameSegment;
    protected List<Point> flameSegments = new ArrayList<>();

    protected List<MovableEntity> killed_enemy_list = new ArrayList<>();

    //1 of 4: RIGHT, DOWN, LEFT, UP
    protected int direction;
    protected int range_unit;

    public static final double EXISTENCE_TIME = 0.6;
    public static final double TIME_FOREACH_IMAGE = 0.2;

    protected double start_time = 0;

    protected boolean done = false;
    protected boolean wallPass = false;

    protected List<Image> flame_images;
    protected List<Image> last_flame_images;

    public FlameLine(int xOrigin, int yOrigin, int rangeUnit, boolean canPassWalls) {
        super(0, 0, null);
        this.x = xOrigin;
        this.y = yOrigin;

        this.range_unit = rangeUnit;
        this.start_time = BombermanGame.getTime();

        this.wallPass = canPassWalls;
    }

    public boolean isDone() {
        return this.done;
    }

    public void setWallPass(boolean b) {
        this.wallPass = b;
    }

    public List<MovableEntity> getKilledEnemies() {
        return killed_enemy_list;
    }

    @Override
    public void render(GraphicsContext gc) {
        //timer
        double current_time = BombermanGame.getTime();
        double remainingTime = EXISTENCE_TIME + this.start_time - current_time;

        if (this.start_time > current_time) {
            remainingTime = EXISTENCE_TIME - (BombermanGame.TIME_COUNT_MAX - this.start_time) - current_time;
        }

        if (remainingTime <= 0) {
            this.img = null;
            this.done = true;
            return;
        }
//may remove code block above?
        if (current_time - start_time <=  TIME_FOREACH_IMAGE) {
            this.img = flame_images.get(0);
            gc.drawImage(last_flame_images.get(0), lastFlameSegment.x, lastFlameSegment.y);
        }
        else if (current_time - start_time <= 2 * TIME_FOREACH_IMAGE) {
            this.img = flame_images.get(1);
            gc.drawImage(last_flame_images.get(1), lastFlameSegment.x, lastFlameSegment.y);
        }
        else {
            this.img = flame_images.get(2);
            gc.drawImage(last_flame_images.get(2), lastFlameSegment.x, lastFlameSegment.y);
        }

        this.flameSegments.forEach(flame -> {
            gc.drawImage(img, flame.x, flame.y);
        });
    }

    @Override
    public void update() {
        burn();
    }

    protected abstract void addAllFlameSegments();
    protected abstract void burn();
}
