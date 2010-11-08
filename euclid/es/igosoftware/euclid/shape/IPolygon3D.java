package es.igosoftware.euclid.shape;

import java.util.List;

import es.igosoftware.euclid.bounding.GAxisAlignedBox;
import es.igosoftware.euclid.vector.IVector3;

public interface IPolygon3D<

GeometryT extends IPolygon<IVector3<?>, GSegment3D, GeometryT, GAxisAlignedBox>

>
         extends
            IPolygon<IVector3<?>, GSegment3D, GeometryT, GAxisAlignedBox> {


   public abstract List<GTriangle3D> triangulate();
}
