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
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.util.*;
import java.io.*;
import java.text.*;
import javax.imageio.*;
import javax.swing.*;

/*******************************************************************************
 * Class utility for drawing graphical color maps with sensing information.
 * 
 * @author		Edgardo Avilés López
 * @version	0.5, 02/25/2006
 ******************************************************************************/
public class ColorMap extends JPanel
	implements DragGestureListener, DragSourceListener, DropTargetListener {
	private static final long serialVersionUID = 9143159172653372529L;
	
	private double valueMin = -10.0d;	// Minimum value
	private double valueMax = 40.0d;	// Maximum value
	private double valueAvg = 0.0d;		// Average value
	
	private int pixRat	= 10;		// Pixelation effect ratio
	private int ratio	= 1000;		// Radio coverage for the value of a node
	private int drgdNod	= -1;		// Index of node being dragged
	private int diaNod = 20;		// Diameter of an node icon
	
	private BufferedImage buffer, backgr = null;	// Image buffers
	private int movX, movY;			// Coordinate of node being dragged
	private DragSource dragSrc;		// Drag source object
	private DropTarget dragTrg;		// Drag target object
	private int scaleType = SCALE_HEAT;
	private Point[] nodesPos;
	private JProgressBar progress = null;
	private String fileUrl, fileType;
	
	public static int SCALE_HEAT = 0, SCALE_LIGHT = 1, SCALE_ENERGY = 2;
	
	private Vector<ColorMapNode> nodes;
	
	/***************************************************************************
	 * Main constructor.
	 **************************************************************************/
	public ColorMap() {
		super();
		setBackground(Color.WHITE);
		nodes = new Vector<ColorMapNode>();		
		dragSrc = new DragSource();
		dragSrc.createDefaultDragGestureRecognizer(
				this, DnDConstants.ACTION_COPY_OR_MOVE, this);
		dragTrg = new DropTarget(this, this);
	}

	/***************************************************************************
	 * Defines the minimum and maximum values for the data scale.
	 * 
	 * @param valueMin	Minimum value
	 * @param valueMax	Maximum value
	 **************************************************************************/
	public void setDataScale(double valueMin, double valueMax) {
		this.valueMin = valueMin;
		this.valueMax = valueMax;
	}

	/***************************************************************************
	 * Returns the minimum value of the scale.
	 * 
	 * @return	Minimum value of the scale
	 **************************************************************************/
	public double getMinimum() {
		return valueMin;
	}
	
	/***************************************************************************
	 * Returns the maximum value of the scale.
	 * 
	 * @return	Maximum value of the scale
	 **************************************************************************/
	public double getMaximum() {
		return valueMax;
	}
	
	/***************************************************************************
	 * Defines the type of color scale to use in drawing the map.
	 * 
	 * @param scaleType	Type of colos scale to use
	 **************************************************************************/
	public void setScaleType(int typeScale) {
		this.scaleType = typeScale;
	}

	/***************************************************************************
	 * Defines the vector of points where the coordinates for each of the data
	 * nodes are specified.
	 * 
	 * @param nodesPos	Position of nodes in the map
	 **************************************************************************/
	public void setNodesPosition(Point[] nodesPos) {
		this.nodesPos = nodesPos;
	}
	
	/***************************************************************************
	 * Defines the background image to be drawn under the color map. The image
	 * will be resized to fit in the width and height of the map.
	 * 
	 * @param backgr	Background image
	 **************************************************************************/
	public void setBackground(BufferedImage backgr) {
		AffineTransform tx = new AffineTransform();
		double s;
		if (getWidth() >= getHeight())
			s = (double)getWidth() / backgr.getWidth();
		else s = (double)getHeight() / backgr.getHeight();
		System.out.println(s);
	    tx.scale(s, s);
	    AffineTransformOp op = new AffineTransformOp(
	    		tx, AffineTransformOp.TYPE_BILINEAR);
		this.backgr = op.filter(backgr, null);
	}
	
	/***************************************************************************
	 * Defines the progress bar object to be updated on drawing.
	 * 
	 * @param progress	Progress bar object
	 **************************************************************************/
	public void setProgress(JProgressBar progress) {
		this.progress = progress;
	}

	/***************************************************************************
	 * Main drawing method.
	 * 
	 * @param g	 Graphic object to draw into
	 **************************************************************************/
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
	
		if (nodes.size() == 0) {
			g2.setColor(Color.WHITE);
			g2.fillRect(0, 0, getWidth(), getHeight());
			return;
		}
		
		if (drgdNod == -1) {
			buffer = g2.getDeviceConfiguration().createCompatibleImage(
					getWidth(), getHeight(), Transparency.TRANSLUCENT);
			drawImage();
			g2.drawImage(buffer, null, 0, 0);
		}
		else drawDrag(g2);
	}

	/***************************************************************************
	 * Draws the color map image in the buffer.
	 **************************************************************************/
	private void drawImage() {
		Graphics2D g2d = buffer.createGraphics();
		g2d.setComposite(AlphaComposite.Src);
		
		drawBackground(g2d);
		if (nodes != null) {
			drawImgageBackground(g2d);
			drawColorGradient(g2d);
			drawNodes(g2d);
		}
		
		g2d.dispose();
	}
	
	/***************************************************************************
	 * If there is a node being dragged, draws a shadow to show the current
	 * position of the node.
	 * 
	 * @param g2d	Graphic object to draw into
	 **************************************************************************/
	private void drawDrag(Graphics2D g2d) {
		int x, y, id;
		double v;
		
		g2d.drawImage(buffer, null, 0, 0);
		
		x = movX;
		y = movY;
		id = nodes.get(drgdNod).getId();
		v = nodes.get(drgdNod).getValue();
		
		drawNode(g2d, id, x, y, v, 0.35f);
	}
	
	/***************************************************************************
	 * Draws the background image in the center of the color map.
	 * 
	 * @param g2d	Graphic object to draw into
	 **************************************************************************/
	private void drawImgageBackground(Graphics2D g2d) {
		if (backgr == null) return;
		int x = (getWidth() - backgr.getWidth()) / 2;
		int y = (getHeight() - backgr.getHeight()) / 2;
		g2d.drawImage(backgr, null, x, y);
	}
	
	/***************************************************************************
	 * Draws the background elements of the color map.
	 * 
	 * @param g2d	Graphic object to draw into
	 **************************************************************************/
	private void drawBackground(Graphics2D g2d) {
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, getWidth(), getHeight());
	}

	/***************************************************************************
	 * Draw layer color gradient for interpolations of the information sensing.
	 * 
	 * @param g2d	Chart to use
	 **************************************************************************/
	private void drawColorGradient(Graphics2D g2d) {
		double color = 0.0d, distMax, distMin, dist, factor, value,
				valueMen = valueMax, valueMay = valueMin, sumFact;
		
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_OFF);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

		g2d.setComposite(AlphaComposite.getInstance(
				AlphaComposite.SRC_OVER, 0.75f));
		
		ColorMapNode node;
		
		valueAvg = 0.0d;
		for (int k = 0; k < nodes.size(); k++)
			if (nodes.get(k) != null) {
				value = nodes.get(k).getValue();
				if (value < valueMen) valueMen = value;
				if (value > valueMay) valueMay = value;
				valueAvg += value;
			}
		valueAvg /= nodes.size();
		
		for (int i = 0; i < getWidth(); i = i + pixRat)
			for (int j = 0; j < getHeight(); j = j + pixRat) {

				distMin = 10000.0d;
				distMax = 0.0d;
				for (int k = 0; k < nodes.size(); k++)
					if (nodes.get(k) != null) {
						node = nodes.get(k);
						dist = distance(i, j, node.getPosition().x,
								node.getPosition().y);
						if (dist < distMin) distMin = dist;
						if (dist > distMax) distMax = dist;
					}
				
				sumFact = 0.0d;
				for (int k = 0; k < nodes.size(); k++)
					if (nodes.get(k) != null) {
						node = nodes.get(k);
						dist = distance(i, j, node.getPosition().x,
								node.getPosition().y);
						sumFact += distMin / dist;
					}
					
				color = 0.0d;
				for (int k = 0; k < nodes.size(); k++)
					if (nodes.get(k) != null) {
						node = nodes.get(k);
						dist = distance(i, j , node.getPosition().x,
								node.getPosition().y);
						factor = (distMin / dist) / sumFact;
						value = node.getValue();
						value = interpolate(value, valueAvg, 1, 0,
								Math.cos(interpolate(
										0, Math.PI / 2, 0, ratio, dist)));
						value = value * factor;
						color += value;
				}
				
				color = interpolate(0.0d, 1.0d, valueMin, valueMax, color);
				g2d.setColor(scaleColor(color));
				g2d.fillRect(i, j, pixRat, pixRat);			
			}
	}
	
	/***************************************************************************
	 * Draw the nodes in the color map.
	 * 
	 * @param g2d	Graphic object to draw into
	 **************************************************************************/
	private void drawNodes(Graphics2D g2d) {
		int x, y, id;
		double v;
		
		for (int i = 0; i < nodes.size(); i++)
			if (nodes.get(i) != null) {
				ColorMapNode node = nodes.get(i);
				
				x = node.getPosition().x;
				y = node.getPosition().y;
				id = node.getId();
				v = node.getValue();
				drawNode(g2d, id, x, y, v, 1.0f);
			}
	}
	
	/***************************************************************************
	 * Draws a node in the given coordinate and with the given reading value.
	 * 
	 * @param g2d		Graphic object to draw into
	 * @param id		Node ID
	 * @param x			X axis position
	 * @param y			Y axis position
	 * @param v			Value of reading
	 * @param opacity	Opacity of node drawing
	 **************************************************************************/
	private void drawNode(Graphics2D g2d, int id, int x, int y,
			double v, float opacidad) {
		NumberFormat f = new DecimalFormat("0.0");
		String vs = f.format(v);
		
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);	
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
				0.85f * opacidad));
			
		int t = diaNod - 4;
		g2d.setColor(Color.BLACK);
		g2d.fillOval(Math.round(x - t / 2.0f), Math.round(y - t / 2.0f), t, t);

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_OFF);
		
		g2d.setColor(Color.WHITE);
		g2d.setFont(new Font("Arial", Font.BOLD, 11));
		g2d.drawString(id + "", Math.round(x - g2d.getFontMetrics().
				stringWidth(id + "") / 2.0f), Math.round(y + 4));

		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
				0.25f * opacidad));
		g2d.setColor(Color.BLACK);

		g2d.setFont(new Font("Arial", Font.BOLD, 9));
		g2d.drawString(vs + "", Math.round(x + 1 - g2d.getFontMetrics().
				stringWidth(vs + "") / 2.0f), Math.round(y + 1 + 20 - 4));
			
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
				1.0f * opacidad));
		g2d.setColor(Color.WHITE);

		g2d.setFont(new Font("Arial", Font.BOLD, 9));
		g2d.drawString(vs + "", Math.round(x - g2d.getFontMetrics().
				stringWidth(vs + "") / 2.0f), Math.round(y + 20 - 4));
	}
	
	/***************************************************************************
	 * Calculates the distance between the two given points.
	 * 
	 * @param x1	First X axis position
	 * @param y1	First Y axis position
	 * @param x2	Last X axis position
	 * @param y2	Last Y axis position
	 * @return		A double with the distance between the two points
	 **************************************************************************/
	private double distance(int x1, int y1, int x2, int y2) {
		return Math.sqrt(Math.pow(Math.abs(x2 - x1), 2) +
				Math.pow(Math.abs(y2 - y1), 2));
	}

	/***************************************************************************
	 * Given <i>y0</i>=<i>f</i>(<i>x0</i>) and <i>y1</i>=<i>f</i>(<i>x1</i>),
	 * returns the corresponding value to the interpolation of <i>x</i> with the
	 * line &lt;<i>x0</i>,<i>y0</i>&gt; and &lt;<i>x1</i>,<i>y1</i>&gt;.
	 * 
	 * @param y0	Initial result
	 * @param y1	Final result
	 * @param x0	Initial value
	 * @param x1	Final value
	 * @param x	    Value to interpolate
	 * @return		Interpolation result
	 **************************************************************************/
	private double interpolate(double y0, double y1, double x0,
			double x1, double x) {
		double m = (y0 - y1) / (x0 - x1);
		double b = (x1 * y0 - x0 * y1) / (x1 - x0);
		return m * x + b;
	}

	/***************************************************************************
	 * Given <code>x</code> a number between <code>0.0</code> and
	 * <code>1.0</code>, returns the equivalent color to the used color scale.
	 * 
	 * @param x	Number to calculate in the scale
	 * @return	A color equivalent to the value in the used color scale
	 **************************************************************************/
	private Color scaleColor(double x) {	
		float f = 0.0f;		
		try {			
			if (scaleType == SCALE_HEAT) {
			
				if (x < 0.0) return new Color(0.0f, 0.0f, 0.0f);
				if (x > 1.0) return new Color(1.0f, 0.0f, 0.0f);
				if (x <= 0.2) {
					f = (float)(x / 0.2d);
					return new Color(0.0f, 0.0f, f);
				} else if (x <= 0.4) {
					f = (float)((x - 0.2d) / 0.2d);
					return new Color(0.0f, f, 1.0f);
				} else if (x <= 0.6) {
					f = (float)((x - 0.4d) / 0.2d); 
					return new Color(0.0f, 1.0f, 1.0f - f);
				} else if (x <= 0.8) {
					f = (float)((x - 0.6d) / 0.2d); 
					return new Color(f, 1.0f, 0.0f);
				} else {
					f = (float)((x - 0.8d) / 0.2d); 
					return new Color(1.0f, 1.0f - f, 0.0f);
				}
			
			} else if (scaleType == SCALE_ENERGY) {
				
				if (x < 0.0) return new Color(1.0f, 0.0f, 0.0f);
				if (x > 1.0) return new Color(0.0f, 1.0f, 0.0f);
				if (x <= 0.5d) {
					f = (float)(x / 0.5d);
					return new Color(1.0f, f, 0.0f);
				} else {
					f = (float)((x - 0.5d) / 0.5d);
					return new Color(1.0f - f, 1.0f, 0.0f);
				}
				
				
			} else if (scaleType == SCALE_LIGHT) {
				
				if (x < 0.0) return new Color(0.0f, 0.0f, 0.0f);
				if (x > 1.0) return new Color(1.0f, 1.0f, 1.0f);
				f = (float)x;
				return new Color(f, f, f);
				
			} else return new Color(0.0f, 0.0f, 0.0f);
		} catch (IllegalArgumentException e) {
			System.out.println("Error: " + f);
			return new Color(0.0f, 0.0f, 0.0f);
		}
	}

	/***************************************************************************
	 * Generates and stores an image of the current status of the color map.
	 * 
	 * @param file	File path to store the exported image
	 * @param type	Desired image type (jpg, gif, png, or bmp)
	 **************************************************************************/
	public void exportAsImage(String file, String type) {
		if (nodes == null) return;
			
		this.fileUrl = file;
		this.fileType = type;
			
		new Thread() {
			public void run() {
				int width = getWidth();
				int height = getHeight();
				
				if (progress != null) {
					progress.setString("Exporting...");
					progress.setVisible(true);
				}
	
				BufferedImage buffer = new BufferedImage(
						(int)Math.round(width), (int)Math.round(height),
						BufferedImage.TYPE_INT_RGB);
	
				Graphics2D g2db = buffer.createGraphics();
				g2db.setComposite(AlphaComposite.Src);
				
				drawBackground(g2db);
				drawImgageBackground(g2db);
				drawColorGradient(g2db);
				drawNodes(g2db);
				
				g2db.dispose();
	
				try {
					File arch = new File(fileUrl);
					ImageIO.write(buffer, fileType, arch);
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null,
							"Error saving the image " + fileUrl,
							"Error saving", JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
				}
				
				if (progress != null) {
					progress.setString("Loading...");
					progress.setVisible(false);
				}
			}
		}.start();
	}

	/***************************************************************************
	 * Adds a new node to the color map.
	 * 
	 * @param node	Node to add
	 * @see			ColorMapNode
	 **************************************************************************/
	public void addNode(ColorMapNode node) {
		nodes.add(node);
	}

	/***************************************************************************
	 * Replaces the given node ID with the new node object.
	 * 
	 * @param id		Node ID to replace
	 * @param newValue	New node object
	 * @see				ColorMapNode
	 **************************************************************************/
	public void changeNode(int id, ColorMapNode newValue) {
		for (int i = 0; i < nodes.size(); i++)
			if (nodes.get(i).getId() == id) {
				nodes.set(id, newValue);
				return;
			}	
	}

	/***************************************************************************
	 * Checks if the given node ID exists in the color map.
	 * 
	 * @param id	Node ID to look
	 * @return		<code>True</code> if the node exists in the color map
	 **************************************************************************/
	public boolean nodeExists(int id) {
		for (int i = 0; i < nodes.size(); i++)
			if (nodes.get(i).getId() == id)
				return true;
		return false;
	}

	/***************************************************************************
	 * Removes the node with the given ID.
	 * 
	 * @param id	Node ID to delete
	 **************************************************************************/
	public void deleteNode(int id) {
		for (int i = 0; i < nodes.size(); i++)
			if (nodes.get(i).getId() == id) {
				nodes.remove(i);
				return;
			}
	}

	/***************************************************************************
	 * Removes all the nodes in the color map.
	 **************************************************************************/
	public void deleteAllNodes() {
		nodes.removeAllElements();
	}

	/***************************************************************************
	 * Method to handle the process to initialize the dragging of a node.
	 * 
	 * @param evt	Dragging event
	 **************************************************************************/
	public void dragGestureRecognized(DragGestureEvent evt) {
		int x = evt.getDragOrigin().x;
		int y = evt.getDragOrigin().y;
		double d = 0;
		
		if (!isEnabled()) return;
		
		drgdNod = -1;
		
		for (int k = 0; k < nodes.size(); k++)
			if (nodes.get(k) != null) {
				d = distance(x, y, nodes.get(k).getPosition().x,
						nodes.get(k).getPosition().y);
				if (d < diaNod / 2.0d) drgdNod = k;
			}
		
		if (drgdNod > -1) {
			Transferable t = new StringSelection(dragTrg.toString());
			dragSrc.startDrag(evt, DragSource.DefaultMoveDrop, t, this);
		}
	}

	/***************************************************************************
	 * Method that handles the completion of the dragging of a node. When the
	 * dragged node is released, the new position is validated and then stored
	 * in the dragged node values.
	 * 
	 * @param evt	Dragging event
	 **************************************************************************/
	public void dragDropEnd(DragSourceDropEvent evt) {
		int nx = evt.getLocation().x - getLocationOnScreen().x;
		int ny = evt.getLocation().y - getLocationOnScreen().y;
		
		if (drgdNod > -1) {
			if ((nx >= 0) && (nx <= getWidth()) && (ny >= 0) &&
					(ny <= getHeight())) {
				ColorMapNode n = nodes.get(drgdNod);
				n.setPosition(nx, ny);
				nodes.set(drgdNod, n);
				nodesPos[n.getId()] = new Point(nx, ny);
			}
			repaint();
			drgdNod = -1;
		}
	}

	/***************************************************************************
	 * Identifies the current dragging coordinate and redraws the color map.
	 * 
	 * @param evt	Dragging event
	 **************************************************************************/
	public void dragOver(DropTargetDragEvent evt) {
		movX = evt.getLocation().x;
		movY = evt.getLocation().y;
		this.repaint();
	}

	//--------------------------------------------------------------------------
	// Unused dragging interface methods.
	//==========================================================================
	
	public void dragEnter(DragSourceDragEvent evt)			{}
	public void dragOver(DragSourceDragEvent evt)			{}
	public void dragExit(DragSourceEvent evt)				{}
	public void dropActionChanged(DragSourceDragEvent evt)	{}
	public void dragEnter(DropTargetDragEvent evt) 			{}
	public void dropActionChanged(DropTargetDragEvent arg0)	{}
	public void dragExit(DropTargetEvent arg0) 				{}
	public void drop(DropTargetDropEvent arg0)				{}
	
}
