package pattypan;

import javafx.application.Platform;

public class Launcher {
    public static void main(String[] args) {
        // Initialise JavaFX.
        Platform.startup(() -> {});
        Main.main(args);
    }
}
