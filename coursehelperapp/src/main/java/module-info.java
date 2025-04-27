module com.coursehelper {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;

    opens com.coursehelper to javafx.fxml;
    exports com.coursehelper;
}
