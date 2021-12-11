package uet.oop.bomberman.entities.StillEntities;

import javafx.scene.image.Image;
import uet.oop.bomberman.Base.GameMap;
import uet.oop.bomberman.BombermanGame;
import uet.oop.bomberman.entities.Item.PowerUpBombs;
import uet.oop.bomberman.entities.Item.PowerUpFlames;
import uet.oop.bomberman.entities.Item.PowerUpSpeed;
import uet.oop.bomberman.entities.Item.PowerUpWallPass;
import uet.oop.bomberman.graphics.Sprite;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Brick extends BreakableStillObject {
    private final double TIME_BROKEN_STATE = TIME_FOREACH_IMAGE * 3.1;

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

        if (deleted) {
            GameMap.removeStillObject(this);
            dropRandomItem();
            return;
        }
        else if (broken) {
            countdownUntilRemoved();
            return;
        }

        this.break_start_time = BombermanGame.getTime();
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

    private void countdownUntilRemoved() {
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

    private void dropRandomItem() {
        final double PROBABILITY = 0.4;

        Random random = new Random();
        int n = random.nextInt(0, 100);

        if (n < PROBABILITY * 100) {
            int xUnit = this.x / Sprite.SCALED_SIZE;
            int yUnit = this.y / Sprite.SCALED_SIZE;

            switch (random.nextInt(4)) {
                case 0 -> GameMap.addItem(new PowerUpSpeed(xUnit, yUnit));
                case 1 -> GameMap.addItem(new PowerUpBombs(xUnit, yUnit));
                case 2 -> GameMap.addItem(new PowerUpFlames(xUnit, yUnit));
                case 3 -> GameMap.addItem(new PowerUpWallPass(xUnit, yUnit));
            }
        }
    }
}
