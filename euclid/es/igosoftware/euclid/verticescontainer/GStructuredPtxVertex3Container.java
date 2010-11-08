package es.igosoftware.euclid.verticescontainer;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import es.igosoftware.euclid.colors.GColorPrecision;
import es.igosoftware.euclid.colors.IColor;
import es.igosoftware.euclid.matrix.GMatrix33D;
import es.igosoftware.euclid.matrix.GMatrix44D;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.utils.GBufferUtils;
import es.igosoftware.euclid.vector.GVector2I;
import es.igosoftware.euclid.vector.GVector3D;
import es.igosoftware.euclid.vector.GVectorPrecision;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.euclid.vector.IVectorI2;

public final class GStructuredPtxVertex3Container
         extends
            GStructuredVertexContainerWithDefaultsAbstract<IVector3<?>, GPtx3Group, GStructuredPtxVertex3Container> {

   //----------------------------------------------------------------------------
   // Strategy-pattern classes to handle short/int vectors
   private static final class RowColumnHandlerS
            implements
               RowColumnHandler {

      private static final int ELEMENT_SIZE = 2;

      private ShortBuffer      _buffer;


      private RowColumnHandlerS(final int initialCapacity) {
         _buffer = GBufferUtils.createShortBuffer(initialCapacity * ELEMENT_SIZE, false);
      }


      @Override
      public IVectorI2<?> getRowColumn(final int index) {
         return GBufferUtils.getVector(_buffer, index);
      }


      @Override
      public void growBuffer(final int newCapacity) {
         _buffer = GBufferUtils.growBuffer(newCapacity * ELEMENT_SIZE, _buffer);
      }


      @Override
      public void putRowColumn(final int index,
                               final IVectorI2<?> vector) {
         GBufferUtils.putVector(_buffer, index, vector);
      }


      @Override
      public void shrinkBuffer(final int size) {
         _buffer = GBufferUtils.shrinkBuffer(size * ELEMENT_SIZE, _buffer);
      }
   }


   private static final class RowColumnHandlerI
            implements
               RowColumnHandler {

      private static final int ELEMENT_SIZE = 2;

      private IntBuffer        _buffer;


      private RowColumnHandlerI(final int initialCapacity) {
         _buffer = GBufferUtils.createIntBuffer(initialCapacity * ELEMENT_SIZE, false);
      }


      @Override
      public IVectorI2<?> getRowColumn(final int index) {
         return GBufferUtils.getVector(_buffer, index);
      }


      @Override
      public void growBuffer(final int newCapacity) {
         _buffer = GBufferUtils.growBuffer(newCapacity * ELEMENT_SIZE, _buffer);
      }


      @Override
      public void putRowColumn(final int index,
                               final IVectorI2<?> vector) {
         GBufferUtils.putVector(_buffer, index, vector);
      }


      @Override
      public void shrinkBuffer(final int size) {
         _buffer = GBufferUtils.shrinkBuffer(size * ELEMENT_SIZE, _buffer);
      }
   }


   public GStructuredPtxVertex3Container(final GVectorPrecision vectorPrecision,
                                         final GColorPrecision colorPrecision,
                                         final GProjection projection,
                                         final int initialCapacity,
                                         final boolean withIntensities,
                                         final boolean withColors,
                                         final boolean withNormals,
                                         final boolean withUserData,
                                         final boolean storeAsRawData,
                                         final GVectorPrecision rowColumnPrecision,
                                         final boolean withRowColumn,
                                         final IVectorI2 defaultRowColumn,
                                         final GPtx3Group defaultGroup) {
      this(vectorPrecision, colorPrecision, projection, initialCapacity, withIntensities, 0, withColors, null, withNormals, null,
           withUserData, 0, storeAsRawData, rowColumnPrecision, withRowColumn, defaultRowColumn, defaultGroup);
   }


   public GStructuredPtxVertex3Container(final GVectorPrecision vectorPrecision,
                                         final GColorPrecision colorPrecision,
                                         final GProjection projection,
                                         final boolean withIntensities,
                                         final boolean withColors,
                                         final boolean withNormals,
                                         final boolean withUserData,
                                         final boolean storeAsRawData,
                                         final GVectorPrecision rowColumnPrecision,
                                         final boolean withRowColumn,
                                         final IVectorI2 defaultRowColumn,
                                         final GPtx3Group defaultGroup) {
      this(vectorPrecision, colorPrecision, projection, 3, withIntensities, 0, withColors, null, withNormals, null, withUserData,
           0, storeAsRawData, rowColumnPrecision, withRowColumn, defaultRowColumn, defaultGroup);
   }


   public GStructuredPtxVertex3Container(final GVectorPrecision vectorPrecision,
                                         final GColorPrecision colorPrecision,
                                         final GProjection projection,
                                         final boolean withIntensities,
                                         final boolean withColors,
                                         final boolean withNormals,
                                         final boolean withUserData,
                                         final GVectorPrecision rowColumnPrecision,
                                         final boolean withRowColumn,
                                         final IVectorI2 defaultRowColumn,
                                         final GPtx3Group defaultGroup) {
      this(vectorPrecision, colorPrecision, projection, 3, withIntensities, 0, withColors, null, withNormals, null, withUserData,
           0, true, rowColumnPrecision, withRowColumn, defaultRowColumn, defaultGroup);
   }


   public GStructuredPtxVertex3Container(final GVectorPrecision vectorPrecision,
                                         final GColorPrecision colorPrecision,
                                         final GProjection projection,
                                         final boolean withIntensities,
                                         final boolean withColors,
                                         final boolean withNormals,
                                         final boolean withUserData,
                                         final boolean storeAsRawData,
                                         final GVectorPrecision rowColumnPrecision,
                                         final boolean withRowColumn,
                                         final GPtx3Group defaultGroup) {
      this(vectorPrecision, colorPrecision, projection, 3, withIntensities, 0, withColors, null, withNormals, null, withUserData,
           0, storeAsRawData, rowColumnPrecision, withRowColumn, GVector2I.ZERO, defaultGroup);
   }


   public GStructuredPtxVertex3Container(final GVectorPrecision vectorPrecision,
                                         final GColorPrecision colorPrecision,
                                         final GProjection projection,
                                         final boolean withIntensities,
                                         final boolean withColors,
                                         final boolean withNormals,
                                         final boolean withUserData,
                                         final GVectorPrecision rowColumnPrecision,
                                         final boolean withRowColumn,
                                         final GPtx3Group defaultGroup) {
      this(vectorPrecision, colorPrecision, projection, 3, withIntensities, 0, withColors, null, withNormals, null, withUserData,
           0, true, rowColumnPrecision, withRowColumn, GVector2I.ZERO, defaultGroup);
   }


   public GStructuredPtxVertex3Container(final GVectorPrecision vectorPrecision,
                                         final GColorPrecision colorPrecision,
                                         final GProjection projection,
                                         final boolean withIntensities,
                                         final boolean withColors,
                                         final boolean withNormals,
                                         final boolean withUserData,
                                         final boolean storeAsRawData,
                                         final GVectorPrecision rowColumnPrecision,
                                         final boolean withRowColumn,
                                         final IVectorI2 defaultRowColumn) {
      this(vectorPrecision, colorPrecision, projection, 3, withIntensities, 0, withColors, null, withNormals, null, withUserData,
           0, storeAsRawData, rowColumnPrecision, withRowColumn, defaultRowColumn, null);
   }


   public GStructuredPtxVertex3Container(final GVectorPrecision vectorPrecision,
                                         final GColorPrecision colorPrecision,
                                         final GProjection projection,
                                         final boolean withIntensities,
                                         final boolean withColors,
                                         final boolean withNormals,
                                         final boolean withUserData,
                                         final GVectorPrecision rowColumnPrecision,
                                         final boolean withRowColumn,
                                         final IVectorI2 defaultRowColumn) {
      this(vectorPrecision, colorPrecision, projection, 3, withIntensities, 0, withColors, null, withNormals, null, withUserData,
           0, true, rowColumnPrecision, withRowColumn, defaultRowColumn, null);
   }


   public GStructuredPtxVertex3Container(final GVectorPrecision vectorPrecision,
                                         final GColorPrecision colorPrecision,
                                         final GProjection projection,
                                         final boolean withIntensities,
                                         final boolean withColors,
                                         final boolean withNormals,
                                         final boolean withUserData,
                                         final boolean storeAsRawData,
                                         final GVectorPrecision rowColumnPrecision,
                                         final boolean withRowColumn) {
      this(vectorPrecision, colorPrecision, projection, 3, withIntensities, 0, withColors, null, withNormals, null, withUserData,
           0, storeAsRawData, rowColumnPrecision, withRowColumn, GVector2I.ZERO, null);
   }


   public GStructuredPtxVertex3Container(final GVectorPrecision vectorPrecision,
                                         final GColorPrecision colorPrecision,
                                         final GProjection projection,
                                         final boolean withIntensities,
                                         final boolean withColors,
                                         final boolean withNormals,
                                         final boolean withUserData,
                                         final GVectorPrecision rowColumnPrecision,
                                         final boolean withRowColumn) {
      this(vectorPrecision, colorPrecision, projection, 3, withIntensities, 0, withColors, null, withNormals, null, withUserData,
           0, true, rowColumnPrecision, withRowColumn, GVector2I.ZERO, null);
   }


   public GStructuredPtxVertex3Container(final GVectorPrecision vectorPrecision,
                                         final GColorPrecision colorPrecision,
                                         final GProjection projection,
                                         final boolean withIntensities,
                                         final float defaultIntensity,
                                         final boolean withColors,
                                         final IColor defaultColor,
                                         final boolean withNormals,
                                         final IVector3<?> defaultNormal,
                                         final boolean withUserData,
                                         final long defaultUserData,
                                         final boolean storeAsRawData,
                                         final GVectorPrecision rowColumnPrecision,
                                         final boolean withRowColumn,
                                         final IVectorI2 defaultRowColumn,
                                         final GPtx3Group defaultGroup) {
      super(vectorPrecision, colorPrecision, projection, 3, withIntensities, defaultIntensity, withColors, defaultColor,
            withNormals, defaultNormal, withUserData, defaultUserData, storeAsRawData, rowColumnPrecision, withRowColumn,
            defaultRowColumn, defaultGroup);

   }


   public GStructuredPtxVertex3Container(final GVectorPrecision vectorPrecision,
                                         final GColorPrecision colorPrecision,
                                         final GProjection projection,
                                         final int initialCapacity,
                                         final boolean withIntensities,
                                         final float defaultIntensity,
                                         final boolean withColors,
                                         final IColor defaultColor,
                                         final boolean withNormals,
                                         final IVector3<?> defaultNormal,
                                         final boolean withUserData,
                                         final long defaultUserData,
                                         final boolean storeAsRawData,
                                         final GVectorPrecision rowColumnPrecision,
                                         final boolean withRowColumn,
                                         final IVectorI2 defaultRowColumn,
                                         final GPtx3Group defaultGroup) {
      super(vectorPrecision, colorPrecision, projection, initialCapacity, withIntensities, defaultIntensity, withColors,
            defaultColor, withNormals, defaultNormal, withUserData, defaultUserData, storeAsRawData, rowColumnPrecision,
            withRowColumn, defaultRowColumn, defaultGroup);

   }


   @Override
   protected GPtx3Group initializeDefaultGroup() {

      return new GPtx3Group(_vectorPrecision, _colorPrecision, _projection, GVector3D.ZERO, 3, hasIntensities(),
               _defaultIntensity, hasColors(), _defaultColor, hasNormals(), _defaultNormal, hasUserData(), _defaultUserData, 0,
               0, GVector3D.ZERO, GMatrix33D.IDENTITY, GMatrix44D.IDENTITY);
   }


   @Override
   protected GStructuredVertexContainerWithDefaultsAbstract.RowColumnHandler initializeRowColumnHandler() {
      switch (_rowColumnPrecision) {
         case SHORT:
            return new RowColumnHandlerS(_capacity);
         case INT:
            return new RowColumnHandlerI(_capacity);
         default:
            throw new IllegalArgumentException("Invalid rowColumn precision");
      }
   }


   @Override
   protected GCommonVertexContainerAbstract.VectorHandler<IVector3<?>> initializeNormalsHandler() {
      switch (_vectorPrecision) {
         case FLOAT:
            return new Vector3HandlerF(_capacity);
         case DOUBLE:
            return new Vector3HandlerD(_capacity);
         default:
            throw new IllegalArgumentException("Invalid vector precision");
      }
   }


   @Override
   protected GCommonVertexContainerAbstract.VectorHandler<IVector3<?>> initializePointsHandler() {
      switch (_vectorPrecision) {
         case FLOAT:
            return new Vector3HandlerF(_capacity);
         case DOUBLE:
            return new Vector3HandlerD(_capacity);
         default:
            throw new IllegalArgumentException("Invalid vector precision");
      }
   }


   @Override
   protected String getStringName() {
      return "GStructuredPtxVertex3Container";
   }


   @Override
   public GStructuredPtxVertex3Container newEmptyContainer(final int initialCapacity) {

      return newEmptyContainer(initialCapacity, _projection, storageAsRawData());
   }


   @Override
   public GStructuredPtxVertex3Container newEmptyContainer(final int initialCapacity,
                                                           final GProjection projection) {

      return newEmptyContainer(initialCapacity, projection, storageAsRawData());
   }


   @Override
   public GStructuredPtxVertex3Container newEmptyContainer(final int initialCapacity,
                                                           final boolean storageAsRawData) {

      return newEmptyContainer(initialCapacity, _projection, storageAsRawData);
   }


   @Override
   public GStructuredPtxVertex3Container newEmptyContainer(final int initialCapacity,
                                                           final GProjection projection,
                                                           final boolean storageAsRawData) {

      return new GStructuredPtxVertex3Container(_vectorPrecision, _colorPrecision, projection, initialCapacity, hasIntensities(),
               _defaultIntensity, hasColors(), _defaultColor, hasNormals(), _defaultNormal, hasUserData(), _defaultUserData,
               storageAsRawData, _rowColumnPrecision, hasRowColumn(), _defaultRowColumn, null);
   }


   @Override
   public byte dimensions() {
      return 3;
   }


}
