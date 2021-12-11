package uet.oop.bomberman.Base;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;

import uet.oop.bomberman.BombermanGame;
import uet.oop.bomberman.Menu;
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
    private static final int PLAYER_SPEED = 60;
    private final int ENEMY_SPEED;
    private final int ONEAL_PURSUIT_RANGE;


    private static char[][] baseMap = new char[BombermanGame.HEIGHT][BombermanGame.WIDTH];

    private static List<Bomber> players = new ArrayList<>();
    private static List<MovableEntity> bots = new ArrayList<>();
    private static List<MovableEntity> killed_enemy_list = new ArrayList<>();
    private static List<Item> items = new ArrayList<>();
    private static List<Entity> backGroundObjects = new ArrayList<>();
    private static List<Entity> walls = new ArrayList<>();
    private static List<BreakableStillObject> breakableStillObjects = new ArrayList<>();
    private static List<Bomb> bombs = new ArrayList<>();


    public GameMap(int level, Scene scene) {
        //        PLAYER_SPEED = 50 + 3 * level;
        ENEMY_SPEED = 45 + 5 * level;
        ONEAL_PURSUIT_RANGE = 6 + level;

        clearMap();
        create(level);
        setPlayer(Menu.getPlayersNumber(), scene);
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
        return identifyObjectAt(x, y, breakableStillObjects);
    }

    public static Entity getStillObjectAt(int x, int y) {
        Entity w = identifyObjectAt(x, y, walls);
        if (w != null) return w;

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


    /**  Movable Entity  */
    public static void addBot(MovableEntity entity) {
        bots.add(entity);
    }

    public static List<MovableEntity> getBots() {
        return bots;
    }

    public static List<MovableEntity> getMovableEntities() {
        List<MovableEntity> result = new ArrayList<>();
        result.addAll(bots);
        result.addAll(players);
        return result;
    }

    public static MovableEntity getMovableEntityAt(MovableEntity except_movableEntity, int x, int y) {
        MovableEntity me = identifyObjectAt(x, y, bots);
        if (me != null && me != except_movableEntity) return me;

        me = identifyObjectAt(x, y, players);
        if (me != except_movableEntity) return me;
        return null;
    }

    public static List<Bomber> getPlayers() {
        return List.copyOf(players);
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

    public static List<Point> getPlayerPositionsUnit() {
        List<Point> result = new ArrayList<>();

        for (Bomber player : players) {
            Point cell = getMostStandingCell(player.getX(), player.getY());
            result.add(cell);
        }

        return result;
    }

    public static void setPlayer(int players_number, Scene scene) throws NullPointerException {
        players.get(0).setOnPlayerEvents(scene, 1);

        if (players_number == 2) {
            Bomber player2 = new Bomber(generateRandomID(), 29, 11, PLAYER_SPEED);

            //based on space and size of the map
            players.add(player2);
            player2.setOnPlayerEvents(scene, 2);
        }
        else players.get(0).setOnPlayerEvents(scene, 2);
    }


    /**  Item  */
    public static List<Item> getItems() {
        return items;
    }

    public static Item getItemAt(int x, int y) {
        return identifyObjectAt(x, y, items);
    }

    public static boolean removeItem(Item item) {
        return items.remove(item);
    }


    /**  Bomb  */
    public static List<Bomb> getBombs() {
        return bombs;
    }

    public static Bomb getBombAt(int x, int y) {
        return identifyObjectAt(x, y, bombs);
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
    private void create(int level) {
        try {
            FileInputStream in = new FileInputStream("res/levels/level" + level + ".txt");
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

                        case 'p' -> players.add(new Bomber(generateRandomID(), i, j, PLAYER_SPEED));
                        case '1' -> bots.add(new Balloon(i, j, ENEMY_SPEED));
                        case '2' -> bots.add(new OneAl(i, j, ENEMY_SPEED, ONEAL_PURSUIT_RANGE));
                    }

                    // remove all characters except walls, bricks and portals.
                    if (baseMap[j][i] != '#'
                        && baseMap[j][i] != '*'
                        && baseMap[j][i] != 'x') baseMap[j][i] = ' ';
                }
            }
            for (char[] chars : baseMap) System.out.println(chars);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is used for bots to identify obstacles, helps find a path.
     * @return
     */
    public static char[][] getBaseMap() {
        char[][] result = new char[baseMap.length][baseMap[0].length];

        for (int y = 0; y < result.length; y++) {
            for (int x = 0; x < result[y].length; x++) {
                result[y][x] = baseMap[y][x];
            }
        }

        for (Bomb bomb : bombs) {
            Point p = getMostStandingCell(bomb.getX(), bomb.getY());
            result[p.y][p.x] = '0';
        }
        
        for (MovableEntity bot : bots) {
            Point p = getMostStandingCell(bot.getX(), bot.getY());
            result[p.y][p.x] = '0';
        }
        
        return result;
    }

    public void clearMap() {
        for (char[] chars : baseMap) {
            chars = null;
        }
        players.clear();
        bots.clear();
        killed_enemy_list.clear();
        items.clear();
        backGroundObjects.clear();
        walls.clear();
        breakableStillObjects.clear();
        bombs.clear();
    }

    public void render(GraphicsContext graphicsContext) {
        backGroundObjects.forEach(v -> v.render(graphicsContext));
        walls.forEach(v -> v.render(graphicsContext));
        items.forEach(v -> v.render(graphicsContext));
        bombs.forEach(v -> v.render(graphicsContext));
        breakableStillObjects .forEach(v -> v.render(graphicsContext));
        bots.forEach(v -> v.render(graphicsContext));
        players.forEach(v -> v.render(graphicsContext));
    }

    public void updateEntities(Scene scene) {
        updateStillObjects();
        updateMovableEntities();
        updatePlayers(scene);
        updateBombs();
    }

    private void updateStillObjects() {
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

    private void updateMovableEntities() {
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

    private void updatePlayers(Scene scene) {
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

    private void updateBombs() throws NullPointerException {
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

    public List<MovableEntity> getKilledEnemies() {
        return List.copyOf(killed_enemy_list);
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

    public static Point getMostStandingCell(int x, int y) {
        int xUnit = x / Sprite.SCALED_SIZE;
        int yUnit = y / Sprite.SCALED_SIZE;

        int odd_x = x - xUnit * Sprite.SCALED_SIZE;
        int odd_y = y - yUnit * Sprite.SCALED_SIZE;

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

        return new Point(xUnit, yUnit);
    }


    private static double getRectArea(double a, double b) {
        return a * b;
    }

    private static <T extends Entity> T identifyObjectAt(int x, int y, List<T> entityList) {
        for (T entity : entityList) {
            Point objectRange_from = new Point(entity.getX(), entity.getY());
            Point objectRange_to = new Point(entity.getX() + Sprite.SCALED_SIZE - 1,
                    entity.getY() + Sprite.SCALED_SIZE - 1);

            if ( (objectRange_from.x <= x && x <= objectRange_to.x)
                    && (objectRange_from.y <= y && y <= objectRange_to.y) ) {
                return entity;
            }
        }
        return null;
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
