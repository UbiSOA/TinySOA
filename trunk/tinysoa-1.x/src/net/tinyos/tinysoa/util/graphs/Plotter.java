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
import java.awt.font.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;
import java.text.*;
import java.util.*;
import javax.imageio.*;
import javax.swing.*;

import net.tinyos.tinysoa.common.*;

/*******************************************************************************
 * Class utility to plot information.
 * 
 * @author		Edgardo Avilés López
 * @version	0.5, 02/25/2006
 ******************************************************************************/
public class Plotter extends JPanel {
	private static final long serialVersionUID = -2891710764794158927L;

	@SuppressWarnings("unchecked")
	private Vector<Vector> data;
	private double margLeft = 52, margHigher = 20, margRight = 20, margLower = 62,
		width, high, sepHor = 12, sepVer = 20;
	private double x, y;
	private Color colorLineBackgr = new Color(0xcccccc),
		colorLine = new Color(0x000000);
	private double max, min, difMaxMin;
	private Date current, previous;
	private long dif = 1000 * 30;
	private int ALIGNMENT_LEFT = 0, ALIGNMENT_CENTER = 1,
		ALIGNMENT_RIGHT = 2;
	private Font sourceAxisVer = new Font("Arial", Font.PLAIN, 10),
		sourceAxisHor = new Font("Arial", Font.BOLD, 9),
		sourceLegend = new Font("Arial", Font.PLAIN, 11);
	private Color[] colors = new Color[]{ new Color(0x000000),
			new Color(0x9bbdde), new Color(0xffbc46), new Color(0xa2c488),
			new Color(0xd6b9db), new Color(0xcdc785), new Color(0xdea19b),
			new Color(0xb9dbcf), new Color(0xffdc46), new Color(0x9b88c4),
			new Color(0x85cd9b), new Color(0xd8d7c8), new Color(0x97968c) };
	private JProgressBar progress = null;
	private String fileUrl, fileType;
	private BufferedImage buffer;
	
	/***************************************************************************
	 * Contructor main of plotter.
	 **************************************************************************/
	public Plotter() {
		this.setOpaque(true);
		this.setBackground(Color.WHITE);
	}
	
	/***************************************************************************
	 * Define the vector data to plotter.
	 * 
	 * @param data	Vector data to plotter
	 **************************************************************************/
	@SuppressWarnings("unchecked")
	public void defData(Vector<Vector> data) {
		this.data = data;
		
		max = 0;
		min = 100000;
		
		Enumeration e = data.elements();
		while (e.hasMoreElements()) {
			Enumeration e2 = ((Vector)e.nextElement()).elements();
			while (e2.hasMoreElements()) {
				Reading d = (Reading)(e2.nextElement());
				double value = Double.parseDouble(d.getValue());
				if (value > max) max = value;
				if (value < min) min = value;
			}
		}
	
		difMaxMin = (max - min) * 0.2d;
		if (difMaxMin < 0.5) difMaxMin = (max - min) * 0.5d;
		if (difMaxMin == 0) difMaxMin = 0.2d;
		if (max > 0) max = max + difMaxMin;
		if (min > 0) min = min - difMaxMin;
		if (min > max) { max = 0; min = 0; }
	}

	/***************************************************************************
	 * Define the final time of graphic.
	 * 
	 * @param time	Final time
	 **************************************************************************/
	public void defTime(long time) {
		defTime(time, dif);
	}

	/***************************************************************************
	 * Define the final time of graphic y and difference with the initial time.
	 * 
	 * @param time	Final time
	 * @param dif	Unlike Desired with the initial time
	 **************************************************************************/
	public void defTime(long time, long dif) {
		this.current = new Date(time);
		this.dif = dif;
		previous = new Date(current.getTime() - dif);
	}

	/***************************************************************************
	 * Define the difference between initial time and final.
	 * 
	 * @param dif	Unlike desired between the range of time graphic
	 **************************************************************************/
	public void defDif(long dif) {
		this.dif = dif;
		previous = new Date(current.getTime() - dif);
	}

