package uet.oop.bomberman.entities.Motion;

import uet.oop.bomberman.Base.GameMap;
import uet.oop.bomberman.Base.Point;
import uet.oop.bomberman.BombermanGame;
import uet.oop.bomberman.entities.MovableEntities.MovableEntity;
import uet.oop.bomberman.graphics.Sprite;

public abstract class Movement {
    public static final int RIGHT = 0;
    public static final int LEFT = 1;
    public static final int UP = 2;
    public static final int DOWN = 3;
    public static final int FREEZE = 4;

    protected int speed;
    protected double period;
    protected double calc_period = 0;

    protected int x, y;
    protected int direction = FREEZE;

    //not used
    protected int distance_to_object_ahead = Integer.MAX_VALUE;

    protected boolean justSpawnBomb = false;

    protected MovableEntity entity;

    //move concurrently (LEFT||RIGHT) && (UP||DOWN) 1 pixel.
    protected Point diagonalDirection;

    public Movement(MovableEntity entity, int x, int y, int speed) {
        this.entity = entity;
        this.x = x;
        this.y = y;
        this.speed = speed;

        this.period = (double) 1 / speed;
        this.calc_period = BombermanGame.getTime();
    }

    public int getDirection() {
        return this.direction;
    }

    public void setJustSpawnBomb(boolean state) {
        this.justSpawnBomb = state;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void speedUp() {
        this.speed += 2;
    }

    public void stop() {
        this.direction = Movement.FREEZE;
    }

    //8 cases at all.
    protected void moveDiagonally() {
        if (diagonalDirection == null) {
            System.out.println("CANT MOVE DIAGONALLY");
            return;
        }

        if (diagonalDirection.x == Movement.LEFT) {
            switch (diagonalDirection.y) {
                case Movement.DOWN -> { //moving LEFT DOWN
                    this.x--;
                    this.y++;
                }

                case Movement.UP -> { //moving LEFT UP
                    this.x--;
                    this.y--;
                }
            }
        }
        else if (diagonalDirection.x == Movement.RIGHT) {
            switch (diagonalDirection.y) {
                case Movement.DOWN -> { //moving RIGHT DOWN
                    this.x++;
                    this.y++;
                }

                case Movement.UP -> { //moving RIGHT UP
                    this.x++;
                    this.y--;
                }
            }
        }
    }

    //not used
    protected void getCLoserToObject() {
        if (distance_to_object_ahead > 0 && distance_to_object_ahead < 1) {
            switch (direction) {
                case UP -> y -= distance_to_object_ahead;
                case LEFT -> x -= distance_to_object_ahead;
            }
        }
        if (distance_to_object_ahead > Sprite.SCALED_SIZE
                && distance_to_object_ahead < Sprite.SCALED_SIZE + speed) {

            switch (direction) {
                case DOWN -> y += distance_to_object_ahead - Sprite.SCALED_SIZE;
                case RIGHT -> x += distance_to_object_ahead - Sprite.SCALED_SIZE;
            }
        }
    }

    //not used
    protected boolean hasCollision(int moving_distance) {
        Point startingPoint = new Point(x, y);
        Point destinationPoint = null;

        switch (direction) {
            case RIGHT -> destinationPoint = new Point(x + moving_distance, y);

            case LEFT -> destinationPoint = new Point(x - moving_distance, y);

            case DOWN -> destinationPoint = new Point(x, y + moving_distance);

            case UP -> destinationPoint = new Point(x, y - moving_distance);

            case FREEZE -> {
                return false;
            }
        }

        try {
            if (destinationPoint == null) {
                throw new NullPointerException("Destination point is not initialized.");
            }

            //if 1(or more) still object is standing in movement range_unit:
            distance_to_object_ahead = GameMap.distanceToStillObjectAhead(startingPoint, destinationPoint);

            if ((direction == LEFT || direction == UP)
                    && (distance_to_object_ahead >= 0
                    && distance_to_object_ahead < moving_distance)) {
//                System.out.println("Still object: " + distance_to_object_ahead + " and direction: " + direction);
                return true;
            } else if ((direction == RIGHT || direction == DOWN)
                    && (distance_to_object_ahead >= Sprite.SCALED_SIZE
                    && distance_to_object_ahead < moving_distance + Sprite.SCALED_SIZE)) {
//                System.out.println("Still object: " + distance_to_object_ahead + " and direction: " + direction);
                return true;
            }

            //if 1(or more) movable object is standing in movement range_unit:
            distance_to_object_ahead = GameMap.distanceToMovableEntityAhead(this.entity,
                    startingPoint, destinationPoint);

            if ((direction == LEFT || direction == UP)
                    && (distance_to_object_ahead >= 0
                    && distance_to_object_ahead < moving_distance)) {
//                System.out.println("Enemy: " + distance_to_object_ahead + " and direction: " + direction);
                return true;
            } else if ((direction == RIGHT || direction == DOWN)
                    && (distance_to_object_ahead >= Sprite.SCALED_SIZE
                    && distance_to_object_ahead < moving_distance + Sprite.SCALED_SIZE)) {
//                System.out.println("Enemy: " + distance_to_object_ahead + " and direction: " + direction);
                return true;
            }
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * @return false when the corresponding edge's odd of this object,
     * which is out of the blocking object's edge,
     * is larger than SCALED_SIZE / 2.
     */
    protected boolean isBlockedCompletely() {
        if ( ! isTouchingAnObjectAhead()) return false;

        switch (this.direction) {
            case RIGHT -> {
                int odd_y = this.y % Sprite.SCALED_SIZE;
                if (odd_y != 0) {
                    //Have 1 object ABOVE but don't have any object UNDER this entity.
                    if (GameMap.containsObjectAt(this.entity, this.x + Sprite.SCALED_SIZE, this.y)
                            && !GameMap.containsObjectAt(this.entity, this.x + Sprite.SCALED_SIZE,
                            this.y + Sprite.SCALED_SIZE - 1)
                            && odd_y > Sprite.SCALED_SIZE / 2) {

                        diagonalDirection = new Point(Movement.RIGHT, Movement.DOWN);
                        return false;
                    }

                    //Have 1 object UNDER but don't have any object ABOVE this entity.
                    if (GameMap.containsObjectAt(this.entity, this.x + Sprite.SCALED_SIZE,
                            this.y + Sprite.SCALED_SIZE - 1)
                            && !GameMap.containsObjectAt(this.entity, this.x + Sprite.SCALED_SIZE, this.y)
                            && odd_y < Sprite.SCALED_SIZE / 2) {

                        diagonalDirection = new Point(Movement.RIGHT, Movement.UP);
                        return false;
                    }
                }
            }

            case DOWN -> {
                int odd_x = this.x % Sprite.SCALED_SIZE;
                if (odd_x != 0) {
                    //Have 1 object to the LEFT but don't have any object to the RIGHT of this entity.
                    if (GameMap.containsObjectAt(this.entity, this.x, this.y + Sprite.SCALED_SIZE)
                            && !GameMap.containsObjectAt(this.entity, this.x + Sprite.SCALED_SIZE - 1,
                            this.y + Sprite.SCALED_SIZE)
                            && odd_x > Sprite.SCALED_SIZE / 2) {

                        diagonalDirection = new Point(Movement.RIGHT, Movement.DOWN);
                        return false;
                    }

                    //Have 1 object to the RIGHT but don't have any object to the LEFT of this entity.
                    if (GameMap.containsObjectAt(this.entity, this.x + Sprite.SCALED_SIZE - 1,
                            this.y + Sprite.SCALED_SIZE)
                            && !GameMap.containsObjectAt(this.entity, this.x, this.y + Sprite.SCALED_SIZE)
                            && odd_x < Sprite.SCALED_SIZE / 2) {

                        diagonalDirection = new Point(Movement.LEFT, Movement.DOWN);
                        return false;
                    }
                }
            }

            case LEFT -> {
                int odd_y = this.y % Sprite.SCALED_SIZE;
                if (odd_y != 0) {
                    //Have 1 object ABOVE but don't have any object UNDER this entity.
                    if (GameMap.containsObjectAt(this.entity, this.x - 1, this.y)
                            && !GameMap.containsObjectAt(this.entity, this.x - 1, this.y + Sprite.SCALED_SIZE - 1)
                            && odd_y > Sprite.SCALED_SIZE / 2) {

                        diagonalDirection = new Point(Movement.LEFT, Movement.DOWN);
                        return false;
                    }

                    //Have 1 object UNDER but don't have any object ABOVE this entity.
                    if (GameMap.containsObjectAt(this.entity, this.x - 1, this.y + Sprite.SCALED_SIZE - 1)
                            && !GameMap.containsObjectAt(this.entity, this.x - 1, this.y)
                            && odd_y < Sprite.SCALED_SIZE / 2) {

                        diagonalDirection = new Point(Movement.LEFT, Movement.UP);
                        return false;
                    }
                }
            }

            case UP -> {
                int odd_x = this.x % Sprite.SCALED_SIZE;
                if (odd_x != 0) {
                    //Have 1 object to the LEFT but don't have any object to the RIGHT of this entity.
                    if (GameMap.containsObjectAt(this.entity, this.x, this.y - 1)
                            && !GameMap.containsObjectAt(this.entity, this.x + Sprite.SCALED_SIZE - 1,this.y - 1)
                            && odd_x > Sprite.SCALED_SIZE / 2) {

                        diagonalDirection = new Point(Movement.RIGHT, Movement.UP);
                        return false;
                    }

                    //Have 1 object to the RIGHT but don't have any object to the LEFT of this entity.
                    if (GameMap.containsObjectAt(this.entity, this.x + Sprite.SCALED_SIZE - 1, this.y - 1)
                            && !GameMap.containsObjectAt(this.entity, this.x, this.y - 1)
                            && odd_x < Sprite.SCALED_SIZE / 2) {

                        diagonalDirection = new Point(Movement.LEFT, Movement.UP);
                        return false;
                    }
                }
            }
        }

        return true;
    }

    //Based on direction.
    protected boolean isTouchingAnObjectAhead() {
        switch (this.direction) {
            case RIGHT -> {
                if (GameMap.containsObjectAt(this.entity, x + Sprite.SCALED_SIZE, y)
                        || GameMap.containsObjectAt(this.entity,
                                x + Sprite.SCALED_SIZE,
                                y + Sprite.SCALED_SIZE - 1)) {
                    return true;
                }
            }
            case LEFT -> {
                if (GameMap.containsObjectAt(this.entity, x - 1, y)
                        || GameMap.containsObjectAt(this.entity,
                                x - 1,
                                y + Sprite.SCALED_SIZE - 1)) {
                    return true;
                }
            }
            case DOWN -> {
                if (GameMap.containsObjectAt(this.entity, x, y + Sprite.SCALED_SIZE)
                        || GameMap.containsObjectAt(this.entity,
                                x + Sprite.SCALED_SIZE - 1,
                                y + Sprite.SCALED_SIZE)) {
                    return true;
                }
            }
            case UP -> {
                if (GameMap.containsObjectAt(this.entity, x, y - 1)
                        || GameMap.containsObjectAt(this.entity,
                                x + Sprite.SCALED_SIZE - 1,
                                y - 1)) {
                    return true;
                }
            }
        }

        return false;
    }

    //Based on direction.
    //Have the same codes to isTouchingAnObjectAhead() method.
    protected boolean canMove(int direction) {
        switch (direction) {
            case RIGHT -> {
                if (GameMap.containsObjectAt(this.entity, x + Sprite.SCALED_SIZE, y)
                        || GameMap.containsObjectAt(this.entity,
                                x + Sprite.SCALED_SIZE,
                                y + Sprite.SCALED_SIZE - 1)) {
                    return true;
                }
            }
            case LEFT -> {
                if (GameMap.containsObjectAt(this.entity, x - 1, y)
                        || GameMap.containsObjectAt(this.entity,
                                x - 1,
                                y + Sprite.SCALED_SIZE - 1)) {
                    return true;
                }
            }
            case DOWN -> {
                if (GameMap.containsObjectAt(this.entity, x, y + Sprite.SCALED_SIZE)
                        || GameMap.containsObjectAt(this.entity,
                                x + Sprite.SCALED_SIZE - 1,
                                y + Sprite.SCALED_SIZE)) {
                    return true;
                }
            }
            case UP -> {
                if (GameMap.containsObjectAt(this.entity, x, y - 1)
                        || GameMap.containsObjectAt(this.entity,
                                x + Sprite.SCALED_SIZE - 1,
                                y - 1)) {
                    return true;
                }
            }
        }

        return false;
    }

    //Based on direction.
    //not used
    protected boolean isOnStillObjectAhead() {
        switch (this.direction) {
            case UP -> {
                if (GameMap.containsObjectAt(this.entity, this.x, this.y)
                        || GameMap.containsObjectAt(this.entity, this.x + Sprite.SCALED_SIZE - 1, this.y)) {
                    return true;
                }
            }
            case LEFT -> {
                if (GameMap.containsObjectAt(this.entity, this.x, this.y)
                        || GameMap.containsObjectAt(this.entity, this.x, this.y + Sprite.SCALED_SIZE - 1)) {
                    return true;
                }
            }
            case DOWN -> {
                if (GameMap.containsObjectAt(this.entity, this.x, this.y + Sprite.SCALED_SIZE - 1)
                        || GameMap.containsObjectAt(this.entity, this.x + Sprite.SCALED_SIZE - 1,
                                this.y + Sprite.SCALED_SIZE - 1)) {
                    return true;
                }
            }
            case RIGHT -> {
                if (GameMap.containsObjectAt(this.entity, this.x + Sprite.SCALED_SIZE - 1, this.y)
                        || GameMap.containsObjectAt(this.entity, this.x + Sprite.SCALED_SIZE - 1,
                                this.y + Sprite.SCALED_SIZE - 1)) {
                    return true;
                }
            }
        }

        return false;
    }

    protected boolean isOnBomb() {
        Point player_from = new Point(x, y);
        Point player_to = new Point(x + Sprite.SCALED_SIZE - 1, y + Sprite.SCALED_SIZE - 1);

        for (int i = player_from.x; i <= player_to.x; i++) {
            for (int j = player_from.y; j <= player_to.y; j++) {
                if (GameMap.getBombAt(i, j) != null) return true;
            }
        }

        return false;
    }

    /**
     * @return couple of coordinates x and y after a move.
     */
    public abstract Point run();

    /**
     * Used with different purposes.
     */
    public abstract void setDirection(int d);
}
