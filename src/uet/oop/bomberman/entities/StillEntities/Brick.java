package uet.oop.bomberman.entities.StillEntities;

import javafx.scene.image.Image;
import uet.oop.bomberman.BombermanGame;
import uet.oop.bomberman.graphics.Sprite;

import java.util.ArrayList;
import java.util.List;

public class Brick extends BreakableStillObject {
    private List<Image> brokenState_images;

    public Brick(int xUnit, int yUnit) {
        super(xUnit, yUnit, Sprite.brick.getFxImage());

        brokenState_images = new ArrayList<>() {{
            add(Sprite.brick_exploded.getFxImage());
            add(Sprite.brick_exploded1.getFxImage());
            add(Sprite.brick_exploded2.getFxImage());
        }};
    }

    @Override
    public void update() {
        updateImage();

        if (broken) {
            countdownUntilRemoved(TIME_FOREACH_IMAGE * 3.3);
        }
    }

    @Override
    protected void updateImage() {
        if (deleted) this.img = null;
        else if (broken) {
            //timer
            double current_time = BombermanGame.getTime();
            double brokenState_time = current_time - this.break_start_time;
            //If program's timer reach to maximum value and turn back to 0:
            if (brokenState_time < 0) {
                brokenState_time = (BombermanGame.getTime() - this.break_start_time) + current_time;
            }

            if (brokenState_time < TIME_FOREACH_IMAGE) {
                this.img = brokenState_images.get(0);
            }
            else if (brokenState_time < 2 * TIME_FOREACH_IMAGE) {
                this.img = brokenState_images.get(1);
            }
            else this.img = brokenState_images.get(2);
        }
    }

    private void countdownUntilRemoved(final double TIME_BROKEN_STATE) {
        double current_time = BombermanGame.getTime();
        double brokenState_time = current_time - this.break_start_time;
        //If program's timer reach to maximum value and turn back to 0:
        if (brokenState_time < 0) {
            brokenState_time = (BombermanGame.getTime() - this.break_start_time) + current_time;
        }

        if (brokenState_time >= TIME_BROKEN_STATE) {
            this.deleted = true;
        }
    }

}
