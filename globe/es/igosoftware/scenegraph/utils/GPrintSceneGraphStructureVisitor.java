package es.igosoftware.scenegraph.utils;

import es.igosoftware.scenegraph.INode;
import es.igosoftware.scenegraph.IVisitor;
import es.igosoftware.util.StringUtils;

public class GPrintSceneGraphStructureVisitor
         implements
            IVisitor {


   @Override
   public void visit(final INode node) {
      System.out.println(getLevel(node) + node);
   }


   private String getLevel(final INode node) {
      return StringUtils.spaces(node.getDepth() * 2);
   }

}
