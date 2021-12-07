package uet.oop.bomberman.Motion;

import uet.oop.bomberman.Base.GameMap;
import uet.oop.bomberman.Base.Point;
import uet.oop.bomberman.BombermanGame;
import uet.oop.bomberman.entities.Entity;
import uet.oop.bomberman.entities.MovableEntities.MovableEntity;
import uet.oop.bomberman.graphics.Sprite;

import java.util.HashSet;
import java.util.Set;

public abstract class Movement {
    public static final int RIGHT = 0;
    public static final int LEFT = 1;
    public static final int UP = 2;
    public static final int DOWN = 3;
    public static final int FREEZE = 4;

    protected double pixels_foreach_unitOfMoveLength = 1;
    /**
     * Speed is the number of units of move length a character can move continuously
     * per 1 second.
     * Maximum value is about 70.
     */
    protected int speed;
    /** period = 1 / speed, is the number of seconds needed to move 1 unit. */
    protected double period;
    /** calc_period is a supporting variable, used to make new time mark right at the time a new move is done. */
    protected double calc_period = 0;

    protected int x, y;
    protected int direction = FREEZE;

    //not used
    protected Double distance_to_stillObject_ahead = Double.MAX_VALUE;

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

    //Unused
    public int getSpeed() {
        return speed;
    }

    //Unused
    public void setSpeed(int speed) {
        this.speed = speed;
        this.period = (double) 1 / speed;
    }

    public void speedUp(int n) {
        this.speed += n;
        this.period = (double) 1 / this.speed;
    }

    public void stop() {
        this.direction = Movement.FREEZE;
    }

    //Based on direction.
    protected Entity getObjectAhead() {
        Entity e;
//need to consider if the moving entity is being blocked partially or completely...
        switch (this.direction) {
            case RIGHT -> {
                e = GameMap.getObjectAt(null, x + Sprite.SCALED_SIZE, y);
                if (e != null) return e;

                e = GameMap.getObjectAt(null, x + Sprite.SCALED_SIZE, y + Sprite.SCALED_SIZE - 1);
                if (e != null) return e;
            }

            case LEFT -> {
                e = GameMap.getObjectAt(null, x - 1, y);
                if (e != null) return e;

                e = GameMap.getObjectAt(null, x - 1, y + Sprite.SCALED_SIZE - 1);
                if (e != null) return e;
            }

            case DOWN -> {
                e = GameMap.getObjectAt(null, x, y + Sprite.SCALED_SIZE);
                if (e != null) return e;

                e = GameMap.getObjectAt(null, x + Sprite.SCALED_SIZE - 1, y + Sprite.SCALED_SIZE);
                if (e != null) return e;
            }

            case UP -> {
                e = GameMap.getObjectAt(null, x, y - 1);
                if (e != null) return e;

                e = GameMap.getObjectAt(null, x + Sprite.SCALED_SIZE - 1, y - 1);
                if (e != null) return e;
            }
        }

        return null;
    }

    //8 cases at all.
    protected void moveDiagonally(int pixels_foreach_move) {
        if (diagonalDirection == null) {
            System.out.println("CANT MOVE DIAGONALLY");
            return;
        }

        if (diagonalDirection.x == Movement.LEFT) {
            switch (diagonalDirection.y) {
                case Movement.DOWN -> { //moving LEFT DOWN
                    this.x -= pixels_foreach_move;
                    this.y += pixels_foreach_move;
                }

                case Movement.UP -> { //moving LEFT UP
                    this.x -= pixels_foreach_move;
                    this.y -= pixels_foreach_move;
                }
            }
        }
        else if (diagonalDirection.x == Movement.RIGHT) {
            switch (diagonalDirection.y) {
                case Movement.DOWN -> { //moving RIGHT DOWN
                    this.x += pixels_foreach_move;
                    this.y += pixels_foreach_move;
                }

                case Movement.UP -> { //moving RIGHT UP
                    this.x += pixels_foreach_move;
                    this.y -= pixels_foreach_move;
                }
            }
        }
    }

