package uet.oop.bomberman.entities.StillEntities;

import javafx.scene.image.Image;
import uet.oop.bomberman.entities.Entity;

public abstract class BreakableStillObject extends Entity {
    //objects respectively turn its state from broken to deleted if being damaged.
    protected boolean broken = false;
    protected boolean deleted = false;

    protected final double TIME_FOREACH_IMAGE = 0.3;
    protected double break_start_time;

    public BreakableStillObject(int xUnit, int yUnit, Image img) {
        super(xUnit, yUnit, img);

    }

    public boolean isBroken() {
        return broken;
    }

    public void setBroken(boolean broken) {
        this.broken = broken;
    }

    public boolean isDeleted() {
        return deleted;
    }

    protected abstract void updateImage();
}
