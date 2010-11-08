package es.igosoftware.euclid.pointscloud.octree;

import java.io.Serializable;

import es.igosoftware.euclid.bounding.GAxisAlignedBox;
import es.igosoftware.euclid.octree.GOTNode;

public abstract class GPCNode
         implements
            Serializable {

   private static final long     serialVersionUID = 1L;


   private final GAxisAlignedBox _bounds;


   protected GPCNode(final GOTNode node) {
      _bounds = node.getBounds();
   }


   public GAxisAlignedBox getBounds() {
      return _bounds;
   }


   @Override
   public abstract String toString();

}
