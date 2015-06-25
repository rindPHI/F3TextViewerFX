package de.dominicscheurer.quicktxtview.view;

import de.dominicscheurer.quicktxtview.MainApp;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;

public class RootLayoutController {
	@FXML
	private MenuItem closeMenuItem;
	
	private MainApp mainApp;
	
	@FXML
	private void initialize() {
		closeMenuItem.setOnAction(event -> {System.exit(0);});
	}

	@FXML
	private void handleSetFileSizeThreshold() {
		mainApp.getFileViewerCtrl().setFileSizeThreshold(mainApp.showMaxFileSizeSettingsDialog());
	}
	
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}
}
