package uet.oop.bomberman.Base;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import javafx.scene.canvas.GraphicsContext;
import uet.oop.bomberman.BombermanGame;
import uet.oop.bomberman.entities.Motion.Movement;
import uet.oop.bomberman.entities.*;
import uet.oop.bomberman.entities.MovableEntities.Balloon;
import uet.oop.bomberman.entities.MovableEntities.Bomber;
import uet.oop.bomberman.entities.MovableEntities.MovableEntity;
import uet.oop.bomberman.entities.MovableEntities.OneAl;
import uet.oop.bomberman.entities.StillEntities.*;
import uet.oop.bomberman.graphics.Sprite;

public class GameMap {
    private int level = 1;
    private static int initialPlayerSpeed = 500;
    private static int initialEnemySpeed = 40;

    private static Bomber bomber;

    private static char[][] baseMap = new char[BombermanGame.HEIGHT][BombermanGame.WIDTH];

    private static List<Entity> grass = new ArrayList<>();
    private static List<Entity> walls = new ArrayList<>();
    private static List<MovableEntity> movableEntities = new ArrayList<>();
    private static List<BreakableStillObject> breakableStillObjects = new ArrayList<>();
    private static List<Bomb> bombs = new ArrayList<>();


    public static List<Entity> getWalls() {
        return walls;
    }

    public static List<BreakableStillObject> getBreakableStillObjects() {
        return breakableStillObjects;
    }

    public static BreakableStillObject getBreakableStillObjectAt(int x, int y) {
        for (BreakableStillObject object : breakableStillObjects) {
            Point objectRange_from = new Point(object.getX(), object.getY());
            Point objectRange_to = new Point(object.getX() + Sprite.SCALED_SIZE - 1,
                    object.getY() + Sprite.SCALED_SIZE - 1);

            if ( (objectRange_from.x <= x && x <= objectRange_to.x)
                    && (objectRange_from.y <= y && y <= objectRange_to.y) ) {
                return object;
            }
        }
        return null;
    }

    public static Entity getStillObjectAt(int x, int y) {
        for (Entity wall : walls) {
            Point objectRange_from = new Point(wall.getX(), wall.getY());
            Point objectRange_to = new Point(wall.getX() + Sprite.SCALED_SIZE - 1,
                    wall.getY() + Sprite.SCALED_SIZE - 1);

            if ( (objectRange_from.x <= x && x <= objectRange_to.x)
                    && (objectRange_from.y <= y && y <= objectRange_to.y) ) {
                return wall;
            }
        }

        return getBreakableStillObjectAt(x, y);
    }
    //using the 2D array base_map
    public static boolean containsStillObjectAt(int x, int y) {
        int xUnit = x / Sprite.SCALED_SIZE;
        int yUnit = y / Sprite.SCALED_SIZE;

        if (baseMap[yUnit][xUnit] == '#' //Wall
                || baseMap[yUnit][xUnit] == '*' //Brick
                || baseMap[yUnit][xUnit] == 'x') { //Portal
            return true;
        }

        return false;
    }

    public static void removeStillObject(BreakableStillObject obj) {
        if (breakableStillObjects.remove(obj)) {
            int xUnit = obj.getX() / Sprite.SCALED_SIZE;
            int yUnit = obj.getY() / Sprite.SCALED_SIZE;
            baseMap[yUnit][xUnit] = ' ';
        }
    }

    public static void removeStillObjectAt(int x, int y) {
        //never remove Wall
        BreakableStillObject object = getBreakableStillObjectAt(x, y);

        if (object == null) return;

        if (breakableStillObjects.remove(object)) {
            int xUnit = object.getX() / Sprite.SCALED_SIZE;
            int yUnit = object.getY() / Sprite.SCALED_SIZE;
            baseMap[yUnit][xUnit] = ' ';
        }
    }


    public static List<MovableEntity> getMovableEntities() {
        return movableEntities;
    }

