package uet.oop.bomberman.graphics;

import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;

public class ImageLists {
    public static List<Image> player1MovingUpImages = new ArrayList<>() {{
        add(Sprite.player1_up.getFxImage());
        add(Sprite.player1_up_1.getFxImage());
        add(Sprite.player1_up_2.getFxImage());
    }};

    public static List<Image> player1MovingDownImages = new ArrayList<>() {{
        add(Sprite.player1_down.getFxImage());
        add(Sprite.player1_down_1.getFxImage());
        add(Sprite.player1_down_2.getFxImage());
    }};

    public static List<Image> player1MovingLeftImages = new ArrayList<>() {{
        add(Sprite.player1_left.getFxImage());
        add(Sprite.player1_left_1.getFxImage());
        add(Sprite.player1_left_2.getFxImage());
    }};

    public static List<Image> player1MovingRightImages = new ArrayList<>() {{
        add(Sprite.player1_right.getFxImage());
        add(Sprite.player1_right_1.getFxImage());
        add(Sprite.player1_right_2.getFxImage());
    }};

    public static List<Image> player1DeadImages = new ArrayList<>() {{
        add(Sprite.player1_dead1.getFxImage());
        add(Sprite.player1_dead2.getFxImage());
        add(Sprite.player1_dead3.getFxImage());
    }};


    public static List<Image> player2MovingUpImages = new ArrayList<>() {{
        add(Sprite.player2_up.getFxImage());
        add(Sprite.player2_up_1.getFxImage());
        add(Sprite.player2_up_2.getFxImage());
    }};

    public static List<Image> player2MovingDownImages = new ArrayList<>() {{
        add(Sprite.player2_down.getFxImage());
        add(Sprite.player2_down_1.getFxImage());
        add(Sprite.player2_down_2.getFxImage());
    }};

    public static List<Image> player2MovingLeftImages = new ArrayList<>() {{
        add(Sprite.player2_left.getFxImage());
        add(Sprite.player2_left_1.getFxImage());
        add(Sprite.player2_left_2.getFxImage());
    }};

    public static List<Image> player2MovingRightImages = new ArrayList<>() {{
        add(Sprite.player2_right.getFxImage());
        add(Sprite.player2_right_1.getFxImage());
        add(Sprite.player2_right_2.getFxImage());
    }};

    public static List<Image> player2DeadImages = new ArrayList<>() {{
        add(Sprite.player2_dead1.getFxImage());
        add(Sprite.player2_dead2.getFxImage());
        add(Sprite.player2_dead3.getFxImage());
    }};


    public static List<Image> balloonLeftImages = new ArrayList<>() {{
        add(Sprite.balloom_left1.getFxImage());
        add(Sprite.balloom_left2.getFxImage());
        add(Sprite.balloom_left3.getFxImage());
    }};

    public static List<Image> balloonRightImages = new ArrayList<>() {{
        add(Sprite.balloom_right1.getFxImage());
        add(Sprite.balloom_right2.getFxImage());
        add(Sprite.balloom_right3.getFxImage());
    }};

    public static List<Image> bombImages = new ArrayList<>() {{
        add(Sprite.bomb.getFxImage());
        add(Sprite.bomb_1.getFxImage());
        add(Sprite.bomb_2.getFxImage());
    }};

    public static List<Image> bombExplodedImages = new ArrayList<>() {{
       add(Sprite.bomb_exploded.getFxImage());
       add(Sprite.bomb_exploded1.getFxImage());
       add(Sprite.bomb_exploded2.getFxImage());
    }};

    public static List<Image> explosionHorizontalImages = new ArrayList<>() {{
       add(Sprite.explosion_horizontal.getFxImage());
       add(Sprite.explosion_horizontal1.getFxImage());
       add(Sprite.explosion_horizontal2.getFxImage());
    }};

    public static List<Image> explosionVerticalImages = new ArrayList<>() {{
       add(Sprite.explosion_vertical.getFxImage());
       add(Sprite.explosion_vertical1.getFxImage());
       add(Sprite.explosion_vertical2.getFxImage());
    }};

    public static List<Image> explosionHorizontalLeftLastImages = new ArrayList<>() {{
       add(Sprite.explosion_horizontal_left_last.getFxImage());
       add(Sprite.explosion_horizontal_left_last1.getFxImage());
       add(Sprite.explosion_horizontal_left_last2.getFxImage());
    }};

    public static List<Image> explosionHorizontalRightLastImages = new ArrayList<>() {{
       add(Sprite.explosion_horizontal_right_last.getFxImage());
       add(Sprite.explosion_horizontal_right_last1.getFxImage());
       add(Sprite.explosion_horizontal_right_last2.getFxImage());
    }};

    public static List<Image> explosionVerticalTopLastImages = new ArrayList<>() {{
       add(Sprite.explosion_vertical_top_last.getFxImage());
       add(Sprite.explosion_vertical_top_last1.getFxImage());
       add(Sprite.explosion_vertical_top_last2.getFxImage());
    }};

    public static List<Image> explosionVerticalBottomLastImages = new ArrayList<>() {{
       add(Sprite.explosion_vertical_down_last.getFxImage());
       add(Sprite.explosion_vertical_down_last1.getFxImage());
       add(Sprite.explosion_vertical_down_last2.getFxImage());
    }};

    public static List<Image> oneAlLeftImages = new ArrayList<>() {{
        add(Sprite.oneal_left1.getFxImage());
        add(Sprite.oneal_left2.getFxImage());
        add(Sprite.oneal_left3.getFxImage());
    }};

    public static List<Image> oneAlRightImages = new ArrayList<>() {{
        add(Sprite.oneal_right1.getFxImage());
        add(Sprite.oneal_right2.getFxImage());
        add(Sprite.oneal_right3.getFxImage());
    }};

    public static List<Image> dollLeftImages = new ArrayList<>() {{
        add(Sprite.doll_left1.getFxImage());
        add(Sprite.doll_left2.getFxImage());
        add(Sprite.doll_left3.getFxImage());
    }};

    public static List<Image> dollRightImages = new ArrayList<>() {{
        add(Sprite.doll_right1.getFxImage());
        add(Sprite.doll_right2.getFxImage());
        add(Sprite.doll_right3.getFxImage());
    }};

    public static List<Image> mobDeadImages = new ArrayList<>() {{
        add(Sprite.mob_dead1.getFxImage());
        add(Sprite.mob_dead2.getFxImage());
        add(Sprite.mob_dead3.getFxImage());
    }};
}