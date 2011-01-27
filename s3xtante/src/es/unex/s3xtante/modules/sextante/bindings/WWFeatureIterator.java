package es.unex.s3xtante.modules.sextante.bindings;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.BitSet;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

import es.igosoftware.globe.layers.Feature;
import es.unex.sextante.core.Sextante;
import es.unex.sextante.dataObjects.FeatureImpl;
import es.unex.sextante.dataObjects.IFeature;
import es.unex.sextante.dataObjects.IFeatureIterator;
import es.unex.sextante.dataObjects.vectorFilters.IVectorLayerFilter;
import es.unex.sextante.exceptions.IteratorException;


public class WWFeatureIterator
         implements
            IFeatureIterator {

   private Feature[]                     m_Features;
   private int                           m_iIndex;
   private int                           m_iSelectedIndex;
   private int                           m_iCardinality;
   private ArrayList<IVectorLayerFilter> m_Filters;
   private BitSet                        m_BitSet;
   private Rectangle2D                   m_Extent;


   public WWFeatureIterator() {

      //this creates a dummy iterator
      m_iCardinality = 0;
      m_iSelectedIndex = 0;
      m_iIndex = 0;
      m_Features = null;
      m_Filters = null;
      m_Extent = new Rectangle2D.Double();

   }


   public WWFeatureIterator(final Feature[] features,
                            final ArrayList<IVectorLayerFilter> filters) {

      m_iIndex = 0;
      m_iSelectedIndex = 0;
      m_Features = features;

      m_Filters = filters;
      calculate();

   }


   public boolean hasNext() {

      return m_iCardinality > m_iSelectedIndex;

   }


   public IFeature next() throws IteratorException {

      try {
         while (!m_BitSet.get(m_iIndex)) {
            m_iIndex++;
         }
         final Geometry geom = m_Features[m_iIndex]._geometry;
         final Object[] record = m_Features[m_iIndex]._attributes;
         final IFeature feature = new FeatureImpl(geom, record);
         m_iIndex++;
         m_iSelectedIndex++;
         return feature;
      }
      catch (final Exception e) {
         Sextante.addErrorToLog(e);
         m_iIndex++;
         m_iSelectedIndex++;
         throw new IteratorException();
      }

   }


   public void close() {}


   public int getFeatureCount() {

      return m_iCardinality;

   }


   private void calculate() {

      m_iCardinality = 0;
      Envelope envelope = null;
      try {
         final int iTotalCount = m_Features.length;
         m_BitSet = new BitSet(iTotalCount);
         for (int i = 0; i < iTotalCount; i++) {
            final Geometry geom = m_Features[i]._geometry;
            final Object[] record = m_Features[i]._attributes;
            final IFeature feature = new FeatureImpl(geom, record);
            boolean bAccept = true;
            for (int j = 0; j < m_Filters.size(); j++) {
               bAccept = bAccept && m_Filters.get(j).accept(feature, i);
            }
            if (bAccept) {
               if (m_iCardinality == 0) {
                  envelope = geom.getEnvelopeInternal();
               }
               else {
                  envelope.expandToInclude(geom.getEnvelopeInternal());
               }
               m_iCardinality++;
               m_BitSet.set(i);
            }
         }
      }
      catch (final Exception e) {
         m_iCardinality = 0;
         m_BitSet.clear();
      }

      if (m_iCardinality == 0) {
         m_Extent = new Rectangle2D.Double();
      }
      else {
         m_Extent = new Rectangle2D.Double(envelope.getMinX(), envelope.getMinY(), envelope.getWidth(), envelope.getHeight());
      }


   }


   @Override
   public Rectangle2D getExtent() {

      return m_Extent;

   }


   public WWFeatureIterator getNewInstance() {

      final WWFeatureIterator iter = new WWFeatureIterator();
      iter.m_Features = this.m_Features;
      iter.m_Filters = this.m_Filters;
      iter.m_BitSet = this.m_BitSet;
      iter.m_iCardinality = this.m_iCardinality;
      iter.m_iIndex = 0;
      iter.m_iSelectedIndex = 0;
      iter.m_Extent = this.m_Extent;

      return iter;

   }


}