    public static MovableEntity getMovableEntityAt(MovableEntity except_movableEntity, int x, int y) {
        for (MovableEntity movableEntity : movableEntities) {
            if (movableEntity.equals(except_movableEntity)) continue;

            Point objectRange_from = new Point(movableEntity.getX(), movableEntity.getY());
            Point objectRange_to = new Point(movableEntity.getX() + Sprite.SCALED_SIZE - 1,
                    movableEntity.getY() + Sprite.SCALED_SIZE - 1);

            if ( (objectRange_from.x <= x && x <= objectRange_to.x)
                    && (objectRange_from.y <= y && y <= objectRange_to.y) ) {
                return movableEntity;
            }
        }
        return null;
    }
    //not used, may be removed
    public static void removeMovableEntityAt(int x, int y) {
        movableEntities.remove(getMovableEntityAt(null, x, y));
    }


    public static List<Bomb> getBombs() {
        return bombs;
    }

    public static Bomb getBombAt(int x, int y) {
        for (Bomb bomb : bombs) {
            Point objectRange_from = new Point(bomb.getX(), bomb.getY());
            Point objectRange_to = new Point(bomb.getX() + Sprite.SCALED_SIZE - 1,
                    bomb.getY() + Sprite.SCALED_SIZE - 1);

            if ( (objectRange_from.x <= x && x <= objectRange_to.x)
                    && (objectRange_from.y <= y && y <= objectRange_to.y) ) {
                return bomb;
            }
        }
        return null;
    }

    public static boolean addBomb(Bomb bomb) {
        return bombs.add(bomb);
    }

    public static boolean removeBomb(Bomb bomb) {
        return bombs.remove(bomb);
    }


    public static Entity getObjectAt(int x, int y) {
        Entity e;

        e = getMovableEntityAt(null, x, y);
        if (e != null) return e;

        e = getStillObjectAt(x, y);
        if (e != null) return e;

        e = getBombAt(x, y);

        return e;
    }

    public static boolean containsObjectAt(MovableEntity except_movableEntity, int x, int y) {
        return getMovableEntityAt(except_movableEntity, x, y) != null
                || getStillObjectAt(x, y) != null
                || getBombAt(x, y) != null;
    }
    //a soft object is an object that can be removed.
    public static boolean containsSoftObjectAt(MovableEntity except_movableEntity, int x, int y) {
        return getMovableEntityAt(except_movableEntity, x, y) != null
                || getBreakableStillObjectAt(x, y) != null;
    }
    //not used, may be removed
    public static void removeObjectAt(int x, int y) {
        if (movableEntities.remove(getMovableEntityAt(null, x, y))) return;
        if (bombs.remove(getBombAt(x, y))) return;
        removeStillObjectAt(x, y);
    }

    public static void removeObject(Entity entity) {
        if (entity instanceof MovableEntity) {
            movableEntities.remove(entity);
            return;
        }

        if (entity instanceof Bomb) {
            bombs.remove(entity);
            return;
        }

        removeStillObject((BreakableStillObject) entity);
    }

