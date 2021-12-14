package uet.oop.bomberman.entities.MovableEntities;

import javafx.scene.image.Image;
import uet.oop.bomberman.Base.Point;
import uet.oop.bomberman.BombermanGame;
import uet.oop.bomberman.Motion.Movement;
import uet.oop.bomberman.graphics.Sprite;

/**
 * Bot is an entity that do not have ability to place bomb.
 * Bots include Balloon, Doll, OneAl.
 */
public abstract class Bot extends MovableEntity {
    public Bot(int xUnit, int yUnit, Image img) {
        super(xUnit, yUnit, img);
    }

    @Override
    protected void imageUpdate() {
        if (dead) {
            //timer
            double current_time = BombermanGame.getTime();
            double dead_time = current_time - this.dead_startTime;
            //if current_time reach to maximum value and turn back to 0:
            if (dead_time < 0) {
                dead_time = (BombermanGame.TIME_COUNT_MAX - this.dead_startTime) + current_time;
            }

            if (dead_time <= TIME_FOREACH_IMAGE) {
                this.img = deadState_images.get(0);
            }
            else if (dead_time <= 1.6 * TIME_FOREACH_IMAGE) {
                this.img = deadState_images.get(1);
            }
            else if (dead_time <= 2.2 * TIME_FOREACH_IMAGE) {
                this.img = deadState_images.get(2);
            }
            else this.img = deadState_images.get(3);
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

            countdownUntilRemoved(TIME_FOREACH_IMAGE * 2.8);
        }

        if (!dead) {
            dead_startTime = BombermanGame.getTime();
            move();
            // can change handling oneal's collision with bomber to move transparently
            movement.getObjectsAhead().forEach(obj -> {
                if (obj instanceof Bomber) {
                    ((Bomber) obj).setDead(true);
                }
            });
        }
    }

}
