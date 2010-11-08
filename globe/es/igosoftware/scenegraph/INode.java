package es.igosoftware.scenegraph;

import es.igosoftware.scenegraph.GPositionRenderableLayer.PickResult;
import gov.nasa.worldwind.Disposable;
import gov.nasa.worldwind.geom.Extent;
import gov.nasa.worldwind.geom.Line;
import gov.nasa.worldwind.geom.Matrix;
import gov.nasa.worldwind.render.DrawContext;

import java.util.List;

public interface INode
         extends
            Disposable {


   public boolean isPickable();


   public boolean pick(final DrawContext dc,
                       final Matrix parentMatrix,
                       final boolean terrainChanged,
                       final Line ray,
                       final List<PickResult> pickResults);


   public String getName();


   public void setVisible(final boolean visible);


   public boolean isVisible();


   //   public Extent getBounds();


   public GGroupNode getParent();


   public GGroupNode getRoot();


   public void setParent(final GGroupNode parent);


   public void reparentTo(final GGroupNode parent);


   public void preRender(final DrawContext dc,
                         final Matrix parentMatrix,
                         final boolean terrainChanged);


   public void render(final DrawContext dc,
                      final Matrix parentMatrix,
                      final boolean terrainChanged);


   public Extent getBoundsInModelCoordinates(final Matrix parentMatrix,
                                             final boolean matrixChanged);


   public void cleanCaches();


   public boolean hasScaleTransformation();


   public void redraw();


   public void addDisposeListener(final Runnable runnable);


   public void acceptVisitor(final IVisitor visitor);


   public int getDepth();


   public void preFetchContents(final DrawContext dc);


}
