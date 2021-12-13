package uet.oop.bomberman.entities.Bomb;

import uet.oop.bomberman.Base.GameMap;
import uet.oop.bomberman.entities.Entity;
import uet.oop.bomberman.Motion.Movement;
import uet.oop.bomberman.Base.Point;
import uet.oop.bomberman.entities.MovableEntities.MovableEntity;
import uet.oop.bomberman.entities.StillEntities.BreakableStillObject;
import uet.oop.bomberman.entities.StillEntities.Brick;
import uet.oop.bomberman.entities.StillEntities.StableStillObject.Wall;
import uet.oop.bomberman.graphics.ImageLists;
import uet.oop.bomberman.graphics.Sprite;

public class HorizontalFlameLine extends FlameLine {
    public HorizontalFlameLine(int xOrigin, int yOrigin, int direction, int rangeUnit, boolean canPassWalls)
            throws Exception {

        super(xOrigin, yOrigin, rangeUnit, canPassWalls);

        if (direction != Movement.LEFT && direction != Movement.RIGHT) {
            throw new Exception("HorizontalFlameLine Construction failed");
        }

        this.flame_images = ImageLists.explosionHorizontalImages;

        if (direction == Movement.LEFT) last_flame_images = ImageLists.explosionHorizontalLeftLastImages;
        if (direction == Movement.RIGHT) last_flame_images = ImageLists.explosionHorizontalRightLastImages;

        this.direction = direction;

        addAllFlameSegments();
    }

    @Override
    protected void addAllFlameSegments() {
        if (this.direction == Movement.RIGHT) {
            int i = 1;
            for (; i < range_unit; i++) {
                //check whether there's a Wall or a Brick laying on flame line.
                if (!wallPass && GameMap.containsStillObjectAt(x + i * Sprite.SCALED_SIZE, y)) {

                    Entity entity = GameMap.getStillObjectAt(x + i * Sprite.SCALED_SIZE, y);

                    if (entity instanceof Wall || entity instanceof Brick) break;
                }
                flameSegments.add(new Point(x + i * Sprite.SCALED_SIZE, y));
            }
            lastFlameSegment = new Point(x + i * Sprite.SCALED_SIZE, y);
        }

        if (this.direction == Movement.LEFT) {
            int i = 1;
            for (; i < range_unit; i++) {
                //check whether there's a Wall or a Brick laying on flame line.
                if (!wallPass && GameMap.containsStillObjectAt(x - i * Sprite.SCALED_SIZE, y)) {

                    Entity entity = GameMap.getStillObjectAt(x - i * Sprite.SCALED_SIZE, y);

                    if (entity instanceof Wall || entity instanceof Brick) break;
                }
                flameSegments.add(new Point(x - i * Sprite.SCALED_SIZE, y));
            }
            lastFlameSegment = new Point(x - i * Sprite.SCALED_SIZE, y);
        }
    }

    @Override
    protected void burn() {
        //temporary variables
        MovableEntity movableEntity;
        BreakableStillObject object;
        Bomb bomb;

        try {
            //body of a flame line
            //still objects able to be broken here is items only.
            for (Point flame : flameSegments) {
                //in case flame's SIZE equals to object's SIZE
                boolean canDiscard = false;

                for (int i = 0; i < Sprite.SCALED_SIZE; i++) {
                    if (canDiscard) break;

                    {
                        movableEntity = GameMap.getMovableEntityAt(null,
                                flame.x + i, flame.y);
                        object = GameMap.getBreakableStillObjectAt(flame.x + i, flame.y);
                        bomb = GameMap.getBombAt(flame.x + i, flame.y);

                        if (object != null) {
                            canDiscard = true;
                            object.setBroken(true);
                        }
                        if (movableEntity != null) {
                            canDiscard = true;
                            if (!movableEntity.isDead()) killed_enemy_list.add(movableEntity);//add once
                            movableEntity.setDead(true);
                        }
                        if (bomb != null) {
                            canDiscard = true;
                            bomb.setTimeOver(true);
                        }
                    }

                    {
                        movableEntity = GameMap.getMovableEntityAt(null,
                                flame.x + i, flame.y + Sprite.SCALED_SIZE - 1);
                        object = GameMap.getBreakableStillObjectAt(flame.x + i,
                                flame.y + Sprite.SCALED_SIZE - 1);
                        bomb = GameMap.getBombAt(flame.x + i, flame.y + Sprite.SCALED_SIZE - 1);

                        if (object != null) {
                            canDiscard = true;
                            object.setBroken(true);
                        }
                        if (movableEntity != null) {
                            canDiscard = true;
                            if (!movableEntity.isDead()) killed_enemy_list.add(movableEntity);
                            movableEntity.setDead(true);
                        }
                        if (bomb != null) {
                            canDiscard = true;
                            bomb.setTimeOver(true);
                        }
                    }
                }
            }

            //last flame
            for (int i = 0; i < Sprite.SCALED_SIZE; i++) {
                {
                    movableEntity = GameMap.getMovableEntityAt(null,
                            lastFlameSegment.x + i, lastFlameSegment.y);
                    object = GameMap.getBreakableStillObjectAt(lastFlameSegment.x + i,
                            lastFlameSegment.y);
                    bomb = GameMap.getBombAt(lastFlameSegment.x + i, lastFlameSegment.y);

                    if (movableEntity != null) {
                        if (!movableEntity.isDead()) killed_enemy_list.add(movableEntity);//add once
                        movableEntity.setDead(true);
                    }
                    if (object != null) object.setBroken(true);
                    if (bomb != null) bomb.setTimeOver(true);
                }

                {
                    movableEntity = GameMap.getMovableEntityAt(null, lastFlameSegment.x + i,
                            lastFlameSegment.y + Sprite.SCALED_SIZE - 1);
                    object = GameMap.getBreakableStillObjectAt(lastFlameSegment.x + i,
                            lastFlameSegment.y + Sprite.SCALED_SIZE - 1);
                    bomb = GameMap.getBombAt(lastFlameSegment.x + i,
                            lastFlameSegment.y + Sprite.SCALED_SIZE - 1);

                    if (movableEntity != null) {
                        if (!movableEntity.isDead()) killed_enemy_list.add(movableEntity);//add once
                        movableEntity.setDead(true);
                    }
                    if (object != null) object.setBroken(true);
                    if (bomb != null) bomb.setTimeOver(true);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
