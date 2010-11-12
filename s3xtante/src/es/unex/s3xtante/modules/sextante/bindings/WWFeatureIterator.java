package es.unex.s3xtante.modules.sextante.bindings;

import java.util.ArrayList;

import es.igosoftware.globe.layers.Feature;
import es.unex.sextante.dataObjects.FeatureImpl;
import es.unex.sextante.dataObjects.IFeature;
import es.unex.sextante.dataObjects.IFeatureIterator;
import es.unex.sextante.exceptions.IteratorException;

public class WWFeatureIterator
         implements
            IFeatureIterator {

   private final Object m_Features;
   private int          m_iFeature;


   public WWFeatureIterator(final Object features) {

      m_iFeature = 0;
      m_Features = features;

   }


   @Override
   public boolean hasNext() {

      if (m_Features instanceof ArrayList) {
         return m_iFeature < ((ArrayList) m_Features).size();
      }
      else if (m_Features instanceof Feature[]) {
         return m_iFeature < ((Feature[]) m_Features).length;
      }
      else {
         return false;
      }

   }


   @Override
   public IFeature next() throws IteratorException {

      try {
         Feature feature;
         if (m_Features instanceof ArrayList) {
            feature = (Feature) ((ArrayList) m_Features).get(m_iFeature);
         }
         else if (m_Features instanceof Feature[]) {
            feature = ((Feature[]) m_Features)[m_iFeature];
         }
         else {
            return null;
         }
         final FeatureImpl retFeature = new FeatureImpl(feature._geometry, feature._attributes);
         m_iFeature++;
         return retFeature;
      }
      catch (final Exception e) {
         throw new IteratorException();
      }

   }


   @Override
   public void close() {


   }


}
