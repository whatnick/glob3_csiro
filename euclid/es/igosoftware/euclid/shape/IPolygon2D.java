package es.igosoftware.euclid.shape;

import java.util.List;

import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.vector.IVector2;

public interface IPolygon2D<

GeometryT extends IPolygon<IVector2<?>, GSegment2D, GeometryT, GAxisAlignedRectangle>

>
         extends
            IPolygon<IVector2<?>, GSegment2D, GeometryT, GAxisAlignedRectangle> {


   public abstract List<GTriangle2D> triangulate();
}
