module furkan.coit20258_assignment2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.base;
    requires java.sql;
    exports Controller;  // Export the package containing your controllers
    opens Controller to javafx.fxml;  // Open the package to JavaFX's FXMLLoader

    opens furkan.coit20258_assignment2 to javafx.fxml;
    exports furkan.coit20258_assignment2;
    
    opens Model to javafx.base;
}
