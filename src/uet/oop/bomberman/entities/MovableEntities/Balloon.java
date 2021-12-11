package uet.oop.bomberman.entities.MovableEntities;

import uet.oop.bomberman.BombermanGame;
import uet.oop.bomberman.Motion.Movement;
import uet.oop.bomberman.Base.Point;
import uet.oop.bomberman.Motion.RandomMovement;
import uet.oop.bomberman.graphics.ImageLists;
import uet.oop.bomberman.graphics.Sprite;

import java.util.ArrayList;
import java.util.List;

public class Balloon extends MovableEntity {
    public Balloon(int xUnit, int yUnit, int initialSpeedByPixel) {
        super(xUnit, yUnit, Sprite.balloom_right1.getFxImage());

        movement = new RandomMovement(this, this.x, this.y, initialSpeedByPixel);

        imageListArray = new List[4];
        imageListArray[Movement.LEFT] = ImageLists.balloonLeftImages;
        imageListArray[Movement.RIGHT] = ImageLists.balloonRightImages;

        deadState_images = new ArrayList<>() {{
            add(Sprite.balloom_dead.getFxImage());
        }};
    }

    @Override
    protected void move() {
        Point tmp = movement.run();
        x = tmp.x;
        y = tmp.y;

        if (movement.getDirection() == Movement.RIGHT || movement.getDirection() == Movement.LEFT) {
            prev_direction = movement.getDirection();
        }
    }

    @Override
    public void update() {
        imageUpdate();

        if (dead && !deleted) {
            movement.stop();

            countdownUntilRemoved(TIME_FOREACH_IMAGE * 1.2);
        }

        if (!dead) {
            dead_startTime = BombermanGame.getTime();
//            move();
            // may change handling balloon's collision with bomber to move transparently in the future.
            movement.getObjectsAhead().forEach(obj -> {
                if (obj instanceof Bomber) {
                    ((Bomber) obj).setDead(true);
                }
            });
        }
    }

    @Override
    protected void imageUpdate() {
        if (deleted) {
            this.img = null;
        }
        else if (dead) {
            this.img = deadState_images.get(0);
        }
        else {
            if (movement.getDirection() == Movement.FREEZE) {
                this.img = imageListArray[this.prev_direction].get(0);
            }
            else {
                this.img = Sprite.movingImage(imageListArray[this.prev_direction].get(0),
                        imageListArray[this.prev_direction].get(1),
                        imageListArray[this.prev_direction].get(2),
                        BombermanGame.getTime(), TIME_FOREACH_IMAGE * 3);
            }
        }
    }

    private void killPlayer() {

    }
//    @Override
//    protected void countdownUntilRemoved() {
//        double current_time = BombermanGame.getTime();
//        double dying_time = current_time - this.dead_startTime;
//        //if TIMER reach to maximum value and turn back to 0:
//        if (dying_time < 0) {
//            dying_time = BombermanGame.TIME_COUNT_MAX - this.dead_startTime + current_time;
//        }
//
//        if (dead && dying_time >= TIME_FOREACH_IMAGE * 1.2) {
//            this.dead = true;
//        }
//    }
}
