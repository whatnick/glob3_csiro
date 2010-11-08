package es.igosoftware.euclid.pointscloud.octree;

import java.util.Arrays;
import java.util.Map;

import es.igosoftware.euclid.octree.GOTInnerNode;
import es.igosoftware.euclid.octree.GOTLeafNode;
import es.igosoftware.euclid.octree.GOTNode;

public class GPCInnerNode
         extends
            GPCNode {

   private static final long serialVersionUID = 1L;


   private static GPCNode convert(final GOTNode node,
                                  final Map<String, GPCLeafNode> leafNodes) {
      if (node == null) {
         return null;
      }

      if (node instanceof GOTInnerNode) {
         return new GPCInnerNode((GOTInnerNode) node, leafNodes);
      }

      if (node instanceof GOTLeafNode) {
         return new GPCLeafNode((GOTLeafNode) node, leafNodes);
      }

      throw new IllegalArgumentException("Invalid node class: " + node.getClass());
   }


   private static GPCNode[] initializeChildren(final GOTInnerNode node,
                                               final Map<String, GPCLeafNode> leafNodes) {
      final GOTNode[] nodeChildren = node.getChildren();

      final GPCNode[] children = new GPCNode[nodeChildren.length];

      for (int i = 0; i < nodeChildren.length; i++) {
         children[i] = convert(nodeChildren[i], leafNodes);
      }

      return children;
   }


   private final GPCNode[] _children;


   GPCInnerNode(final GOTInnerNode node,
                final Map<String, GPCLeafNode> leafNodes) {
      super(node);

      _children = initializeChildren(node, leafNodes);
   }


   public GPCNode[] getChildren() {
      return _children;
   }


   @Override
   public String toString() {
      return "GPCInnerNode [children=" + Arrays.toString(_children) + "]";
   }


}
