package es.igosoftware.euclid.vector;

import es.igosoftware.euclid.IGeometry;
import es.igosoftware.euclid.matrix.GMatrix44D;
import es.igosoftware.euclid.projection.GProjection;


public interface IVector<

VectorT extends IVector<VectorT, ?>,

GeometryT extends IVector<VectorT, GeometryT>

>
         extends
            IGeometry<VectorT, GeometryT>

{


   public VectorT absoluted();


   public VectorT add(final double delta);


   public VectorT add(final VectorT that);


   public double angle(final VectorT that);


   public VectorT asMutable();


   public String asParseableString();


   public boolean between(final VectorT min,
                          final VectorT max);


   public VectorT clamp(final VectorT lower,
                        final VectorT upper);


   public boolean closeTo(final VectorT that);


   public boolean closeTo(final VectorT that,
                          final double precision);


   public boolean closeToZero();


   public VectorT div(final double scale);


   public VectorT div(final VectorT that);


   public double dot(final VectorT that);


   public double get(final byte i);


   public double[] getCoordinates();


   public VectorT interpolatedTo(final VectorT that,
                                 final double alpha);


   public boolean isNormalized();


   //   public boolean isZero();


   public double length();


   public VectorT max(final VectorT that);


   public VectorT min(final VectorT that);


   public VectorT negated();


   public VectorT normalized();


   public VectorT reciprocal();


   public VectorT rounded();


   public VectorT scale(final double scale);


   public VectorT scale(final VectorT that);


   public double squaredLength();


   public VectorT sub(final double delta);


   public VectorT sub(final VectorT that);


   public VectorT transformedBy(final GMatrix44D matrix);


   public IVector2<?> asVector2();


   public VectorT previousDown();


   public VectorT nextUp();


   public VectorT asDouble();


   public boolean greaterOrEquals(final VectorT that);


   public boolean lessOrEquals(final VectorT that);


   public VectorT reproject(final GProjection sourceProjection,
                            final GProjection targetProjection);

}