	/***************************************************************************
	 * Get the difference between the initial time and final.
	 * 
	 * @return	Difference between the range of time
	 **************************************************************************/
	public long obtDif() {
		return dif;
	}

	/***************************************************************************
	 * Define the status bar used for view the status
	 * current of image saving .
	 * 
	 * @param progress	Status bar to use
	 **************************************************************************/
	public void defProgress(JProgressBar progress) {
		this.progress = progress;
	}

	/***************************************************************************
	 * Generates and save a image of current status of plotter.
	 * 
	 * @param file	URL file to generates and save
	 * @param type		Type of image desired (jpg, gif, png ó bmp)
	 **************************************************************************/
	public void guardarImagen(String file, String type) {
		if (data == null) return;
			
		this.fileUrl = file;
		this.fileType = type;
			
		new Thread() {
			public void run() {
				width = 800;
				high = 400;
				
				if (progress != null) {
					progress.setString("Exporting...");
					progress.setVisible(true);
				}
	
				BufferedImage buffer = new BufferedImage(
						(int)Math.round(width), (int)Math.round(high),
						BufferedImage.TYPE_INT_RGB);
	
				Graphics2D g2db = buffer.createGraphics();
				g2db.setColor(Color.WHITE);
				g2db.fillRect(0, 0, (int)Math.round(width),
						(int)Math.round(high));
				drawBackgr(g2db);
				drawAxis(g2db);
				drawLabelsAxis(g2db);
				drawLegend(g2db);
				drawData(g2db);
				g2db.dispose();
	
				try {
					File arch = new File(fileUrl);
					ImageIO.write(buffer, fileType, arch);
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null,
							"Error saving the image" + fileUrl,
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
	 * Draw the graphic with the information and current status.
	 * 
	 * @param g	Object of chart to use
	 **************************************************************************/
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		super.paint(g2d);
		if (data == null) return;
		
		width = getWidth();
		high = getHeight();
		x = y = 0.0d;
		
		buffer = g2d.getDeviceConfiguration().createCompatibleImage(
				getWidth(), getHeight(), Transparency.TRANSLUCENT);
		
		Graphics2D g2db = buffer.createGraphics();
		drawBackgr(g2db);
		drawAxis(g2db);
		drawLabelsAxis(g2db);
		drawLegend(g2db);
		drawData(g2db);
		g2db.dispose();
		
		g2d.drawImage(buffer, null, 0, 0);
	}

	/***************************************************************************
	 * Draw the graphic background.
	 * 
	 * @param g2d	Chart to use
	 **************************************************************************/
	private void drawBackgr(Graphics2D g2d) {
		// Draw the vertical dotted lines minors -----------------------
		
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_OFF);
		
		g2d.setColor(colorLineBackgr);
		g2d.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_BEVEL, 0.0f, new float[]{6.0f, 4.0f}, 0.0f));
		
		x = margLeft;
		for (int i = 0; i < sepHor; i++) {
			x += (width - margLeft - margRight) / sepHor;
			drawLine(g2d, x, margHigher, x, high - margLower);
		}
		
		// Draw the rectangle external ---------------------------------------
		
		g2d.setStroke(new BasicStroke(1.0f));
		drawRectangle(g2d, margLeft, margHigher, width - margLeft - margRight,
				high - margHigher - margLower);
		
		// Draw the horizontal lines minors ------------------------------
		
		y = margHigher;
		for (int i = 0; i < sepVer; i++) {
			y += (high - margHigher - margLower) / sepVer;
			drawLine(g2d, margLeft, y, width - margRight, y);
		}		
	}
	
