

package es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.expressions;

import java.util.Collection;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.IGeometry2D;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbols.GSymbol2D;


public interface IGeometry2DSymbolizerExpression<GeometryT extends IGeometry2D>
         extends
            IExpression<GeometryT, Collection<? extends GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>>> {


}
