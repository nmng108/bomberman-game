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
import uet.oop.bomberman.entities.Item.*;
import uet.oop.bomberman.entities.MovableEntities.Balloon;
import uet.oop.bomberman.entities.MovableEntities.Bomber;
import uet.oop.bomberman.entities.MovableEntities.MovableEntity;
import uet.oop.bomberman.entities.MovableEntities.OneAl;
import uet.oop.bomberman.entities.StillEntities.*;
import uet.oop.bomberman.entities.StillEntities.StableStillObject.Grass;
import uet.oop.bomberman.entities.StillEntities.StableStillObject.Wall;
import uet.oop.bomberman.graphics.Sprite;

public class GameMap {
    private final int PLAYER_SPEED;
    private final int ENEMY_SPEED;
    private final int ONEAL_PURSUIT_RANGE;

    private static int players_number;

    private static char[][] baseMap = new char[BombermanGame.HEIGHT][BombermanGame.WIDTH];

    private static List<Bomber> players = new ArrayList<>();
    private static List<MovableEntity> bots = new ArrayList<>();
    private static ArrayList[] killed_enemy_list;
    private static List<Item> items = new ArrayList<>();
    private static List<Entity> backGroundObjects = new ArrayList<>();
    private static List<Entity> walls = new ArrayList<>();
    private static List<Portal> openPortals = new ArrayList<>();
    private static List<BreakableStillObject> breakableStillObjects = new ArrayList<>();
    private static List<Bomb> bombs = new ArrayList<>();


    public GameMap(int level, Scene scene) {
        PLAYER_SPEED = 65 + 3 * level;
        ENEMY_SPEED = 45 + 5 * level;
        ONEAL_PURSUIT_RANGE = 6 + level;
        players_number = Menu.getPlayersNumber();

        clearMap();
        create(level);
        setPlayer(scene);
    }


    /**
     * Still Object(Wall, Brick, Portal)
     * Breakable still objects: Brick, Portal(transfer from this list to backGroundObjects list when broken)
     */
    public static List<Entity> getWalls() {
        return walls;
    }

    public static void addOpenPortal(Portal portal) {
        openPortals.add(portal);
    }

    public static List<Portal> getOpenPortals() {
        return openPortals;
    }

    public static Portal getOpenPortalAt(int x, int y) {
        return identifyObjectAt(x, y, openPortals);
    }

