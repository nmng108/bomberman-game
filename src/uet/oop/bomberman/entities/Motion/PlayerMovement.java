package uet.oop.bomberman.entities.Motion;

import uet.oop.bomberman.Base.Point;
import uet.oop.bomberman.BombermanGame;
import uet.oop.bomberman.entities.MovableEntities.MovableEntity;

public class PlayerMovement extends Movement {
    public PlayerMovement(MovableEntity entity, int x, int y, int speed) {
        super(entity, x, y, speed);
    }

    @Override
    public void setDirection(int d) {
        this.direction = d;
    }

    @Override
    public Point run() {
        //timer
        double current_time = BombermanGame.getTime();

        if ((current_time - this.calc_period < this.period && current_time > this.calc_period)
                || ((BombermanGame.TIME_COUNT_MAX - this.calc_period) + current_time < this.period)
                && this.calc_period > current_time) {
            return new Point(x, y);
        }
        else {
            this.calc_period = BombermanGame.getTime();
        }

        //movement handling
        if (isTouchingAnObjectAhead()) {
            if ( ! isBlockedCompletely() ) {
                this.moveDiagonally();
            }
            else if (justSpawnBomb) {
                switch (direction) {
                    case RIGHT -> {
                        if (isTouchingAnObjectAhead()) {
                            if ( ! isBlockedCompletely() ) {
                                this.moveDiagonally();
                            }
                            else if (isOnBomb()) x += 1;
                        }
                        else x += 1;
                    }
                    case LEFT -> {
                        if (isTouchingAnObjectAhead()) {
                            if ( ! isBlockedCompletely() ) {
                                this.moveDiagonally();
                            }
                            else if (isOnBomb()) x -= 1;
                        }
                        else x -= 1;
                    }
                    case UP -> {
                        if (isTouchingAnObjectAhead()) {
                            if ( ! isBlockedCompletely() ) {
                                this.moveDiagonally();
                            }
                            else if (isOnBomb()) y -= 1;
                        }
                        else y -= 1;
                    }
                    case DOWN -> {
                        if (isTouchingAnObjectAhead()) {
                            if ( ! isBlockedCompletely() ) {
                                this.moveDiagonally();
                            }
                            else if (isOnBomb()) y += 1;
                        }
                        else y += 1;
                    }
                }

                if (!isOnBomb()) this.justSpawnBomb = false;
            }
            System.out.println(x + " " + y);
            return new Point(x, y);
        }

        switch (direction) {
            case RIGHT -> x += 1;
            case LEFT -> x -= 1;
            case UP -> y -= 1;
            case DOWN -> y += 1;
        }

        return new Point(x, y);
    }

}