    //initial entities from text map
    public static Bomber create() {
        try {
            FileInputStream in = new FileInputStream("res/levels/Level1.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line = reader.readLine();
            if (line != null) {
//                String[] splitString = line.split(" ");
                for (int i = 0; (line = reader.readLine()) != null && !line.isBlank(); i++) {
                    baseMap[i] = line.toCharArray();
                }
            }

            for (int i = 0; i < BombermanGame.WIDTH; i++) {
                for (int j = 0; j < BombermanGame.HEIGHT; j++) {

                    if (baseMap[j][i] != '#') { //if being not Wall
                        grass.add(new Grass(i, j, Sprite.grass.getFxImage()));
                    }

                    switch (baseMap[j][i]) {
                        case '#' -> walls.add(new Wall(i, j, Sprite.wall.getFxImage()));
                        case '*' -> breakableStillObjects.add(new Brick(i, j, Sprite.brick.getFxImage()));
                        case 'x' -> breakableStillObjects.add(new Portal(i, j, Sprite.portal.getFxImage()));


                        case 'p' -> {
                            bomber = new Bomber(i, j, initialPlayerSpeed, Sprite.player_up.getFxImage());
                            movableEntities.add(bomber);
                        }
                        case '1' -> movableEntities.add(new Balloon(i, j, initialEnemySpeed,
                                Sprite.balloom_right1.getFxImage()));
                        case '2' -> movableEntities.add(new OneAl(i, j, initialEnemySpeed,
                                Sprite.oneal_right1.getFxImage(), 60));
                    }
                }
            }
            for (char[] chars : baseMap) System.out.println(chars);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return bomber;
    }

    //used in the method render() of main class.
    public static void render(GraphicsContext graphicsContext) {
        grass.forEach(v -> v.render(graphicsContext));
        walls.forEach(v -> v.render(graphicsContext));
        breakableStillObjects .forEach(v -> v.render(graphicsContext));
        bombs.forEach(v -> v.render(graphicsContext));
        movableEntities.forEach(v -> v.render(graphicsContext));
    }

    //3 entity updating methods below are used in the method update() of main class.
    public static void updateStillObjects() {
        BreakableStillObject object = null;
        int index = 0;

        while(index < GameMap.getBreakableStillObjects().size()) {
            for (; index < GameMap.getBreakableStillObjects().size(); index++) {
                object = GameMap.getBreakableStillObjects().get(index);

                if (object.isBroken()) object.update();

                if (object.isDeleted()) break;
            }

            if (object.isDeleted()) removeObject(object);
        }
    }

    public static void updateMovableEntities() {
        MovableEntity entity = null;
        int index = 0;

        while(index < GameMap.getMovableEntities().size()) {
            for (; index < GameMap.getMovableEntities().size(); index++) {
                entity = GameMap.getMovableEntities().get(index);

                entity.update();

                if (entity.isDeleted()) break;
            }

            if (entity.isDeleted()) removeObject(entity);
        }
    }

    public static void updateBomb() {
        Bomb bomb = null;
        int index = 0;

        while (index < GameMap.getBombs().size()) {
            for (; index < GameMap.getBombs().size(); index++) {
                bomb = GameMap.getBombs().get(index);

                bomb.update();

                if (bomb.isDone()) break;
            }

            if (bomb.isDone()) {
                GameMap.removeBomb(bomb);
                bomber.increaseOneBomb();
            }
        }
    }



    /**
     * Find the distance to the nearest still object found ahead.
     * Only available for horizontal or vertical motion.
     * @return the positive value based on horizontal or vertical direction.
     *      This value is equal to
     *          +, MAX_VALUE if no still object found in the range_unit,
     *          +, 0 (minimum) if touching a still object from the beginning while moving LEFT or UP.
     *          +, 32 if touching a still object from the beginning while moving RIGHT or DOWN.
     */
    public static int distanceToStillObjectAhead(Point from, Point to) throws NullPointerException {
        int direction = determinesDirection(from, to);

        switch (direction) {
            case Movement.RIGHT -> {
                final int i_yUNIT_SOURCE = from.y / Sprite.SCALED_SIZE;
                final double d_xUNIT_DEST = (double) to.x / Sprite.SCALED_SIZE;

                for (int xUnit = (from.x / Sprite.SCALED_SIZE) + 1;
                        xUnit <= d_xUNIT_DEST + 1 - 1 / Sprite.SCALED_SIZE; xUnit++) {

                    if (getStillObjectAt(xUnit * Sprite.SCALED_SIZE,
                                            i_yUNIT_SOURCE * Sprite.SCALED_SIZE) != null) {
                        return xUnit * Sprite.SCALED_SIZE - from.x;
                    }

                    // if starting point's yUnit is a decimal, we'll handle the point
                    //  besides the above point because this entity lay on that too.
                    if (from.y % Sprite.SCALED_SIZE != 0) {
                        if (getStillObjectAt(xUnit * Sprite.SCALED_SIZE,
                                                (i_yUNIT_SOURCE + 1) * Sprite.SCALED_SIZE) != null) {
                            return xUnit * Sprite.SCALED_SIZE - from.x;
                        }
                    }
                }
            }

            case Movement.LEFT -> {
                final int i_Y_UNIT = from.y / Sprite.SCALED_SIZE;
                final double d_X_UNIT_DEST = (double) to.x / Sprite.SCALED_SIZE;

                for (int xUnit = (from.x / Sprite.SCALED_SIZE); xUnit - 1 / Sprite.SCALED_SIZE >= d_X_UNIT_DEST; xUnit--) {

                    if (getStillObjectAt( xUnit * Sprite.SCALED_SIZE - 1,
                                            i_Y_UNIT * Sprite.SCALED_SIZE) != null) {
                        return from.x - xUnit * Sprite.SCALED_SIZE;
                    }

                    // if starting point's yUnit is a decimal, we'll handle the point
                    //  besides the above point because this entity lay on that too.
                    if (from.y % Sprite.SCALED_SIZE != 0) {
                        if (getStillObjectAt( xUnit  * Sprite.SCALED_SIZE - 1,
                                                (i_Y_UNIT + 1) * Sprite.SCALED_SIZE) != null) {
                            return from.x - xUnit * Sprite.SCALED_SIZE;
                        }
                    }
                }

            }

            case Movement.DOWN -> {
                final int i_X_UNIT = from.x / Sprite.SCALED_SIZE;
                final double d_Y_UNIT_DEST = (double) to.y / Sprite.SCALED_SIZE;

                for (int yUnit = (from.y / Sprite.SCALED_SIZE) + 1;
                        yUnit <= d_Y_UNIT_DEST + 1 - 1 / Sprite.SCALED_SIZE; yUnit++) {

                    if (getStillObjectAt(i_X_UNIT * Sprite.SCALED_SIZE,
                                            yUnit * Sprite.SCALED_SIZE) != null) {
                        return yUnit * Sprite.SCALED_SIZE - from.y;
                    }

                    // if starting point's xUnit is a decimal, we'll handle the point
                    //  besides the above point because this entity lay on that too.
                    if (from.x % Sprite.SCALED_SIZE != 0) {
                        if (getStillObjectAt((i_X_UNIT + 1) * Sprite.SCALED_SIZE,
                                                yUnit * Sprite.SCALED_SIZE) != null) {
                            return yUnit * Sprite.SCALED_SIZE - from.y;
                        }
                    }
                }
            }

            case Movement.UP -> {
                final int i_X_UNIT = from.x / Sprite.SCALED_SIZE;
                final double d_Y_UNIT_DEST = (double) to.y / Sprite.SCALED_SIZE;

                for (int yUnit = (from.y / Sprite.SCALED_SIZE);
                        yUnit - 1 / Sprite.SCALED_SIZE >= d_Y_UNIT_DEST; yUnit--) {

                    if (getStillObjectAt(i_X_UNIT * Sprite.SCALED_SIZE,
                                            yUnit * Sprite.SCALED_SIZE - 1) != null) {
                        return from.y - yUnit * Sprite.SCALED_SIZE;
                    }

                    // if starting point's xUnit is a decimal, we'll handle the point
                    //  besides the above point because this entity lay on that too.
                    if (from.x % Sprite.SCALED_SIZE != 0) {
                        if (getStillObjectAt((i_X_UNIT + 1) * Sprite.SCALED_SIZE,
                                                yUnit * Sprite.SCALED_SIZE - 1) != null) {
                            return from.y - yUnit * Sprite.SCALED_SIZE;
                        }
                    }
                }
            }
        }
        return Integer.MAX_VALUE;
    }

    /**
     * Find the distance to the nearest movable object found ahead.
     * Only available for horizontal or vertical motion.
     * @return the positive value based on horizontal or vertical direction.
     *      This value is equal to
     *          +, MAX_VALUE if no still object found in the range_unit,
     *          +, 0 (minimum) if touching a still object from the beginning while moving LEFT or UP.
     *          +, 32 if touching a still object from the beginning while moving RIGHT or DOWN.
     */
    public static int distanceToMovableEntityAhead(Entity source_entity, Point from, Point to) throws NullPointerException {
        int direction = determinesDirection(from, to);
        int minDistance = Integer.MAX_VALUE;

        switch (direction) {
            case Movement.UP -> {
                for(MovableEntity movableEntity : movableEntities) {
                    if (movableEntity.equals(source_entity)) continue;

                    if ( Math.abs(movableEntity.getX() - from.x) < Sprite.SCALED_SIZE
                            && (movableEntity.getY() + Sprite.SCALED_SIZE >= to.y
                                && movableEntity.getY() <= from.y) ) {

                        int distance = from.y - movableEntity.getY();
                        if (minDistance > distance) {
                            minDistance = distance;
                        }
                    }
                }
                return minDistance;
            }

            case Movement.DOWN -> {
                for(MovableEntity movableEntity : movableEntities) {
                    if (movableEntity.equals(source_entity)) continue;

                    if ( Math.abs(movableEntity.getX() - from.x) < Sprite.SCALED_SIZE
                            && (movableEntity.getY() >= from.y
                                && movableEntity.getY() <= to.y + Sprite.SCALED_SIZE) ) {

                        int distance = movableEntity.getY() - from.y;
                        if (minDistance > distance) {
                            minDistance = distance;
                        }
                    }
                }
                return minDistance;
            }

            case Movement.LEFT -> {
                for(MovableEntity movableEntity : movableEntities) {
                    if (movableEntity.equals(source_entity)) continue;

                    if ( Math.abs(movableEntity.getY() - from.y) < Sprite.SCALED_SIZE
                            && ( movableEntity.getY() + Sprite.SCALED_SIZE >= to.y
                                && movableEntity.getX() <= from.x ) ) {

                        int distance = from.x - movableEntity.getX();
                        if (minDistance > distance) {
                            minDistance = distance;
                        }
                    }
                }
                return minDistance;
            }

            case Movement.RIGHT -> {
                for(MovableEntity movableEntity : movableEntities) {
                    if (movableEntity.equals(source_entity)) continue;

                    if ( Math.abs(movableEntity.getY() - from.y) < Sprite.SCALED_SIZE
                            && ( movableEntity.getX() >= from.x
                                && movableEntity.getX() <= to.x + Sprite.SCALED_SIZE ) ) {

                        int distance = movableEntity.getX() - from.x;
                        if (minDistance > distance) {
                            minDistance = distance;
                        }
                    }
                }
                return minDistance;
            }

        }

        return minDistance;
    }

    public static double distanceToPlayer(Point e) {
        double x_difference = Math.abs(e.x - bomber.getX());
        double y_difference = Math.abs(e.y - bomber.getY());

        return Math.sqrt(Math.pow(x_difference, 2) + Math.pow(y_difference, 2));
    }

    /**
     * Determine the direction which is based on starting point and destination point.
     * Available for 4 directions declared.
     * @param from
     * @param to
     * @return
     * @throws NullPointerException
     */
    private static int determinesDirection(Point from, Point to) throws NullPointerException {
        if (from == null || to == null) {
            throw new NullPointerException("starting point or destination point is null");
        }

        int direction = Movement.FREEZE;

        if (from.x == to.x) {
            if (from.y < to.y) {
                direction = Movement.DOWN;
            } else if (from.y > to.y) {
                direction = Movement.UP;
            }
        }

        if (from.y == to.y) {
            if (from.x < to.x) {
                direction = Movement.RIGHT;
            } else if (from.x > to.x){
                direction = Movement.LEFT;
            }
        }

        return direction;
    }
}
