package uet.oop.bomberman.entities.MovableEntities;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import uet.oop.bomberman.*;
import uet.oop.bomberman.Base.GameMap;
import uet.oop.bomberman.Base.Point;
import uet.oop.bomberman.entities.Bomb.Bomb;
import uet.oop.bomberman.Motion.Movement;
import uet.oop.bomberman.Motion.PlayerMovement;
import uet.oop.bomberman.entities.Entity;
import uet.oop.bomberman.entities.Item.Item;
import uet.oop.bomberman.graphics.ImageLists;
import uet.oop.bomberman.graphics.Sprite;

import java.util.List;

/**
 * Extended by Player and AI
 * Currently used by Player only.
 */
public class Bomber extends MovableEntity {
    private final int ID;

    private int explosion_range = 1;
    private int remaining_bombs = 2;

    public Bomber(final int ID, int xUnit, int yUnit, int initialSpeedByPixel) {
        super( xUnit, yUnit, Sprite.player_up.getFxImage());

        this.ID = ID;

        movement = new PlayerMovement(this, x, y, initialSpeedByPixel);

        imageListArray = new List[4];
        deadState_images = ImageLists.playerDeadImages;

        imageListArray[Movement.DOWN] = ImageLists.playerMovingDownImages;
        imageListArray[Movement.UP] = ImageLists.playerMovingUpImages;
        imageListArray[Movement.LEFT] = ImageLists.playerMovingLeftImages;
        imageListArray[Movement.RIGHT] = ImageLists.playerMovingRightImages;
    }


    public int getID() {
        return this.ID;
    }

    private void setDirection(int direction) {
//        this.direction = direction;
        movement.setDirection(direction);

        if (movement.getDirection() != Movement.FREEZE) {
            this.prev_direction = direction;
        }
    }

