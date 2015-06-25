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
