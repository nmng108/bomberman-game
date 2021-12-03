package uet.oop.bomberman.entities;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import uet.oop.bomberman.BombermanGame;
import uet.oop.bomberman.Base.GameMap;
import uet.oop.bomberman.entities.Motion.Movement;
import uet.oop.bomberman.graphics.ImageLists;
import uet.oop.bomberman.graphics.Sprite;

public class Bomb extends Entity {
    private final double EXISTENCE_TIME = 4;
    private final double TIME_FOREACH_IMAGE = 0.6;

    private double start_time = 0;
    private double flame_start_time = 0;
    private int range_unit = 1;

    private boolean done = false;
    private boolean timeOver = false;
    private boolean exploded = false; //used internally in this class

    HorizontalFlameLine leftFlames;
    HorizontalFlameLine rightFlames;
    VerticalFlameLine topFlames;
    VerticalFlameLine bottomFlames;

    public Bomb(int xUnit, int yUnit, Image img, int range_unit) {
        super(xUnit, yUnit, img);
        this.range_unit = range_unit;
        this.start_time = BombermanGame.getTime();

    }

    public boolean isDone() {
        return this.done;
    }

    public void setTimeOver(boolean timeOver) {
        this.timeOver = timeOver;
    }

    @Override
    public void update() {
        imageUpdate();

        if (timeOver && !done) {
            if (!exploded) {
                explode();
                this.flame_start_time = BombermanGame.getTime();
            }

            //timer
            //countdown until the time completing all bomb's operations, and then it's deleted.
            double current_time = BombermanGame.getTime();
            double remainingTime = FlameLine.EXISTENCE_TIME + this.flame_start_time - current_time;

            //when program's timer reach to max value and turn back to 0
            if (this.flame_start_time > current_time) {
                remainingTime = FlameLine.EXISTENCE_TIME
                        - (BombermanGame.TIME_COUNT_MAX - this.flame_start_time)
                        - current_time;
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
        super.render(gc);
        if (timeOver && !done) {
            showFlames(gc);
        }
    }

    private void showFlames(GraphicsContext gc) {
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
            double current_time = BombermanGame.getTime();
            double exploding_time = current_time - this.start_time;

            //if TIMER reach to maximum value and turn back to 0:
            if (this.start_time > current_time) {
                exploding_time = (BombermanGame.TIME_COUNT_MAX - this.start_time) + current_time;
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
        double current_time = BombermanGame.getTime();
        double remaining_time = EXISTENCE_TIME + this.start_time - current_time;

        //when program's timer reach to max value and turn back to 0
        if (this.start_time > current_time) {
            remaining_time = EXISTENCE_TIME - (BombermanGame.TIME_COUNT_MAX - this.start_time) - current_time;
        }

        if (remaining_time % 1 <= 0.015) {
            System.out.println("Countdown " + (int) remaining_time);
        }

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
            topFlames.update();
            rightFlames.update();
            leftFlames.update();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void burn() {
        if (GameMap.containsSoftObjectAt(null, x, y)) {
            GameMap.getMovableEntityAt(null, x, y).setDead(true);
        }
    }
}
