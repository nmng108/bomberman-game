package uet.oop.bomberman.entities.Bomb;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import uet.oop.bomberman.BombermanGame;
import uet.oop.bomberman.Base.GameMap;
import uet.oop.bomberman.entities.Entity;
import uet.oop.bomberman.Motion.Movement;
import uet.oop.bomberman.entities.MovableEntities.Bomber;
import uet.oop.bomberman.entities.MovableEntities.MovableEntity;
import uet.oop.bomberman.graphics.ImageLists;
import uet.oop.bomberman.graphics.Sprite;

import java.util.ArrayList;
import java.util.List;

public class Bomb extends Entity {
    private final double EXISTENCE_TIME = 3;
    private final double TIME_FOREACH_IMAGE = 0.5;

    private double start_time = 0;
    private double current_time = 0;
    private double counted_time = 0;
    private double flame_start_time = 0;

    private int range_unit = 1;

    private boolean countdownStopped = false;
    private boolean done = false;
    private boolean timeOver = false;
    private boolean exploded = false; //used internally in this class

    private int owner_ID;

    HorizontalFlameLine leftFlames;
    HorizontalFlameLine rightFlames;
    VerticalFlameLine topFlames;
    VerticalFlameLine bottomFlames;

    List<MovableEntity> killed_enemy_list = new ArrayList<>();

    public Bomb(int xUnit, int yUnit, Image img, int owner_ID, int range_unit) {
        super(xUnit, yUnit, img);
        this.owner_ID = owner_ID;
        this.range_unit = range_unit;
        this.start_time = BombermanGame.getTime();
    }

    public boolean isDone() {
        return this.done;
    }

    public boolean hasExploded() {
        return this.exploded;
    }

    public void setTimeOver(boolean timeOver) {
        this.timeOver = timeOver;
    }

    public void setStartTime(int t) {
        this.start_time = t;
    }

    @Override
    public void update() {
        imageUpdate();

        //timer
        // stop getting time only when the game is paused.
        this.current_time = BombermanGame.getTime();

        if (countdownStopped) {
            countdownStopped = false;
            start_time = current_time - counted_time;
        }


        if (timeOver && !done) {
            if (!exploded) {
                explode();
                this.flame_start_time = BombermanGame.getTime();
            }

            //timer
            //countdown until the time completing all bomb's operations, and then it's deleted.
            double remainingTime = FlameLine.EXISTENCE_TIME + this.flame_start_time - this.current_time;

            //when program's timer reach to max value and turn back to 0
            if (this.flame_start_time > this.current_time) {
                remainingTime = FlameLine.EXISTENCE_TIME
                        - (BombermanGame.TIME_COUNT_MAX - this.flame_start_time)
                        - this.current_time;
            }

            if (remainingTime <= 0) {
                this.done = true;
            }

            return;
        }

        countdown();
    }

    @Override
    public void render(GraphicsContext gc) {
        try {
            super.render(gc);

            if (timeOver && !done) {
                showFlames(gc);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void showFlames(GraphicsContext gc) throws NullPointerException {
        leftFlames.render(gc);
        rightFlames.render(gc);
        bottomFlames.render(gc);
        topFlames.render(gc);
    }

    private void imageUpdate() {
        if (done) {
            this.img = null;
        }
        else if (timeOver) {
            double exploding_time = this.current_time - this.start_time;

            //if TIMER reach to maximum value and turn back to 0:
            if (this.start_time > this.current_time) {
                exploding_time = (BombermanGame.TIME_COUNT_MAX - this.start_time) + this.current_time;
            }


            if (exploding_time <= FlameLine.TIME_FOREACH_IMAGE) {
                this.img = ImageLists.bombExplodedImages.get(0);
            }
            else if (exploding_time <= 2 * FlameLine.TIME_FOREACH_IMAGE) {
                this.img = ImageLists.bombExplodedImages.get(1);
            }
            else {
                this.img = ImageLists.bombExplodedImages.get(2);
            }
        }
        else {
            this.img = Sprite.movingImage(ImageLists.bombImages.get(0),
                    ImageLists.bombImages.get(1),
                    ImageLists.bombImages.get(2),
                    (int) BombermanGame.getTime(), TIME_FOREACH_IMAGE);
        }

    }

    private void countdown() {
        //timer
        this.counted_time = this.current_time - this.start_time;
        double remaining_time = EXISTENCE_TIME - this.counted_time;

//        if (remaining_time % 1 <= 0.015) {
//            System.out.println("Countdown " + (int) remaining_time);
//        }

        if (remaining_time <= 0) { //10, 100... is time's Math.pow(10, 3).
            this.timeOver = true;

            this.flame_start_time = BombermanGame.getTime();

            explode();
        }
    }

    private void explode() {
        burn();
        throwFlame();
        exploded = true;
    }

    private void throwFlame() {
        try {
            leftFlames = new HorizontalFlameLine(x, y, Movement.LEFT, range_unit);
            rightFlames = new HorizontalFlameLine(x, y, Movement.RIGHT, range_unit);
            bottomFlames = new VerticalFlameLine(x, y, Movement.DOWN, range_unit);
            topFlames = new VerticalFlameLine(x, y, Movement.UP, range_unit);

            bottomFlames.update();
            killed_enemy_list.addAll(bottomFlames.getKilledEnemies());

            topFlames.update();
            killed_enemy_list.addAll(topFlames.getKilledEnemies());

            rightFlames.update();
            killed_enemy_list.addAll(rightFlames.getKilledEnemies());

            leftFlames.update();
            killed_enemy_list.addAll(leftFlames.getKilledEnemies());

            int index = 0;
            while (index < killed_enemy_list.size()) {
                for (; index < killed_enemy_list.size(); index++) {
                    if (killed_enemy_list.get(index) instanceof Bomber) {
                        if (((Bomber) killed_enemy_list.get(index)).getID() == this.owner_ID) {
                            killed_enemy_list.remove(index);
                            break;
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    //will be replaced by horizontal and vertical flame line; throwFlame() will also be shortened in the next update.
    private void burn() {
        MovableEntity e = GameMap.getMovableEntityAt(null, x, y);
        if (e != null) {
            e.setDead(true);
            killed_enemy_list.add(e);
        }
    }

    public List<MovableEntity> getKilledEnemies() {
        return this.killed_enemy_list;
    }

    public int getOwnerID() {
        return this.owner_ID;
    }

    public void stopCountdown() {
        this.countdownStopped = true;
    }
}
