/*

 IGO Software SL  -  info@igosoftware.es

 http://www.glob3.org

-------------------------------------------------------------------------------
 Copyright (c) 2010, IGO Software SL
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
     * Redistributions of source code must retain the above copyright
       notice, this list of conditions and the following disclaimer.
     * Redistributions in binary form must reproduce the above copyright
       notice, this list of conditions and the following disclaimer in the
       documentation and/or other materials provided with the distribution.
     * Neither the name of the IGO Software SL nor the
       names of its contributors may be used to endorse or promote products
       derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL IGO Software SL BE LIABLE FOR ANY
 DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
-------------------------------------------------------------------------------

*/


package es.igosoftware.euclid.bounding;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import es.igosoftware.euclid.GGeometryAbstract;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.util.GAssert;
import es.igosoftware.util.GCollections;
import es.igosoftware.util.GMath;
import es.igosoftware.util.ITransformer;


public abstract class GAxisAlignedOrthotope<

VectorT extends IVector<VectorT, ?>,

GeometryT extends GAxisAlignedOrthotope<VectorT, GeometryT>

>
         extends
            GGeometryAbstract<VectorT, GeometryT>
         implements
            IBounds<VectorT, GeometryT> {

   private static final long serialVersionUID = 1L;


   @SuppressWarnings("unchecked")
   public static <VectorT extends IVector<VectorT, ?>> GAxisAlignedOrthotope<VectorT, ?> merge(final Iterable<GAxisAlignedOrthotope<VectorT, ?>> orthotopes) {
      final Iterator<GAxisAlignedOrthotope<VectorT, ?>> iterator = orthotopes.iterator();
      if (!iterator.hasNext()) {
         return null;
      }

      final GAxisAlignedOrthotope<VectorT, ?> exemplar = iterator.next();

      if (exemplar.dimensions() == 2) {
         final Iterable<GAxisAlignedRectangle> rectangles = GCollections.collect(orthotopes,
                  new ITransformer<GAxisAlignedOrthotope<VectorT, ?>, GAxisAlignedRectangle>() {
                     @Override
                     public GAxisAlignedRectangle transform(final GAxisAlignedOrthotope<VectorT, ?> element) {
                        return (GAxisAlignedRectangle) element;
                     }
                  });

         return (GAxisAlignedOrthotope<VectorT, ?>) GAxisAlignedRectangle.merge(rectangles);
      }
      else if (exemplar.dimensions() == 3) {
         final Iterable<GAxisAlignedBox> boxes = GCollections.collect(orthotopes,
                  new ITransformer<GAxisAlignedOrthotope<VectorT, ?>, GAxisAlignedBox>() {
                     @Override
                     public GAxisAlignedBox transform(final GAxisAlignedOrthotope<VectorT, ?> element) {
                        return (GAxisAlignedBox) element;
                     }
                  });

         return (GAxisAlignedOrthotope<VectorT, ?>) GAxisAlignedBox.merge(boxes);
      }
      else {
         throw new IllegalArgumentException("Unsupported points type (" + exemplar.getClass() + ")");
      }
   }


   //   @SuppressWarnings("unchecked")
   //   public static <VectorT extends IVector<VectorT, ?>> GAxisAlignedOrthotope<VectorT, ?> minimumOrthotope(final IVertexContainer<VectorT, ?> vertices) {
   //      if (vertices.dimensions() == 2) {
   //         final IVertexContainer<IVector2<?>, ?> vertices2 = (IVertexContainer<IVector2<?>, ?>) vertices;
   //         return (GAxisAlignedOrthotope<VectorT, ?>) GAxisAlignedRectangle.minimumBoundingRectangle(vertices2.pointsIterator());
   //      }
   //      else if (vertices.dimensions() == 3) {
   //         final IVertexContainer<IVector3<?>, ?> vertices3 = (IVertexContainer<IVector3<?>, ?>) vertices;
   //         return (GAxisAlignedOrthotope<VectorT, ?>) GAxisAlignedBox.minimumBoundingBox(vertices3.pointsIterator());
   //      }
   //      else {
   //         throw new IllegalArgumentException("Dimension " + vertices.dimensions() + " not supported");
   //      }
   //   }


   @SuppressWarnings("unchecked")
   public static <VectorT extends IVector<VectorT, ?>> GAxisAlignedOrthotope<VectorT, ?> minimumOrthotope(final VectorT... points) {
      if (points.length == 0) {
         throw new IllegalArgumentException("Empty points");
      }

      final VectorT exemplar = points[0];
      VectorT lower = exemplar;
      VectorT upper = exemplar;
      for (int i = 1; i < points.length; i++) {
         final VectorT point = points[i];
         lower = lower.min(point);
         upper = upper.max(point);
      }

      if (exemplar instanceof IVector3) {
         return (GAxisAlignedOrthotope<VectorT, ?>) new GAxisAlignedBox((IVector3) lower, (IVector3) upper);
      }
      else if (exemplar instanceof IVector2) {
         return (GAxisAlignedOrthotope<VectorT, ?>) new GAxisAlignedRectangle((IVector2) lower, (IVector2) upper);
      }
      else {
         throw new IllegalArgumentException("Unsupported points type (" + exemplar.getClass() + ")");
      }
   }


   @SuppressWarnings("unchecked")
   public static <VectorT extends IVector<VectorT, ?>> GAxisAlignedOrthotope<VectorT, ?> create(final VectorT lower,
                                                                                                final VectorT upper) {
      GAssert.notNull(lower, "lower");
      GAssert.notNull(upper, "upper");

      if (lower instanceof IVector3) {
         return (GAxisAlignedOrthotope<VectorT, ?>) new GAxisAlignedBox((IVector3) lower, (IVector3) upper);
      }
      else if (lower instanceof IVector2) {
         return (GAxisAlignedOrthotope<VectorT, ?>) new GAxisAlignedRectangle((IVector2) lower, (IVector2) upper);
      }
      else {
         throw new IllegalArgumentException("Unsupported points type (" + lower.getClass() + ")");
      }
   }


   @SuppressWarnings("unchecked")
   public static <VectorT extends IVector<VectorT, ?>> GAxisAlignedOrthotope<VectorT, ?> minimumOrthotope(final Iterable<VectorT> points) {
      final Iterator<? extends VectorT> iterator = points.iterator();
      if (!iterator.hasNext()) {
         throw new IllegalArgumentException("Empty points");
      }

      final VectorT exemplar = iterator.next();
      if (exemplar instanceof IVector3) {
         return (GAxisAlignedOrthotope<VectorT, ?>) GAxisAlignedBox.minimumBoundingBox((Iterable<? extends IVector3<?>>) points);
      }
      else if (exemplar instanceof IVector2) {
         return (GAxisAlignedOrthotope<VectorT, ?>) GAxisAlignedRectangle.minimumBoundingRectangle((Iterable<? extends IVector2<?>>) points);
      }
      else {
         throw new IllegalArgumentException("Unsupported points type (" + exemplar.getClass() + ")");
      }
   }


   public final VectorT _lower;
   public final VectorT _upper;
   public final VectorT _center;
   public final VectorT _extent;


   public GAxisAlignedOrthotope(final VectorT lower,
                                final VectorT upper) {
      GAssert.notNull(lower, "lower");
      GAssert.notNull(upper, "upper");

      _lower = lower.min(upper);
      _upper = lower.max(upper);

      _extent = _upper.sub(_lower);
      _center = _lower.add(_upper).div(2);
   }


   @Override
   public final byte dimensions() {
      return _lower.dimensions();
   }


   @Override
   public final double precision() {
      return _lower.precision();
   }


   @Override
   public final int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((_lower == null) ? 0 : _lower.hashCode());
      result = prime * result + ((_upper == null) ? 0 : _upper.hashCode());
      return result;
   }


   @Override
   public final boolean equals(final Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      final GAxisAlignedOrthotope<?, ?> other = (GAxisAlignedOrthotope<?, ?>) obj;
      if (_lower == null) {
         if (other._lower != null) {
            return false;
         }
      }
      else if (!_lower.equals(other._lower)) {
         return false;
      }
      if (_upper == null) {
         if (other._upper != null) {
            return false;
         }
      }
      else if (!_upper.equals(other._upper)) {
         return false;
      }
      return true;
   }


   public final VectorT getExtent() {
      return _extent;
   }


   public final VectorT getCenter() {
      return _center;
   }


   @Override
   public final String toString() {
      //return getStringName() + " [" + lower + " -> " + upper + "]" + ", extent=" + extent + "]";
      return getStringName() + " [" + _lower + " -> " + _upper + "]";
   }


   protected abstract String getStringName();


   @Override
   public final void save(final DataOutputStream output) throws IOException {
      _lower.save(output);
      _upper.save(output);
   }


   public abstract GAxisAlignedOrthotope<VectorT, GeometryT> expandedByDistance(final double delta);


   public abstract GAxisAlignedOrthotope<VectorT, GeometryT> expandedByDistance(final VectorT delta);


   public abstract GAxisAlignedOrthotope<VectorT, GeometryT> translatedBy(final VectorT delta);


   public abstract List<VectorT> getVertices();


   //@Override
   @Override
   public final VectorT closestPoint(final VectorT point) {
      if (contains(point)) {
         return point;
      }

      return closestPointOnBoundary(point);
   }


   @Override
   public final VectorT closestPointOnBoundary(final VectorT point) {
      return point.clamp(_lower, _upper);
   }


   @Override
   public final double squaredDistance(final VectorT point) {
      // http://www.gamedev.net/community/forums/topic.asp?topic_id=490900&whichpage=1&#3203179
      if (contains(point)) {
         return 0;
      }

      return closestPointOnBoundary(point).squaredDistance(point);
   }


   @Override
   public final boolean contains(final VectorT point) {
      return point.between(_lower, _upper);
   }


   @Override
   public final boolean containsOnBoundary(final VectorT point) {
      return GMath.closeToZero(distanceToBoundary(point));
   }


   @Override
   public final double squaredDistanceToBoundary(final VectorT point) {
      return closestPointOnBoundary(point).squaredDistance(point);
   }


   @Override
   public final double distanceToBoundary(final VectorT point) {
      return closestPointOnBoundary(point).distance(point);
   }


   public String asParseableString() {
      return _lower.asParseableString() + "_" + _upper.asParseableString();
   }


   public GAxisAlignedRectangle asRectangle() {
      return new GAxisAlignedRectangle(_lower.asVector2(), _upper.asVector2());
   }


   @Override
   public boolean closeTo(final GeometryT that) {
      return _lower.closeTo(that._lower) && _upper.closeTo(that._upper);
   }


   public boolean isFullInside(final GAxisAlignedOrthotope<VectorT, ?> that) {
      return _lower.greaterOrEquals(that._lower) && _upper.lessOrEquals(that._upper);
   }


   public abstract GAxisAlignedOrthotope<VectorT, GeometryT>[] subdivideByAxis(final byte axis);


   public abstract GAxisAlignedOrthotope<VectorT, GeometryT>[] subdivideAtCenter();


   public abstract boolean touches(final GAxisAlignedOrthotope<VectorT, ?> that);

}
