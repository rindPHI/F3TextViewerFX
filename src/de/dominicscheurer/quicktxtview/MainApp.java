/* This file is part of F3TextViewerFX.
 * 
 * F3TextViewer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * F3TextViewer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with F3TextViewer.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Copyright 2015 by Dominic Scheurer <dscheurer@dominic-scheurer.de>.
 */

package de.dominicscheurer.quicktxtview;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import de.dominicscheurer.quicktxtview.model.FileSize;
import de.dominicscheurer.quicktxtview.model.FileSize.FileSizeUnits;
import de.dominicscheurer.quicktxtview.view.CharsetSettingsDialogController;
import de.dominicscheurer.quicktxtview.view.FileViewerController;
import de.dominicscheurer.quicktxtview.view.MaxFileSizeSettingsDialogController;
import de.dominicscheurer.quicktxtview.view.RootLayoutController;

public class MainApp extends Application {

	private static final FileSize STD_FILESIZE_THRESHOLD = new FileSize(5, FileSizeUnits.KB);
	private Stage primaryStage;
	private BorderPane rootLayout;
	private FileViewerController fileViewerCtrl;
	private File initiallyExpandedDirectory = null;

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Quick F3 Text Viewer: Press F3 to see contents of all files in the directory.");

		initRootLayout();

		showFileViewer();
	}

	/**
	 * Initializes the root layout.
	 */
	public void initRootLayout() {
		try {
			// Load root layout from fxml file.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
			rootLayout = (BorderPane) loader.load();
			
			RootLayoutController rootLayoutCtrl = (RootLayoutController) loader.getController();
			rootLayoutCtrl.setMainApp(this);

			// Show the scene containing the root layout.
			Scene scene = new Scene(rootLayout);
			
			scene.setOnKeyReleased(event -> {if (event.getCode().equals(KeyCode.F3)) fileViewerCtrl.toggleInShowContentsMode();});
			
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Shows the person overview inside the root layout.
	 */
	public void showFileViewer() {
		try {
			FXMLLoader loader = new FXMLLoader();
			AnchorPane fileSystemView = (AnchorPane) loader.load(getClass().getResourceAsStream("view/FileViewer.fxml"));
			
			rootLayout.setCenter(fileSystemView);
			
			fileViewerCtrl = loader.getController();
			
			if (initiallyExpandedDirectory != null) {
				fileViewerCtrl.expandToDirectory(initiallyExpandedDirectory);
			}
			

			Map<String, String> params = getParameters().getNamed();
			File file;
			if (params.containsKey("dir") && (file = new File(params.get("dir")).getAbsoluteFile()).exists()) {
				fileViewerCtrl.expandToDirectory(file);
			}
			else {
				System.out.println("Usage: F3TextViewer [--dir=DIRECTORY_TO_EXPAND]");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @return Returns the chosen file size threshold in bytes or the standard of 5KB.
	 */
	public FileSize showMaxFileSizeSettingsDialog() {
		try {
			FXMLLoader loader = new FXMLLoader();
			AnchorPane page = (AnchorPane) loader.load(getClass().getResourceAsStream("view/MaxFileSizeSettingsDialog.fxml"));
			
			// Create the dialog Stage.
	        Stage dialogStage = new Stage();
	        dialogStage.setTitle("Choose file size threshold");
	        dialogStage.initModality(Modality.WINDOW_MODAL);
	        dialogStage.initOwner(primaryStage);
	        Scene scene = new Scene(page);
	        dialogStage.setScene(scene);
	        
	        MaxFileSizeSettingsDialogController controller = loader.getController();
	        controller.setMaxFileSizeInitially(fileViewerCtrl.getFileSizeThreshold());
	        controller.setDialogStage(dialogStage);
	        
	        dialogStage.showAndWait();
	        
	        if (controller.isOkClicked()) {
	        	return controller.getMaxFileSize();
	        }
	        
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return STD_FILESIZE_THRESHOLD;
	}
	
	/**
	 * @return Returns the chosen file size threshold in bytes or the standard of 5KB.
	 */
	public Charset showCharsetSettingsDialog() {
		try {
			FXMLLoader loader = new FXMLLoader();
			AnchorPane page = (AnchorPane) loader.load(getClass().getResourceAsStream("view/CharsetSettingsDialog.fxml"));
			
			// Create the dialog Stage.
	        Stage dialogStage = new Stage();
	        dialogStage.setTitle("Choose charset");
	        dialogStage.initModality(Modality.WINDOW_MODAL);
	        dialogStage.initOwner(primaryStage);
	        Scene scene = new Scene(page);
	        dialogStage.setScene(scene);
	        
	        CharsetSettingsDialogController controller = loader.getController();
	        controller.setSelectedCharsetInitially(fileViewerCtrl.getCharset());
	        controller.setDialogStage(dialogStage);
	        
	        dialogStage.showAndWait();
	        
	        if (controller.isOkClicked()) {
	        	return controller.getSelectedCharset();
	        }
	        
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return fileViewerCtrl.getCharset();
	}

	/**
	 * Returns the main stage.
	 * 
	 * @return
	 */
	public Stage getPrimaryStage() {
		return primaryStage;
	}
	
	public FileViewerController getFileViewerCtrl() {
		return fileViewerCtrl;
	}

	public static void main(String[] args) {
		launch(args);
	}
}
