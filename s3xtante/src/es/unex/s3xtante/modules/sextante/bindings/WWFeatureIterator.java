

package es.unex.s3xtante.modules.sextante.bindings;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.globe.IGlobeFeatureCollection;
import es.igosoftware.globe.layers.IGlobeFeature;
import es.igosoftware.util.GMath;
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

   private IGlobeFeatureCollection<IVector2<?>, GAxisAlignedRectangle> _features;
   private int                                                         _index;
   private int                                                         _selectedIndex;
   private int                                                         _cardinality;
   private List<IVectorLayerFilter>                                    _filters;
   private BitSet                                                      _bitSet;
   private Rectangle2D                                                 _extent;


   public WWFeatureIterator() {
      //this creates a dummy iterator
      _cardinality = 0;
      _selectedIndex = 0;
      _index = 0;
      _features = null;
      _filters = null;
      _extent = new Rectangle2D.Double();
   }


   public WWFeatureIterator(final IGlobeFeatureCollection<IVector2<?>, GAxisAlignedRectangle> features,
                            final List<IVectorLayerFilter> filters) {
      _index = 0;
      _selectedIndex = 0;

      _features = features;

      _filters = new ArrayList<IVectorLayerFilter>(filters);

      calculate();
   }


   @Override
   public boolean hasNext() {
      return (_cardinality > _selectedIndex);
   }


   @Override
   public IFeature next() throws IteratorException {

      try {
         while (!_bitSet.get(_index)) {
            _index++;
         }
         final IGlobeFeature<IVector2<?>, GAxisAlignedRectangle> globeFeature = _features.get(_index);
         final IBoundedGeometry<IVector2<?>, ?, GAxisAlignedRectangle> euclidGeom = globeFeature.getGeometry();
         final List<Object> record = globeFeature.getAttributes();
         final Geometry jtsGeom = GJTSUtils.toJTS(euclidGeom);
         final IFeature sextanteFeature = new FeatureImpl(jtsGeom, record.toArray());
         _index++;
         _selectedIndex++;
         return sextanteFeature;
      }
      catch (final Exception e) {
         Sextante.addErrorToLog(e);
         _index++;
         _selectedIndex++;
         throw new IteratorException();
      }

   }


   @Override
   public void close() {
   }


   @Override
   public int getFeatureCount() {
      return _cardinality;
   }


   @SuppressWarnings("null")
   private void calculate() {
      _cardinality = 0;
      Envelope envelope = null;
      try {
         final int iTotalCount = GMath.toInt(_features.size());
         _bitSet = new BitSet(iTotalCount);
         for (int i = 0; i < iTotalCount; i++) {
            final IGlobeFeature<IVector2<?>, GAxisAlignedRectangle> globeFeature = _features.get(i);
            final IBoundedGeometry<IVector2<?>, ?, GAxisAlignedRectangle> euclidGeom = globeFeature.getGeometry();
            final List<Object> record = globeFeature.getAttributes();
            final Geometry jtsGeom = GJTSUtils.toJTS(euclidGeom);
            final IFeature sextanteFeature = new FeatureImpl(jtsGeom, record.toArray());
            boolean bAccept = true;
            for (int j = 0; j < _filters.size(); j++) {
               bAccept = bAccept && _filters.get(j).accept(sextanteFeature, i);
            }
            if (bAccept) {
               if (_cardinality == 0) {
                  envelope = jtsGeom.getEnvelopeInternal();
               }
               else {
                  envelope.expandToInclude(jtsGeom.getEnvelopeInternal());
               }
               _cardinality++;
               _bitSet.set(i);
            }
         }
      }
      catch (final Exception e) {
         _cardinality = 0;
         _bitSet.clear();
      }

      if (_cardinality == 0) {
         _extent = new Rectangle2D.Double();
      }
      else {
         _extent = new Rectangle2D.Double(envelope.getMinX(), envelope.getMinY(), envelope.getWidth(), envelope.getHeight());
      }
   }


   @Override
   public Rectangle2D getExtent() {
      return _extent;
   }


   public WWFeatureIterator getNewInstance() {
      final WWFeatureIterator iter = new WWFeatureIterator();
      iter._features = _features;
      iter._filters = _filters;
      iter._bitSet = _bitSet;
      iter._cardinality = _cardinality;
      iter._index = 0;
      iter._selectedIndex = 0;
      iter._extent = _extent;

      return iter;
   }


}
