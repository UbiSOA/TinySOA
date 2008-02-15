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

package net.tinyos.tinysoa.util;

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
 * Class utility for generating graphical topology with information sensing.
 * 
 * @author		Edgardo Avilés López
 * @version	0.5, 02/25/2006
 ******************************************************************************/
public class TopologyPlotter extends JPanel
	implements DragGestureListener, DragSourceListener, DropTargetListener {
	private static final long serialVersionUID = 9143159172653372529L;
	
	private double valueMin = -10.0d;	// Value minimum
	private double valueMax = 40.0d;	// Valor maximum
	private double valuePro = 0.0d;	// Value average
	
	private int tamPix	= 10;		// Pixel size of the graph
	private int radio	= 1000;		// Radio coverage of the value of a node
	private int movNod	= -1;		// Index of node in drag
	private int diaNod = 20;		// Diameter of icon of node
	
	private BufferedImage buffer, backgr = null;		// Buffer of image
	private int movX, movY;		// Position of node in drag
	private DragSource sourceDrag;	// Source of drag
	private DropTarget destinyDrag;	// Destiny of drag
	private int typeScale = SCALE_HEAT;
	private Point[] posTopologyNodes;
	private JProgressBar progress = null;
	private String fileUrl, fileType;
	
	public static int SCALE_HEAT = 0, SCALE_LIGHT = 1, SCALE_ENERGY = 2;
	
	private Vector<NodeTopologyChart> node;
	
	/***************************************************************************
	 * Constructor main of the class.
	 **************************************************************************/
	public TopologyPlotter() {
		super();
		setBackground(Color.WHITE);
		node = new Vector<NodeTopologyChart>();		
		sourceDrag = new DragSource();
		sourceDrag.createDefaultDragGestureRecognizer(
				this, DnDConstants.ACTION_COPY_OR_MOVE, this);
		destinyDrag = new DropTarget(this, this);
	}

	/***************************************************************************
	 * Define the value minimum and maximum of the scale.
	 * 
	 * @param valueMin	Value minimum of scale
	 * @param valueMax	Value maximum of scale
	 **************************************************************************/
	public void defEscala(double valueMin, double valueMax) {
		this.valueMin = valueMin;
		this.valueMax = valueMax;
	}

	/***************************************************************************
	 * Returns the minimum value of scale.
	 * 
	 * @return	Minimun value of scale
	 **************************************************************************/
	public double obtMinimum() {
		return valueMin;
	}
	
	/***************************************************************************
	 * Returns the maximum value of scale.
	 * 
	 * @return	Maximum value of scale
	 **************************************************************************/
	public double obtMaximum() {
		return valueMax;
	}
	
	/***************************************************************************
	 * Define type of scale to use.
	 * 
	 * @param typeScale	Type of scale
	 **************************************************************************/
	public void defTypeScale(int typeScale) {
		this.typeScale = typeScale;
	}

	/***************************************************************************
	 * Define the settlement points which specifies the coordinates 
	 * of each of the nodes in the graph.
	 * 
	 * @param posTopologyNodes	Position of nodes in the chart
	 **************************************************************************/
	public void defPosNodes(Point[] posTopologyNodes) {
		this.posTopologyNodes = posTopologyNodes;
	}
	
	/***************************************************************************
	 * Set the background image to be used in the graph. The image is resized 
	 * to the size of the graph.
	 * 
	 * @param backgr	Image of background
	 **************************************************************************/
	public void defBackgr(BufferedImage backgr) {
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
	 * Define the progress bar to use.
	 * 
	 * @param progress	Progress bar
	 **************************************************************************/
	public void defProgress(JProgressBar progress) {
		this.progress = progress;
	}

	/***************************************************************************
	 * Method main of draw of component.
	 * 
	 * @param g	 Chart to use
	 **************************************************************************/
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
	
		if (node.size() == 0) {
			g2.setColor(Color.WHITE);
			g2.fillRect(0, 0, getWidth(), getHeight());
			return;
		}
		
		if (movNod == -1) {
			buffer = g2.getDeviceConfiguration().createCompatibleImage(
					getWidth(), getHeight(), Transparency.TRANSLUCENT);
			drawImage();
			g2.drawImage(buffer, null, 0, 0);
		}
		else drawDrag(g2);
	}

	/***************************************************************************
	 * Draw the image of the topology in the buffer.
	 **************************************************************************/
	private void drawImage() {
		Graphics2D g2d = buffer.createGraphics();
		g2d.setComposite(AlphaComposite.Src);
		
		drawBackground(g2d);
		if (node != null) {
			drawImgBackground(g2d);
			drawGradiente(g2d);
			drawNodes(g2d);
		}
		
		g2d.dispose();
	}
	
	/***************************************************************************
	 * If this is dragging a node graph a shadow node in question with their 
	 * current position.
	 * 
	 * @param g2d	Chart to use
	 **************************************************************************/
	private void drawDrag(Graphics2D g2d) {
		int x, y, id;
		double v;
		
		g2d.drawImage(buffer, null, 0, 0);
		
		x = movX;
		y = movY;
		id = node.get(movNod).getId();
		v = node.get(movNod).getValue();
		
		drawNode(g2d, id, x, y, v, 0.35f);
	}
	
	/***************************************************************************
	 * Draw the background image in the center of the graph.
	 * 
	 * @param g2d	Chart to use
	 **************************************************************************/
	private void drawImgBackground(Graphics2D g2d) {
		if (backgr == null) return;
		int x = (getWidth() - backgr.getWidth()) / 2;
		int y = (getHeight() - backgr.getHeight()) / 2;
		g2d.drawImage(backgr, null, x, y);
	}
	
	/***************************************************************************
	 * Draw the bottom of the image.
	 * 
	 * @param g2d	Chart to use
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
	private void drawGradiente(Graphics2D g2d) {
		
		double color = 0.0d, distMax, distMen, dist, factor, value,
				valueMen = valueMax, valueMay = valueMin, sumFact;
		
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_OFF);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

		g2d.setComposite(AlphaComposite.getInstance(
				AlphaComposite.SRC_OVER, 0.75f));
		
		NodeTopologyChart nodo;
		
		valuePro = 0.0d;
		for (int k = 0; k < node.size(); k++)
			if (node.get(k) != null) {
				value = node.get(k).getValue();
				if (value < valueMen) valueMen = value;
				if (value > valueMay) valueMay = value;
				valuePro += value;
			}
		valuePro /= node.size();
		
		for (int i = 0; i < getWidth(); i = i + tamPix)
			for (int j = 0; j < getHeight(); j = j + tamPix) {

				distMen = 10000.0d;
				distMax = 0.0d;
				for (int k = 0; k < node.size(); k++)
					if (node.get(k) != null) {
						nodo = node.get(k);
						dist = distance(i, j, nodo.getPosition().x,
								nodo.getPosition().y);
						if (dist < distMen) distMen = dist;
						if (dist > distMax) distMax = dist;
					}
				
				sumFact = 0.0d;
				for (int k = 0; k < node.size(); k++)
					if (node.get(k) != null) {
						nodo = node.get(k);
						dist = distance(i, j, nodo.getPosition().x,
								nodo.getPosition().y);
						sumFact += distMen / dist;
					}
					
				color = 0.0d;
				for (int k = 0; k < node.size(); k++)
					if (node.get(k) != null) {
						nodo = node.get(k);
						dist = distance(i, j , nodo.getPosition().x,
								nodo.getPosition().y);
						factor = (distMen / dist) / sumFact;
						value = nodo.getValue();
						value = interpolate(value, valuePro, 1, 0,
								Math.cos(interpolate(
										0, Math.PI / 2, 0, radio, dist)));
						value = value * factor;
						color += value;
				}
				
				color = interpolate(0.0d, 1.0d, valueMin, valueMax, color);
				g2d.setColor(scaleColor(color));
				g2d.fillRect(i, j, tamPix, tamPix);			
			}
	}
	
	/***************************************************************************
	 * Draw the nodes in the chart.
	 * 
	 * @param g2d	Chart to use
	 **************************************************************************/
	private void drawNodes(Graphics2D g2d) {
		
		int x, y, id;
		double v;
		
		for (int i = 0; i < node.size(); i++)
			if (node.get(i) != null) {
				NodeTopologyChart nodo = node.get(i);
				
				x = nodo.getPosition().x;
				y = nodo.getPosition().y;
				id = nodo.getId();
				v = nodo.getValue();
				drawNode(g2d, id, x, y, v, 1.0f);
			}
	}
	
	/***************************************************************************
	 * Draw a node with the positions and data.
	 * 
	 * @param g2d		Chart to use
	 * @param id		ID node
	 * @param x		Value X of position
	 * @param y		Value Y of position
	 * @param v		Value of reading
	 * @param opacity	Opacity of node draw
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
	 * It calculates the distance between the two coordinates given.
	 * 
	 * @param x0	Value X of initial point
	 * @param y0	Value Y of initial point
	 * @param x1	Value X of final point
	 * @param y1	Value Y of final point
	 * @return		A double with the distance between the points
	 **************************************************************************/
	private double distance(int x0, int y0, int x1, int y1) {
		return Math.sqrt(Math.pow(Math.abs(x1 - x0), 2) +
				Math.pow(Math.abs(y1 - y0), 2));
	}

	/***************************************************************************
	 * Taking <i> y0 </ i> = <i> f </ i> (x0 <i> </ i>) and <i> y1 </ i> = <i> f </ i> (<i> x1 </ i>) 
	 * that returns the value at the interpolation <i> x </ i> with the line created by the coordinates 
	 * (<i> x0 </ i> <i> y0 </ i>) and (x1 <i> </ i> <i> y1 </ i>).
	 * 
	 * @param y0	Initial result
	 * @param y1	Final result
	 * @param x0	Initial value
	 * @param x1	Final value
	 * @param x	    Value to interpolation
	 * @return		Interpolation result
	 **************************************************************************/
	private double interpolate(double y0, double y1, double x0,
			double x1, double x) {
		double m = (y0 - y1) / (x0 - x1);
		double b = (x1 * y0 - x0 * y1) / (x1 - x0);
		return m * x + b;
	}

	/***************************************************************************
	 * Being <code> i </ code> a value between 0.0 <code> </ code> and <code> 1.0 </ code> 
	 * This returns the color corresponding to the scale used.
	 * 
	 * @param i	    Value to search in the scale
	 * @return		A color corresponding to the value in the scale
	 **************************************************************************/
	private Color scaleColor(double i) {	
		float f = 0.0f;		
		try {
			
			if (typeScale == SCALE_HEAT) {
			
				if (i < 0.0) return new Color(0.0f, 0.0f, 0.0f);
				if (i > 1.0) return new Color(1.0f, 0.0f, 0.0f);
				if (i <= 0.2) {
					f = (float)(i / 0.2d);
					return new Color(0.0f, 0.0f, f);
				} else if (i <= 0.4) {
					f = (float)((i - 0.2d) / 0.2d);
					return new Color(0.0f, f, 1.0f);
				} else if (i <= 0.6) {
					f = (float)((i - 0.4d) / 0.2d); 
					return new Color(0.0f, 1.0f, 1.0f - f);
				} else if (i <= 0.8) {
					f = (float)((i - 0.6d) / 0.2d); 
					return new Color(f, 1.0f, 0.0f);
				} else {
					f = (float)((i - 0.8d) / 0.2d); 
					return new Color(1.0f, 1.0f - f, 0.0f);
				}
			
			} else if (typeScale == SCALE_ENERGY) {
				
				if (i < 0.0) return new Color(1.0f, 0.0f, 0.0f);
				if (i > 1.0) return new Color(0.0f, 1.0f, 0.0f);
				if (i <= 0.5d) {
					f = (float)(i / 0.5d);
					return new Color(1.0f, f, 0.0f);
				} else {
					f = (float)((i - 0.5d) / 0.5d);
					return new Color(1.0f - f, 1.0f, 0.0f);
				}
				
				
			} else if (typeScale == SCALE_LIGHT) {
				
				if (i < 0.0) return new Color(0.0f, 0.0f, 0.0f);
				if (i > 1.0) return new Color(1.0f, 1.0f, 1.0f);
				f = (float)i;
				return new Color(f, f, f);
				
			} else return new Color(0.0f, 0.0f, 0.0f);
		} catch (IllegalArgumentException e) {
			System.out.println("Error: " + f);
			return new Color(0.0f, 0.0f, 0.0f);
		}
	}

	/***************************************************************************
	 * It generates and saves a picture of the current status of plotter.
	 * 
	 * @param file	URL file to generate and save
	 * @param type	Type desired image (jpg, gif, png ó bmp)
	 **************************************************************************/
	public void saveImage(String file, String type) {
		if (node == null) return;
			
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
				drawImgBackground(g2db);
				drawGradiente(g2db);
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
	 * Add a node in the chart
	 * 
	 * @param nodo	Node to add
	 * @see			NodeTopologyChart
	 **************************************************************************/
	public void addNode(NodeTopologyChart nodo) {
		node.add(nodo);
	}

	/***************************************************************************
	 * 	Amendment to the node with the specified ID.
	 * 
	 * @param id			ID del nodo a reemplazar	 	
	 * @param newValue	    Node with which to replace
	 * @see					NodeTopologyChart
	 **************************************************************************/
	public void changeNode(int id, NodeTopologyChart newValue) {
		for (int i = 0; i < node.size(); i++)
			if (node.get(i).getId() == id) {
				node.set(id, newValue);
				return;
			}	
	}

	/***************************************************************************
	 * Find the ID node in the current nodes.
	 * 
	 * @param id	Node ID searching
	 * @return		True if the node exists in figure
	 **************************************************************************/
	public boolean existNode(int id) {
		for (int i = 0; i < node.size(); i++)
			if (node.get(i).getId() == id)
				return true;
		return false;
	}

	/***************************************************************************
	 * It removes the node with the specified ID.
	 * 
	 * @param id	ID del nodo a eliminar
	 **************************************************************************/
	public void deleteNode(int id) {
		for (int i = 0; i < node.size(); i++)
			if (node.get(i).getId() == id) {
				node.remove(i);
				return;
			}
	}

	/***************************************************************************
	 * Removes all nodes in the graph.
	 **************************************************************************/
	public void deleteAllNodes() {
		node.removeAllElements();
	}

	/***************************************************************************
	 * Proceedings to recognize and initialize the drag.
	 * 
	 * @param evt	Event of drag
	 **************************************************************************/
	public void dragGestureRecognized(DragGestureEvent evt) {
		int x = evt.getDragOrigin().x;
		int y = evt.getDragOrigin().y;
		double d = 0;
		
		if (!isEnabled()) return;
		
		movNod = -1;
		
		for (int k = 0; k < node.size(); k++)
			if (node.get(k) != null) {
				d = distance(x, y, node.get(k).getPosition().x,
						node.get(k).getPosition().y);
				if (d < diaNod / 2.0d) movNod = k;
			}
		
		if (movNod > -1) {
			Transferable t = new StringSelection(destinyDrag.toString());
			sourceDrag.startDrag(evt, DragSource.DefaultMoveDrop, t, this);
		}
	}

	/***************************************************************************
	 * Procedures to complete the drag and define the new position of the node, 
	 * before that the position is validated.
	 * 
	 * @param evt	Event of drag
	 **************************************************************************/
	public void dragDropEnd(DragSourceDropEvent evt) {
		int nx = evt.getLocation().x - getLocationOnScreen().x;
		int ny = evt.getLocation().y - getLocationOnScreen().y;
		
		if (movNod > -1) {
			if ((nx >= 0) && (nx <= getWidth()) && (ny >= 0) &&
					(ny <= getHeight())) {
				NodeTopologyChart n = node.get(movNod);
				n.setPosition(nx, ny);
				node.set(movNod, n);
				posTopologyNodes[n.getId()] = new Point(nx, ny);
			}
			repaint();
			movNod = -1;
		}
	}

	/***************************************************************************
	 * Identifies the current position of the drag and redraws the screen.
	 * 
	 * @param evt	Event of drag
	 **************************************************************************/
	public void dragOver(DropTargetDragEvent evt) {
		movX = evt.getLocation().x;
		movY = evt.getLocation().y;
		this.repaint();
	}

	//--------------------------------------------------------------------------
	//
	// Procedures interfaces unused.
	//
	//==========================================================================
	
	public void dragEnter(DragSourceDragEvent evt)				{}
	public void dragOver(DragSourceDragEvent evt)				{}
	public void dragExit(DragSourceEvent evt)					{}
	public void dropActionChanged(DragSourceDragEvent evt)		{}
	public void dragEnter(DropTargetDragEvent evt) 			{}
	public void dropActionChanged(DropTargetDragEvent arg0)	{}
	public void dragExit(DropTargetEvent arg0) 				{}
	public void drop(DropTargetDropEvent arg0)					{}
	
}
