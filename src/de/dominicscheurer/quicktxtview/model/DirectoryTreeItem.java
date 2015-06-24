package de.dominicscheurer.quicktxtview.model;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

public class DirectoryTreeItem extends TreeItem<File> {
	// We cache whether the File is a leaf or not. A File is a leaf if
	// it is not a directory and does not have any files contained within
	// it. We cache this as isLeaf() is called often, and doing the
	// actual check on File is expensive.
	private boolean isLeaf;

	// We do the children and leaf testing only once, and then set these
	// booleans to false so that we do not check again during this
	// run. A more complete implementation may need to handle more
	// dynamic file system situations (such as where a folder has files
	// added after the TreeView is shown). Again, this is left as an
	// exercise for the reader.
	private boolean isFirstTimeChildren = true;
	private boolean isFirstTimeLeaf = true;

	public DirectoryTreeItem(File file) {
		this.setValue(file);
	}

	@Override
	public ObservableList<TreeItem<File>> getChildren() {
		if (isFirstTimeChildren) {
			isFirstTimeChildren = false;

			// First getChildren() call, so we actually go off and
			// determine the children of the File contained in this TreeItem.
			super.getChildren().setAll(buildChildren(this));
		}
		return super.getChildren();
	}

	@Override
	public boolean isLeaf() {
		if (isFirstTimeLeaf) {
			isFirstTimeLeaf = false;
			isLeaf = getChildren().isEmpty();
		}

		return isLeaf;
	}

	private ObservableList<TreeItem<File>> buildChildren(TreeItem<File> TreeItem) {
		File f = TreeItem.getValue();
		if (f != null && f.isDirectory()) {
			File[] files = f.listFiles(file -> file.isDirectory());
			if (files != null) {
				Arrays.sort(files, new Comparator<File>() {
					@Override
					public int compare(File o1, File o2) {
						return o1.getName().compareTo(o2.getName());
					}
				});
				
				ObservableList<TreeItem<File>> children = FXCollections
						.observableArrayList();

				for (File childFile : files) {
					children.add(new DirectoryTreeItem(new ShortNamedFile(childFile)));
				}

				return children;
			}
		}

		return FXCollections.emptyObservableList();
	}

	public static DirectoryTreeItem[] getFileSystemRoots() {
		File[] roots = File.listRoots();
		DirectoryTreeItem[] result = new DirectoryTreeItem[roots.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = new DirectoryTreeItem(roots[i]);
		}
		
		return result;
	}
	
	private class ShortNamedFile extends File {
		private static final long serialVersionUID = -2583593639733945042L;

		public ShortNamedFile(File file) {
			super(file.getAbsolutePath());
		}
		
		@Override
		public String toString() {
			return getName();
		}
	}
}
