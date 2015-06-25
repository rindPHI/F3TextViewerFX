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
