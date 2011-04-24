

package es.igosoftware.euclid.experimental.vectorial.rendering.features;

import es.igosoftware.euclid.ISurface2D;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.vector.IVector2;


public interface ISurfaceRenderingShape<

GeometryT extends ISurface2D<? extends IFiniteBounds<IVector2, ?>>

>
         extends
            IRenderingShape<GeometryT> {

}
