module com.coursehelper {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.coursehelper to javafx.fxml;
    exports com.coursehelper;
}
