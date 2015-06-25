/* This file is part of F3TextViewerFX.
 * 
 * F3TextViewerFX is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * F3TextViewerFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with F3TextViewerFX.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Copyright 2015 by Dominic Scheurer <dscheurer@dominic-scheurer.de>.
 */

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
