

package es.igosoftware.euclid.experimental.vectorial.rendering.features;

import es.igosoftware.euclid.ISurface2D;
import es.igosoftware.euclid.bounding.IFinite2DBounds;


public interface ISurface2DRenderingShape<

GeometryT extends ISurface2D<? extends IFinite2DBounds<?>>

>
         extends
            I2DRenderingShape<GeometryT> {

}