    private List<BreakableStillObject> getBreakableStillObjects() {
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

    public static int getPlayersNumber() {
        return players_number;
    }

    public static List<Bomber> getPlayers() {
        return players;
    }

    private Bomber getPlayer(int ID) {
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

    public void setPlayer(Scene scene) throws NullPointerException {
        Bomber player1 = getPlayer(1);
        Bomber player2 = getPlayer(2);
        if (player1 == null || player2 == null) {
            System.out.println("Set player1's event handler unsuccessfully");
            return;
        }

        player1.setOnPlayerEvents(scene);

        if (players_number == 2) {
            player2.setOnPlayerEvents(scene);

            killed_enemy_list = new ArrayList[2];
            killed_enemy_list[0] = new ArrayList<>();
            killed_enemy_list[1] = new ArrayList<>();
        }
        else if (players_number == 1) {
            killed_enemy_list = new ArrayList[1];
            killed_enemy_list[0] = new ArrayList<>();

            players.remove(player2);
        }
        else System.out.println("player numbers is not satisfactory");
    }


    /**  Item  */
    public static void addItem(Item item) {
        items.add(item);
    }

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

    public static void addBomb(Bomb bomb) {
        bombs.add(bomb);
    }


    /**  Methods for common objects.  */
    public static Entity getObjectAt(MovableEntity except_movableEntity, int x, int y) {
        // Not used to get items.
        Entity e;

        e = getMovableEntityAt(except_movableEntity, x, y);
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
                        case 'w' -> items.add(new PowerUpWallPass(i, j));

                        case 'p' -> players.add(new Bomber(1, i, j, PLAYER_SPEED));
                        case 'q' -> players.add(new Bomber(2, i, j, PLAYER_SPEED));
                        case '1' -> bots.add(new Balloon(i, j, ENEMY_SPEED));
                        case '2' -> bots.add(new OneAl(i, j, ENEMY_SPEED, ONEAL_PURSUIT_RANGE));
                    }

                    // remove all characters except walls, bricks and openPortals.
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
        items.clear();
        backGroundObjects.clear();
        walls.clear();
        breakableStillObjects.clear();
        bombs.clear();
    }

    public void render(GraphicsContext graphicsContext) {
        backGroundObjects.forEach(v -> v.render(graphicsContext));
        walls.forEach(v -> v.render(graphicsContext));
        openPortals.forEach(v -> v.render(graphicsContext));
        items.forEach(v -> v.render(graphicsContext));
        bombs.forEach(v -> v.render(graphicsContext));
        breakableStillObjects .forEach(v -> v.render(graphicsContext));
        bots.forEach(v -> v.render(graphicsContext));
        players.forEach(v -> v.render(graphicsContext));
    }

    public void updateEntities(Scene scene) {
        updateStillObjects();
        updateBots();
        updatePlayers(scene);
        updateBombs();
        updatePortals();
    }

    private void updateStillObjects() {
        BreakableStillObject object;
        int index = 0;

        for (; index < this.getBreakableStillObjects().size(); index++) {
            object = this.getBreakableStillObjects().get(index);

            object.update();

            if (object.isDeleted()) {
                index -= 1;
            }
        }
    }

    private void updateBots() {
        MovableEntity entity = null;
        int index = 0;

        while(index < GameMap.getBots().size()) {
            for (; index < GameMap.getBots().size(); index++) {
                entity = GameMap.getBots().get(index);

                entity.update();

                if (entity.isDeleted()) break;
            }

            if (entity.isDeleted()) {
                removeObject(entity);
            }
        }
    }

    private void updatePlayers(Scene scene) {
        Bomber player;
        int index = 0;

        for (; index < players.size(); index++) {
            player = players.get(index);

            player.update();

            if (player.isDeleted()) {
                player.removeHandler(scene);
                players.remove(player);

                index --;
            }
        }
    }

    private void updateBombs() throws NullPointerException {
        Bomb bomb = null;
        int index = 0;

        for (List<MovableEntity> list : killed_enemy_list) {
            list.clear();
        }

        while (index < GameMap.getBombs().size()) {
            for (; index < GameMap.getBombs().size(); index++) {
                bomb = GameMap.getBombs().get(index);

                bomb.update();

                if (bomb.isDone()) break;
            }

            if (bomb.isDone()) {
                if (bombs.remove(bomb)) {
                    Bomber owner = getPlayer(bomb.getOwnerID());
                    if (owner != null) {
                        owner.takeBackOneBomb();
                        int i = players.indexOf(owner);
                        killed_enemy_list[i].addAll(bomb.getKilledEnemies());
                    }
                }
            }
        }
    }

    private void updatePortals() {
        openPortals.forEach(Portal::update);
    }

    public List<MovableEntity>[] getKilledEnemies() throws Exception {
        return killed_enemy_list;
    }


    /**
     * Find the distance to the nearest still object found ahead (except items).
     * Only available for horizontal or vertical motion.
     * @return the positive value based on horizontal or vertical direction.
     *      This value is equal to
     *          +, MAX_VALUE if no still object found in the range_unit,
     *          +, 0 (minimum) if touching a still object from the beginning while moving LEFT or UP.
     *          +, 32 if touching a still object from the beginning while moving RIGHT or DOWN.
     */
    public static double distanceToObjectAhead(MovableEntity entity) throws NullPointerException {
        Point from = new Point(entity.getX(), entity.getY());

        int direction;
        if (entity.getDirection() == Movement.FREEZE) direction = entity.getPrev_direction();
        else direction = entity.getDirection();

        switch (direction) {
            case Movement.RIGHT -> {
                Entity obj;

                for (int i_x = entity.getX() + Sprite.SCALED_SIZE; ; i_x++) {
                    //left-top angle of the object
                    obj = getObjectAt(null, i_x, from.y);
                    if (obj != null) {
                        return i_x - (entity.getX() + Sprite.SCALED_SIZE);
                    }

                    //left-bottom angle of the object
                    obj = getObjectAt(null, i_x, from.y + Sprite.SCALED_SIZE - 1);
                    if (obj != null) {
                        return i_x - (entity.getX() + Sprite.SCALED_SIZE);
                    }
                }
            }

            case Movement.LEFT -> {
                Entity obj;

                for (int i_x = entity.getX() - 1; ; i_x--) {
                    //left-top angle of the object
                    obj = getObjectAt(null, i_x, from.y);
                    if (obj != null) {
                        return entity.getX() - 1 - i_x;
                    }

                    //left-bottom angle of the object
                    obj = getObjectAt(null, i_x, from.y + Sprite.SCALED_SIZE - 1);
                    if (obj != null) {
                        return entity.getX() - 1 - i_x;
                    }
                }
            }

            case Movement.DOWN -> {
                Entity obj;

                for (int i_y = entity.getY() + Sprite.SCALED_SIZE; ; i_y++) {
                    //left-top angle of the object
                    obj = getObjectAt(null, entity.getX(), i_y);
                    if (obj != null) {
                        return i_y - (entity.getY() + Sprite.SCALED_SIZE);
                    }

                    //right-top angle of the object
                    obj = getObjectAt(null, entity.getX() + Sprite.SCALED_SIZE - 1, i_y);
                    if (obj != null) {
                        return i_y - (entity.getY() + Sprite.SCALED_SIZE);
                    }
                }
            }

            case Movement.UP -> {
                Entity obj;

                for (int i_y = entity.getY() - 1; ; i_y--) {
                    //left-top angle of the object
                    obj = getObjectAt(null, entity.getX(), i_y);
                    if (obj != null) {
                        return entity.getY() - 1 - i_y;
                    }

                    //right-top angle of the object
                    obj = getObjectAt(null, entity.getX() + Sprite.SCALED_SIZE - 1,
                            i_y);
                    if (obj != null) {
                        return entity.getY() - 1 - i_y;
                    }
                }
            }
        }

        return Double.MAX_VALUE;
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
