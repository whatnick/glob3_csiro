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

import java.util.ArrayList;
import java.util.List;

import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.vector.IVector2;


public final class GComplexPolygon2D
         extends
            GComplexPolytope<IVector2, GSegment2D, GAxisAlignedRectangle, ISimplePolygon2D>
         implements
            IComplexPolygon2D {

   private static final long serialVersionUID = 1L;


   public GComplexPolygon2D(final ISimplePolygon2D hull,
                            final List<? extends ISimplePolygon2D> holes) {
      super(hull, holes);
   }


   @Override
   public boolean contains(final IVector2 point) {
      if (!_hull.contains(point)) {
         return false;
      }

      for (final IPolygon2D hole : _holes) {
         if (hole.contains(point)) {
            return false;
         }
      }

      return true;
   }


   @Override
   public boolean isSelfIntersected() {
      if (_hull.isSelfIntersected()) {
         return true;
      }

      for (final IPolygon2D hole : _holes) {
         if (hole.isSelfIntersected()) {
            return true;
         }
      }

      return false;
   }


   @Override
   protected String getStringName() {
      return "ComplexPolygon";
   }


   @Override
   public GAxisAlignedRectangle getBounds() {
      return _hull.getBounds();
   }


   @Override
   public double squaredDistance(final IVector2 point) {
      double min = _hull.squaredDistance(point);

      for (final IPolygon2D hole : _holes) {
         final double current = hole.squaredDistance(point);
         if (current < min) {
            min = current;
         }
      }

      return min;
   }


   @Override
   protected List<GSegment2D> initializeEdges() {
      final List<GSegment2D> result = new ArrayList<GSegment2D>();
      result.addAll(_hull.getEdges());
      for (final IPolygon2D hole : _holes) {
         result.addAll(hole.getEdges());
      }
      return result;
   }


   @Override
   public List<GTriangle2D> triangulate() {
      throw new IllegalArgumentException("Not yet implemented");
   }


   @Override
   public boolean isConvex() {
      return false;
   }


}
