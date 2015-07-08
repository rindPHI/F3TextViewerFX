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

import java.io.File;
import java.util.Comparator;

import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import de.dominicscheurer.quicktxtview.MainApp;

public class RootLayoutController {
	@FXML
	private MenuItem closeMenuItem;
	
	@FXML
	private ToggleGroup sortOrderToggleGroup;
	
	private MainApp mainApp;
	
	@FXML
	private void initialize() {
		closeMenuItem.setOnAction(event -> {System.exit(0);});
		sortOrderToggleGroup.selectedToggleProperty().addListener((ov, toggle, newToggle) -> {
			if (sortOrderToggleGroup.getSelectedToggle() != null) {
				final String chosenOption = ((RadioMenuItem) sortOrderToggleGroup.getSelectedToggle()).getId();
				
				final Comparator<File> comparator;
				if (chosenOption.equals("lastAccessed")) {
					comparator = FileViewerController.FILE_ACCESS_CMP;
				} else if (chosenOption.equals("lastAccessedReverse")) {
					comparator = FileViewerController.FILE_ACCESS_CMP_REVERSE;
				} else if (chosenOption.equals("fileNameAsc")) {
					comparator = FileViewerController.FILE_NAME_CMP;
				} else if (chosenOption.equals("fileNameDesc")) {
					comparator = FileViewerController.FILE_NAME_CMP_REVERSE;
				} else {
					comparator = FileViewerController.FILE_ACCESS_CMP;
				}
				
				mainApp.getFileViewerCtrl().setFileComparator(comparator);
			}
		});
	}

	@FXML
	private void handleSetFileSizeThreshold() {
		mainApp.getFileViewerCtrl().setFileSizeThreshold(mainApp.showMaxFileSizeSettingsDialog());
	}

	@FXML
	private void handleSelectCharset() {
		mainApp.getFileViewerCtrl().setCharset(mainApp.showCharsetSettingsDialog());
	}
	
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}
}
