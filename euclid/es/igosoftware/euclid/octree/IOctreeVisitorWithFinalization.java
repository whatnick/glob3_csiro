package es.igosoftware.euclid.octree;

public interface IOctreeVisitorWithFinalization
         extends
            IOctreeVisitor {

   public void finishedInnerNode(final GOTInnerNode inner) throws IOctreeVisitor.AbortVisiting;


   public void finishedOctree(final GOctree octree) throws IOctreeVisitor.AbortVisiting;

}
