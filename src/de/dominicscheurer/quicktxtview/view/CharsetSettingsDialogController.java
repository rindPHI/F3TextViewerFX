package de.dominicscheurer.quicktxtview.view;

import java.nio.charset.Charset;
import java.util.SortedMap;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;

public class CharsetSettingsDialogController {
	private static final SortedMap<String, Charset> AVAILABLE_CHARSETS = Charset
			.availableCharsets();
	
	@FXML
	private ChoiceBox<String> charsetChoiceBox;

	private boolean okClicked;
	private Stage dialogStage;
	private Charset selectedCharset = Charset.defaultCharset();

	@FXML
	private void initialize() {
		charsetChoiceBox.getItems().addAll(AVAILABLE_CHARSETS.keySet());
	}
	
	public void setSelectedCharsetInitially(Charset initially) {
		int i = 0;
		for (String key : charsetChoiceBox.getItems()) {
			if (AVAILABLE_CHARSETS.get(key).equals(initially)) {
				charsetChoiceBox.getSelectionModel().select(i);
			}
			
			i++;
		}
	}
	
	public Charset getSelectedCharset() {
		return selectedCharset;
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
		if (!charsetChoiceBox.getSelectionModel().isEmpty()) {
			selectedCharset = AVAILABLE_CHARSETS.get(charsetChoiceBox.getSelectionModel().getSelectedItem());

			okClicked = true;
			dialogStage.close();
		} else {
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

}
