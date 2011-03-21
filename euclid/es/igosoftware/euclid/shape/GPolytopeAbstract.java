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


package es.igosoftware.euclid.shape;

import java.util.Collections;
import java.util.List;

import es.igosoftware.euclid.GGeometryAbstract;
import es.igosoftware.euclid.bounding.IBounds;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.util.GAssert;


public abstract class GPolytopeAbstract<

VectorT extends IVector<VectorT, ?, ?>,

SegmentT extends GSegment<VectorT, SegmentT, BoundsT>,

GeometryT extends GPolytopeAbstract<VectorT, SegmentT, GeometryT, BoundsT>,

BoundsT extends IBounds<VectorT, BoundsT>

>
         extends
            GGeometryAbstract<VectorT, GeometryT>
         implements
            IPolytope<VectorT, SegmentT, GeometryT, BoundsT> {

   private static final long serialVersionUID = 1L;


   private List<SegmentT>    _edges;


   @Override
   public final List<SegmentT> getEdges() {
      if (_edges == null) {
         final List<SegmentT> initialEdges = initializeEdges();
         GAssert.notEmpty(initialEdges, "edges");
         _edges = Collections.unmodifiableList(initialEdges);
      }
      return _edges;
   }


   protected abstract List<SegmentT> initializeEdges();


   @Override
   public VectorT closestPoint(final VectorT point) {
      GAssert.notNull(point, "point");

      if (contains(point)) {
         return point;
      }

      return closestPointOnBoundary(point);
   }


   public VectorT closestPointOnBoundary(final VectorT point) {
      GAssert.notNull(point, "point");

      double minDistance = Double.POSITIVE_INFINITY;
      VectorT closestPoint = null;

      for (final SegmentT edge : getEdges()) {
         final VectorT currentPoint = edge.closestPointOnBoundary(point);
         final double currentDistance = currentPoint.squaredDistance(point);

         if (currentDistance <= minDistance) {
            minDistance = currentDistance;
            closestPoint = currentPoint;
         }
      }

      return closestPoint;
   }


   @SuppressWarnings("unchecked")
   @Override
   public final boolean closeTo(final IPolytope<VectorT, SegmentT, ?, BoundsT> that) {
      if (getClass() == that.getClass()) {
         return closeTo((GeometryT) that);
      }
      return false;
   }

}
