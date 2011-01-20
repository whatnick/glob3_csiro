package es.unex.meigas.gui;

import java.awt.geom.Point2D;

public class World2Device {

	private double m_dXMin;
	private double m_dYMax;
	private double m_dScale;

	public void setXMin(double min) {
		
		m_dXMin = min;
		
	}

	public void setYMax(double max) {
		
		m_dYMax = max;
		
	}
	
	public void setScale(double scale) {

		m_dScale = scale;
		
	}
	
	public Point2D transformPoint(Point2D pt){
		
		
		double x,y;
		
		x = pt.getX() - m_dXMin;
		x *= m_dScale;
		y = m_dYMax - pt.getY() ;
		y *= m_dScale;
		
		return new Point2D.Double(x, y);
		
	}
	
	public double transformDistance(double d){
		
		return d * m_dScale;
		
	}


}
