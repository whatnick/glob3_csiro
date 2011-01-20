package es.unex.meigas.core;

import java.awt.geom.Point2D;
import java.io.File;

public class Picture {

	private String m_sDescription = "Nueva imagen";
	private Point2D m_Coords = new Point2D.Double();
	private File m_File = null;
	private double m_dOrientation = 0;

	public Point2D getCoords() {

		return m_Coords;

	}

	public void setCoords(Point2D coords) {

		m_Coords = coords;

	}

	public File getFile() {

		return m_File;

	}

	public void setFile(File file) {

		m_File = file;

	}

	public String getDescription() {

		return m_sDescription;

	}

	public void setDescription(String description) {

		m_sDescription = description;

	}

	public double getOrientation() {

		return m_dOrientation;

	}

	public void setOrientation(double orientation) {

		m_dOrientation = orientation;

	}

	public String toString(){

		return m_sDescription;

	}

	public Picture getNewInstance(){

		Picture pic = new Picture();
		pic.setCoords(m_Coords);
		pic.setDescription(m_sDescription);
		pic.setFile(m_File);
		pic.setOrientation(m_dOrientation);

		return pic;

	}

}