	/***************************************************************************
	 * Draw the axis of the lines
	 * 
	 * @param g2d	Chart to use
	 **************************************************************************/
	private void drawAxis(Graphics2D g2d) {
		// Draw the axis vertical ----------------------------------------------
		
		g2d.setColor(colorLine);
		g2d.setStroke(new BasicStroke(2.0f));
		
		drawLine(g2d, margLeft, margHigher, margLeft, high - margLower);
		
		//Draw separations smaller vertical axis --------------------
		
		y = margHigher;
		for (int i = 0; i < sepVer; i++) {
			y += (high - margHigher - margLower) / sepVer;
			if ((i + 1) % (sepVer / 4) != 0)
				drawLine(g2d, margLeft, y, margLeft - 6, y);
		}
		
		// Draw separations greater the vertical axis --------------------
		
		y = margHigher;
		for (int i = 0; i <= 4; i++) {
			drawLine(g2d, margLeft, y, margLeft - 12, y);
			y += (high - margHigher - margLower) / 4;
		}
		
		// Draw the horizontal axis --------------------------------------------
		
		drawLine(g2d, margLeft, high - margLower, width - margRight,
				high - margLower);
		
		// Draw separations greater the horizontal axis ------------------
		
		x = margLeft;
		for (int i = 0; i <= sepHor; i++) {
			drawLine(g2d, x, high - margLower, x, high - margLower + 4);
			x += (width - margLeft - margRight) / 4;
		}
	}
	
	/***************************************************************************
	 * Draw the axis labels.
	 * 
	 * @param g2d	Chart to use
	 **************************************************************************/
	private void drawLabelsAxis(Graphics2D g2d) {
		// Draw the labels of vertical axis -------------------------------
		
		if (current == null) return;
		
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		NumberFormat nf;
		if (difMaxMin > 2) nf = new DecimalFormat("0");
		else nf = new DecimalFormat("0.0");
		
		y = margHigher;
		for (int i = 0; i <= 4; i++) {
			drawText(g2d, sourceAxisVer,
					nf.format(min * (i / 4.0d) + max * (1.0d - i / 4.0d)),
					margLeft - 16, y, this.ALIGNMENT_RIGHT);
			y += (high - margHigher - margLower) / 4;
		}
		
		// Draw the labels in horizontal axis -----------------------------
		
		x = margLeft;
		for (int i = 0; i <= 4; i++) {
			Date t = new Date((long)(current.getTime() * (i / 4.0d) +
					previous.getTime() * (1.0d - i / 4.0d)));
			String c = new SimpleDateFormat("EEE d/M").format(t);
			String c2 = new SimpleDateFormat("HH:mm:ss").format(t);
			c = c.substring(0, 1).toUpperCase() + c.substring(1);
			int al = this.ALIGNMENT_CENTER;
			if (i == 0) al = this.ALIGNMENT_LEFT;
			if (i == 4) al = this.ALIGNMENT_RIGHT;
			drawText(g2d, sourceAxisHor, c, x, high - margLower + 12, al);
			drawText(g2d, new Font(sourceAxisHor.getFamily(), Font.PLAIN,
					sourceAxisHor.getSize()), c2, x, high - margLower + 22, al);
			x += (width - margLeft - margRight) / 4;
		}
	}

	/***************************************************************************
	 *Draw the data of chart.
	 * 
	 * @param g2d	Chart to use
	 **************************************************************************/
	@SuppressWarnings("unchecked")
	private void drawData(Graphics2D g2d) {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		
		g2d.setClip((int)Math.round(margLeft) + 1, (int)Math.round(margHigher),
				(int)Math.round(width - margRight),
				(int)Math.round(high - margLower) - 1);

		Enumeration e = data.elements();
		while (e.hasMoreElements()) {
			GeneralPath path = new GeneralPath();
			
			boolean started = false;
			Vector readings = (Vector)e.nextElement();
			
			Enumeration e2 = readings.elements();
			while (e2.hasMoreElements()) {
				Reading data = (Reading)e2.nextElement();
				
				double x, y;
				try {
					x = interpolate(0, g2d.getClip().getBounds().width,
							previous.getTime(), current.getTime(),
							((Date)format.parse(data.getDateTime())).getTime());
					y = interpolate(g2d.getClip().getBounds().height, 0, min,
							max, Double.parseDouble(data.getValue()));
				} catch (Exception ex) { ex.printStackTrace(); return; }

				if (!started) {
					path.moveTo((float)x, (float)y);
					g2d.setColor(colors[data.getNid() % colors.length]);
					started = true;
				}				
				path.lineTo((float)x, (float)y);
				if (x < 0) break;
			}			
			g2d.draw(path);
			
			if (dif < 1000 * 60) {
			
			e2 = readings.elements();
			while (e2.hasMoreElements()) {
				Reading dato = (Reading)e2.nextElement();
				double x, y;
				try {
					x = interpolate(0, g2d.getClip().getBounds().width,
							previous.getTime(), current.getTime(),
							((Date)format.parse(dato.getDateTime())).getTime());
					y = interpolate(g2d.getClip().getBounds().height, 0, min,
							max, Double.parseDouble(dato.getValue()));
				} catch (Exception ex) { ex.printStackTrace(); return; }
				drawPoint(g2d, x, y);
				if (x < 0) break;
			}
			}

		}
		
	}
	
