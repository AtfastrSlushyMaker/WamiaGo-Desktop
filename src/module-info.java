module org.wamiago.wamiago {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires java.sql;

    opens org.wamiago.wamiago to javafx.fxml;
    exports org.wamiago.wamiago;
    exports org.wamiago.wamiago.test;
    opens org.wamiago.wamiago.test to javafx.fxml;
}