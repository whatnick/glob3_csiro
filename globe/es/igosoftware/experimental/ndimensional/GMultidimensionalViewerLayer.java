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


package es.igosoftware.experimental.ndimensional;


import es.igosoftware.euclid.features.IGlobeFeatureCollection;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.globe.GGlobeApplication;
import es.igosoftware.globe.IGlobeApplication;
import es.igosoftware.globe.IGlobeVectorLayer;
import es.igosoftware.globe.actions.ILayerAction;
import es.igosoftware.globe.attributes.GBooleanLayerAttribute;
import es.igosoftware.globe.attributes.GFloatLayerAttribute;
import es.igosoftware.globe.attributes.GRangeLayerAttribute;
import es.igosoftware.globe.attributes.GSelectionLayerAttribute;
import es.igosoftware.globe.attributes.GSeparatorAttribute;
import es.igosoftware.globe.attributes.ILayerAttribute;
import es.igosoftware.io.GFileName;
import es.igosoftware.util.GAssert;
import es.igosoftware.util.GRange;
import es.igosoftware.util.LRUCache;
import es.igosoftware.utils.GPositionBox;
import es.igosoftware.utils.GWWUtils;
import gov.nasa.worldwind.View;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Box;
import gov.nasa.worldwind.geom.Frustum;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.layers.AbstractLayer;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.util.WWMath;

import java.awt.Cursor;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.media.opengl.GL;
import javax.swing.Icon;


