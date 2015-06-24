package de.dominicscheurer.quicktxtview.view;

import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;

public class RootLayoutController {
	@FXML
	private MenuItem closeMenuItem;
	
	@FXML
	private void initialize() {
		closeMenuItem.setOnAction(event -> {System.exit(0);});
	}
}
