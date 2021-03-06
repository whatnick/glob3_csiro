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

import es.igosoftware.euclid.GAngle;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.bounding.GDisk;
import es.igosoftware.euclid.experimental.algorithms.GPolygonSegment2DIntersections;
import es.igosoftware.euclid.utils.GShapeUtils;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVectorFunction;


public final class GTriangle2D
         extends
            GTriangle<IVector2, GSegment2D, GAxisAlignedRectangle>
         implements
            ISimplePolygon2D {

   private static final long serialVersionUID = 1L;


   public GTriangle2D(final IVector2 v0,
                      final IVector2 v1,
                      final IVector2 v2) {
      super(v0, v1, v2);
   }


   public boolean isCaps(final GAngle tolerance) {
      final double radiansTolerance = tolerance.getRadians();

      final List<GSegment2D> edges = getEdges();

      final double l1 = edges.get(0).getLength();
      final double l2 = edges.get(1).getLength();
      final double l3 = edges.get(2).getLength();

      final double angle1 = Math.acos((l2 * l2 + l3 * l3 - l1 * l1) / (2 * l2 * l3));
      if ((Math.PI - angle1) <= radiansTolerance) {
         return true;
      }

      final double angle2 = Math.acos((l1 * l1 + l3 * l3 - l2 * l2) / (2 * l1 * l3));
      if ((Math.PI - angle2) <= radiansTolerance) {
         return true;
      }

      final double angle3 = Math.PI - angle1 - angle2;
      return (Math.PI - angle3) <= radiansTolerance;

   }


   @Override
   public GAxisAlignedRectangle getBounds() {
      final IVector2 lower = _v0.min(_v1).min(_v2);
      final IVector2 upper = _v0.max(_v1).max(_v2);
      return new GAxisAlignedRectangle(lower, upper);
   }


   @Override
   public boolean isSelfIntersected() {
      return false;
   }


   @Override
   protected List<GSegment2D> initializeEdges() {
      final List<GSegment2D> result = new ArrayList<GSegment2D>(3);
      result.add(new GSegment2D(_v2, _v1));
      result.add(new GSegment2D(_v0, _v2));
      result.add(new GSegment2D(_v1, _v0));
      return result;
   }


   @Override
   public GTriangle2D transform(final IVectorFunction<IVector2> transformer) {
      if (transformer == null) {
         return this;
      }
      return new GTriangle2D(transformer.apply(_v0), transformer.apply(_v1), transformer.apply(_v2));
   }


   // Returns the signed triangle area.
   // The result is positive if the triangle is ccw,
   // negative if the triangle is cw,
   // zero if the triangle is degenerate.
   @Override
   public double area() {
      return GShapeUtils.signedArea(_v0, _v1, _v2);
   }


   @Override
   public boolean isCounterClockWise() {
      return GShapeUtils.isCounterClockWise(_v0, _v1, _v2);
   }


   @Override
   public boolean isClockWise() {
      return GShapeUtils.isClockWise(_v0, _v1, _v2);
   }


   public GDisk getCircumscribedDisk() {
      final IVector2 e1 = _v2.sub(_v1);
      final IVector2 e2 = _v0.sub(_v2);
      final IVector2 e3 = _v1.sub(_v0);

      final double d0 = -e2.dot(e3);
      final double d1 = -e3.dot(e1);
      final double d2 = -e1.dot(e2);

      final double c0 = d1 * d2;
      final double c1 = d2 * d0;
      final double c2 = d0 * d1;


      final IVector2 temp0 = _v0.scale(c1 + c2);
      final IVector2 temp1 = _v1.scale(c2 + c0);
      final IVector2 temp2 = _v2.scale(c0 + c1);

      final double c = c0 + c1 + c2;
      final IVector2 center = temp0.add(temp1).add(temp2).div(2.0 * c);

      final double radius = Math.sqrt((d0 + d1) * (d1 + d2) * (d2 + d0) / c) / 2;

      return new GDisk(center, radius);
   }


   public GDisk getInscribedDisk() {
      final double l1 = _v1.distance(_v2);
      final double l2 = _v0.distance(_v2);
      final double l3 = _v0.distance(_v1);


      final IVector2 v1TimesL1 = _v0.scale(l1);
      final IVector2 v2TimesL2 = _v1.scale(l2);
      final IVector2 v3TimesL3 = _v2.scale(l3);

      final double perimeter = l1 + l2 + l3;
      final IVector2 center = v1TimesL1.add(v2TimesL2).add(v3TimesL3).div(perimeter);

      final double s = perimeter / 2.0;
      final double area = Math.sqrt(s * (s - l1) * (s - l2) * (s - l3));
      final double radius = area / perimeter;

      return new GDisk(center, radius * 2);
   }


   @Override
   public List<GSegment2D> getIntersections(final GSegment2D segment) {
      return GPolygonSegment2DIntersections.getIntersections(this, segment);
   }


}
