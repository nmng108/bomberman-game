package uet.oop.bomberman.entities.Motion;

import uet.oop.bomberman.Base.GameMap;
import uet.oop.bomberman.Base.Point;
import uet.oop.bomberman.BombermanGame;
import uet.oop.bomberman.entities.MovableEntities.MovableEntity;
import uet.oop.bomberman.graphics.Sprite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class EnhancedRandomMovement extends Movement {
    private int remaining_cells = 1;
    private int remaining_steps = 0;

    private double minimum_range_to_approach = 40;
    private double time_for_new_speed = 2;
    private double start_time_with_newSpeed = 0;

    public EnhancedRandomMovement(MovableEntity entity, int x, int y, int initialSpeedByPixel, int minRange_approach) {
        super(entity, x, y, initialSpeedByPixel);
        this.start_time_with_newSpeed = BombermanGame.getTime();
        this.minimum_range_to_approach = minRange_approach;
    }

    @Override
    public void setDirection(int except_direction) {
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

        do {
            this.remaining_cells = rand.nextInt(5);
        } while (this.remaining_cells == 0);

        this.remaining_steps = this.remaining_cells * Sprite.SCALED_SIZE;
    }

    @Override
    public Point run() {
        changeSpeed();

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
                this.moveDiagonally();
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

    //still get error with timer, which leads to change come slower.
    private void changeSpeed() {
        Random random = new Random();

        //timer
        double current_time = BombermanGame.getTime();
        double timer = current_time - this.start_time_with_newSpeed;

        if (timer < 0) {
            timer = BombermanGame.getTime() - this.start_time_with_newSpeed + current_time;
        }

        if (timer >= time_for_new_speed) {
            this.speed = random.nextInt(40, 100);
            this.period = (double) 1 / this.speed;

            this.time_for_new_speed = random.nextDouble(1.5, 6);

            this.start_time_with_newSpeed = current_time;

            System.out.println("New speed: " + this.speed);
            System.out.println("time for new speed: " + this.time_for_new_speed);
        }
    }
}