package uet.oop.bomberman.entities.MovableEntities;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import uet.oop.bomberman.Base.Point;
import uet.oop.bomberman.BombermanGame;
import uet.oop.bomberman.Motion.EnhancedRandomMovement;
import uet.oop.bomberman.Motion.Movement;import uet.oop.bomberman.graphics.ImageLists;
import uet.oop.bomberman.graphics.Sprite;

import java.util.ArrayList;
import java.util.List;

public class Doll extends Bot {
    public Doll(int xUnit, int yUnit, int initialSpeedByPixel) {
        super(xUnit, yUnit, Sprite.doll_left1.getFxImage());

        movement = new EnhancedRandomMovement(this, this.x, this.y, initialSpeedByPixel);

        imageListArray = new List[2];
        imageListArray[Movement.LEFT] = ImageLists.dollLeftImages;
        imageListArray[Movement.RIGHT] = ImageLists.dollRightImages;

        deadState_images = new ArrayList<>() {{
            add(Sprite.doll_dead.getFxImage());
            addAll(ImageLists.mobDeadImages);
        }};

    }
}
