package uet.oop.bomberman.Motion;

import uet.oop.bomberman.Base.Point;
import uet.oop.bomberman.BombermanGame;
import uet.oop.bomberman.entities.MovableEntities.MovableEntity;
import uet.oop.bomberman.graphics.Sprite;

import java.util.ArrayList;
import java.util.Random;

public class RandomMovement extends Movement {
    protected double remaining_steps = 0;

    public RandomMovement(MovableEntity entity, int x, int y, int speed) {
        super(entity, x, y, speed);
    }

    @Override
    protected void setDirection(int except_direction) {
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


        double remaining_cells = rand.nextDouble(1, 5);
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


        //handle movement
        if (this.remaining_steps == 0 || this.direction == Movement.FREEZE) {
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
                    case RIGHT -> x += 1;
                    case LEFT -> x -= 1;
                    case UP -> y -= 1;
                    case DOWN -> y += 1;
                }
                remaining_steps -= 1;
            }

            return new Point(x, y);
        }

        switch (direction) {
            case RIGHT -> x += 1;
            case LEFT -> x -= 1;
            case UP -> y -= 1;
            case DOWN -> y += 1;
        }
        remaining_steps -= 1;

        return new Point(x, y);
    }
}