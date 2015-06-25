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

public class FileSize {

	public static enum FileSizeUnits {
		BYTE("Bytes", 1), KB("KB", 1024), MB("MB", 1024 * 1024);

		private String name;
		private int multiplicator;

		private FileSizeUnits(String name, int multiplicator) {
			this.name = name;
			this.multiplicator = multiplicator;
		}

		@Override
		public String toString() {
			return name;
		}

		public int toBytes(int quantity) {
			return quantity * multiplicator;
		}
	}
	
	private int size;
	private FileSizeUnits unit;
	
	public FileSize(int size, FileSizeUnits unit) {
		super();
		this.size = size;
		this.unit = unit;
	}

	public int getSize() {
		return size;
	}

	public FileSizeUnits getUnit() {
		return unit;
	}
	
	public int toBytes() {
		return unit.toBytes(size);
	}

}
