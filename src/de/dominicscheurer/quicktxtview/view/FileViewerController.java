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
import java.io.FileFilter;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFText2HTML;

import de.dominicscheurer.quicktxtview.model.DirectoryTreeItem;
import de.dominicscheurer.quicktxtview.model.FileSize;
import de.dominicscheurer.quicktxtview.model.FileSize.FileSizeUnits;

public class FileViewerController {
	public static final Comparator<File> FILE_ACCESS_CMP =
			(f1, f2) -> Long.compare(f1.lastModified(), f2.lastModified());
			
	public static final Comparator<File> FILE_ACCESS_CMP_REVERSE =
			(f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified());

	public static final Comparator<File> FILE_NAME_CMP =
			(f1, f2) -> f1.getName().compareTo(f2.getName());

	public static final Comparator<File> FILE_NAME_CMP_REVERSE =
			(f1, f2) -> f2.getName().compareTo(f1.getName());
	
	private static final String FILE_VIEWER_CSS_FILE = "FileViewer.css";
	private static final String ERROR_TEXT_FIELD_CSS_CLASS = "errorTextField";

	private FileSize fileSizeThreshold = new FileSize(1, FileSizeUnits.MB);
	private Charset charset = Charset.defaultCharset();
	private Comparator<File> fileComparator = FILE_ACCESS_CMP;

	@FXML
	private TreeView<File> fileSystemView;
	
	@FXML
	private WebView directoryContentView;
	
	@FXML
	private TextField filePatternTextField;
	
	@FXML
	private Label fileSizeThresholdLabel;
	
	private boolean isInShowContentsMode = false;
	
	private String fileTreeViewerCSS;
	private Pattern filePattern;
	
	@FXML
	private void initialize() {
		filePattern = Pattern.compile(filePatternTextField.getText());
		
		filePatternTextField.setOnKeyReleased(event -> {
			final String input = filePatternTextField.getText();
			try {
				Pattern p = Pattern.compile(input);
				filePattern = p;
				
				filePatternTextField.getStyleClass().remove(ERROR_TEXT_FIELD_CSS_CLASS);
				
				if (!fileSystemView.getSelectionModel().isEmpty()) {
					showDirectoryContents(fileSystemView.getSelectionModel().getSelectedItem());
				}
			} catch (PatternSyntaxException e) {
				filePatternTextField.getStyleClass().add(ERROR_TEXT_FIELD_CSS_CLASS);
			}
			
			filePatternTextField.applyCss();
		});
		
		fileSystemView.getSelectionModel().selectedItemProperty().addListener(
				(observable, oldValue, newValue) -> showDirectoryContents(newValue));
		
		{
			Scanner s = new Scanner(getClass().getResourceAsStream(FILE_VIEWER_CSS_FILE));
			s.useDelimiter("\\A");
			fileTreeViewerCSS = s.hasNext() ? s.next() : "";
			s.close();
		}
		
		DirectoryTreeItem[] roots = DirectoryTreeItem.getFileSystemRoots();
		if (roots.length > 1) {
			TreeItem<File> dummyRoot = new TreeItem<File>();
			dummyRoot.getChildren().addAll(roots);
			fileSystemView.setShowRoot(false);
			fileSystemView.setRoot(dummyRoot);
		}
		else {
			fileSystemView.setRoot(roots[0]);
		}
		
		fileSystemView.getRoot().setExpanded(true);
		refreshFileSizeLabel();
	}
	
	public void toggleInShowContentsMode() {
		isInShowContentsMode = !isInShowContentsMode;
		refreshFileSystemView();
	}
	
	public void setFileSizeThreshold(FileSize fileSizeThreshold) {
		this.fileSizeThreshold = fileSizeThreshold;
		refreshFileSystemView();
		refreshFileSizeLabel();
	}
	
	public FileSize getFileSizeThreshold() {
		return fileSizeThreshold;
	}
	
	public void setCharset(Charset charset) {
		this.charset = charset;
		refreshFileContentsView();
	}
	
	public Charset getCharset() {
		return charset;
	}

	public Comparator<File> getFileComparator() {
		return fileComparator;
	}

	public void setFileComparator(Comparator<File> fileComparator) {
		this.fileComparator = fileComparator;
		refreshFileContentsView();
	}
	
	private void refreshFileSizeLabel() {
		fileSizeThresholdLabel.setText(fileSizeThreshold.getSize() + " " + fileSizeThreshold.getUnit().toString());
	}
	
