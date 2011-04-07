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

import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.util.GMath;


public final class GDisk
         extends
            GNBall<IVector2, GDisk>
         implements
            IBounds2D<GDisk> {

   private static final long serialVersionUID = 1L;


   public GDisk(final IVector2 center,
                final double radius) {
      super(center, radius);
   }


   @Override
   public GDisk expandedByDistance(final double delta) {
      return new GDisk(_center, _radius + delta);
   }


   @Override
   protected String getStringName() {
      return "Disk";
   }


   @Override
   public boolean touches(final IBounds2D<?> that) {
      return that.touchesWithDisk(this);
   }


   @Override
   public boolean touchesWithDisk(final GDisk disk) {
      final double radius = _radius + disk._radius;
      return GMath.lessOrEquals(_center.squaredDistance(disk._center), radius * radius);
      //      return (center.squaredDistance(disk.center) <= radiusSquared);
   }


   @Override
   public boolean touchesWithRectangle(final GAxisAlignedRectangle rectangle) {
      return rectangle.touchesWithDisk(this);
   }


   @Override
   public GDisk getBounds() {
      return this;
   }


   @Override
   public boolean touchesWithCapsule2D(final GCapsule2D capsule) {
      return capsule.touchesWithDisk(this);
   }


   @Override
   public GAxisAlignedRectangle asAxisAlignedOrthotope() {
      return new GAxisAlignedRectangle(_center.sub(_radius), _center.add(_radius));
   }


   @Override
   public boolean touches(final GDisk that) {
      return touchesWithDisk(that);
   }


   @Override
   public boolean touchesBounds(final IBounds<IVector2, ?> that) {
      return touches((IBounds2D<?>) that);
   }

}
