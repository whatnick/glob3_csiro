package es.igosoftware.euclid.octree;


public interface IOctreeVisitor {

   public static class AbortVisiting
            extends
               Exception {
      private static final long serialVersionUID = 1L;
   }


   public void visitOctree(final GOctree octree) throws IOctreeVisitor.AbortVisiting;


   public void visitInnerNode(final GOTInnerNode inner) throws IOctreeVisitor.AbortVisiting;


   public void visitLeafNode(final GOTLeafNode leaf) throws IOctreeVisitor.AbortVisiting;


}
