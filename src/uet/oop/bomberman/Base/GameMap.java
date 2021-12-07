package uet.oop.bomberman.Base;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;

import uet.oop.bomberman.BombermanGame;
import uet.oop.bomberman.Motion.Movement;
import uet.oop.bomberman.entities.*;
import uet.oop.bomberman.entities.Bomb.Bomb;
import uet.oop.bomberman.entities.Item.Item;
import uet.oop.bomberman.entities.Item.PowerUpBombs;
import uet.oop.bomberman.entities.Item.PowerUpFlames;
import uet.oop.bomberman.entities.Item.PowerUpSpeed;
import uet.oop.bomberman.entities.MovableEntities.Balloon;
import uet.oop.bomberman.entities.MovableEntities.Bomber;
import uet.oop.bomberman.entities.MovableEntities.MovableEntity;
import uet.oop.bomberman.entities.MovableEntities.OneAl;
import uet.oop.bomberman.entities.StillEntities.*;
import uet.oop.bomberman.entities.StillEntities.StableStillObject.Grass;
import uet.oop.bomberman.entities.StillEntities.StableStillObject.Wall;
import uet.oop.bomberman.graphics.Sprite;

public class GameMap {
    private int level = 1;
    private static int initial_player_speed = 60;
    private static int initial_enemy_speed = 200;
    private static int oneal_approach_range = 100;


    private static char[][] baseMap = new char[BombermanGame.HEIGHT][BombermanGame.WIDTH];

    private static List<Bomber> players = new ArrayList<>();
    private static List<MovableEntity> bots = new ArrayList<>();
    private static List<MovableEntity> killed_enemy_list = new ArrayList<>();
    private static List<Item> items = new ArrayList<>();
    private static List<Entity> backGroundObjects = new ArrayList<>();
    private static List<Entity> walls = new ArrayList<>();
    private static List<BreakableStillObject> breakableStillObjects = new ArrayList<>();
    private static List<Bomb> bombs = new ArrayList<>();


    public GameMap(int level) {
        clearMap();
        create("level" + String.valueOf(level));
    }
    public static Bomber getPlayer2() {
        if (players.size() == 2) return players.get(1);
        else return null;
    }
    /**
     * Still Object(Wall, Brick, Portal)
     * Breakable still objects: Brick, Portal(transfer from this list to backGroundObjects list when broken)
     */
    public static List<Entity> getWalls() {
        return walls;
    }