	/***************************************************************************
	 * Draw a line with coordinates specified.
	 * 
	 * @param g2d	Chart to use
	 * @param x1	Value X of initial point
	 * @param y1	Value Y of initial point
	 * @param x2	Value X of final point
	 * @param y2	Value Y of final point
	 **************************************************************************/
	private void drawLine(Graphics2D g2d, double x1, double y1,
			double x2, double y2) {
		g2d.drawLine((int)Math.round(x1), (int)Math.round(y1),
				(int)Math.round(x2), (int)Math.round(y2));
	}
	
	/***************************************************************************
	 * Draw a rectangle with width and high certain at coordinates indicated.
	 * 
	 * @param g2d	Chart to use
	 * @param x		Value X of position
	 * @param y		Value Y of position
	 * @param width	Desired width
	 * @param high	Desired high
	 **************************************************************************/
	private void drawRectangle(Graphics2D g2d, double x, double y,
			double width, double high) {
		g2d.drawRect((int)Math.round(x), (int)Math.round(y),
				(int)Math.round(width), (int)Math.round(high));		
	}
	
	/***************************************************************************
	 * Draw a string of text in a position indicated with a type of letter and
	 * alignment specific.
	 * 
	 * @param g2d			Chart to use
	 * @param source		Type of letter to use
	 * @param text		 	Text a chart
	 * @param x			    Value X of the position
	 * @param y			    Value Y of the position
	 * @param alignment	    Alignment of text
	 **************************************************************************/
	private void drawText(Graphics2D g2d, Font source, String text,
			double x, double y, int alignment) {
		TextLayout tl = new TextLayout(text, source,
				g2d.getFontRenderContext());
		if (alignment == ALIGNMENT_LEFT)
			tl.draw(g2d, (float)(x),
					(float)(y - tl.getBounds().getCenterY()));			
		if (alignment == ALIGNMENT_CENTER)
			tl.draw(g2d, (float)(x - tl.getBounds().getCenterX()),
					(float)(y - tl.getBounds().getCenterY()));
		if (alignment == ALIGNMENT_RIGHT)
			tl.draw(g2d, (float)(x - tl.getBounds().getWidth()),
					(float)(y - tl.getBounds().getCenterY()));
	}
	
	/***************************************************************************
	 * Returns width of a text string with the font indicated.
	 * 
	 * @param g2d		Chat to use
	 * @param source	Type of letter to use
	 * @param text	    Text to measure
	 * @return			Width of text
	 **************************************************************************/
	private double widthText(Graphics2D g2d, Font source, String text) {
		TextLayout tl = new TextLayout(text, source,
				g2d.getFontRenderContext());
		return tl.getBounds().getWidth();
	}
	
	/***************************************************************************
	 * Draw a circle filling in the position and diameter indicated.
	 * 
	 * @param g2d		Chart to use
	 * @param x		    Value X of position
	 * @param y		    Value Y of position
	 * @param diameter	Diameter circle
	 **************************************************************************/
	private void drawCircleFull(Graphics2D g2d, double x, double y,
			double diameter) {
		g2d.fillOval((int)Math.round(x - diameter / 2.0d),
				(int)Math.round(y - diameter / 2.0d),
				(int)Math.round(diameter), (int)Math.round(diameter));
	}
	
