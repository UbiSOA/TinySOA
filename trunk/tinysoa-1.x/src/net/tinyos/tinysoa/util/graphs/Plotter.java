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
 * Utility class to plot sensing information.
 * 
 * @author		Edgardo Avilés López
 * @version	0.5, 02/25/2006
 ******************************************************************************/
public class Plotter extends JPanel {
	private static final long serialVersionUID = -2891710764794158927L;

	@SuppressWarnings("unchecked")
	private Vector<Vector> data;
	private double marginLeft = 52, marginUpper = 20, marginRight = 20,
		marginBottom = 62, width, high, gapHorz = 12, gapVert = 20;
	private double x, y;
	private Color colorLineBackgr = new Color(0xcccccc),
		colorLine = new Color(0x000000);
	private double max, min, difMaxMin;
	private Date current, previous;
	private long dif = 1000 * 30;
	private int ALIGNMENT_LEFT = 0, ALIGNMENT_CENTER = 1,
		ALIGNMENT_RIGHT = 2;
	private Font fontAxisVert = new Font("Arial", Font.PLAIN, 10),
		fontAxisHorz = new Font("Arial", Font.BOLD, 9),
		fontLegend = new Font("Arial", Font.PLAIN, 11);
	private Color[] colors = new Color[]{ new Color(0x000000),
			new Color(0x9bbdde), new Color(0xffbc46), new Color(0xa2c488),
			new Color(0xd6b9db), new Color(0xcdc785), new Color(0xdea19b),
			new Color(0xb9dbcf), new Color(0xffdc46), new Color(0x9b88c4),
			new Color(0x85cd9b), new Color(0xd8d7c8), new Color(0x97968c) };
	private JProgressBar progress = null;
	private String filePath, fileType;
	private BufferedImage buffer;
	
	/***************************************************************************
	 * Main constructor of plotter.
	 **************************************************************************/
	public Plotter() {
		this.setOpaque(true);
		this.setBackground(Color.WHITE);
	}
	
	/***************************************************************************
	 * Defines the vector containing the plot data.
	 * 
	 * @param data	Vector data to plot
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
	 * Defines the final time of plotting.
	 * 
	 * @param time	Final time to plot
	 **************************************************************************/
	public void defTime(long time) {
		defTime(time, dif);
	}

	/***************************************************************************
	 * Defines the final time of plotting and the difference to rest and take as
	 * initial time.
	 * 
	 * @param time	Final time to plot
	 * @param dif	Difference to rest and take as initial time
	 **************************************************************************/
	public void defTime(long time, long dif) {
		this.current = new Date(time);
		this.dif = dif;
		previous = new Date(current.getTime() - dif);
	}

	/***************************************************************************
	 * Defines the difference between initial and final time.
	 * 
	 * @param dif	Desired difference between plotting times
	 **************************************************************************/
	public void defDif(long dif) {
		this.dif = dif;
		previous = new Date(current.getTime() - dif);
	}

	/***************************************************************************
	 * Gets the difference between the initial and final times.
	 * 
	 * @return	Difference between the range of times
	 **************************************************************************/
	public long obtDif() {
		return dif;
	}

	/***************************************************************************
	 * Defines the status bar object to be used when showing the status on
	 * exporting the plot as an image.
	 * 
	 * @param progress	Status bar object to use on exporting
	 **************************************************************************/
	public void defProgress(JProgressBar progress) {
		this.progress = progress;
	}

