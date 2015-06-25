package de.dominicscheurer.quicktxtview.view;

import de.dominicscheurer.quicktxtview.model.FileSize;
import de.dominicscheurer.quicktxtview.model.FileSize.FileSizeUnits;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class MaxFileSizeSettingsDialogController {
	@FXML
	private TextField maxFileSizeTextField;

	@FXML
	private ChoiceBox<FileSizeUnits> unitChoiceBox;

	private FileSize maxFileSize;
	private boolean okClicked;

	private Stage dialogStage;

	@FXML
	private void initialize() {
		unitChoiceBox.getItems().add(FileSizeUnits.BYTE);
		unitChoiceBox.getItems().add(FileSizeUnits.KB);
		unitChoiceBox.getItems().add(FileSizeUnits.MB);
		
		unitChoiceBox.getSelectionModel().select(1);
	}

	public void setMaxFileSizeInitially(FileSize maxFileSize) {
		this.maxFileSize = maxFileSize;
		
		maxFileSizeTextField.setText(Integer.toString(maxFileSize.getSize()));
		unitChoiceBox.getSelectionModel().select(unitChoiceBox.getItems().indexOf(maxFileSize.getUnit()));
	}

	public FileSize getMaxFileSize() {
		return maxFileSize;
	}

	/**
	 * Sets the stage of this dialog.
	 * 
	 * @param dialogStage
	 */
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	/**
	 * Returns true if the user clicked OK, false otherwise.
	 * 
	 * @return
	 */
	public boolean isOkClicked() {
		return okClicked;
	}

	/**
	 * Called when the user clicks ok.
	 */
	@FXML
	private void handleOk() {
		if (isInputValid()) {
			maxFileSize = new FileSize(
					Integer.parseInt(maxFileSizeTextField.getText()),
					unitChoiceBox.getSelectionModel().getSelectedItem());

			okClicked = true;
			dialogStage.close();
		}
		else {
			okClicked = false;
		}
	}

	/**
	 * Called when the user clicks cancel.
	 */
	@FXML
	private void handleCancel() {
		dialogStage.close();
	}

	/**
	 * Validates the user input in the text fields.
	 * 
	 * @return true if the input is valid
	 */
	private boolean isInputValid() {
		String errorMessage = "";

		try {
			Integer.parseInt(maxFileSizeTextField.getText());
		} catch (NumberFormatException e) {
			errorMessage = "Enter a positive number for the file size threshold.";
		}

		if (errorMessage.length() == 0) {
			return true;
		} else {
			// Show the error message.
			Alert alert = new Alert(AlertType.ERROR);
			alert.initOwner(dialogStage);
			alert.setTitle("Invalid Fields");
			alert.setHeaderText("Please correct invalid fields");
			alert.setContentText(errorMessage);

			alert.showAndWait();

			return false;
		}
	}
}
