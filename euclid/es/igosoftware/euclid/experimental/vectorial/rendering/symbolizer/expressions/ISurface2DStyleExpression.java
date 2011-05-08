

package es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.expressions;

import es.igosoftware.euclid.ISurface2D;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.experimental.vectorial.rendering.styling.ISurface2DStyle;


public interface ISurface2DStyleExpression<GeometryT extends ISurface2D<? extends IFinite2DBounds<?>>>
         extends
            IExpression<GeometryT, ISurface2DStyle> {


}
