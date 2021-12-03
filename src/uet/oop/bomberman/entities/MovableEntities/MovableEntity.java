package uet.oop.bomberman.entities.MovableEntities;

import javafx.scene.image.Image;
import uet.oop.bomberman.BombermanGame;
import uet.oop.bomberman.entities.Motion.Movement;
import uet.oop.bomberman.entities.Entity;

import java.util.List;

public abstract class MovableEntity extends Entity {
    protected Movement movement;
    protected int prev_direction = Movement.RIGHT;
//    protected int direction = Movement.FREEZE;

    protected final double TIME_FOREACH_IMAGE = 0.18;
    protected double dead_startTime = 0;

    //objects respectively turn its state from dead to deleted if being damaged.
    protected boolean dead = false;
    protected boolean deleted = false;

    protected List<Image>[] imageListArray;
    protected List<Image> deadState_images;

    public MovableEntity(int xUnit, int yUnit, Image img) {
        super(xUnit, yUnit, img);
    }

    public boolean isDeleted() {
        return this.deleted;
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    protected void countdownUntilRemoved(final double TIME_DEAD_STATE) {
        //timer
        double current_time = BombermanGame.getTime();
        double dead_time = current_time - this.dead_startTime;
        //if current_time reach to maximum value and turn back to 0:
        if (dead_time < 0) {
            dead_time = BombermanGame.TIME_COUNT_MAX - this.dead_startTime + current_time;
        }

        if (dead && dead_time >= TIME_DEAD_STATE) {
            this.deleted = true;
        }
    }

    protected abstract void move();
    protected abstract void imageUpdate();
}
