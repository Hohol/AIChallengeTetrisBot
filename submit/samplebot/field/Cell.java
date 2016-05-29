// Copyright 2015 theaigames.com (developers@theaigames.com)

//    Licensed under the Apache License, Version 2.0 (the "License");
//    you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at

//        http://www.apache.org/licenses/LICENSE-2.0

//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//    See the License for the specific language governing permissions and
//    limitations under the License.
//	
//    For the full copyright and license information, please view the LICENSE
//    file that was distributed with this source code.

package samplebot.field;

import java.awt.Point;

/**
 * Cell class
 * 
 * Represents one Cell in the playing field.
 * Has some basic methods already implemented.
 * 
 * @author Jim van Eeden <jim@starapple.nl>
 */

public class Cell {

	private Point location;
	private CellType state;
	
	public Cell() {
		this.location = null;
		this.state = CellType.EMPTY;
	}

	public Cell(int x, int y, CellType type) {
		this.location = new Point(x, y);
		this.state = type;
	}
	
	public boolean isOutOfBoundaries(Field f) {
		if(this.location.x >= f.getWidth() || this.location.x < 0 || this.location.y >= f.getHeight()) 
			return true;
		return false;
	}

	public boolean hasCollision(Field f) {
		Cell cell = f.getCell(this.location.x, this.location.y);
		if(cell == null)
			return false;
		return (this.state == CellType.SHAPE && (cell.isSolid() || cell.isBlock()));
	}
	
	public void setShape() {
		this.state = CellType.SHAPE;
	}
	
	public void setLocation(int x, int y) {
		if(this.location == null)
			this.location = new Point();
		
		this.location.setLocation(x, y);
	}
	
	public boolean isShape() {
		return this.state == CellType.SHAPE;
	}
	
	public boolean isSolid() {
		return this.state == CellType.SOLID;
	}
	
	public boolean isBlock() {
		return this.state == CellType.BLOCK;
	}
	
	public boolean isEmpty() {
		return this.state == CellType.EMPTY;
	}

	public CellType getState() {
		return this.state;
	}
	
	public Point getLocation() {
		return this.location;
	}
}
