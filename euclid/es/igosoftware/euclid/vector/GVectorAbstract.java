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


package es.igosoftware.euclid.vector;

import es.igosoftware.euclid.GGeometryAbstract;
import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.util.GMath;


public abstract class GVectorAbstract<

VectorT extends IVector<VectorT, ?, BoundsT>,

GeometryT extends GVectorAbstract<VectorT, GeometryT, BoundsT>,

BoundsT extends GAxisAlignedOrthotope<VectorT, BoundsT>

>
         extends
            GGeometryAbstract<VectorT, GeometryT>
         implements
            IVector<VectorT, GeometryT, BoundsT> {

   private static final long serialVersionUID = 1L;


   @Override
   public boolean isNormalized() {
      return (GMath.closeTo(squaredLength(), 1));
   }


   @Override
   public double length() {
      return Math.sqrt(squaredLength());
   }


   @Override
   public final boolean contains(final VectorT point) {
      return closeTo(point);
   }


   @Override
   public final double angle(final VectorT that) {
      final double normProduct = length() * that.length();
      if (GMath.closeToZero(normProduct)) {
         throw new RuntimeException("the product of the lenght() of the vectors is zero");
      }

      final double dot = dot(that) / normProduct;
      final double campledDot = GMath.clamp(dot, -1, 1);
      return Math.acos(campledDot);
   }


   @Override
   public final VectorT closestPoint(final VectorT point) {
      return point;
   }


   @Override
   public final VectorT clamp(final VectorT min,
                              final VectorT max) {
      return max(min).min(max);
   }


   @Override
   public abstract boolean equals(final Object that);


   @SuppressWarnings("unchecked")
   @Override
   public VectorT getCentroid() {
      return (VectorT) this;
   }


}