    public static void addBackGroundObject(Entity entity) {
        backGroundObjects.add(entity);
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

    public static boolean removeStillObject(BreakableStillObject obj) {
        if (breakableStillObjects.contains(obj)) {
            int xUnit = obj.getX() / Sprite.SCALED_SIZE;
            int yUnit = obj.getY() / Sprite.SCALED_SIZE;
            baseMap[yUnit][xUnit] = ' ';
        }
        return breakableStillObjects.remove(obj);
    }

    public static boolean removeStillObjectAt(int x, int y) {
        //never remove Wall
        BreakableStillObject object = getBreakableStillObjectAt(x, y);

        if (breakableStillObjects.contains(object)) {
            int xUnit = object.getX() / Sprite.SCALED_SIZE;
            int yUnit = object.getY() / Sprite.SCALED_SIZE;
            baseMap[yUnit][xUnit] = ' ';
        }
        return breakableStillObjects.remove(object);
    }


    /**  Movable Entity  */
    public static void addBot(MovableEntity entity) {
        bots.add(entity);
    }

    public static List<MovableEntity> getMovableEntities() {
        List<MovableEntity> result = new ArrayList<>();
        result.addAll(bots);
        result.addAll(players);
        return result;
    }

    public static MovableEntity getMovableEntityAt(MovableEntity except_movableEntity, int x, int y) {
        for (MovableEntity movableEntity : bots) {
            if (movableEntity.equals(except_movableEntity)) continue;

            Point objectRange_from = new Point(movableEntity.getX(), movableEntity.getY());
            Point objectRange_to = new Point(movableEntity.getX() + Sprite.SCALED_SIZE - 1,
                    movableEntity.getY() + Sprite.SCALED_SIZE - 1);

            if ( (objectRange_from.x <= x && x <= objectRange_to.x)
                    && (objectRange_from.y <= y && y <= objectRange_to.y) ) {
                return movableEntity;
            }
        }

        for (Bomber player : players) {
            if (player.equals(except_movableEntity)) continue;

            Point objectRange_from = new Point(player.getX(), player.getY());
            Point objectRange_to = new Point(player.getX() + Sprite.SCALED_SIZE - 1,
                    player.getY() + Sprite.SCALED_SIZE - 1);

            if ( (objectRange_from.x <= x && x <= objectRange_to.x)
                    && (objectRange_from.y <= y && y <= objectRange_to.y) ) {
                return player;
            }
        }

        return null;
    }

    private static Bomber getPlayer(int ID) {
        for (Bomber player : players) {
            if (player.getID() == ID) return player;
        }
        return null;
    }

    public static List<Integer> getPlayerIDs() {
        List<Integer> result = new ArrayList<>();

        for (Bomber player : players) {
            result.add(player.getID());
        }

        return result;
    }

    public static void setPlayer(int number, Scene scene) throws NullPointerException {
        players.get(0).setOnPlayerEvents(scene, 1);

        if (number == 2) {
            Bomber player2 = new Bomber(generateRandomID(), 29, 11, initial_player_speed
            );

            //based on space and size of the map
            players.add(player2);
            player2.setOnPlayerEvents(scene, 2);
        }
    }

    //not used, may be removed
    public static boolean removeMovableEntityAt(int x, int y) {
        MovableEntity e = getMovableEntityAt(null, x, y);
        if (e != null) {
            if (e instanceof Bomber) return players.remove(e);
            else return bots.remove(e);
        }
        return false;
    }


    /**  Item  */
    public static List<Item> getItems() {
        return items;
    }

    public static Item getItemAt(int x, int y) {
        for (Item item : items) {
            Point item_from = new Point(item.getX(), item.getY());
            Point item_to = new Point(item.getX() + Sprite.SCALED_SIZE - 1,
                    item.getY() + Sprite.SCALED_SIZE - 1);

            if ( (item_from.x <= x && x <= item_to.x)
                    && (item_from.y <= y && y <= item_to.y) ) {
                return item;
            }
        }

        return null;
    }

    public static boolean removeItemAt(int x, int y) {
        return items.remove(getItemAt(x, y));
    }

    public static boolean removeItem(Item item) {
        return items.remove(item);
    }


    /**  Bomb  */
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

    /**
     * The bug rises explicitly by adding new bomb.
     * @param bomb
     * @return
     */
    public static Bomb addBomb(Bomb bomb) {
        for (Integer playerID : getPlayerIDs()) {
            if (playerID == bomb.getOwnerID()) {
                bombs.add(bomb);
                return bomb;
            }
        }
        System.out.println("The old bombers haven't been removed completely.");
        return null;
    }


    /**  Methods for common objects.  */
    public static Entity getObjectAt(MovableEntity except_movableEntity, int x, int y) {
        Entity e;

        e = getMovableEntityAt(except_movableEntity, x, y);
        if (e != null) return e;

        e = getStillObjectAt(x, y);
        if (e != null) return e;

        e = getBombAt(x, y);
        if (e != null) return e;

        e = getItemAt(x, y);
        return e;
    }

    public static boolean containsObjectAt(MovableEntity except_movableEntity, int x, int y) {
        return getMovableEntityAt(except_movableEntity, x, y) != null
                || getStillObjectAt(x, y) != null
                || getBombAt(x, y) != null;
    }

    public static boolean removeObject(Entity entity) {
        if (entity instanceof Bomber) {
            return players.remove(entity);
        }

        if (entity instanceof MovableEntity) { //means bot
            return bots.remove(entity);
        }

        if (entity instanceof Bomb) {
            return bombs.remove(entity);
        }

        return removeStillObject((BreakableStillObject) entity);
    }


    /**
     * Initial entities from text map.
     */
    public static void create(String level) {
        try {
            FileInputStream in = new FileInputStream("res/levels/" + level + ".txt");
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
                        backGroundObjects.add(new Grass(i, j));
                    }

                    switch (baseMap[j][i]) {
                        case '#' -> walls.add(new Wall(i, j));
                        case '*' -> breakableStillObjects.add(new Brick(i, j));
                        case 'x' -> breakableStillObjects.add(new Portal(i, j));

                        case 'b' -> items.add(new PowerUpBombs(i, j));
                        case 's' -> items.add(new PowerUpSpeed(i, j));
                        case 'f' -> items.add(new PowerUpFlames(i, j));

                        case 'p' -> players.add(new Bomber(generateRandomID(), i, j, initial_player_speed));
                        case '1' -> bots.add(new Balloon(i, j, initial_enemy_speed));
                        case '2' -> bots.add(new OneAl(i, j, initial_enemy_speed, oneal_approach_range));
                    }
                }
            }
            for (char[] chars : baseMap) System.out.println(chars);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void clearMap() {
        for (char[] chars : baseMap) {
            chars = null;
        }
        players.clear();
        System.out.println(players.size());
        bots.clear();
        killed_enemy_list.clear();
        items.clear();
        backGroundObjects.clear();
        walls.clear();
        breakableStillObjects.clear();
        bombs.clear();
    }
    //used in the method render() of main class.
    public static void render(GraphicsContext graphicsContext) {
        backGroundObjects.forEach(v -> v.render(graphicsContext));
        walls.forEach(v -> v.render(graphicsContext));
        items.forEach(v -> v.render(graphicsContext));
        bombs.forEach(v -> v.render(graphicsContext));
        breakableStillObjects .forEach(v -> v.render(graphicsContext));
        bots.forEach(v -> v.render(graphicsContext));
        players.forEach(v -> v.render(graphicsContext));
    }

