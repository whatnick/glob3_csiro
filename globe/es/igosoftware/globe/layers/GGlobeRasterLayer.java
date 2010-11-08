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
