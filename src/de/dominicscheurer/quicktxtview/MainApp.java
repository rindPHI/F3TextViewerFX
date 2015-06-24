package de.dominicscheurer.quicktxtview;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import de.dominicscheurer.quicktxtview.view.FileViewerController;

public class MainApp extends Application {

	private Stage primaryStage;
	private BorderPane rootLayout;
	private FileViewerController controller;
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

			// Show the scene containing the root layout.
			Scene scene = new Scene(rootLayout);
			
			scene.setOnKeyReleased(event -> {if (event.getCode().equals(KeyCode.F3)) controller.toggleInShowContentsMode();});
			
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
			
			controller = loader.getController();
			
			if (initiallyExpandedDirectory != null) {
				controller.expandToDirectory(initiallyExpandedDirectory);
			}
			

			Map<String, String> params = getParameters().getNamed();
			File file;
			if (params.containsKey("dir") && (file = new File(params.get("dir")).getAbsoluteFile()).exists()) {
				controller.expandToDirectory(file);
			}
			else {
				System.out.println("Usage: F3TextViewer [directoryToExpand]");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the main stage.
	 * 
	 * @return
	 */
	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public static void main(String[] args) {
		launch(args);
	}
}
