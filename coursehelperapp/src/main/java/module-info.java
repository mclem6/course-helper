module com.coursehelper {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires transitive java.sql;
    requires javafx.base;

    opens com.coursehelper to javafx.fxml;
    exports com.coursehelper;
}
