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


package es.igosoftware.globe.layers;

import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.globe.IGlobeApplication;
import es.igosoftware.globe.IGlobeRasterLayer;
import es.igosoftware.globe.actions.ILayerAction;
import es.igosoftware.globe.attributes.ILayerAttribute;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.SurfaceImage;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.List;

import javax.swing.Icon;


public class GGlobeRasterLayer
         extends
            RenderableLayer
         implements
            IGlobeRasterLayer {

   private SurfaceImage        m_SurfaceImage;
   private RasterRenderer      m_Renderer;
   private WritableRaster      m_Raster;
   private final RasterGeodata m_Extent;

   private double              m_dNoData = -99999d;


   public GGlobeRasterLayer(final Object imageSource,
                            final RasterGeodata extent) {

      super();

      m_Extent = extent;

      if (imageSource instanceof WritableRaster) {
         m_Raster = (WritableRaster) imageSource;
         m_Renderer = new RasterRenderer(this);
         m_Renderer.setColoringMethod(RasterRenderer.COLORING_METHOD_COLOR_RAMP);
         final BufferedImage img = m_Renderer.getImage();
         m_SurfaceImage = new SurfaceImage(img, extent.getAsSector());
      }
      else if (imageSource instanceof BufferedImage) {
         final BufferedImage img = (BufferedImage) imageSource;
         m_Raster = (WritableRaster) img.getData();
         m_Renderer = new RasterRenderer(this);
         m_Renderer.setColoringMethod(RasterRenderer.COLORING_METHOD_RGB);
         m_SurfaceImage = new SurfaceImage(imageSource, extent.getAsSector());
      }

      this.addRenderable(m_SurfaceImage);

   }


   @Override
   public Sector getExtent() {

      return m_SurfaceImage.getSector();

   }


   public RasterRenderer getRenderer() {

      return m_Renderer;

   }


   @Override
   public void redraw() {

      final BufferedImage img = m_Renderer.getImage();
      if (img != null) {
         m_SurfaceImage.setImageSource(img, m_SurfaceImage.getSector());
      }

   }


   @Override
   public double getNoDataValue() {

      return m_dNoData;

   }


   public void setNoDataValue(final double noData) {

      m_dNoData = noData;

   }


   @Override
   public WritableRaster getRaster() {

      return m_Raster;

   }


   @Override
   public RasterGeodata getRasterGeodata() {

      return m_Extent;

   }


   @Override
   public Icon getIcon(final IGlobeApplication application) {

      return null;

   }


   @Override
   public GProjection getProjection() {

      return m_Extent._crs;

   }


   @Override
   public void setProjection(final GProjection proj) {

      m_Extent._crs = proj;

   }


   @Override
   public List<ILayerAttribute<?>> getLayerAttributes(final IGlobeApplication application) {
      return null;
   }


   @Override
   public void doDefaultAction(final IGlobeApplication application) {
      application.zoomToSector(getExtent());
   }


   @Override
   public List<ILayerAction> getLayerActions(final IGlobeApplication application) {
      return null;
   }

}
