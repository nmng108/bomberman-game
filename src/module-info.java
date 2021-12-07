module uet.oop.bomberman {
    requires javafx.controls;
    requires javafx.fxml;

//    requires org.controlsfx.controls;
//    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;
//    requires org.json;
    requires java.net.http;

    opens uet.oop.bomberman to javafx.fxml;
    exports uet.oop.bomberman;
    exports uet.oop.bomberman.Base;
    opens uet.oop.bomberman.Base to javafx.fxml;
    exports uet.oop.bomberman.Motion;
    opens uet.oop.bomberman.Motion to javafx.fxml;
}