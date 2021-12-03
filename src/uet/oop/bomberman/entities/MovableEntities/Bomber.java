package uet.oop.bomberman.entities.MovableEntities;

import javafx.scene.image.Image;
import uet.oop.bomberman.*;
import uet.oop.bomberman.Base.GameMap;
import uet.oop.bomberman.Base.Point;
import uet.oop.bomberman.entities.Bomb;
import uet.oop.bomberman.entities.Motion.Movement;
import uet.oop.bomberman.entities.Motion.PlayerMovement;
import uet.oop.bomberman.graphics.ImageLists;
import uet.oop.bomberman.graphics.Sprite;

import java.util.List;

public class Bomber extends MovableEntity {
    private int explosion_range = 3;
    private int max_spawnedBomb = 2;

    public Bomber(int xUnit, int yUnit, int initialSpeedByPixel, Image image) {
        super( xUnit, yUnit, image);

        movement = new PlayerMovement(this, x, y, initialSpeedByPixel);

        imageListArray = new List[4];
        deadState_images = ImageLists.playerDeadImages;

        imageListArray[Movement.DOWN] = ImageLists.playerMovingDownImages;
        imageListArray[Movement.UP] = ImageLists.playerMovingUpImages;
        imageListArray[Movement.LEFT] = ImageLists.playerMovingLeftImages;
        imageListArray[Movement.RIGHT] = ImageLists.playerMovingRightImages;
    }

    public void setDirection(int direction) {
//        this.direction = direction;
        movement.setDirection(direction);

        if (movement.getDirection() != Movement.FREEZE) {
            this.prev_direction = direction;
        }
    }

    public void spawnBomb() {
        if (max_spawnedBomb == 0) return;

        movement.setJustSpawnBomb(true);

        int xUnit = this.x / Sprite.SCALED_SIZE;
        int yUnit = this.y / Sprite.SCALED_SIZE;

        int odd_x = this.x - xUnit * Sprite.SCALED_SIZE;
        int odd_y = this.y - yUnit * Sprite.SCALED_SIZE;

        double area_1 = getRectArea(odd_x, Sprite.SCALED_SIZE - odd_y);
        double area_2 = getRectArea(Sprite.SCALED_SIZE - odd_x, Sprite.SCALED_SIZE - odd_y);
        double area_3 = getRectArea(Sprite.SCALED_SIZE - odd_x, odd_y);
        double area_4 = getRectArea(odd_x, odd_y);
        double areaMax = Math.max(Math.max(area_1, area_2), Math.max(area_3, area_4));

        if (areaMax == area_1) xUnit += 1;

        if (areaMax == area_3) yUnit += 1;
        if (areaMax == area_4) {
            xUnit += 1;
            yUnit += 1;
        }

        try {
            if (GameMap.getBombAt(xUnit * Sprite.SCALED_SIZE, yUnit * Sprite.SCALED_SIZE) != null) {
                return;
            }

            GameMap.addBomb(new Bomb(xUnit, yUnit, Sprite.bomb.getFxImage(), explosion_range));
            max_spawnedBomb -= 1;
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

        System.out.println("Entities: " + GameMap.getMovableEntities().size());
        System.out.println("Still objects: " + GameMap.getWalls().size());
    }

    public void increaseOneBomb() {
        max_spawnedBomb++;
    }

    public void increaseExplosionRange() {
        explosion_range += 1;
    }

    public void increaseSpeed() {
        movement.speedUp();
    }

    @Override
    protected void move() {
        Point tmp = movement.run();
        try {
            if (x == tmp.x && y == tmp.y && movement.getDirection() != Movement.FREEZE) {
                switch (movement.getDirection()) {
                    case Movement.RIGHT -> {
                        if (GameMap.getObjectAt(x + Sprite.SCALED_SIZE, y) != null)
                            System.out.println(GameMap.getObjectAt(x + Sprite.SCALED_SIZE, y).toString());
                        else System.out.println(GameMap.getObjectAt(x + Sprite.SCALED_SIZE,
                                    y + Sprite.SCALED_SIZE).toString());
                    }
                    case Movement.LEFT -> {
                        if (GameMap.getObjectAt(x - 1, y) != null)
                            System.out.println(GameMap.getObjectAt(x - 1, y).toString());
                        else System.out.println(GameMap.getObjectAt(x - 1, y + Sprite.SCALED_SIZE).toString());
                    }
                    case Movement.UP -> {
                        if (GameMap.getObjectAt(x, y - 1) != null)
                           System.out.println(GameMap.getObjectAt(x, y - 1).toString());
                        else System.out.println(GameMap.getObjectAt(x + Sprite.SCALED_SIZE, y - 1).toString());
                    }
                    case Movement.DOWN -> {
                        if (GameMap.getObjectAt(x, y + Sprite.SCALED_SIZE) != null)
                            System.out.println(GameMap.getObjectAt(x, y + Sprite.SCALED_SIZE).toString());
                        else System.out.println(GameMap.getObjectAt(x + Sprite.SCALED_SIZE,
                                y + Sprite.SCALED_SIZE).toString());
                    }
                }
            }
            x = tmp.x;
            y = tmp.y;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void update() {
        imageUpdate();

        if (dead && !deleted) {
            movement.stop();

            countdownUntilRemoved(TIME_FOREACH_IMAGE * 3.1);

            if (this.img == deadState_images.get(2)) {
                System.out.println("GAME OVER!");
            }
        }

        if (!dead) {
            dead_startTime = BombermanGame.getTime();
            move();
        }
    }

    @Override
    protected void imageUpdate() {
        if (deleted) {
            this.img = null;
        }
        else if (dead) {
            //timer
            double current_time = BombermanGame.getTime();
            double dead_time = current_time - this.dead_startTime;
            //if current_time reach to maximum value and turn back to 0:
            if (dead_time < 0) {
                dead_time = (BombermanGame.TIME_COUNT_MAX - this.dead_startTime) + current_time;
            }


            if (dead_time < TIME_FOREACH_IMAGE) {
                this.img = deadState_images.get(2);
            }
            else if (dead_time < 2 * TIME_FOREACH_IMAGE) {
                this.img = deadState_images.get(1);
            }
            else {
                this.img = deadState_images.get(0);
            }
        }
        else {
            if (movement.getDirection() == Movement.FREEZE) {
                this.img = imageListArray[this.prev_direction].get(0);
            }
            else {
                int direction = movement.getDirection();
                this.img = Sprite.movingImage(imageListArray[direction].get(1),
                                                imageListArray[direction].get(2),
                                                BombermanGame.getTime(), TIME_FOREACH_IMAGE * 2);
            }
        }

    }

//    @Override
//    protected void countdownUntilRemoved() {
//        double current_time = BombermanGame.getTime();
//        double dying_time = current_time - this.dead_startTime;
//        //if TIMER reach to maximum value and turn back to 0:
//        if (dying_time < 0) dying_time = BombermanGame.TIME_COUNT_MAX - this.dead_startTime + current_time;
//
//        if (dead && dying_time >= TIME_FOREACH_IMAGE * 3.1) {
//            this.dead = true;
//        }
//    }

    private double getRectArea(double a, double b) {
        return a * b;
    }
}
