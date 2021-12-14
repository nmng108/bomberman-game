package uet.oop.bomberman.entities.MovableEntities;

import uet.oop.bomberman.BombermanGame;
import uet.oop.bomberman.Motion.Movement;
import uet.oop.bomberman.Base.Point;
import uet.oop.bomberman.Motion.RandomMovement;
import uet.oop.bomberman.graphics.ImageLists;
import uet.oop.bomberman.graphics.Sprite;

import java.util.ArrayList;
import java.util.List;

public class Balloon extends Bot {
    public Balloon(int xUnit, int yUnit, int initialSpeedByPixel) {
        super(xUnit, yUnit, Sprite.balloom_right1.getFxImage());

        movement = new RandomMovement(this, this.x, this.y, initialSpeedByPixel);

        imageListArray = new List[4];
        imageListArray[Movement.LEFT] = ImageLists.balloonLeftImages;
        imageListArray[Movement.RIGHT] = ImageLists.balloonRightImages;

        deadState_images = new ArrayList<>() {{
            add(Sprite.balloom_dead.getFxImage());
            addAll(ImageLists.mobDeadImages);
        }};
    }
}
