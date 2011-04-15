

package es.igosoftware.euclid.ntree;

import java.util.Collection;

import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.vector.IVector;


public class GGTLeafNode<VectorT extends IVector<VectorT, ?>, ElementT>
         extends
            GGTNode<VectorT, ElementT> {


   GGTLeafNode(final GGTInnerNode<VectorT, ElementT> parent,
               final GAxisAlignedOrthotope<VectorT, ?> bounds,
               final Collection<GElementGeometryPair<VectorT, ElementT>> elements) {
      super(parent, bounds, elements);
   }


   @Override
   public void depthFirstAcceptVisitor(final IGTDepthFirstVisitor<VectorT, ElementT> visitor)
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
   public final Collection<GElementGeometryPair<VectorT, ElementT>> getAllElements() {
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