public class GMultidimensionalViewerLayer
         extends
            AbstractLayer
         implements
            IGlobeVectorLayer {

   private static final double MIN_PROYECTED_SIZE = 10;

   private static final String NONE               = "<none>";


   private static class PointsCloudCacheKey {
      final String                       _variableName;
      final int                          _time;
      final Globe                        _globe;
      final double                       _verticalExaggeration;
      final Vec4                         _referencePoint;
      final Map<String, GRange<Integer>> _dimensionsRanges;
      final float                        _alpha;


      private PointsCloudCacheKey(final String variableName,
                                  final int time,
                                  final Globe globe,
                                  final double verticalExaggeration,
                                  final Vec4 referencePoint,
                                  final Map<String, GRange<Integer>> dimensionsRanges,
                                  final float alpha) {
         _variableName = variableName;
         _time = time;
         _globe = globe;
         _verticalExaggeration = verticalExaggeration;
         _referencePoint = referencePoint;
         _dimensionsRanges = dimensionsRanges;
         _alpha = alpha;
      }


      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + Float.floatToIntBits(_alpha);
         result = prime * result + ((_dimensionsRanges == null) ? 0 : _dimensionsRanges.hashCode());
         result = prime * result + ((_globe == null) ? 0 : _globe.hashCode());
         result = prime * result + ((_referencePoint == null) ? 0 : _referencePoint.hashCode());
         result = prime * result + _time;
         result = prime * result + ((_variableName == null) ? 0 : _variableName.hashCode());
         long temp;
         temp = Double.doubleToLongBits(_verticalExaggeration);
         result = prime * result + (int) (temp ^ (temp >>> 32));
         return result;
      }


      @Override
      public boolean equals(final Object obj) {
         if (this == obj) {
            return true;
         }
         if (obj == null) {
            return false;
         }
         if (getClass() != obj.getClass()) {
            return false;
         }
         final PointsCloudCacheKey other = (PointsCloudCacheKey) obj;
         if (Float.floatToIntBits(_alpha) != Float.floatToIntBits(other._alpha)) {
            return false;
         }
         if (_dimensionsRanges == null) {
            if (other._dimensionsRanges != null) {
               return false;
            }
         }
         else if (!_dimensionsRanges.equals(other._dimensionsRanges)) {
            return false;
         }
         if (_globe == null) {
            if (other._globe != null) {
               return false;
            }
         }
         else if (!_globe.equals(other._globe)) {
            return false;
         }
         if (_referencePoint == null) {
            if (other._referencePoint != null) {
               return false;
            }
         }
         else if (!_referencePoint.equals(other._referencePoint)) {
            return false;
         }
         if (_time != other._time) {
            return false;
         }
         if (_variableName == null) {
            if (other._variableName != null) {
               return false;
            }
         }
         else if (!_variableName.equals(other._variableName)) {
            return false;
         }
         if (Double.doubleToLongBits(_verticalExaggeration) != Double.doubleToLongBits(other._verticalExaggeration)) {
            return false;
         }
         return true;
      }


   }


   private static class VectorsCloudCacheKey
            extends
               PointsCloudCacheKey {


      private final float                                    _vectorsFactor;
      private final IMultidimensionalData.VectorColorization _vectorsColorization;


      private VectorsCloudCacheKey(final String variableName,
                                   final int time,
                                   final Globe globe,
                                   final double verticalExaggeration,
                                   final Vec4 referencePoint,
                                   final float vectorsFactor,
                                   final IMultidimensionalData.VectorColorization vectorsColorization,
                                   final Map<String, GRange<Integer>> dimensionsRanges,
                                   final float alpha) {
         super(variableName, time, globe, verticalExaggeration, referencePoint, dimensionsRanges, alpha);
         _vectorsFactor = vectorsFactor;
         _vectorsColorization = vectorsColorization;
      }


      @Override
      public int hashCode() {
         final int prime = 31;
         int result = super.hashCode();
         result = prime * result + ((_vectorsColorization == null) ? 0 : _vectorsColorization.hashCode());
         result = prime * result + Float.floatToIntBits(_vectorsFactor);
         return result;
      }


      @Override
      public boolean equals(final Object obj) {
         if (this == obj) {
            return true;
         }
         if (!super.equals(obj)) {
            return false;
         }
         if (getClass() != obj.getClass()) {
            return false;
         }
         final VectorsCloudCacheKey other = (VectorsCloudCacheKey) obj;
         if (_vectorsColorization != other._vectorsColorization) {
            return false;
         }
         if (Float.floatToIntBits(_vectorsFactor) != Float.floatToIntBits(other._vectorsFactor)) {
            return false;
         }
         return true;
      }

   }


   private final LRUCache<PointsCloudCacheKey, IMultidimensionalData.PointsCloud, RuntimeException>   _pointsCloudsCache;
   private final LRUCache<VectorsCloudCacheKey, IMultidimensionalData.VectorsCloud, RuntimeException> _vectorsCloudsCache;

   {
      _pointsCloudsCache = new LRUCache<PointsCloudCacheKey, IMultidimensionalData.PointsCloud, RuntimeException>(25,
               new LRUCache.ValueFactory<PointsCloudCacheKey, IMultidimensionalData.PointsCloud, RuntimeException>() {
                  @Override
                  public IMultidimensionalData.PointsCloud create(final PointsCloudCacheKey key) {
                     final GGlobeApplication application = GGlobeApplication.instance();

                     final Cursor currentCursor = Cursor.getDefaultCursor();
                     if (application != null) {
                        application.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                     }

                     try {
                        final IMultidimensionalData.PointsCloud pointsCloud = _data.calculateValuePointsCloud(key._variableName,
                                 key._time, key._globe, key._verticalExaggeration, key._referencePoint, key._dimensionsRanges,
                                 key._alpha);
                        return pointsCloud;
                     }
                     finally {
                        if (application != null) {
                           application.getFrame().setCursor(currentCursor);
                        }
                     }
                  }
               });


      _vectorsCloudsCache = new LRUCache<VectorsCloudCacheKey, IMultidimensionalData.VectorsCloud, RuntimeException>(25,
               new LRUCache.ValueFactory<VectorsCloudCacheKey, IMultidimensionalData.VectorsCloud, RuntimeException>() {
                  @Override
                  public IMultidimensionalData.VectorsCloud create(final VectorsCloudCacheKey key) {
                     final GGlobeApplication application = GGlobeApplication.instance();

                     final Cursor currentCursor = Cursor.getDefaultCursor();
                     if (application != null) {
                        application.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                     }

                     try {
                        final IMultidimensionalData.VectorsCloud pointsCloud = _data.calculateVectorsCloud(key._variableName,
                                 key._time, key._globe, key._verticalExaggeration, key._referencePoint, key._vectorsFactor,
                                 key._vectorsColorization, key._dimensionsRanges);
                        return pointsCloud;
                     }
                     finally {
                        if (application != null) {
                           application.getFrame().setCursor(currentCursor);
                        }
                     }
                  }
               });


   }


   private final IMultidimensionalData                                                                _data;

   private Globe                                                                                      _lastGlobe;
   private double                                                                                     _lastVerticalExaggeration;

   private Sector                                                                                     _sector;
   private Box                                                                                        _bounds;

   private boolean                                                                                    _renderExtent        = false;

   private int                                                                                        _time                = 0;


   private Position                                                                                   _referencePosition;
   private Vec4                                                                                       _referencePoint;

   private float                                                                                      _pointSize           = 1;
   private boolean                                                                                    _smoothPoints        = false;
   private boolean                                                                                    _smoothVectors       = true;
   private boolean                                                                                    _animatingTime       = false;


   private String                                                                                     _valueVariableName;
   private String                                                                                     _vectorVariableName;

   private float                                                                                      _vectorsFactor       = 10000;

   private final Object                                                                               _timerMutex          = new Object();

   private final Map<String, GRange<Integer>>                                                         _dimensionsRanges;

   private IMultidimensionalData.VectorColorization                                                   _vectorsColorization = IMultidimensionalData.VectorColorization.WHITE;

   private float                                                                                      _pointsAlpha         = 1;
   private float                                                                                      _vectorsAlpha        = 1;


   public GMultidimensionalViewerLayer(final IMultidimensionalData data) {
      GAssert.notNull(data, "data");
      _data = data;

      _dimensionsRanges = new HashMap<String, GRange<Integer>>();
      for (final String dimensionName : _data.getNonTimeDimensionsNames()) {
         _dimensionsRanges.put(dimensionName, new GRange<Integer>(0, _data.getDimensionLenght(dimensionName) - 1));
      }


      final Thread ticker = new Thread() {

         @Override
         public void run() {
            try {
               while (true) {
                  if (_animatingTime) {
                     int newTime = getTime() + 1;
                     if (newTime >= _data.getTimeDimensionLength()) {
                        newTime = 0;
                     }
                     setTime(newTime);
                  }
                  Thread.sleep(250);
               }
            }
            catch (final InterruptedException e) {
               e.printStackTrace();
            }
         }
      };
      ticker.setPriority(Thread.MIN_PRIORITY);
      ticker.setDaemon(true);
      ticker.start();
   }


   @Override
   public String getName() {
      //      return "Multidimensional Viewer" + _data.getName();
      return _data.getName();
   }


   @Override
   public Icon getIcon(final IGlobeApplication application) {
      return application.getSmallIcon(GFileName.relative("pointscloud.png"));
   }


   @Override
   public Sector getExtent() {
      return _sector;
   }


   @Override
   public GProjection getProjection() {
      return GProjection.EPSG_4326;
   }


   @Override
   public void redraw() {
      // fire event to force a redraw
      firePropertyChange(AVKey.LAYER, null, this);
   }


   @Override
   public List<ILayerAttribute<?>> getLayerAttributes(final IGlobeApplication application) {
      final List<ILayerAttribute<?>> result = new ArrayList<ILayerAttribute<?>>();

      //      createGenericLayerAttributes(result);
      //      result.add(new GSeparatorAttribute());

      createPointsCloudLayerAttributes(result);
      result.add(new GSeparatorAttribute());

      if (_data.getAvailableVectorVariablesNames() != null) {
         createVectorsCloudLayerAttributes(result);
         result.add(new GSeparatorAttribute());
      }

      if (_data.getTimeDimensionName() != null) {
         createTimeLayerAttributes(result);
         result.add(new GSeparatorAttribute());
      }

      createDimensionsRangesLayerAttributes(result);
      result.add(new GSeparatorAttribute());

      return result;
   }


   private void createDimensionsRangesLayerAttributes(final List<ILayerAttribute<?>> layerAttributes) {
      final List<String> dimensionsNames = _data.getNonTimeDimensionsNames();

      if (dimensionsNames == null) {
         return;
      }

      for (final String dimensionName : dimensionsNames) {
         final int dimensionLenght = _data.getDimensionLenght(dimensionName);
         final int dimensionMaxValue = dimensionLenght - 1;

         final GRange<Integer> minimumMaximum = new GRange<Integer>(0, dimensionMaxValue);
         final ILayerAttribute<?> dimensionRangeAttribute = new GRangeLayerAttribute<Integer>("Dimension " + dimensionName,
                  "dimension_" + dimensionName, false, minimumMaximum, 1) {

            @Override
            public boolean isVisible() {
               return true;
            }


            @Override
            public GRange<Integer> get() {
               return getDimensionRange(dimensionName);
            }


            @Override
            public void set(final GRange<Integer> value) {
               setDimensionRange(dimensionName, value);
            }
         };
         layerAttributes.add(dimensionRangeAttribute);

      }
   }


   //   private void createGenericLayerAttributes(final List<ILayerAttribute<?>> layerAttributes) {
   //      final GBooleanLayerAttribute renderExtent = new GBooleanLayerAttribute("Render Bounds") {
   //         @Override
   //         public void set(final Boolean value) {
   //            setRenderExtent(value);
   //         }
   //
   //
   //         @Override
   //         public boolean isVisible() {
   //            return true;
   //         }
   //
   //
   //         @Override
   //         public Boolean get() {
   //            return isRenderExtent();
   //         }
   //      };
   //      layerAttributes.add(renderExtent);
   //   }


   private void createTimeLayerAttributes(final List<ILayerAttribute<?>> layerAttributes) {
      final ILayerAttribute<?> time = new GFloatLayerAttribute("Time", "Time", 0, _data.getTimeDimensionLength() - 1,
               GFloatLayerAttribute.WidgetType.SPINNER, 1) {

         @Override
         public boolean isVisible() {
            return true;
         }


         @Override
         public Float get() {
            return Float.valueOf(getTime());
         }


         @Override
         public void set(final Float value) {
            setTime(value.intValue());
         }
      };
      layerAttributes.add(time);


      final GBooleanLayerAttribute animateTime = new GBooleanLayerAttribute("Animate Time") {
         @Override
         public void set(final Boolean value) {
            setAnimatingTime(value);
         }


         @Override
         public boolean isVisible() {
            return true;
         }


         @Override
         public Boolean get() {
            return isAnimatingTime();
         }
      };
      layerAttributes.add(animateTime);
   }


   private void createVectorsCloudLayerAttributes(final List<ILayerAttribute<?>> layerAttributes) {
      final List<String> availableVectorVariables = _data.getAvailableVectorVariablesNames();

      if (availableVectorVariables == null) {
         return;
      }


      final String[] vectorOptions = new String[availableVectorVariables.size() + 1];

      vectorOptions[0] = NONE;
      int i = 1;
      for (final String availableVectorVariable : availableVectorVariables) {
         vectorOptions[i++] = availableVectorVariable;
      }


      final GSelectionLayerAttribute<String> vectorVariable = new GSelectionLayerAttribute<String>("Vector Variable", "",
               vectorOptions) {
         @Override
         public void set(final String value) {
            _vectorVariableName = value.equals(NONE) ? null : value;
            redraw();
         }


         @Override
         public boolean isVisible() {
            return true;
         }


         @Override
         public String get() {
            return (_vectorVariableName == null) ? NONE : _vectorVariableName;
         }
      };
      layerAttributes.add(vectorVariable);


      final ILayerAttribute<?> smoothVectors = new GBooleanLayerAttribute("Smooth Vectors") {
         @Override
         public final boolean isVisible() {
            return true;
         }


         @Override
         public Boolean get() {
            return isSmoothVectors();
         }


         @Override
         public void set(final Boolean value) {
            setSmoothVectors(value);
         }
      };
      layerAttributes.add(smoothVectors);


      final ILayerAttribute<?> vectorsFactor = new GFloatLayerAttribute("Vectors Factor", null, 1, 50000,
               GFloatLayerAttribute.WidgetType.SPINNER, 1000) {
         @Override
         public boolean isVisible() {
            return true;
         }


         @Override
         public Float get() {
            return getVectorsFactor();
         }


         @Override
         public void set(final Float value) {
            setVectorsFactor(value);
         }
      };
      layerAttributes.add(vectorsFactor);


      final ILayerAttribute<?> colorization = new GSelectionLayerAttribute<IMultidimensionalData.VectorColorization>(
               "Colorization", null, IMultidimensionalData.VectorColorization.values()) {

         @Override
         public boolean isVisible() {
            return true;
         }


         @Override
         public IMultidimensionalData.VectorColorization get() {
            return _vectorsColorization;
         }


         @Override
         public void set(final IMultidimensionalData.VectorColorization value) {
            setVectorsColorization(value);
         }
      };
      layerAttributes.add(colorization);


      final ILayerAttribute<?> vectorsAlpha = new GFloatLayerAttribute("Vectors Alpha", "VectorsAlpha", 0.1f, 1f,
               GFloatLayerAttribute.WidgetType.SPINNER, 0.1f) {

         @Override
         public boolean isVisible() {
            return true;
         }


         @Override
         public Float get() {
            return getVectorsAlpha();
         }


         @Override
         public void set(final Float value) {
            setVectorsAlpha(value);
         }
      };
      layerAttributes.add(vectorsAlpha);


   }


   private void createPointsCloudLayerAttributes(final List<ILayerAttribute<?>> layerAttributes) {
      final List<String> availableValueVariables = _data.getAvailableValueVariablesNames();
      final String[] valuesOptions = new String[availableValueVariables.size() + 1];

      valuesOptions[0] = NONE;
      int i = 1;
      for (final String availableValueVariable : availableValueVariables) {
         valuesOptions[i++] = availableValueVariable;
      }


      final GSelectionLayerAttribute<String> valueVariable = new GSelectionLayerAttribute<String>("Points Variable", "",
               valuesOptions) {
         @Override
         public void set(final String value) {
            _valueVariableName = value.equals(NONE) ? null : value;
            redraw();
         }


         @Override
         public boolean isVisible() {
            return true;
         }


         @Override
         public String get() {
            return (_valueVariableName == null) ? NONE : _valueVariableName;
         }
      };
      layerAttributes.add(valueVariable);


      final ILayerAttribute<?> smoothPoints = new GBooleanLayerAttribute("Smooth Points") {
         @Override
         public final boolean isVisible() {
            return true;
         }


         @Override
         public Boolean get() {
            return isSmoothPoints();
         }


         @Override
         public void set(final Boolean value) {
            setSmoothPoints(value);
         }
      };
      layerAttributes.add(smoothPoints);


      final ILayerAttribute<?> pointsSize = new GFloatLayerAttribute("Points Size", "PointSize", 1, 24,
               GFloatLayerAttribute.WidgetType.SPINNER, 0.5f) {

         @Override
         public boolean isVisible() {
            return true;
         }


         @Override
         public Float get() {
            return getPointSize();
         }


         @Override
         public void set(final Float value) {
            setPointSize(value);
         }
      };
      layerAttributes.add(pointsSize);


      final ILayerAttribute<?> pointsAlpha = new GFloatLayerAttribute("Points Alpha", "PointsAlpha", 0.1f, 1f,
               GFloatLayerAttribute.WidgetType.SPINNER, 0.1f) {

         @Override
         public boolean isVisible() {
            return true;
         }


         @Override
         public Float get() {
            return getPointsAlpha();
         }


         @Override
         public void set(final Float value) {
            setPointsAlpha(value);
         }
      };
      layerAttributes.add(pointsAlpha);

   }


   @Override
   public void doDefaultAction(final IGlobeApplication application) {
      application.zoomToSector(getExtent());
   }


   @Override
   public List<? extends ILayerAction> getLayerActions(final IGlobeApplication application) {
      return null;
   }


   public boolean isRenderExtent() {
      return _renderExtent;
   }


   public void setRenderExtent(final boolean renderExtent) {
      _renderExtent = renderExtent;
      redraw();
   }


   private boolean isVisible(final DrawContext dc) {
      if (!isEnabled()) {
         return false;
      }

      if (_bounds == null) {
         return true;
      }

      final Frustum frustum = dc.getView().getFrustumInModelCoordinates();

      final boolean isVisibleInFrustum = frustum.intersects(_bounds);
      if (!isVisibleInFrustum) {
         return false;
      }

      final double proyectedSize = WWMath.computeSizeInWindowCoordinates(dc, _bounds);
      if (proyectedSize < MIN_PROYECTED_SIZE) {
         return false;
      }

      return true;
   }


   @Override
   protected void doRender(final DrawContext dc) {
      if (dc.isPickingMode()) {
         return;
      }


      final GPositionBox dataExtent = _data.getBox();
      if (_sector == null) {
         _sector = dataExtent.asSector();
         _referencePosition = new Position(_sector.getCentroid(), dataExtent._center.elevation);
      }

      final Globe globe = dc.getGlobe();
      final double verticalExaggeration = dc.getVerticalExaggeration();

      final boolean terrainChanged = (globe != _lastGlobe) || (verticalExaggeration != _lastVerticalExaggeration);
      if (terrainChanged || (_bounds == null) || (_referencePoint == null)) {
         _lastGlobe = globe;
         _lastVerticalExaggeration = verticalExaggeration;

         _bounds = Sector.computeBoundingBox(globe, verticalExaggeration, _sector, dataExtent._lower.elevation,
                  dataExtent._upper.elevation + 0.01);

         _referencePoint = GWWUtils.toVec4(_referencePosition, globe, verticalExaggeration);
      }

      if (!isVisible(dc)) {
         return;
      }


      if (_renderExtent) {
         GWWUtils.renderExtent(dc, _bounds);
      }


      final Map<String, GRange<Integer>> dimensionsRanges = new HashMap<String, GRange<Integer>>(_dimensionsRanges);
      if (_valueVariableName != null) {
         final IMultidimensionalData.PointsCloud pointsCloud;
         synchronized (_timerMutex) {
            pointsCloud = _pointsCloudsCache.get(new PointsCloudCacheKey(_valueVariableName, _time, globe, verticalExaggeration,
                     _referencePoint, dimensionsRanges, _pointsAlpha));
         }

         if (pointsCloud != null) {
            renderPointsCloud(dc, pointsCloud);
         }
      }

      if (_vectorVariableName != null) {
         final IMultidimensionalData.VectorsCloud vectorsCloud;
         synchronized (_timerMutex) {
            vectorsCloud = _vectorsCloudsCache.get(new VectorsCloudCacheKey(_vectorVariableName, _time, globe,
                     verticalExaggeration, _referencePoint, _vectorsFactor, _vectorsColorization, dimensionsRanges, _vectorsAlpha));
         }

         if (vectorsCloud != null) {
            renderVectorsCloud(dc, vectorsCloud);
         }
      }

   }


   private void renderPointsCloud(final DrawContext dc,
                                  final IMultidimensionalData.PointsCloud pointsCloud) {

      final FloatBuffer pointsBuffer = pointsCloud._pointsBuffer;
      final FloatBuffer colorsBuffer = pointsCloud._colorsBuffer;

      if (pointsBuffer.capacity() == 0) {
         return;
      }


      final GL gl = dc.getGL();

      gl.glPointSize(_pointSize);


      if (isSmoothPoints()) {
         gl.glEnable(GL.GL_BLEND);
         gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
         gl.glHint(GL.GL_POINT_SMOOTH_HINT, GL.GL_NICEST);

         gl.glEnable(GL.GL_POINT_SMOOTH);
      }
      else {
         gl.glDisable(GL.GL_BLEND);

         gl.glDisable(GL.GL_POINT_SMOOTH);
      }


      final View view = dc.getView();
      view.pushReferenceCenter(dc, _referencePoint);

      GWWUtils.pushOffset(gl);


      if (colorsBuffer == null) {
         gl.glColor3f(1, 1, 1);
      }

      gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
      if (colorsBuffer != null) {
         gl.glEnableClientState(GL.GL_COLOR_ARRAY);
      }
      gl.glEnable(GL.GL_COLOR_MATERIAL);

      gl.glVertexPointer(3, GL.GL_FLOAT, 0, pointsBuffer.rewind());
      if (colorsBuffer != null) {
         gl.glColorPointer(4, GL.GL_FLOAT, 0, colorsBuffer.rewind());
      }

      final int count = pointsBuffer.capacity() / 3;
      gl.glDrawArrays(GL.GL_POINTS, 0, count);

      gl.glDisable(GL.GL_COLOR_MATERIAL);
      gl.glDisableClientState(GL.GL_COLOR_ARRAY);
      gl.glDisableClientState(GL.GL_VERTEX_ARRAY);


      GWWUtils.popOffset(gl);
      view.popReferenceCenter(dc);


      gl.glDisable(GL.GL_POINT_SMOOTH);

      gl.glPointSize(1);

      gl.glColor3f(1, 1, 1);

   }


   private void renderVectorsCloud(final DrawContext dc,
                                   final IMultidimensionalData.VectorsCloud vectorsCloud) {

      final FloatBuffer pointsBuffer = vectorsCloud._pointsBuffer;
      final FloatBuffer colorsBuffer = vectorsCloud._colorsBuffer;

      if (pointsBuffer.capacity() == 0) {
         return;
      }


      final GL gl = dc.getGL();

      gl.glPointSize(_pointSize);


      if (isSmoothVectors()) {
         gl.glEnable(GL.GL_BLEND);
         gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
         gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_NICEST);

         gl.glEnable(GL.GL_LINE_SMOOTH);
      }
      else {
         gl.glDisable(GL.GL_BLEND);

         gl.glDisable(GL.GL_LINE_SMOOTH);
      }


      final View view = dc.getView();
      view.pushReferenceCenter(dc, _referencePoint);

      GWWUtils.pushOffset(gl);

      if (colorsBuffer == null) {
         gl.glColor3f(1, 1, 1);
      }

      gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
      if (colorsBuffer != null) {
         gl.glEnableClientState(GL.GL_COLOR_ARRAY);
      }
      gl.glEnable(GL.GL_COLOR_MATERIAL);

      gl.glVertexPointer(3, GL.GL_FLOAT, 0, pointsBuffer.rewind());
      if (colorsBuffer != null) {
         gl.glColorPointer(3, GL.GL_FLOAT, 0, colorsBuffer.rewind());
      }

      final int count = pointsBuffer.capacity() / 3;
      gl.glDrawArrays(GL.GL_LINES, 0, count);

      gl.glDisable(GL.GL_COLOR_MATERIAL);
      gl.glDisableClientState(GL.GL_COLOR_ARRAY);
      gl.glDisableClientState(GL.GL_VERTEX_ARRAY);


      GWWUtils.popOffset(gl);
      view.popReferenceCenter(dc);


      gl.glDisable(GL.GL_LINE_SMOOTH);

      gl.glPointSize(1);

      gl.glColor3f(1, 1, 1);

   }


   public float getPointSize() {
      return _pointSize;
   }


   public void setPointSize(final float pointSize) {
      _pointSize = pointSize;
      redraw();
   }


   public void setSmoothPoints(final boolean smooth) {
      _smoothPoints = smooth;
      redraw();
   }


   public boolean isSmoothPoints() {
      return _smoothPoints;
   }


   public void setSmoothVectors(final boolean smooth) {
      _smoothVectors = smooth;
      redraw();
   }


   public boolean isSmoothVectors() {
      return _smoothVectors;
   }


   public int getTime() {
      return _time;
   }


   public void setTime(final int time) {
      if (time == _time) {
         return;
      }

      synchronized (_timerMutex) {
         final int oldTime = _time;
         _time = time;
         firePropertyChange("Time", oldTime, _time);
         redraw();
      }
   }


   public boolean isAnimatingTime() {
      return _animatingTime;
   }


   public void setAnimatingTime(final boolean animatingTime) {
      _animatingTime = animatingTime;
   }


   public float getVectorsFactor() {
      return _vectorsFactor;
   }


   public void setVectorsFactor(final float vectorsFactor) {
      _vectorsFactor = vectorsFactor;
      redraw();
   }


   private GRange<Integer> getDimensionRange(final String dimensionName) {
      return _dimensionsRanges.get(dimensionName);
   }


   private void setDimensionRange(final String dimensionName,
                                  final GRange<Integer> newRange) {

      final GRange<Integer> oldRange = _dimensionsRanges.get(dimensionName);
      if (oldRange.equals(newRange)) {
         return;
      }

      _dimensionsRanges.put(dimensionName, newRange);
      firePropertyChange("dimension_" + dimensionName, oldRange, newRange);
   }


   public IMultidimensionalData.VectorColorization getVectorsColorization() {
      return _vectorsColorization;
   }


   public void setVectorsColorization(final IMultidimensionalData.VectorColorization vectorsColorization) {
      if (vectorsColorization == _vectorsColorization) {
         return;
      }

      _vectorsColorization = vectorsColorization;
      redraw();
   }


   public float getPointsAlpha() {
      return _pointsAlpha;
   }


   public void setPointsAlpha(final float pointsAlpha) {
      _pointsAlpha = pointsAlpha;
      redraw();
   }


   public float getVectorsAlpha() {
      return _vectorsAlpha;
   }


   public void setVectorsAlpha(final float vectorsAlpha) {
      _vectorsAlpha = vectorsAlpha;
      redraw();
   }


   @Override
   public IGlobeFeatureCollection getFeaturesCollection() {
      return null;
   }


}
