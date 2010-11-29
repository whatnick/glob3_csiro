/*

 IGO Software SL  -  info@igosoftware.es

 http://www.glob3.org

-------------------------------------------------------------------------------
 Copyright (c) 2010, IGO Software SL
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
     * Redistributions of source code must retain the above copyright
       notice, this list of conditions and the following disclaimer.
     * Redistributions in binary form must reproduce the above copyright
       notice, this list of conditions and the following disclaimer in the
       documentation and/or other materials provided with the distribution.
     * Neither the name of the IGO Software SL nor the
       names of its contributors may be used to endorse or promote products
       derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL IGO Software SL BE LIABLE FOR ANY
 DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
-------------------------------------------------------------------------------

*/


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


   public List<IGlobeLayer> getGlobeLayers();


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
