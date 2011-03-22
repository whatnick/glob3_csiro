

package es.unex.s3xtante.modules.sextante.bindings;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.vividsolutions.jts.geom.Geometry;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.features.IGlobeFeatureCollection;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.util.FilterIterator;
import es.igosoftware.utils.GJTSUtils;
import es.unex.sextante.core.Sextante;
import es.unex.sextante.dataObjects.FeatureImpl;
import es.unex.sextante.dataObjects.IFeature;
import es.unex.sextante.dataObjects.IFeatureIterator;
import es.unex.sextante.dataObjects.vectorFilters.IVectorLayerFilter;
import es.unex.sextante.exceptions.IteratorException;


public class WWFeatureIterator
         implements
            IFeatureIterator {

   private final Iterator<IGlobeFeature<IVector2<?>, IBoundedGeometry<IVector2<?>, ?, GAxisAlignedRectangle>, GAxisAlignedRectangle>> _iterator;


   public WWFeatureIterator(final IGlobeFeatureCollection<IVector2<?>, IBoundedGeometry<IVector2<?>, ?, GAxisAlignedRectangle>, GAxisAlignedRectangle, ?> features,
                            final List<IVectorLayerFilter> filters) {

      final List<SextanteFilterPredicate> predicates = new ArrayList<SextanteFilterPredicate>();
      for (final IVectorLayerFilter filter : filters) {
         predicates.add(new SextanteFilterPredicate(filter));

      }

      _iterator = new FilterIterator<IGlobeFeature<IVector2<?>, IBoundedGeometry<IVector2<?>, ?, GAxisAlignedRectangle>, GAxisAlignedRectangle>>(
               features.iterator(), predicates.toArray(new SextanteFilterPredicate[0]));

   }


   @Override
   public boolean hasNext() {

      return _iterator.hasNext();


   }


   @Override
   public IFeature next() throws IteratorException {

      try {
         final IGlobeFeature<IVector2<?>, IBoundedGeometry<IVector2<?>, ?, GAxisAlignedRectangle>, GAxisAlignedRectangle> globeFeature = _iterator.next();
         final IBoundedGeometry<IVector2<?>, ?, GAxisAlignedRectangle> euclidGeom = globeFeature.getDefaultGeometry();
         final List<Object> record = globeFeature.getAttributes();
         final Geometry jtsGeom = GJTSUtils.toJTS(euclidGeom);
         final IFeature sextanteFeature = new FeatureImpl(jtsGeom, record.toArray());
         return sextanteFeature;
      }
      catch (final Exception e) {
         Sextante.addErrorToLog(e);
         throw new IteratorException();
      }

   }


   @Override
   public void close() {
   }

}
