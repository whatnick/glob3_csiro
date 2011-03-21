

package es.igosoftware.euclid.ntree;

import java.util.Collection;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.vector.IVector;


public class GGTLeafNode<

VectorT extends IVector<VectorT, ?, ?>,

BoundsT extends GAxisAlignedOrthotope<VectorT, ?>,

ElementT,

GeometryT extends IBoundedGeometry<VectorT, ?, ? extends IFiniteBounds<VectorT, ?>>

>
         extends
            GGTNode<VectorT, BoundsT, ElementT, GeometryT> {


   GGTLeafNode(final GGTInnerNode<VectorT, BoundsT, ElementT, GeometryT> parent,
               final BoundsT bounds,
               final Collection<ElementT> elements) {
      super(parent, bounds, elements);
   }


   @Override
   public void depthFirstAcceptVisitor(final IGTDepthFirstVisitor<VectorT, BoundsT, ElementT, GeometryT> visitor)
                                                                                                                 throws IGTBreadFirstVisitor.AbortVisiting {
      visitor.visitLeafNode(this);
   }


   @Override
   public int getLeafNodesCount() {
      return 1;
   }


   @Override
   public int getInnerNodesCount() {
      return 0;
   }


   @Override
   public final Collection<ElementT> getAllElements() {
      return getElements();
   }


   @Override
   public final int getAllElementsCount() {
      return getElementsCount();
   }


   @Override
   protected void validate() {
      if (getParent().getChildIndex(this) == -1) {
         System.out.println("invalid parent");
      }
   }

}
