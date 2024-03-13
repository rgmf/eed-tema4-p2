module com.proferoman {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.almasb.fxgl.all;

    opens assets.textures;
    opens assets.sounds;

    opens com.proferoman to javafx.fxml;
    exports com.proferoman;
}
