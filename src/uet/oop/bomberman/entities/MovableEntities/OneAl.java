package uet.oop.bomberman.entities.MovableEntities;

import uet.oop.bomberman.Motion.PlayerPursuitMovement;
import uet.oop.bomberman.Motion.Movement;
import uet.oop.bomberman.graphics.ImageLists;
import uet.oop.bomberman.graphics.Sprite;

import java.util.ArrayList;
import java.util.List;

public class OneAl extends Bot {
    public OneAl(int xUnit, int yUnit, int initialSpeedByPixel, int minRange_approach) {
        super(xUnit, yUnit, Sprite.oneal_right1.getFxImage());

        movement = new PlayerPursuitMovement(this, this.x, this.y, initialSpeedByPixel, minRange_approach);

        imageListArray = new List[4];
        imageListArray[Movement.LEFT] = ImageLists.oneAlLeftImages;
        imageListArray[Movement.RIGHT] = ImageLists.oneAlRightImages;

        deadState_images = new ArrayList<>() {{
            add(Sprite.oneal_dead.getFxImage());
            addAll(ImageLists.mobDeadImages);
        }};
    }
}
