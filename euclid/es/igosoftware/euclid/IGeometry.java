package es.igosoftware.euclid;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;

import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.euclid.vector.IVectorTransformer;

public interface IGeometry<

VectorT extends IVector<VectorT, ?>,

GeometryT extends IGeometry<VectorT, GeometryT>

>
         extends
            Serializable,
            Cloneable {

   public void save(final String fileName) throws IOException;


   public abstract void save(final DataOutputStream output) throws IOException;


   public abstract byte dimensions();


   public boolean contains(final VectorT point);


   public double squaredDistance(final VectorT point);


   public double distance(final VectorT point);


   public VectorT closestPoint(final VectorT point);


   public abstract Object clone();


   public GeometryT transformedBy(final IVectorTransformer<VectorT> transformer);


   public double precision();


   //   public boolean closeTo(final GeometryT that);

}