    //3 entity updating methods below are used in the method update() of main class.
    public static void updateStillObjects() {
        BreakableStillObject object = null;
        int index = 0;

        while(index < GameMap.getBreakableStillObjects().size()) {
            for (; index < GameMap.getBreakableStillObjects().size(); index++) {
                object = GameMap.getBreakableStillObjects().get(index);

                if (object.isBroken()) object.update();

                if (object.isDeleted()) {
                    break;
                }
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

            if (entity != null && entity.isDeleted()) {
                removeObject(entity);
            }
        }
    }

    public static void updatePlayers(Scene scene) {
        Bomber player = null;
        int index = 0;

        while(index < players.size()) {
            for (; index < players.size(); index++) {
                player = players.get(index);

                player.update();

                if (player.isDeleted()) break;
            }

            if (player.isDeleted()) {
                player.removeHandler(scene, players.indexOf(player) + 1);
//                players.remove(player);
            }
        }
    }

    public static void updateBombs() throws NullPointerException {
        Bomb bomb = null;
        int index = 0;

        killed_enemy_list.clear();

        while (index < GameMap.getBombs().size()) {
            for (; index < GameMap.getBombs().size(); index++) {
                bomb = GameMap.getBombs().get(index);

                bomb.update();

                if (bomb.isDone()) break;
            }

            if (bomb.isDone()) {
                if (bombs.remove(bomb)) {
                    killed_enemy_list.addAll(bomb.getKilledEnemies());
                    Bomber owner = GameMap.getPlayer(bomb.getOwnerID());
                    if (owner != null) owner.takeBackOneBomb();
                }
            }
        }
    }

    public static List<MovableEntity> getKilledEnemies() {
        return new ArrayList<>(killed_enemy_list);
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
    public static double distanceToStillObjectAhead(MovableEntity entity) throws NullPointerException {
        Point from = new Point(entity.getX(), entity.getY());

        switch (entity.getDirection()) {
            case Movement.RIGHT -> {
                Entity obj;

                for (int _i_xUnit = entity.getX() / Sprite.SCALED_SIZE + 1; ; _i_xUnit++) {
                    //left-top angle of the object
                    obj = getStillObjectAt(_i_xUnit * Sprite.SCALED_SIZE, from.y);
                    if (obj != null) {
                        return _i_xUnit * Sprite.SCALED_SIZE - (entity.getX() + Sprite.SCALED_SIZE);
                    }

                    //left-bottom angle of the object
                    obj = getStillObjectAt(_i_xUnit * Sprite.SCALED_SIZE, from.y + Sprite.SCALED_SIZE - 1);
                    if (obj != null) {
                        return _i_xUnit * Sprite.SCALED_SIZE - (entity.getX() + Sprite.SCALED_SIZE);
                    }
                }
            }

            case Movement.LEFT -> {
                Entity obj;

                for (int _i_xUnit = entity.getX() / Sprite.SCALED_SIZE; ; _i_xUnit--) {
                    //left-top angle of the object
                    obj = getStillObjectAt(_i_xUnit * Sprite.SCALED_SIZE, from.y);
                    if (obj != null) {
                        return entity.getX() - (_i_xUnit + 1) * Sprite.SCALED_SIZE;
                    }

                    //left-bottom angle of the object
                    obj = getStillObjectAt(_i_xUnit * Sprite.SCALED_SIZE, from.y + Sprite.SCALED_SIZE - 1);
                    if (obj != null) {
                        return entity.getX() - (_i_xUnit + 1) * Sprite.SCALED_SIZE;
                    }
                }
            }

            case Movement.DOWN -> {
                Entity obj;

                for (int _i_yUnit = entity.getY() / Sprite.SCALED_SIZE + 1; ; _i_yUnit++) {
                    //left-top angle of the object
                    obj = getStillObjectAt(entity.getX(), _i_yUnit * Sprite.SCALED_SIZE);
                    if (obj != null) {
                        return _i_yUnit * Sprite.SCALED_SIZE - (entity.getY() + Sprite.SCALED_SIZE);
                    }

                    //right-top angle of the object
                    obj = getStillObjectAt(entity.getX() + Sprite.SCALED_SIZE - 1,
                            _i_yUnit * Sprite.SCALED_SIZE);
                    if (obj != null) {
                        return _i_yUnit * Sprite.SCALED_SIZE - (entity.getY() + Sprite.SCALED_SIZE);
                    }
                }
            }

            case Movement.UP -> {
                Entity obj;

                for (int _i_yUnit = entity.getY() / Sprite.SCALED_SIZE ; ; _i_yUnit--) {
                    //left-top angle of the object
                    obj = getStillObjectAt(entity.getX(), _i_yUnit * Sprite.SCALED_SIZE);
                    if (obj != null) {
                        return entity.getY() - (_i_yUnit + 1) * Sprite.SCALED_SIZE;
                    }

                    //right-top angle of the object
                    obj = getStillObjectAt(entity.getX() + Sprite.SCALED_SIZE - 1,
                            _i_yUnit * Sprite.SCALED_SIZE);
                    if (obj != null) {
                        return entity.getY() - (_i_yUnit + 1) * Sprite.SCALED_SIZE;
                    }
                }
            }
        }

        return Double.MAX_VALUE;
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
    public static double distanceToMovableEntityAhead(Entity source_entity, Point from, Point to)
            throws NullPointerException {
        int direction = determineDirection(from, to);
        double minDistance = Double.MAX_VALUE;

        switch (direction) {
            case Movement.UP -> {
                for(MovableEntity movableEntity : bots) {
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
                for(MovableEntity movableEntity : bots) {
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
                for(MovableEntity movableEntity : bots) {
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
                for(MovableEntity movableEntity : bots) {
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

    public static double[] distanceToPlayers(Point e) throws NullPointerException {
        double[] distances = new double[players.size()];

        for (int i = 0; i < players.size() ; i++) {
            double x_difference = Math.abs(e.x - players.get(i).getX());
            double y_difference = Math.abs(e.y - players.get(i).getY());

            distances[i] = Math.sqrt(Math.pow(x_difference, 2) + Math.pow(y_difference, 2));
        }

        return distances;
    }

    /**
     * Determine the direction which is based on starting point and destination point.
     * Available for 4 directions declared.
     * @param from
     * @param to
     * @return
     * @throws NullPointerException
     */
    private static int determineDirection(Point from, Point to) throws NullPointerException {
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

    private static int generateRandomID() {
        Random random = new Random();
        int result;

        do {
            result = random.nextInt(100000, 999999);
        } while (getPlayerIDs().contains(result));

        return result;
    }
}