    public void placeBomb() {
        if (remaining_bombs == 0) return;

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
            //if existed a bomb, player don't have ability to place bomb.
            if (GameMap.getBombAt(xUnit * Sprite.SCALED_SIZE, yUnit * Sprite.SCALED_SIZE) != null) {
                return;
            }

            Bomb addedBomb = GameMap.addBomb(
                    new Bomb(xUnit, yUnit, Sprite.bomb.getFxImage(), this.ID, explosion_range));

            ((PlayerMovement) movement).addPlacedBomb(addedBomb);

            remaining_bombs -= 1;
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void takeBackOneBomb() {
        remaining_bombs++;
    }


    public void increaseBombCapacity() {
        remaining_bombs += 1;
    }

    public void increaseExplosionRange() {
        explosion_range += 1;
    }

    public void increaseSpeed() {
        movement.speedUp(50);
    }

    public void detectAndPickUpItem() {
        for (Entity object : movement.getSteppedOverObjects(this)) {
            if (object instanceof Item) {
                ((Item) object).buff(this);
            }
        }
    }

    @Override
    protected void move() {
        Point tmp = movement.run();
        try {
//            if (x == tmp.x && y == tmp.y && movement.getDirection() != Movement.FREEZE) {
//                switch (movement.getDirection()) {
//                    case Movement.RIGHT -> {
//                        if (GameMap.getObjectAt(x + Sprite.SCALED_SIZE, y) != null)
//                            System.out.println(GameMap.getObjectAt(x + Sprite.SCALED_SIZE, y).toString());
//                        else if (GameMap.getObjectAt(x + Sprite.SCALED_SIZE,
//                                y + Sprite.SCALED_SIZE) != null)
//                            System.out.println(GameMap.getObjectAt(x + Sprite.SCALED_SIZE,
//                                    y + Sprite.SCALED_SIZE).toString());
//                    }
//                    case Movement.LEFT -> {
//                        if (GameMap.getObjectAt(x - 1, y) != null)
//                            System.out.println(GameMap.getObjectAt(x - 1, y).toString());
//                        else if (GameMap.getObjectAt(x - 1, y + Sprite.SCALED_SIZE) != null)
//                            System.out.println(GameMap.getObjectAt(x - 1, y + Sprite.SCALED_SIZE).toString());
//                    }
//                    case Movement.UP -> {
//                        if (GameMap.getObjectAt(x, y - 1) != null)
//                           System.out.println(GameMap.getObjectAt(x, y - 1).toString());
//                        else if (GameMap.getObjectAt(x + Sprite.SCALED_SIZE, y - 1) != null)
//                            System.out.println(GameMap.getObjectAt(x + Sprite.SCALED_SIZE, y - 1).toString());
//                    }
//                    case Movement.DOWN -> {
//                        if (GameMap.getObjectAt(x, y + Sprite.SCALED_SIZE) != null)
//                            System.out.println(GameMap.getObjectAt(x, y + Sprite.SCALED_SIZE).toString());
//                        else if (GameMap.getObjectAt(x + Sprite.SCALED_SIZE,
//                                y + Sprite.SCALED_SIZE) != null)
//                            System.out.println(GameMap.getObjectAt(x + Sprite.SCALED_SIZE,
//                                y + Sprite.SCALED_SIZE).toString());
//                    }
//                }
//            }
            x = tmp.x;
            y = tmp.y;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
//        System.out.println(movement.getSpeed());
    }

    @Override
    public void update() {
        imageUpdate();

        if (deleted) {
            movement = null;
            GameMap.removeObject(this);
        }
        if (dead && !deleted) {
            movement.stop();

            countdownUntilRemoved(TIME_FOREACH_IMAGE * 3.1);
        }
        if (!dead) {
            dead_startTime = BombermanGame.getTime();

            move();

            detectAndPickUpItem();

            for (Entity entity : movement.getObjectsAhead()) {
                if (entity instanceof Balloon || entity instanceof OneAl) {
                    this.setDead(true);
                }
            }
        }
    }

    @Override
    protected void imageUpdate() {
        if (dead) {
            //timer
            double current_time = BombermanGame.getTime();
            double dead_time = current_time - this.dead_startTime;
            //if current_time reach to maximum value and turn back to 0: (quite hard to happen)
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

    public void setOnPlayerEvents(Scene scene, int player_ordinal_number) {
        if (player_ordinal_number == 1) {
            scene.addEventHandler(KeyEvent.KEY_PRESSED, set1_a);
            scene.addEventHandler(KeyEvent.KEY_RELEASED, set1_b);
//            scene.addEventHandler(KeyEvent.KEY_RELEASED, set1_b);
        }

        if (player_ordinal_number == 2) {
            scene.addEventHandler(KeyEvent.KEY_PRESSED,set2_a);
            scene.addEventHandler(KeyEvent.KEY_RELEASED,set2_b);

            prev_direction = Movement.LEFT;
        }
    }

    public void removeHandler(Scene scene, int player_ordinal_number) {
        if (player_ordinal_number == 1) {
            scene.removeEventHandler(KeyEvent.KEY_PRESSED, set1_a);
            scene.removeEventHandler(KeyEvent.KEY_RELEASED, set1_b);
        }

        if (player_ordinal_number == 2) {
            scene.removeEventHandler(KeyEvent.KEY_PRESSED,set2_a);
            scene.removeEventHandler(KeyEvent.KEY_RELEASED,set2_b);
        }
    }

    private EventHandler<KeyEvent> set1_a = new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent keyEvent) {
            switch (keyEvent.getCode()) {
                case LEFT -> setDirection(Movement.LEFT);
                case RIGHT -> setDirection(Movement.RIGHT);
                case UP -> setDirection(Movement.UP);
                case DOWN -> setDirection(Movement.DOWN);
                case ENTER -> placeBomb();
            }
        }
    };
    private EventHandler<KeyEvent> set1_b = new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent keyEvent) {
            switch (keyEvent.getCode()) {
                case UP -> {
                    if (movement.getDirection() == Movement.UP)
                        setDirection(Movement.FREEZE);
                }case LEFT -> {
                    if (movement.getDirection() == Movement.LEFT)
                        setDirection(Movement.FREEZE);
                }case RIGHT -> {
                    if (movement.getDirection() == Movement.RIGHT)
                        setDirection(Movement.FREEZE);
                }case DOWN -> {
                    if (movement.getDirection() == Movement.DOWN)
                        setDirection(Movement.FREEZE);
                }
            }
        }
    };
    private EventHandler<KeyEvent> set2_a = new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent keyEvent) {
            switch (keyEvent.getCode()) {
                case A -> setDirection(Movement.LEFT);
                case D -> setDirection(Movement.RIGHT);
                case W -> setDirection(Movement.UP);
                case S -> setDirection(Movement.DOWN);
                case SPACE -> placeBomb();
            }
        }
    };
    private EventHandler<KeyEvent> set2_b = new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent keyEvent) {
            switch (keyEvent.getCode()) {
                case W -> {
                    if (movement.getDirection() == Movement.UP)
                        setDirection(Movement.FREEZE);
                }
                case A -> {
                    if (movement.getDirection() == Movement.LEFT)
                        setDirection(Movement.FREEZE);
                }
                case D -> {
                    if (movement.getDirection() == Movement.RIGHT)
                        setDirection(Movement.FREEZE);
                }
                case S -> {
                    if (movement.getDirection() == Movement.DOWN)
                        setDirection(Movement.FREEZE);
                }
            }
        }
    };

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
