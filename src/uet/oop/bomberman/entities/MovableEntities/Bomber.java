package uet.oop.bomberman.entities.MovableEntities;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import uet.oop.bomberman.*;
import uet.oop.bomberman.Base.GameMap;
import uet.oop.bomberman.Base.Point;
import uet.oop.bomberman.entities.Bomb.Bomb;
import uet.oop.bomberman.Motion.Movement;
import uet.oop.bomberman.Motion.PlayerMovement;
import uet.oop.bomberman.entities.Entity;
import uet.oop.bomberman.entities.Item.Item;
import uet.oop.bomberman.entities.StillEntities.Portal;
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
    private int flash_turns = 0;

    private boolean teleported = false;

    private Portal portal_standing_on = null;

    public Bomber(int ordinal_number, final int ID, int xUnit, int yUnit, int initialSpeedByPixel) {
        super( xUnit, yUnit, Sprite.player1_up.getFxImage());

        this.ID = ID;

        movement = new PlayerMovement(this, x, y, initialSpeedByPixel);

        imageListArray = new List[4];
        switch (ordinal_number) {
            case 1 -> {
                deadState_images = ImageLists.player1DeadImages;
                imageListArray[Movement.DOWN] = ImageLists.player1MovingDownImages;
                imageListArray[Movement.UP] = ImageLists.player1MovingUpImages;
                imageListArray[Movement.LEFT] = ImageLists.player1MovingLeftImages;
                imageListArray[Movement.RIGHT] = ImageLists.player1MovingRightImages;
            }
            case 2 -> {
                deadState_images = ImageLists.player2DeadImages;
                imageListArray[Movement.DOWN] = ImageLists.player2MovingDownImages;
                imageListArray[Movement.UP] = ImageLists.player2MovingUpImages;
                imageListArray[Movement.LEFT] = ImageLists.player2MovingLeftImages;
                imageListArray[Movement.RIGHT] = ImageLists.player2MovingRightImages;
            }
        }
    }

    public void setPosition(int x, int y) {
        ((PlayerMovement) movement).setCoordinates(x, y);
    }

    public int getID() {
        return this.ID;
    }

    public boolean hasTeleported() {
        return teleported;
    }

    private void setDirection(int direction) {
//        this.direction = direction;
        ((PlayerMovement) movement).setDirection(direction);

        if (movement.getDirection() != Movement.FREEZE) {
            this.prev_direction = direction;
        }
    }

    public void placeBomb() {
        if (remaining_bombs == 0) return;

        Point cellUnit = GameMap.getMostStandingCell(this.x, this.y);
        int xUnit = cellUnit.x;
        int yUnit = cellUnit.y;

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

    public void receiveFlashItem() {
        flash_turns += 1;
    }

    private void useFlashItem() {
        if (flash_turns <= 0) return;
        flash_turns -= 1;

        flash(2);
    }

    private void flash(int number_of_cells) {
        int direction;
        if (movement.getDirection() == Movement.FREEZE) direction = prev_direction;
        else direction = movement.getDirection();

        switch (direction) {
            case Movement.LEFT -> {
                if (x - 2 * Sprite.SCALED_SIZE < 0) return;
                if (cannotFlashInto(x - number_of_cells * Sprite.SCALED_SIZE, y)){
                    int d = (int) GameMap.distanceToObjectAhead(this);
                    ((PlayerMovement) movement).setCoordinates(x - d, y);
                    return;
                }

                ((PlayerMovement) movement).setCoordinates(x - number_of_cells * Sprite.SCALED_SIZE, y);
            }

            case Movement.DOWN -> {
                if (y + 2 * Sprite.SCALED_SIZE >= BombermanGame.HEIGHT * Sprite.SCALED_SIZE) return;
                if (cannotFlashInto(x, y + 2 * Sprite.SCALED_SIZE)) {
                    int d = (int) GameMap.distanceToObjectAhead(this);
                    ((PlayerMovement) movement).setCoordinates(x, y + d);
                    return;
                }

                ((PlayerMovement) movement).setCoordinates(x, y + number_of_cells * Sprite.SCALED_SIZE);
            }

            case Movement.UP -> {
                if (y - 2 * Sprite.SCALED_SIZE < 0) return;
                if (cannotFlashInto(x, y - 2 * Sprite.SCALED_SIZE)) {
                    int d = (int) GameMap.distanceToObjectAhead(this);
                    ((PlayerMovement) movement).setCoordinates(x, y - d);
                    return;
                }

                ((PlayerMovement) movement).setCoordinates(x, y - number_of_cells * Sprite.SCALED_SIZE);
            }

            case Movement.RIGHT -> {
                if (x + 2 * Sprite.SCALED_SIZE >= BombermanGame.WIDTH * Sprite.SCALED_SIZE) return;
                if (cannotFlashInto(x + 2 * Sprite.SCALED_SIZE, y)) {
                    int d = (int) GameMap.distanceToObjectAhead(this);
                    ((PlayerMovement) movement).setCoordinates(x + d, y);
                    return;
                }

                ((PlayerMovement) movement).setCoordinates(x + number_of_cells * Sprite.SCALED_SIZE, y);
            }
        }
    }

    private boolean cannotFlashInto(int x, int y) {
        for (int i = y; i < y + Sprite.SCALED_SIZE - 1; i++) {
            for (int j = x; j < x + Sprite.SCALED_SIZE - 1; j++) {
                Entity obj = GameMap.getObjectAt(null, j, i);
                if (obj != null) {
                    return !(obj instanceof Item);
                }
            }
        }

        return false;
    }

    public void detectAndPickUpItem() {
        for (Entity object : movement.getSteppedOverObjects(this)) {
            if (object instanceof Item) {
                ((Item) object).buff(this);
            }
        }
    }

    public void teleportTo(Portal portal) {
        try {
            if (portal == null) throw new Exception("Portal not found");

            this.x = portal.getX();
            this.y = portal.getY();
            ((PlayerMovement) movement).setCoordinates(this.x, this.y);

            this.teleported = true;
            this.portal_standing_on = portal;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void scanPort() {
        if (this.portal_standing_on == null) return;

        Point bomber_from = new Point(this.x, this.y);
        Point bomber_to = new Point(this.x + Sprite.SCALED_SIZE - 1, this.y + Sprite.SCALED_SIZE - 1);

        for (int i = bomber_from.y; i < bomber_to.y; i++) {
            for (int j = bomber_from.x; j < bomber_to.x; j++) {
                if (GameMap.getOpenPortalAt(j, i) == this.portal_standing_on) return;
            }
        }

        this.teleported = false;
        this.portal_standing_on = null;
    }

    @Override
    protected void move() {
        try {
            Point pos = movement.run();
            x = pos.x;
            y = pos.y;
        } catch (Exception e) {
            e.printStackTrace();
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
            scanPort();

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

    /**
     * There are 2 keys set to choose, which labeled 1 and 2.
     * @param scene
     * @param keys_set
     */
    public void setOnPlayerEvents(Scene scene, int keys_set) {
        if (keys_set == 1) {
            scene.addEventHandler(KeyEvent.KEY_PRESSED, set1_a);
            scene.addEventHandler(KeyEvent.KEY_RELEASED, set1_b);
            scene.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
                if (keyEvent.getCode().equals(KeyCode.BRACERIGHT)) useFlashItem();
            });
        }

        if (keys_set == 2) {
            scene.addEventHandler(KeyEvent.KEY_PRESSED,set2_a);
            scene.addEventHandler(KeyEvent.KEY_RELEASED,set2_b);
            scene.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
                if (keyEvent.getCode().equals(KeyCode.E)) useFlashItem();
            });

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