	/***************************************************************************
	 * Generates and saves and image for the current status of the plotter.
	 * 
	 * @param file	File path to the target image
	 * @param type	Desired image type of target file (jpg, gif, png, or bmp)
	 **************************************************************************/
	public void exportAsImage(String file, String type) {
		if (data == null) return;
			
		this.filePath = file;
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
					File arch = new File(filePath);
					ImageIO.write(buffer, fileType, arch);
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null,
							"Error saving the image" + filePath,
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
	 * Draws the plot with the information and current status.
	 * 
	 * @param g	Graphic object to draw into
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
	 * Draws the background.
	 * 
	 * @param g2d	Graphic object to draw into
	 **************************************************************************/
	private void drawBackgr(Graphics2D g2d) {
		// Drawing the vertical dotted minor lines -----------------------------
		
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_OFF);
		
		g2d.setColor(colorLineBackgr);
		g2d.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_BEVEL, 0.0f, new float[]{6.0f, 4.0f}, 0.0f));
		
		x = marginLeft;
		for (int i = 0; i < gapHorz; i++) {
			x += (width - marginLeft - marginRight) / gapHorz;
			drawLine(g2d, x, marginUpper, x, high - marginBottom);
		}
		
		// Drawing the external rectangle --------------------------------------
		
		g2d.setStroke(new BasicStroke(1.0f));
		drawRectangle(g2d, marginLeft, marginUpper,
				width - marginLeft - marginRight,
				high - marginUpper - marginBottom);
		
		// Drawing the horizontal minor lines ----------------------------------
		
		y = marginUpper;
		for (int i = 0; i < gapVert; i++) {
			y += (high - marginUpper - marginBottom) / gapVert;
			drawLine(g2d, marginLeft, y, width - marginRight, y);
		}
	}
	
	/***************************************************************************
	 * Draws the axis lines.
	 * 
	 * @param g2d	Graphic object to draw into
	 **************************************************************************/
	private void drawAxis(Graphics2D g2d) {
		// Drawing the vertical axis -------------------------------------------
		
		g2d.setColor(colorLine);
		g2d.setStroke(new BasicStroke(2.0f));
		
		drawLine(g2d, marginLeft, marginUpper, marginLeft, high - marginBottom);
		
		// Drawing vertical axis minor gaps ------------------------------------
		
		y = marginUpper;
		for (int i = 0; i < gapVert; i++) {
			y += (high - marginUpper - marginBottom) / gapVert;
			if ((i + 1) % (gapVert / 4) != 0)
				drawLine(g2d, marginLeft, y, marginLeft - 6, y);
		}
		
		// Drawing vertical axis major gaps ------------------------------------
		
		y = marginUpper;
		for (int i = 0; i <= 4; i++) {
			drawLine(g2d, marginLeft, y, marginLeft - 12, y);
			y += (high - marginUpper - marginBottom) / 4;
		}
		
		// Drawing horizontal axis ---------------------------------------------
		
		drawLine(g2d, marginLeft, high - marginBottom, width - marginRight,
				high - marginBottom);
		
		// Drawing horizontal axis major gaps ----------------------------------
		
		x = marginLeft;
		for (int i = 0; i <= gapHorz; i++) {
			drawLine(g2d, x, high - marginBottom, x, high - marginBottom + 4);
			x += (width - marginLeft - marginRight) / 4;
		}
	}
	
	/***************************************************************************
	 * Draws the axis labels.
	 * 
	 * @param g2d	Graphic object to draw into
	 **************************************************************************/
	private void drawLabelsAxis(Graphics2D g2d) {
		// Drawing the labels for vertical axis --------------------------------
		
		if (current == null) return;
		
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		NumberFormat nf;
		if (difMaxMin > 2) nf = new DecimalFormat("0");
		else nf = new DecimalFormat("0.0");
		
		y = marginUpper;
		for (int i = 0; i <= 4; i++) {
			drawText(g2d, fontAxisVert,
					nf.format(min * (i / 4.0d) + max * (1.0d - i / 4.0d)),
					marginLeft - 16, y, this.ALIGNMENT_RIGHT);
			y += (high - marginUpper - marginBottom) / 4;
		}
		
		// Drawing the labels for horizontal axis ------------------------------
		
		x = marginLeft;
		for (int i = 0; i <= 4; i++) {
			Date t = new Date((long)(current.getTime() * (i / 4.0d) +
					previous.getTime() * (1.0d - i / 4.0d)));
			String c = new SimpleDateFormat("EEE d/M").format(t);
			String c2 = new SimpleDateFormat("HH:mm:ss").format(t);
			c = c.substring(0, 1).toUpperCase() + c.substring(1);
			int al = this.ALIGNMENT_CENTER;
			if (i == 0) al = this.ALIGNMENT_LEFT;
			if (i == 4) al = this.ALIGNMENT_RIGHT;
			drawText(g2d, fontAxisHorz, c, x, high - marginBottom + 12, al);
			drawText(g2d, new Font(fontAxisHorz.getFamily(), Font.PLAIN,
					fontAxisHorz.getSize()), c2, x,
					high - marginBottom + 22, al);
			x += (width - marginLeft - marginRight) / 4;
		}
	}

	/***************************************************************************
	 * Draws the plot data lines.
	 * 
	 * @param g2d	Graphic object to draw into
	 **************************************************************************/
	@SuppressWarnings("unchecked")
	private void drawData(Graphics2D g2d) {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		
		g2d.setClip((int)Math.round(marginLeft) + 1,
				(int)Math.round(marginUpper),
				(int)Math.round(width - marginRight),
				(int)Math.round(high - marginBottom) - 1);

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
	 * Draws a line from &lt;<i>x<sub>1</sub></i>,<i>y<sub>1</sub></i>&gt; to
	 * &lt;<i>x<sub>2</sub></i>, <i>y<sub>2</sub></i>&gt;.
	 * 
	 * @param g2d	Graphic object to draw into
	 * @param x1	Initial X axis position
	 * @param y1	Initial Y axis position
	 * @param x2	Final X axis position
	 * @param y2	Final Y axis position
	 **************************************************************************/
	private void drawLine(Graphics2D g2d, double x1, double y1,
			double x2, double y2) {
		g2d.drawLine((int)Math.round(x1), (int)Math.round(y1),
				(int)Math.round(x2), (int)Math.round(y2));
	}
	
	/***************************************************************************
	 * Draws a rectangle starting at &lt;<i>x</i>,<i>y</i>&gt; with the given
	 * width and height.
	 * 
	 * @param g2d		Graphic object to draw into
	 * @param x			X axis position
	 * @param y			Y axis position
	 * @param width		Desired width
	 * @param height	Desired height
	 **************************************************************************/
	private void drawRectangle(Graphics2D g2d, double x, double y,
			double width, double height) {
		g2d.drawRect((int)Math.round(x), (int)Math.round(y),
				(int)Math.round(width), (int)Math.round(height));		
	}
	
	/***************************************************************************
	 * Draws a text string in the specified position with the given font and
	 * alignment.
	 * 
	 * @param g2d		Graphic object to draw into
	 * @param font		Font to use
	 * @param text		Text to draw
	 * @param x			X axis position
	 * @param y			Y axis position
	 * @param alignment	Desired text alignment
	 **************************************************************************/
	private void drawText(Graphics2D g2d, Font font, String text,
			double x, double y, int alignment) {
		TextLayout tl = new TextLayout(text, font,
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
	 * Highlights the input color by 15%.
	 * 
	 * @param color	Color to highlight
	 * @return		The color highlighted by 15%
	 **************************************************************************/
	private Color clarifyColor(Color color) {
		int red, green, blue; double amount = 0.15;
		red = (int)Math.round(color.getRed() + color.getRed() * amount);
		green = (int)Math.round(color.getGreen() + color.getGreen() * amount);
		blue = (int)Math.round(color.getBlue() + color.getBlue() * amount);
		if (red > 255) red = 255;
		if (green > 255) green = 255;
		if (blue > 255) blue = 255;
		return new Color(red, green, blue);
	}
	
	/***************************************************************************
	 * Draws a 3D spot in the given coordinate.
	 * 
	 * @param g2d	Graphic object to draw into
	 * @param x	    X axis position
	 * @param y	    Y axis position
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
	 * Draws a nodes legend featuring all the nodes that appear in the plot.
	 * 
	 * @param g2d	Graphic object to draw into
	 **************************************************************************/
	@SuppressWarnings("unchecked")
	private void drawLegend(Graphics2D g2d) {
		if (data == null) return;

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		double legendWidth = 0;
		
		Enumeration e = data.elements();
		while (e.hasMoreElements()) {
			Vector node = (Vector)e.nextElement();
			if (node.size() > 0) legendWidth += 14 + 42;
		}
		
		e = data.elements();
		double x = (width - legendWidth) / 2.0d;
		double y = high - marginBottom + 43;
		while (e.hasMoreElements()) {
			Vector node = (Vector)e.nextElement();
			if (node.size() > 0) {
				int nid = ((Reading)node.get(0)).getNid();
				g2d.setColor(colors[nid % colors.length]);
				drawPoint(g2d, x + 6, y);
				x += 9 + 5;
				g2d.setColor(Color.DARK_GRAY);
				drawText(g2d, fontLegend, "Node " + nid, x, y,
						this.ALIGNMENT_LEFT);
				x += widthText(g2d, fontLegend, "Node " + nid) + 10;
			}
		}
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
	
}
