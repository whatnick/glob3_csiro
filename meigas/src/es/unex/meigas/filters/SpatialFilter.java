package es.unex.meigas.filters;

import java.awt.geom.Rectangle2D;

import es.unex.meigas.core.DasocraticElement;

public class SpatialFilter extends AbstractFilter{

	private Rectangle2D m_Rectangle;

	public SpatialFilter(Rectangle2D rect){

		m_Rectangle = rect;

	}
	public boolean accept(DasocraticElement element) {

		return m_Rectangle.contains(element.getBoundingBox());

	}

	public String toString(){

		return "Filtro espacial: X:" + Double.toString(m_Rectangle.getMinX()) + "-" + Double.toString(m_Rectangle.getMaxX())
					+ " Y:" + Double.toString(m_Rectangle.getMinY()) + "-" + Double.toString(m_Rectangle.getMaxY());

	}

}
