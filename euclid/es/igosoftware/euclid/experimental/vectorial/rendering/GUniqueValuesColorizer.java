

package es.igosoftware.euclid.experimental.vectorial.rendering;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.colors.IColor;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.features.IGlobeFeatureCollection;
import es.igosoftware.euclid.features.IGlobeMutableFeatureCollection;
import es.igosoftware.euclid.mutability.IMutable;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.util.GAssert;
import es.igosoftware.util.ITransformer;


public class GUniqueValuesColorizer
         extends
            GColorizerAbstract {

   private final String                                                                                                  _fieldName;
   private final IColor                                                                                                  _startColor;
   private final IColor                                                                                                  _defaultColor;
   private final boolean                                                                                                 _renderLegends;
   private final ITransformer<Object, String>                                                                            _labeler;

   private int                                                                                                           _fieldIndex;
   private List<String>                                                                                                  _sortedLabels;
   private HashMap<String, IColor>                                                                                       _colors;

   private IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> _lastFeatures;


   public GUniqueValuesColorizer(final String fieldName,
                                 final IColor startColor,
                                 final IColor defaultColor,
                                 final boolean renderLegends,
                                 final ITransformer<Object, String> labeler) {
      GAssert.notNull(fieldName, "fieldName");
      GAssert.notNull(startColor, "startColor");
      GAssert.notNull(defaultColor, "defaultColor");

      _fieldName = fieldName;
      _startColor = startColor;
      _defaultColor = defaultColor;
      _renderLegends = renderLegends;

      _labeler = (labeler != null) ? labeler : new ITransformer<Object, String>() {
         @Override
         public String transform(final Object element) {
            return (element == null) ? "" : element.toString();
         }
      };
   }


   @Override
   public void preprocessFeatures(final IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> features) {

      if (features == _lastFeatures) {
         return;
      }
      _lastFeatures = features;

      if (features instanceof IGlobeMutableFeatureCollection) {
         ((IGlobeMutableFeatureCollection) features).addChangeListener(new IMutable.ChangeListener() {
            @Override
            public void mutableChanged() {
               _lastFeatures = null;
            }
         });
      }

      _fieldIndex = features.getFieldIndex(_fieldName);
      if (_fieldIndex < 0) {
         return;
      }

      final Set<String> labels = new HashSet<String>();
      for (final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature : features) {
         final Object value = feature.getAttribute(_fieldIndex);
         labels.add(_labeler.transform(value));
      }

      _sortedLabels = new ArrayList<String>(labels);
      Collections.sort(_sortedLabels);

      _colors = new HashMap<String, IColor>();
      final IColor[] colors = _startColor.wheel(_sortedLabels.size());
      for (int i = 0; i < _sortedLabels.size(); i++) {
         _colors.put(_sortedLabels.get(i), colors[i]);
      }
   }


   @Override
   public IColor getColor(final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature) {

      if (_fieldIndex < 0) {
         return _defaultColor;
      }

      final String value = _labeler.transform(feature.getAttribute(_fieldIndex));
      final IColor color = _colors.get(value);
      return (color == null) ? _defaultColor : color;
   }


   @Override
   public void preRenderImage(final BufferedImage renderedImage) {

   }


   @Override
   public void postRenderImage(final BufferedImage renderedImage) {
      if (!_renderLegends) {
         return;
      }

      if ((_sortedLabels == null) || _sortedLabels.isEmpty()) {
         return;
      }

      final Graphics2D g2d = renderedImage.createGraphics();
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);


      final int margin = 10;
      final int symbolSize = 12;
      for (int i = 0; i < _sortedLabels.size(); i++) {
         final Object value = _sortedLabels.get(i);
         final Color color = _colors.get(value).asAWTColor();
         g2d.setColor(color);
         final int y = margin + (i * symbolSize);
         g2d.fillOval(margin, y, symbolSize, symbolSize);

         g2d.setColor(color.darker().darker().darker());
         g2d.drawOval(margin, y, symbolSize, symbolSize);

         drawShadowString(g2d, _labeler.transform(value), margin + symbolSize + (margin / 2), y + symbolSize, Color.GRAY,
                  Color.BLACK);
      }

      g2d.dispose();
   }


}