    //not used
    protected void moveCLoserToObject() { //need to supplement distance_to_movableObject
        distance_to_stillObject_ahead = GameMap.distanceToStillObjectAhead(this.entity);

        if (distance_to_stillObject_ahead >= this.pixels_foreach_unitOfMoveLength) return;

        switch (direction) {
            case UP -> y -= distance_to_stillObject_ahead;
            case LEFT -> x -= distance_to_stillObject_ahead;
            case DOWN -> y += distance_to_stillObject_ahead;
            case RIGHT -> x += distance_to_stillObject_ahead;
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
            distance_to_stillObject_ahead = GameMap.distanceToStillObjectAhead(this.entity);

            if ((direction == LEFT || direction == UP)
                    && (distance_to_stillObject_ahead >= 0
                    && distance_to_stillObject_ahead < moving_distance)) {
//                System.out.println("Still object: " + distance_to_stillObject_ahead + " and direction: " + direction);
                return true;
            } else if ((direction == RIGHT || direction == DOWN)
                    && (distance_to_stillObject_ahead >= Sprite.SCALED_SIZE
                    && distance_to_stillObject_ahead < moving_distance + Sprite.SCALED_SIZE)) {
//                System.out.println("Still object: " + distance_to_stillObject_ahead + " and direction: " + direction);
                return true;
            }

            //if 1(or more) movable object is standing in movement range_unit:
            distance_to_stillObject_ahead = GameMap.distanceToMovableEntityAhead(this.entity,
                    startingPoint, destinationPoint);

            if ((direction == LEFT || direction == UP)
                    && (distance_to_stillObject_ahead >= 0
                    && distance_to_stillObject_ahead < moving_distance)) {
//                System.out.println("Enemy: " + distance_to_stillObject_ahead + " and direction: " + direction);
                return true;
            } else if ((direction == RIGHT || direction == DOWN)
                    && (distance_to_stillObject_ahead >= Sprite.SCALED_SIZE
                    && distance_to_stillObject_ahead < moving_distance + Sprite.SCALED_SIZE)) {
//                System.out.println("Enemy: " + distance_to_stillObject_ahead + " and direction: " + direction);
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
    // Unused
    protected boolean canMove(int direction) {
        return false;
    }

    //Based on direction.
    public Set<Entity> getSteppedOverObjects(MovableEntity except_entity) {
        HashSet<Entity> result = new HashSet<>();
        Entity entity = null;

        switch (this.direction) {
            case UP -> {
                entity = GameMap.getObjectAt(except_entity, this.x, this.y);
                if (entity != null && !entity.equals(except_entity)) result.add(entity);

                entity = GameMap.getObjectAt(except_entity, this.x + Sprite.SCALED_SIZE - 1, this.y);
                if (entity != null && !entity.equals(except_entity)) result.add(entity);
            }

            case LEFT -> {
                entity = GameMap.getObjectAt(except_entity, this.x, this.y);
                if (entity != null && !entity.equals(except_entity)) result.add(entity);

                entity = GameMap.getObjectAt(except_entity, this.x, this.y + Sprite.SCALED_SIZE - 1);
                if (entity != null && !entity.equals(except_entity)) result.add(entity);
            }

            case DOWN -> {
                entity = GameMap.getObjectAt(except_entity, this.x, this.y + Sprite.SCALED_SIZE - 1);
                if (entity != null && !entity.equals(except_entity)) result.add(entity);

                entity = GameMap.getObjectAt(except_entity, this.x + Sprite.SCALED_SIZE - 1,
                        this.y + Sprite.SCALED_SIZE - 1);
                if (entity != null && !entity.equals(except_entity)) result.add(entity);
            }

            case RIGHT -> {
                entity = GameMap.getObjectAt(except_entity, this.x + Sprite.SCALED_SIZE - 1, this.y);
                if (entity != null && !entity.equals(except_entity)) result.add(entity);

                entity = GameMap.getObjectAt(except_entity, this.x + Sprite.SCALED_SIZE - 1,
                                this.y + Sprite.SCALED_SIZE - 1);
                if (entity != null && !entity.equals(except_entity)) result.add(entity);
            }
        }

        return result;
    }

    public Set<Entity> getObjectsAhead() {
        HashSet<Entity> result = new HashSet<>();
        Entity entity = null;

        switch (this.direction) {
            case UP -> {
                entity = GameMap.getObjectAt(null, this.x, this.y - 1);
                if (entity != null) result.add(entity);

                entity = GameMap.getObjectAt(null, this.x + Sprite.SCALED_SIZE - 1,
                        this.y - 1);
                if (entity != null) result.add(entity);
            }

            case LEFT -> {
                entity = GameMap.getObjectAt(null, this.x - 1, this.y);
                if (entity != null) result.add(entity);

                entity = GameMap.getObjectAt(null, this.x - 1,
                        this.y + Sprite.SCALED_SIZE - 1);
                if (entity != null) result.add(entity);
            }

            case DOWN -> {
                entity = GameMap.getObjectAt(null, this.x, this.y + Sprite.SCALED_SIZE);
                if (entity != null) result.add(entity);

                entity = GameMap.getObjectAt(null, this.x + Sprite.SCALED_SIZE - 1,
                        this.y + Sprite.SCALED_SIZE);
                if (entity != null) result.add(entity);
            }

            case RIGHT -> {
                entity = GameMap.getObjectAt(null, this.x + Sprite.SCALED_SIZE, this.y);
                if (entity != null) result.add(entity);

                entity = GameMap.getObjectAt(null, this.x + Sprite.SCALED_SIZE,
                                this.y + Sprite.SCALED_SIZE - 1);
                if (entity != null) result.add(entity);
            }
        }

        return result;
    }

    public boolean removeOwner() {
        this.entity = null;
        return true;
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