	private void refreshFileContentsView() {
		if (!fileSystemView.getSelectionModel().isEmpty()) {
			showDirectoryContents(fileSystemView.getSelectionModel().getSelectedItem());
		}
	}
	
	public void expandToDirectory(File file) {
		Iterator<Path> it = file.toPath().iterator();
		
		//FIXME: The below root directory selection *might* not work for Windows systems.
		// => Do something with `file.toPath().getRoot()`.
		TreeItem<File> currentDir = fileSystemView.getRoot();
		while (it.hasNext()) {
			final String currDirName = it.next().toString();
			FilteredList<TreeItem<File>> matchingChildren =
					currentDir.getChildren().filtered(elem -> elem.getValue().getName().equals(currDirName));
			
			if (matchingChildren.size() == 1) {
				matchingChildren.get(0).setExpanded(true);
				currentDir = matchingChildren.get(0);
			}
		}
		
		fileSystemView.getSelectionModel().clearSelection();
		fileSystemView.getSelectionModel().select(currentDir);
		fileSystemView.scrollTo(fileSystemView.getSelectionModel().getSelectedIndex());
		
	}

	private void showDirectoryContents(TreeItem<File> selectedDirectory) {
		if (selectedDirectory == null) {
			return;
		}
		
		final WebEngine webEngine = directoryContentView.getEngine();
		webEngine.loadContent(!isInShowContentsMode ?
				getFileNamesInDirectoryHTML(selectedDirectory.getValue()) :
			    getFileContentsInDirectoryHTML(selectedDirectory.getValue()));
	}
	
	private void refreshFileSystemView() {
		showDirectoryContents(fileSystemView.getSelectionModel().getSelectedItem());
	}
	
	private String getFileNamesInDirectoryHTML(File directory) {
		final StringBuilder sb = new StringBuilder();
		final DecimalFormat df = new DecimalFormat("0.00");
		final File[] files = listFiles(directory);
		
		if (files == null) {
			return "";
		}
		
		sb.append("<html><head>")
		.append("<style type=\"text/css\">")
			.append(fileTreeViewerCSS)
			.append("</style>")
			.append("</head><body><div id=\"fileList\"><ul>");
		boolean even = false;
		for (File file : files) {
			sb.append("<li class=\"")
				.append(even ? "even" : "odd")
				.append("\"><span class=\"fileName\">")
				.append(file.getName())
				.append("</span> <span class=\"fileSize\">(")
				.append(df.format((float) file.length() / 1024))
				.append("K)</span>")
				.append("</li>");
			
			even = !even;
		}
		sb.append("</ul></div></body></html>");
		
		return sb.toString();
	}
	
	private String getFileContentsInDirectoryHTML(File directory) {
		final StringBuilder sb = new StringBuilder();
		final File[] files = listFiles(directory);
		
		if (files == null) {
			return "";
		}
		
		sb.append("<html>")
			.append("<body>")
			.append("<style type=\"text/css\">")
			.append(fileTreeViewerCSS)
			.append("</style>");
		for (File file : files) {
			try {
				String contentsString;
				
				if (file.getName().endsWith(".pdf")) {
					final PDDocument doc = PDDocument.load(file);
					final StringWriter writer = new StringWriter();
					new PDFText2HTML("UTF-8").writeText(doc, writer);
					
					contentsString = writer.toString();
					
					writer.close();
					doc.close();
				} else {
					byte[] encoded = Files.readAllBytes(file.toPath());
					contentsString = new String(encoded, charset);
					
					contentsString = contentsString.replace("<", "&lt;");
					contentsString = contentsString.replace(">", "&gt;");
					contentsString = contentsString.replace("\n", "<br/>");
				}
				
				sb.append("<div class=\"entry\"><h3>")
					.append(file.getName())
					.append("</h3>")
					.append("<div class=\"content\">")
					.append(contentsString)
					.append("</div>")
					.append("</div>");
			} catch (IOException e) {}
		}
		sb.append("</body></html>");
		
		return sb.toString();
	}

	private File[] listFiles(File directory) {
		if (directory == null) {
			return new File[0];
		}
		
		File[] files = directory.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isFile() &&
						filePattern.matcher(pathname.getName().toString()).matches() &&
						pathname.length() <= fileSizeThreshold.toBytes();
			}
		});
		
		if (files == null) {
			return new File[0];
		}
		
		Arrays.sort(files, fileComparator);
		
		return files;
	}
	
}
