package es.igosoftware.globe;

import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.View;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.layers.LayerList;

import java.awt.Dimension;
import java.awt.Image;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JFrame;

public interface IGlobeApplication {


   public Icon getIcon(final String iconName);


   public Icon getIcon(final String iconName,
                       final int width,
                       final int height);


   public Image getImage(final String imageName);


   public Image getImage(final String imageName,
                         final int width,
                         final int height);


   public Dimension initialDimension();


   public abstract IGlobeModule[] getModules();


   public WorldWindowGLCanvas getWorldWindowGLCanvas();


   public JFrame getFrame();


   public View getView();


   public Model getModel();


   public Globe getGlobe();


   public LayerList getLayerList();


   public List<IGlobeLayer> getLayers();


   public void redraw();


   public void goTo(final Position position,
                    final double elevation);


   public void goTo(final Position position,
                    final Angle heading,
                    final Angle pitch,
                    final double elevation);


   public void logInfo(final String msg);


   public void logWarning(final String msg);


   public void logSevere(final String msg);


   public void logSevere(final Throwable e);


   public void logSevere(final String msg,
                         final Throwable e);


   public String getTranslation(final String string);


   public String getTranslation(final String language,
                                final String string);


   public String getCurrentLanguage();


   public void setCurrentLanguage(final String currentLanguage);


   public void addTranslation(final String language,
                              final String string,
                              final String translation);


   public void zoomToSector(final Sector sector);
}
