/*
 * "Copyright (c) 2005-2006 The Regents of the Centro de Investigación y de
 * Educación Superior de la ciudad de Ensenada, Baja California (CICESE).
 *
 * Permission to use, copy, modify, and distribute this software and its
 * documentation for any purpose, without fee, and without written agreement is
 * hereby granted, provided that the above copyright notice, the following
 * two paragraphs and the author appear in all copies of this software.
 * 
 * IN NO EVENT SHALL CICESE BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT,
 * SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES ARISING OUT OF THE USE OF THIS
 * SOFTWARE AND ITS DOCUMENTATION, EVEN IF CICESE HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * CICESE SPECIFICALLY DISCLAIMS ANY WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE.  THE SOFTWARE PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND CICESE
 * HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS."
 * 
 ******************************************************************************/

package net.tinyos.tinysoa.util;

import java.awt.*;
import java.text.*;

/*******************************************************************************
 * Handles node information in the topology chart
 * 
 * @author		Edgardo Avilés López
 * @version	0.1, 07/25/2006
 ******************************************************************************/
public class NodeTopologyChart {
	
	private Point p;
	private double v;
	private int id;
	
	/***************************************************************************
	 * Basic class constructor
	 * 
	 * @param id	ID del nodo
	 **************************************************************************/
	public NodeTopologyChart(int id) {
		this.id = id;
		this.v = 0.0d;
		this.p = new Point(0, 0);
	}
	
	/***************************************************************************
	 * Regular class constructor
	 * 
	 * @param id	Node ID
	 * @param v	Node value
	 **************************************************************************/
	public NodeTopologyChart(int id, double v) {
		this.id = id;
		this.v = v;
		this.p = new Point(0, 0);
	}
	
	/***************************************************************************
	 * Detailed class constructor
	 * 
	 * @param id	Node ID
	 * @param v	Node value
	 * @param x	Value of X in node position
	 * @param y	Value of y in node position
	 **************************************************************************/
	public NodeTopologyChart(int id, double v, int x, int y) {
		this.id = id;
		this.v = v;
		this.p = new Point(x, y);
	}
	
	/***************************************************************************
	 * Sets node position in the chart
	 * 
	 * @param x	Value of X in node position
	 * @param y	Value of y in node position
	 **************************************************************************/
	public void setPosition(int x, int y) {
		p.x = x;
		p.y = y;
	}
	
	/***************************************************************************
	 * Sets node position
	 * 
	 * @param p	Point representing the node position in the chart
	 **************************************************************************/
	public void setPosition(Point p) {
		this.p.x = p.x;
		this.p.y = p.y;
	}
	
	/***************************************************************************
	 * Gets node position in the chart
	 * 
	 * @return	Node position
	 **************************************************************************/
	public Point getPosition() {
		return p;
	}

	/***************************************************************************
	 * Sets the node ID
	 * 
	 * @param id	Node ID
	 **************************************************************************/
	public void setId(int id) {
		this.id = id;
	}
	
	/***************************************************************************
	 * Gets the node ID
	 * 
	 * @return	Node ID
	 **************************************************************************/
	public int getId() {
		return id;
	}
	
	/***************************************************************************
	 * Sets the node value
	 * 
	 * @param v	Node value
	 **************************************************************************/
	public void setValue(double v) {
		this.v = v;
	}
	
	/***************************************************************************
	 * Gets the node value
	 * 
	 * @return Node value
	 **************************************************************************/
	public double getValue() {
		return v;
	}
	
	/***************************************************************************
	 * Returns a string representation of the node values
	 **************************************************************************/
	public String toString() {
		NumberFormat f = new DecimalFormat("0.00");
		return id + ": (" + p.x + "," + p.y + ")," + f.format(v);
	}
	
}
