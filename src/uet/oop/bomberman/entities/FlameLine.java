package uet.oop.bomberman.entities;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import uet.oop.bomberman.BombermanGame;
import uet.oop.bomberman.Base.Point;

import java.util.ArrayList;
import java.util.List;

public abstract class FlameLine extends Entity {
    protected Point lastFlameSegment;
    protected List<Point> flameSegments = new ArrayList<>();

    //1 of 4: RIGHT, DOWN, LEFT, UP
    protected int direction;
    protected int range_unit;

    public static final double EXISTENCE_TIME = 0.6;
    public static final double TIME_FOREACH_IMAGE = 0.2;

    protected double start_time = 0;

    protected boolean done = false;

    protected List<Image> flameImages;
    protected List<Image> lastFlameImages;

    public FlameLine(int xOrigin, int yOrigin, int rangeUnit) {
        super(0, 0, null);
        this.x = xOrigin;
        this.y = yOrigin;

        this.range_unit = rangeUnit;
        this.start_time = BombermanGame.getTime();
    }

    public boolean isDone() {
        return this.done;
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
            this.img = flameImages.get(0);
            gc.drawImage(lastFlameImages.get(0), lastFlameSegment.x, lastFlameSegment.y);
        }
        else if (current_time - start_time <= 2 * TIME_FOREACH_IMAGE) {
            this.img = flameImages.get(1);
            gc.drawImage(lastFlameImages.get(1), lastFlameSegment.x, lastFlameSegment.y);
        }
        else {
            this.img = flameImages.get(2);
            gc.drawImage(lastFlameImages.get(2), lastFlameSegment.x, lastFlameSegment.y);
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
