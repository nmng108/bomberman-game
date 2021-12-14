package uet.oop.bomberman.Motion;

import uet.oop.bomberman.Base.GameMap;
import uet.oop.bomberman.Base.Point;
import uet.oop.bomberman.BombermanGame;
import uet.oop.bomberman.entities.MovableEntities.MovableEntity;
import uet.oop.bomberman.graphics.Sprite;

import java.util.ArrayList;
import java.util.Random;

public class PlayerPursuitMovement extends Movement {
    private int remaining_steps = 0;
    private int target_index = new Random().nextInt(2);

    private final double SPEED_DOWN_RANGE = 6; //proportional to game's difficulty.
    private final double RANGE_TO_PURSUE;
    private double time_for_new_speed = 2;
    private double start_time_with_newSpeed;

    private boolean targetIsRandom = true;
    private boolean targetIsInRange = false;

    private PathFinding[] paths;
    private int min_path;

    public PlayerPursuitMovement(MovableEntity entity, int x, int y, int initialSpeedByPixel,
                                 int range_to_pursue) {
        super(entity, x, y, initialSpeedByPixel);
        this.start_time_with_newSpeed = BombermanGame.getTime();
        this.RANGE_TO_PURSUE = range_to_pursue;

    }

    @Override
    public Point run() {
        //timer
        double current_time = BombermanGame.getTime();

        if ( (current_time - this.calc_period < this.period && current_time > this.calc_period)
                || ((BombermanGame.TIME_COUNT_MAX - this.calc_period) + current_time < this.period
                    && this.calc_period > current_time) ) {
            return new Point(x, y);
        }

        this.calc_period = BombermanGame.getTime();

        changeRandomSpeed();

        setDirection();
        if (!hasAvailablePath()) {
            if (this.remaining_steps <= 0 || this.direction == Movement.FREEZE) {
                    this.setRandomDirection(this.direction);
            }
        }

        //handle random movement
        if (isTouchingAnObjectAhead()) {
            if (!isBlockedCompletely()) {
                this.moveDiagonally(1);
            }
            else {
                //Set another direction
                this.setRandomDirection(this.direction);

                switch (direction) {
                    case RIGHT -> x += pixels_per_1_step;
                    case LEFT -> x -= pixels_per_1_step;
                    case UP -> y -= pixels_per_1_step;
                    case DOWN -> y += pixels_per_1_step;
                }
                this.remaining_steps -= 1;
            }

            return new Point(x, y);
        }

        switch (direction) {
            case RIGHT -> x += pixels_per_1_step;
            case LEFT -> x -= pixels_per_1_step;
            case UP -> y -= pixels_per_1_step;
            case DOWN -> y += pixels_per_1_step;
        }
        this.remaining_steps -= 1;

        return new Point(x, y);
    }

    private void setDirection() {
        try { //paths = null at the beginning of each level.
            if ((x % Sprite.SCALED_SIZE == 0 && y % Sprite.SCALED_SIZE == 0) || paths == null) {
                findPath();
                findInRange();

                if (paths == null || paths.length == 0) return;

                //when all players just went out of range.
                if (targetIsRandom && targetIsInRange) {
                    this.target_index = new Random().nextInt(paths.length);
                    targetIsInRange = false;
                }

                // Choose the other player if there is not any path to pursue the current player.
                // The other choice still have a possibility of not existing any path to follow.
                if (target_index > paths.length - 1 || !paths[target_index].isFound()) {
                    if (this.target_index == 1) this.target_index = 0;
                    else if (paths.length == 2) this.target_index = 1;
                }

                if (paths[target_index] == null) throw new Exception("target's path is not found!");
                if (!paths[target_index].isFound()) return;

                this.direction = paths[target_index].getOrderedDirectionList().poll();
                this.min_path = paths[target_index].getDistance();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Set a random direction and a number of steps.
     */
    private void setRandomDirection(int except_direction) {
        Random rand = new Random();
        ArrayList<Integer> canMove = new ArrayList<>();

        for (int direction_value = 0; direction_value < 4; direction_value++) {
            if (direction_value != except_direction) {
                canMove.add(direction_value);
            }
        }

        do {
            if (canMove.size() == 0) {
                this.direction = Movement.FREEZE;
                return;
            }

            int i = rand.nextInt(canMove.size());
            this.direction = canMove.get(i);

            if (isBlockedCompletely()) canMove.remove(i);
        }
        while (isBlockedCompletely());

        int remaining_cells = rand.nextInt(1, 6);
        this.remaining_steps = remaining_cells * Sprite.SCALED_SIZE;
    }

    /**
     * Find and set a series of directions needed to follow the defined player.
     */
    private void findPath() {
        Point pos = GameMap.getMostStandingCell(x, y);
        int xUnit = pos.x;
        int yUnit = pos.y;

        if (GameMap.getPlayers().size() == 2) paths = new PathFinding[2];
        else if (GameMap.getPlayers().size() == 1) paths = new PathFinding[1];
        else return;

        try {
            for (int i = 0; i < GameMap.getPlayerPositionsUnit().size(); i++) {
                int xTargetUnit = GameMap.getPlayerPositionsUnit().get(i).x;
                int yTargetUnit = GameMap.getPlayerPositionsUnit().get(i).y;

                paths[i] = new PathFinding(xUnit, yUnit, xTargetUnit, yTargetUnit, getBaseMap());
            }

            if (paths == null) throw new Exception("Cant initialize paths in enhanced movement.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Change the character represents bot from '0' to ' ' if it is this oneal.
     */
    private char[][] getBaseMap() {
        char[][] base_map = GameMap.getBaseMap();
        for (MovableEntity bot : GameMap.getBots()) {
            if (bot == this.entity) {
                Point p = GameMap.getMostStandingCell(bot.getX(), bot.getY());
                base_map[p.y][p.x] = ' ';
                return base_map;
            }
        }
        return base_map;
    }

    private void findInRange() {
        try {
            if (paths == null) return;

            if (paths[0].isFound() && (paths[0].getDistance() <= RANGE_TO_PURSUE)) {
                this.target_index = 0;
                targetIsRandom = false;
                targetIsInRange = true;
                return;
            }
            if (paths.length == 2) {
                if (paths[1].isFound() && (paths[1].getDistance() <= RANGE_TO_PURSUE)) {
                    this.target_index = 1;
                    targetIsRandom = false;
                    targetIsInRange = true;
                }
            }
            else targetIsRandom = true;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean hasAvailablePath() {
        if (paths == null || paths.length == 0) return false;

        for (PathFinding path : paths) {
            if (path == null) return false;
            if (path.isFound()) return true;
        }

        return false;
    }

    private void changeRandomSpeed() {
        Random random = new Random();

        //timer
        double current_time = BombermanGame.getTime();
        double timer = current_time - this.start_time_with_newSpeed;

        if (timer < 0) {
            timer = BombermanGame.getTime() - this.start_time_with_newSpeed + current_time;
        }

        if (timer >= time_for_new_speed) {
            if (min_path > SPEED_DOWN_RANGE) this.speed = random.nextInt(55, 70);
            else this.speed = random.nextInt(45, 55);

            this.period = (double) 1 / this.speed;

            this.time_for_new_speed = random.nextDouble(1, 2.5);

            this.start_time_with_newSpeed = current_time;
        }
    }
}