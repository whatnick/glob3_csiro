

package es.igosoftware.euclid.experimental.vectorial.rendering.coloring;

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

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.colors.IColor;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.features.IGlobeFeatureCollection;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.util.GAssert;
import es.igosoftware.util.IFunction;


public class GUniqueValuesColorizer
         extends
            GColorizerAbstract {

   private final String                                                                                  _fieldName;
   private boolean                                                                                       _hasField;

   private final GColorScheme                                                                            _colorScheme;
   private final IColor                                                                                  _defaultColor;
   private final boolean                                                                                 _renderLegends;
   private final IFunction<Object, String>                                                               _labeler;

   private List<String>                                                                                  _sortedLabels;
   private HashMap<String, IColor>                                                                       _colors;

   private IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> _lastFeatures;


   public GUniqueValuesColorizer(final String fieldName,
                                 final GColorScheme colorScheme,
                                 final IColor defaultColor,
                                 final boolean renderLegends,
                                 final IFunction<Object, String> labeler) {
      GAssert.notNull(fieldName, "fieldName");
      GAssert.notNull(colorScheme, "_colorScheme");
      GAssert.notNull(defaultColor, "defaultColor");

      _fieldName = fieldName;
      _colorScheme = colorScheme;
      _defaultColor = defaultColor;
      _renderLegends = renderLegends;

      _labeler = (labeler != null) ? labeler : new IFunction<Object, String>() {
         @Override
         public String apply(final Object element) {
            return (element == null) ? "" : element.toString();
         }
      };
   }


   @Override
   public void preprocessFeatures(final IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> features) {

      if (features == _lastFeatures) {
         return;
      }
      _lastFeatures = features;


      _hasField = features.hasField(_fieldName);
      if (!_hasField) {
         throw new RuntimeException("Field " + _fieldName + " not found in " + features);
         //         return;
      }

      final Set<String> labels = new HashSet<String>();
      for (final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature : features) {
         if (feature.hasAttribute(_fieldName)) {
            final Object value = feature.getAttribute(_fieldName);
            labels.add(_labeler.apply(value));
         }
      }

      _sortedLabels = new ArrayList<String>(labels);
      Collections.sort(_sortedLabels);

      _colors = new HashMap<String, IColor>();
      final List<IColor> colors = _colorScheme.getColors();
      for (int i = 0; i < _sortedLabels.size(); i++) {
         _colors.put(_sortedLabels.get(i), colors.get(i));
      }
   }


   @Override
   public IColor getColor(final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature) {
      if (!_hasField) {
         return _defaultColor;
      }

      final String value = _labeler.apply(feature.getAttribute(_fieldName));
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
         final int y = margin + (i * (symbolSize + 2));
         g2d.fillOval(margin, y, symbolSize, symbolSize);

         g2d.setColor(color.darker().darker().darker());
         g2d.drawOval(margin, y, symbolSize, symbolSize);

         drawShadowString(g2d, _labeler.apply(value), margin + symbolSize + (margin / 2), y + symbolSize, Color.LIGHT_GRAY,
                  Color.BLACK);
      }

      g2d.dispose();
   }


}
