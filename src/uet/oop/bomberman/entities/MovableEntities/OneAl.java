package uet.oop.bomberman.entities.MovableEntities;

import uet.oop.bomberman.Base.Point;
import uet.oop.bomberman.BombermanGame;
import uet.oop.bomberman.Motion.PlayerPursuitMovement;
import uet.oop.bomberman.Motion.Movement;
import uet.oop.bomberman.graphics.ImageLists;
import uet.oop.bomberman.graphics.Sprite;

import java.util.ArrayList;
import java.util.List;

public class OneAl extends MovableEntity {
    public OneAl(int xUnit, int yUnit, int initialSpeedByPixel, int minRange_approach) {
        super(xUnit, yUnit, Sprite.oneal_right1.getFxImage());

        movement = new PlayerPursuitMovement(this, this.x, this.y, initialSpeedByPixel, minRange_approach);

        imageListArray = new List[4];
        imageListArray[Movement.LEFT] = ImageLists.oneAlLeftImages;
        imageListArray[Movement.RIGHT] = ImageLists.oneAlRightImages;

        deadState_images = new ArrayList<>() {{
            add(Sprite.oneal_dead.getFxImage());
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
            move();
            // can change handling oneal's collision with bomber to move transparently
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

}
