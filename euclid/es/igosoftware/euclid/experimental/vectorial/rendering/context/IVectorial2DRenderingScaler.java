

package es.igosoftware.euclid.experimental.vectorial.rendering.context;

import es.igosoftware.euclid.experimental.vectorial.rendering.utils.GAWTPoints;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.IPointsContainer;
import es.igosoftware.euclid.vector.IVector2;


public interface IVectorial2DRenderingScaler {


   public GAWTPoints toScaledAndTranslatedPoints(final IPointsContainer<IVector2> pointsContainer);


   public IVector2 scaleExtent(final IVector2 extent);


   public IVector2 scaleAndTranslatePoint(final IVector2 point);


   public GProjection getProjection();


}
