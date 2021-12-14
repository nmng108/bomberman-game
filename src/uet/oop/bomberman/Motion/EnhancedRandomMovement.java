package uet.oop.bomberman.Motion;

import uet.oop.bomberman.Base.GameMap;
import uet.oop.bomberman.Base.Point;
import uet.oop.bomberman.BombermanGame;
import uet.oop.bomberman.entities.Bomb.Bomb;
import uet.oop.bomberman.entities.Entity;
import uet.oop.bomberman.entities.MovableEntities.MovableEntity;
import uet.oop.bomberman.graphics.Sprite;

import java.util.ArrayList;
import java.util.Random;
import java.util.TreeSet;

public class EnhancedRandomMovement extends RandomMovement {
    TreeSet<Integer> possibleDirection = new TreeSet<>();

    public EnhancedRandomMovement(MovableEntity entity, int x, int y, int speed) {
        super(entity, x, y, speed);
    }

    @Override
    protected void setDirection(int except_direction) {
        Random rand = new Random();

        ArrayList<Integer> canMove = new ArrayList<>();

        if (possibleDirection.size() == 0) {
            for (int direction_value = 0; direction_value < 4; direction_value++) {
                if (direction_value != except_direction) {
                    canMove.add(direction_value);
                }
            }
        } else {
            while (!possibleDirection.isEmpty()) {
                int tmp_d = getPossibleDirection();
                if (tmp_d != except_direction) {
                    canMove.add(tmp_d);
                }
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

        double remaining_cells = rand.nextDouble(1, 4.5);
        this.remaining_steps = remaining_cells * Sprite.SCALED_SIZE;
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

        locateBombsAndDetermineDirection();

        //handle movement
        if (this.possibleDirection.size() != 0 || this.remaining_steps == 0 || this.direction == Movement.FREEZE) {
            this.setDirection(this.direction);
        }

        if (isTouchingAnObjectAhead()) {
            if (!isBlockedCompletely()) {
                this.moveDiagonally(1);
            }
            else {
                //Set another direction
                this.setDirection(this.direction);

                switch (direction) {
                    case RIGHT -> x += pixels_per_1_step;
                    case LEFT -> x -= pixels_per_1_step;
                    case UP -> y -= pixels_per_1_step;
                    case DOWN -> y += pixels_per_1_step;
                }
                remaining_steps -= 1;
            }

            return new Point(x, y);
        }

        switch (direction) {
            case RIGHT -> x += pixels_per_1_step;
            case LEFT -> x -= pixels_per_1_step;
            case UP -> y -= pixels_per_1_step;
            case DOWN -> y += pixels_per_1_step;
        }
        remaining_steps -= 1;

        return new Point(x, y);
    }

    private int getPossibleDirection() {
        if (possibleDirection.size() != 0) {
            return possibleDirection.pollFirst();
        }
        return Movement.FREEZE;
    }

    private void locateBombsAndDetermineDirection() {
        switch (this.direction) {
            case Movement.LEFT, Movement.RIGHT -> {
                locateLeft();
                locateRight();
            }
            case Movement.DOWN, Movement.UP -> {
                locateBottom();
                locateTop();
            }
        }
    }

    private void locateLeft() {
        int tmp_x = this.x;
        int tmp_y = this.y;
        Entity obj;

        for (int i = 1; GameMap.getStillObjectAt(tmp_x, tmp_y) == null; i++) {
            tmp_x = x - i * Sprite.SCALED_SIZE;
            obj = GameMap.getObjectAt(this.entity, tmp_x, tmp_y);

            if (obj instanceof Bomb) {
                double distance = this.x - (obj.getX() + Sprite.SCALED_SIZE);
                if (distance < ((Bomb) obj).getRange()) {

                    if (((Bomb) obj).getRange() - 1.6 * Sprite.SCALED_SIZE <= distance) {
                        possibleDirection.add(Movement.RIGHT);
                    }

                    possibleDirection.add(Movement.UP);
                    possibleDirection.add(Movement.DOWN);
                }
                break;
            }
        }

    }

    private void locateRight() {
        int tmp_x = this.x;
        int tmp_y = this.y;
        Entity obj;

        for (int i = 1; GameMap.getStillObjectAt(tmp_x, tmp_y) == null; i++) {
            tmp_x = x + i * Sprite.SCALED_SIZE;
            obj = GameMap.getObjectAt(this.entity, tmp_x, tmp_y);

            if (obj instanceof Bomb) {
                double distance = obj.getX() - (this.x + Sprite.SCALED_SIZE);
                if (distance < ((Bomb) obj).getRange()) {

                    if (((Bomb) obj).getRange() - 1.6 * Sprite.SCALED_SIZE <= distance) {
                        possibleDirection.add(Movement.LEFT);
                    }

                    possibleDirection.add(Movement.UP);
                    possibleDirection.add(Movement.DOWN);
                }
                break;
            }
        }
    }

    private void locateBottom() {
        int tmp_x = this.x;
        int tmp_y = this.y;
        Entity obj;

        for (int i = 1; GameMap.getStillObjectAt(tmp_x, tmp_y) == null; i++) {
            tmp_y = y + i * Sprite.SCALED_SIZE;
            obj = GameMap.getObjectAt(this.entity, tmp_x, tmp_y);

            if (obj instanceof Bomb) {
                double distance = obj.getY() - (this.y + Sprite.SCALED_SIZE);
                if (distance < ((Bomb) obj).getRange()) {

                    if (((Bomb) obj).getRange() -  1.6 * Sprite.SCALED_SIZE <= distance) {
                        possibleDirection.add(Movement.UP);
                    }

                    possibleDirection.add(Movement.LEFT);
                    possibleDirection.add(Movement.RIGHT);
                }
                break;
            }
        }
    }

    private void locateTop() {
        int tmp_x = this.x;
        int tmp_y = this.y;
        Entity obj;

        for (int i = 1; GameMap.getStillObjectAt(tmp_x, tmp_y) == null; i++) {
            tmp_y = y - i * Sprite.SCALED_SIZE;
            obj = GameMap.getObjectAt(this.entity, tmp_x, tmp_y);

            if (obj instanceof Bomb) {
                double distance = this.y - (obj.getY() + Sprite.SCALED_SIZE);
                if (distance < ((Bomb) obj).getRange()) {

                    if (((Bomb) obj).getRange() - 1.6 * Sprite.SCALED_SIZE <= distance) {
                        possibleDirection.add(Movement.DOWN);
                    }

                    possibleDirection.add(Movement.LEFT);
                    possibleDirection.add(Movement.RIGHT);
                }
                break;
            }
        }
    }
}
