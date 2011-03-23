

package es.unex.s3xtante.modules.sextante.bindings;

import java.util.List;

import com.vividsolutions.jts.geom.Geometry;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.util.IPredicate;
import es.igosoftware.utils.GJTSUtils;
import es.unex.sextante.dataObjects.FeatureImpl;
import es.unex.sextante.dataObjects.IFeature;
import es.unex.sextante.dataObjects.vectorFilters.IVectorLayerFilter;


public class SextanteFilterPredicate
         implements
            IPredicate<IGlobeFeature<IVector2<?>, IBoundedGeometry<IVector2<?>, ?, GAxisAlignedRectangle>>> {

   private final IVectorLayerFilter _filter;


   public SextanteFilterPredicate(final IVectorLayerFilter filter) {

      _filter = filter;

   }


   @Override
   public boolean evaluate(final IGlobeFeature<IVector2<?>, IBoundedGeometry<IVector2<?>, ?, GAxisAlignedRectangle>> globeFeature) {

      final IBoundedGeometry<IVector2<?>, ?, GAxisAlignedRectangle> euclidGeom = globeFeature.getDefaultGeometry();
      final List<Object> record = globeFeature.getAttributes();
      final Geometry jtsGeom = GJTSUtils.toJTS(euclidGeom);
      final IFeature sextanteFeature = new FeatureImpl(jtsGeom, record.toArray());

      return _filter.accept(sextanteFeature, 0/*this index dos es not apply here, so we pass zero*/);

   }

}
