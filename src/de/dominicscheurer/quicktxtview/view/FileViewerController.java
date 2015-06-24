package de.dominicscheurer.quicktxtview.view;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
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
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import de.dominicscheurer.quicktxtview.model.DirectoryTreeItem;

public class FileViewerController {
	private static final String FILE_VIEWER_CSS_FILE = "FileViewer.css";

	private static final String ERROR_TEXT_FIELD_CSS_CLASS = "errorTextField";

	private static final int MAX_FILESIZE_BYTES_TO_DISPLAY = 5 * 1024;

	@FXML
	private TreeView<File> fileSystemView;
	
	@FXML
	private WebView directoryContentView;
	
	@FXML
	private TextField filePatternTextField;
	
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
	}
	
	public void toggleInShowContentsMode() {
		isInShowContentsMode = !isInShowContentsMode;
		showDirectoryContents(fileSystemView.getSelectionModel().getSelectedItem());
	}
	
	public void expandToDirectory(File file) {
		Iterator<Path> it = file.toPath().iterator();
		
		//FIXME: The below root directory selection will not work for Windows systems.
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
			if (file.length() > MAX_FILESIZE_BYTES_TO_DISPLAY) {
				continue;
			}
			
			try {
				byte[] encoded = Files.readAllBytes(file.toPath());
				String contentsString = new String(encoded, Charset.defaultCharset());
				contentsString = contentsString.replace("<", "&lt;");
				contentsString = contentsString.replace(">", "&gt;");
				contentsString = contentsString.replace("\n", "<br/>");
				
				sb.append("<div class=\"entry\"><h3>")
					.append(file.getName())
					.append("</h3>")
					.append("<p>")
					.append(contentsString)
					.append("</p>")
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
				return pathname.isFile() && filePattern.matcher(pathname.getName().toString()).matches();
			}
		});
		
		if (files == null) {
			return new File[0];
		}
		
		Arrays.sort(files, new Comparator<File>() {
			@Override
			public int compare(File o1, File o2) {
				return Long.compare(o1.lastModified(), o2.lastModified());
			}
		});
		
		return files;
	}
	
}
