package uet.oop.bomberman.Motion;

import uet.oop.bomberman.Base.Point;
import uet.oop.bomberman.BombermanGame;
import uet.oop.bomberman.entities.Bomb.Bomb;
import uet.oop.bomberman.entities.MovableEntities.MovableEntity;
import uet.oop.bomberman.graphics.Sprite;

import java.util.ArrayList;

public class PlayerMovement extends Movement {
    //may also add this list to the movable entity
    private ArrayList<Bomb> bombs_standing_on = new ArrayList<>();

    private boolean canStepOverBomb = false;

    public PlayerMovement(MovableEntity entity, int x, int y, int speed) {
        super(entity, x, y, speed);
        this.pixels_foreach_unitOfMoveLength = 1;
    }

    public void addPlacedBomb(Bomb justPlacedBomb) {
        if (justPlacedBomb != null) {
            this.bombs_standing_on.add(justPlacedBomb);
        }
    }

    @Override
    public void setDirection(int d) {
        this.direction = d;
    }

    @Override
    public Point run() {
        //timer
        double current_time = BombermanGame.getTime();
        // Check whether time reach to the time mark(enough for a period) to make a new move.
        // It's hard for the 2nd condition(line 40, 41) to be used. The first condition is used
        // instead in most cases.
        if ((current_time - this.calc_period < this.period && current_time > this.calc_period)
                || ((BombermanGame.TIME_COUNT_MAX - this.calc_period) + current_time < this.period)
                && this.calc_period > current_time) {
            return new Point(x, y);
        }

        this.calc_period = BombermanGame.getTime();

        //handle collision
        if (isTouchingAnObjectAhead()) {
            if ( ! isBlockedCompletely() ) {
                this.moveDiagonally(1);
            }
            else {
                checkAbilityToStepOverBomb();

                if (canStepOverBomb) {
                    switch (this.direction) {
                        //speed up by an old value (1->2) in order to step over the bomb faster
                        case RIGHT -> x += 2;
                        case LEFT -> x -= 2;
                        case UP -> y -= 2;
                        case DOWN -> y += 2;
                    }
//                    moveCLoserToObject(); //still got mistake
                }

                //if stepped out of any bomb just placed, we just take care of the rest placed bombs in each move.
                scanPlacedBombs();
            }
            return new Point(x, y);
        }

        switch (direction) {
            case RIGHT -> x += pixels_foreach_unitOfMoveLength;
            case LEFT -> x -= pixels_foreach_unitOfMoveLength;
            case UP -> y -= pixels_foreach_unitOfMoveLength;
            case DOWN -> y += pixels_foreach_unitOfMoveLength;
        }
//        moveCLoserToObject(); //still got mistake

        return new Point(x, y);
    }

    /**
     * @return true if any player's pixel is on the bomb that placed
     */
    private boolean isOnBombPlaced(Bomb just_spawn_bomb) {
        Point player_from = new Point(x, y);
        Point player_to = new Point(x + Sprite.SCALED_SIZE - 1, y + Sprite.SCALED_SIZE - 1);

        Point bomb_from = new Point(just_spawn_bomb.getX(), just_spawn_bomb.getY());
        Point bomb_to = new Point(just_spawn_bomb.getX() + Sprite.SCALED_SIZE - 1,
                just_spawn_bomb.getY() + Sprite.SCALED_SIZE - 1);


        for (int i = player_from.x; i <= player_to.x; i++) {
            for (int j = player_from.y; j <= player_to.y; j++) {

                if (bomb_from.x <= i && i <= bomb_to.x
                        && bomb_from.y <= j && j <= bomb_to.y ) {
                    return true;
                }
            }
        }

        return false;
    }

    //Based on movement's direction.
    private void checkAbilityToStepOverBomb() {
        for (Bomb bomb : bombs_standing_on) {
            if (getObjectAhead() == bomb) { //
                this.canStepOverBomb = true;
                return;
            }
        }
        this.canStepOverBomb = false;
    }

    //remove the bombs no more standing on.
    private void scanPlacedBombs() {
        int i = 0;
        Bomb bomb = null;

        for (; i < bombs_standing_on.size(); i++) {
            bomb = bombs_standing_on.get(i);

            if (!isOnBombPlaced(bomb)) {
                bombs_standing_on.remove(bomb);
                i--;
            }
        }
    }

}
