# F3TextViewerFX

Simple text viewer for quickly inspecting the content of all text files in a directory at once.

## Build

1. Open build.xml and insert the path to an existing Eclipse installation to the property ECLIPSE_HOME
2. Run `./build.sh` (Linux) in the project directory.

## Usage

1. Start the program with `java -jar F3TextViewerFX.jar [--dir=DIRECTORY_TO_EXPAND]`.
2. Navigate to the directory to inspect; you will be presented the contained files in the right pane.
3. Press **F3** to switch from *file name mode* to *file contents mode*.

## License

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
