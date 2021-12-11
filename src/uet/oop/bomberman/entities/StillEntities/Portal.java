package uet.oop.bomberman.entities.StillEntities;

import uet.oop.bomberman.Base.GameMap;
import uet.oop.bomberman.entities.MovableEntities.OneAl;
import uet.oop.bomberman.graphics.Sprite;


public class Portal extends BreakableStillObject {
    public Portal(int xUnit, int yUnit) {
        super(xUnit, yUnit, Sprite.portal.getFxImage());
    }

    @Override
    public void update() {
        if (broken) {
            deleted = true;
            GameMap.removeStillObject(this);
            GameMap.addBackGroundObject(this);
            spawnOneAl(2);
        }
    }

    @Override
    protected void updateImage() {
    }

    public void spawnOneAl(int quantity) {
        int xUnit = this.x / Sprite.SCALED_SIZE;
        int yUnit = this.y / Sprite.SCALED_SIZE;

        for (int i = 0; i < quantity; i++) {
            try {
                GameMap.addBot(new OneAl(xUnit, yUnit, 40,
                        100));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
