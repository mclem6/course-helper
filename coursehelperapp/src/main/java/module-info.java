module com.coursehelper {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires transitive javafx.graphics;

    opens com.coursehelper to javafx.fxml;
    exports com.coursehelper;
}