	/***************************************************************************
	 * Clarifies the specified color in a 25%.
	 * 
	 * @param color	    Color to clarify
	 * @return			The color clarified in a 25%
	 **************************************************************************/
	private Color clarifyColor(Color color) {
		int red, green, blue;
		red = (int)Math.round(color.getRed() + color.getRed() * 0.15);
		green = (int)Math.round(color.getGreen() + color.getGreen() * 0.15);
		blue = (int)Math.round(color.getBlue() + color.getBlue() * 0.15);
		if (red > 255) red = 255;
		if (green > 255) green = 255;
		if (blue > 255) blue = 255;
		return new Color(red, green, blue);
	}
	
	/***************************************************************************
	 * Draw a point in the vertex position.
	 * 
	 * @param g2d	Chart to use
	 * @param x	    Value X of position
	 * @param y	    Value Y of position
	 **************************************************************************/
	private void drawPoint(Graphics2D g2d, double x, double y) {
		Color c1 = g2d.getColor();
		Color c = g2d.getColor();
		
		float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(),
				c.getBlue(), null);
		int nc = Color.HSBtoRGB(hsb[0], hsb[1] - hsb[1] * 0.30f,
				hsb[2] - hsb[2] * 0.15f);
		c = new Color((nc>>16)&0xFF, (nc>>8)&0xFF, nc&0xFF);
		
		g2d.setColor(c);
		drawCircleFull(g2d, x, y, 9);
		g2d.setColor(clarifyColor(c));
		drawCircleFull(g2d, x, y, 7);
		
		g2d.setColor(clarifyColor(clarifyColor(c)));
		drawCircleFull(g2d, x - 1, y - 1, 5);
		g2d.setColor(clarifyColor(clarifyColor(clarifyColor(c))));
		drawCircleFull(g2d, x - 1, y - 1, 2);
		g2d.setColor(clarifyColor(clarifyColor(clarifyColor(clarifyColor(c)))));
		g2d.setColor(c1);
	}
	
	/***************************************************************************
	 * Draw a legend with the nodes that make up the chart.
	 * 
	 * @param g2d	Chart to use
	 **************************************************************************/
	@SuppressWarnings("unchecked")
	private void drawLegend(Graphics2D g2d) {
		if (data == null) return;

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		double widthLegend = 0;
		
		Enumeration e = data.elements();
		while (e.hasMoreElements()) {
			Vector node = (Vector)e.nextElement();
			if (node.size() > 0) widthLegend += 14 + 42;
		}
		
		e = data.elements();
		double x = (width - widthLegend) / 2.0d;
		double y = high - margLower + 43;
		while (e.hasMoreElements()) {
			Vector node = (Vector)e.nextElement();
			if (node.size() > 0) {
				int nid = ((Reading)node.get(0)).getNid();
				g2d.setColor(colors[nid % colors.length]);
				drawPoint(g2d, x + 6, y);
				x += 9 + 5;
				g2d.setColor(Color.DARK_GRAY);
				drawText(g2d, sourceLegend, "Node " + nid, x, y,
						this.ALIGNMENT_LEFT);
				x += widthText(g2d, sourceLegend, "Node " + nid) + 10;
			}
		}
	}
	
	/***************************************************************************
	 * Taking <i> y0 </ i> = <i> f </ i> (x0 <i> </ i>) and 
	 * <i> y1 </ i> = <i> f </ i> (<i> x1 </ i>) that returns the value 
	 * at the interpolation <i> x </ i> with the line created by the coordinates 
	 * (<i> x0 </ i> <i> y0 </ i>) and (x1 <i> </ i> <i> y1 </ i>).
	 * 
	 * @param y0	Initial result
	 * @param y1	Final result
	 * @param x0	Initial value
	 * @param x1	Final value
	 * @param x	    Value to interpolate
	 * @return		Interpolate result
	 **************************************************************************/
	private double interpolate(double y0, double y1, double x0,
			double x1, double x) {
		double m = (y0 - y1) / (x0 - x1);
		double b = (x1 * y0 - x0 * y1) / (x1 - x0);
		return m * x + b;
	}
	
}
