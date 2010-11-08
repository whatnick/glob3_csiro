package es.igosoftware.pointscloud.scenegraph;

import java.awt.Color;

import es.igosoftware.euclid.pointscloud.octree.GPCInnerNode;
import es.igosoftware.euclid.pointscloud.octree.GPCLeafNode;
import es.igosoftware.euclid.pointscloud.octree.GPCNode;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.pointscloud.GPointsCloudLayer;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.render.DrawContext;

public final class GSGGroupNode
         extends
            GSGNode {

   private final GSGNode[] _children;


   public GSGGroupNode(final GPCInnerNode node,
                       final GProjection projection,
                       final GPointsCloudLayer layer) {
      super(node.getBounds(), projection, layer);

      _children = initializeChildren(node, projection, layer);
   }


   private static GSGNode[] initializeChildren(final GPCInnerNode node,
                                               final GProjection projection,
                                               final GPointsCloudLayer layer) {
      final GPCNode[] nodeChildren = node.getChildren();

      final GSGNode[] children = new GSGNode[nodeChildren.length];

      for (int i = 0; i < nodeChildren.length; i++) {
         children[i] = convert(nodeChildren[i], projection, layer);
      }

      return children;
   }


   private static GSGNode convert(final GPCNode node,
                                  final GProjection projection,
                                  final GPointsCloudLayer layer) {
      if (node == null) {
         return null;
      }

      if (node instanceof GPCInnerNode) {
         return new GSGGroupNode((GPCInnerNode) node, projection, layer);
      }

      if (node instanceof GPCLeafNode) {
         return new GSGPointsNode((GPCLeafNode) node, projection, layer);
      }

      throw new IllegalArgumentException("Invalid node class " + node.getClass());
   }


   @Override
   public final void doPreRender(final DrawContext dc,
                                 final boolean changed) {
      for (final GSGNode child : _children) {
         if (child != null) {
            child.doPreRender(dc, changed);
         }
      }
   }


   @Override
   protected final int doRender(final DrawContext dc) {
      int rendered = 0;
      for (final GSGNode child : _children) {
         if (child != null) {
            rendered += child.render(dc);
         }
      }
      return rendered;
   }


   @Override
   protected final void setPriority(final float priority) {
      //      if (priority == _priority) {
      //         return;
      //      }
      super.setPriority(priority);
      for (final GSGNode child : _children) {
         if (child != null) {
            child.setPriority(priority);
         }
      }
   }


   @Override
   public final void initialize(final DrawContext dc) {
      for (final GSGNode child : _children) {
         if (child != null) {
            child.initialize(dc);
         }
      }
   }


   @Override
   public void setColorFromElevation(final boolean colorFromElevation) {
      for (final GSGNode child : _children) {
         if (child != null) {
            child.setColorFromElevation(colorFromElevation);
         }
      }
   }


   @Override
   public void reload() {
      for (final GSGNode child : _children) {
         if (child != null) {
            child.reload();
         }
      }
   }


   @Override
   public Position getHomePosition() {
      for (final GSGNode child : _children) {
         if (child != null) {
            return child.getHomePosition();
         }
      }
      return null;
   }


   @Override
   public void setPointsColor(final Color pointsColor) {
      for (final GSGNode child : _children) {
         if (child != null) {
            child.setPointsColor(pointsColor);
         }
      }
   }


}
