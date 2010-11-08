package es.igosoftware.euclid.vector;

import java.util.Iterator;
import java.util.List;

import es.igosoftware.euclid.IGeometry;

public interface IPointsContainer<

VectorT extends IVector<VectorT, ?>,

GeometryT extends IPointsContainer<VectorT, GeometryT>

>
         extends
            IGeometry<VectorT, GeometryT>,
            Iterable<VectorT> {


   public List<VectorT> getPoints();


   public VectorT getPoint(final int i);


   public int getPointsCount();


   @Override
   public Iterator<VectorT> iterator();

}
