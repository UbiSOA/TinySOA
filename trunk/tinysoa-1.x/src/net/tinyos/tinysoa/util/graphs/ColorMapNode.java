/*
 *  Copyright 2006 Edgardo Avilés López
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *    
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * 
 ******************************************************************************/

package net.tinyos.tinysoa.util.graphs;

import java.awt.*;
import java.text.*;

/*******************************************************************************
 * Handles and stores the information of a node in the color map.
 * 
 * @author		Edgardo Avilés López
 * @version	0.1, 07/25/2006
 ******************************************************************************/
public class ColorMapNode {
	
	private Point p;
	private double v;
	private int id;
	
	/***************************************************************************
	 * Basic class constructor.
	 * 
	 * @param id	Node ID
	 **************************************************************************/
	public ColorMapNode(int id) {
		this.id = id;
		this.v = 0.0d;
		this.p = new Point(0, 0);
	}
	
	/***************************************************************************
	 * Regular class constructor.
	 * 
	 * @param id	Node ID
	 * @param v		Node value
	 **************************************************************************/
	public ColorMapNode(int id, double v) {
		this.id = id;
		this.v = v;
		this.p = new Point(0, 0);
	}
	
	/***************************************************************************
	 * Detailed class constructor.
	 * 
	 * @param id	Node ID
	 * @param v		Node value
	 * @param x		Node X position in the map
	 * @param y		Node Y position in the map
	 **************************************************************************/
	public ColorMapNode(int id, double v, int x, int y) {
		this.id = id;
		this.v = v;
		this.p = new Point(x, y);
	}
	
	/***************************************************************************
	 * Sets node position in the map.
	 * 
	 * @param x	Node X axis position
	 * @param y	Node Y axis position
	 **************************************************************************/
	public void setPosition(int x, int y) {
		p.x = x;
		p.y = y;
	}
	
	/***************************************************************************
	 * Sets node position in the map.
	 * 
	 * @param p	Node coordinates in the map
	 **************************************************************************/
	public void setPosition(Point p) {
		this.p.x = p.x;
		this.p.y = p.y;
	}
	
	/***************************************************************************
	 * Gets node position in the map.
	 * 
	 * @return	Node coordinates in the map
	 **************************************************************************/
	public Point getPosition() {
		return p;
	}

	/***************************************************************************
	 * Sets the node ID.
	 * 
	 * @param id	Node ID
	 **************************************************************************/
	public void setId(int id) {
		this.id = id;
	}
	
	/***************************************************************************
	 * Gets the node ID.
	 * 
	 * @return	Node ID
	 **************************************************************************/
	public int getId() {
		return id;
	}
	
	/***************************************************************************
	 * Sets the node value.
	 * 
	 * @param v	Node value
	 **************************************************************************/
	public void setValue(double v) {
		this.v = v;
	}
	
	/***************************************************************************
	 * Gets the node value.
	 * 
	 * @return Node value
	 **************************************************************************/
	public double getValue() {
		return v;
	}
	
	/***************************************************************************
	 * Returns a string representation of the node data.
	 **************************************************************************/
	public String toString() {
		NumberFormat f = new DecimalFormat("0.00");
		return id + ": (" + p.x + "," + p.y + ")," + f.format(v);
	}
	
}
