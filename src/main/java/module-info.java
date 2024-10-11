module org.testing.project_2_1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires javafx.base;

    opens org.testing.project_2_1 to javafx.fxml;
    exports org.testing.project_2_1;
}