package es.igosoftware.euclid.verticescontainer;

import java.util.List;

import es.igosoftware.euclid.colors.IColor;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.GVectorPrecision;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.euclid.vector.IVectorI2;
import es.igosoftware.util.IListInt;
import es.igosoftware.util.IPredicate;

public interface IStructuredVertexContainer<

VectorT extends IVector<VectorT, ?>,

VertexT extends IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>,

GroupT extends IStructuredVertexContainer.IVertexGroup<VectorT, IVertexContainer.Vertex<VectorT>, GroupT>,
//GroupT extends IStructuredVertexContainer.IVertexGroup<VectorT, VertexT, GroupT>,


MutableT extends IStructuredVertexContainer<VectorT, VertexT, GroupT, MutableT>

>
         extends
            IVertexContainer<VectorT, VertexT, MutableT> {


   //////////////////////////////////////////////////////////////////////////
   public static interface IVertexGroup<

   VectorT extends IVector<VectorT, ?>,

   VertexT extends IVertexContainer.Vertex<VectorT>,

   MutableT extends IVertexGroup<VectorT, VertexT, MutableT>

   >

            extends
               IUnstructuredVertexContainer<VectorT, VertexT, MutableT> {

      public VectorT translate(VectorT point);


      public VectorT rotate(VectorT point);


      public VectorT transform(VectorT point);


      public VectorT inverseTranslate(VectorT point);


      public VectorT inverseRotate(VectorT point);


      public VectorT inverseTransform(VectorT point);


      public void addVertexIndex(int index);


      public void removeVertexIndex(int index);


      public IListInt getIndexList();

   }
   //////////////////////////////////////////////////////////////////////////


   //////////////////////////////////////////////////////////////////////////
   public static class StructuredVertex<

   VectorT extends IVector<VectorT, ?>,

   GroupT extends IStructuredVertexContainer.IVertexGroup<VectorT, IVertexContainer.Vertex<VectorT>, GroupT>
   //GroupT extends IStructuredVertexContainer.IVertexGroup<VectorT, ?, GroupT>
   >
            extends
               IVertexContainer.Vertex<VectorT> {

      public final IVectorI2 _rowColumn;
      public final GroupT    _group;


      public StructuredVertex(final VectorT point,
                              final float intensity,
                              final VectorT normal,
                              final IColor color,
                              final long userData,
                              final IVectorI2 rowColumn,
                              final GroupT group) {
         super(point, intensity, normal, color, userData);
         _rowColumn = rowColumn;
         _group = group;
      }


      @Override
      public String toString() {
         return "StructuredVertex [group= [" + _group + "], rowColumn=" + _rowColumn + ", color=" + _color + ", intensity="
                + _intensity + ", normal=" + _normal + ", point=" + _point + ", userData=" + _userData + "]";
      }

   }


   //////////////////////////////////////////////////////////////////////////


   public IVectorI2 getRowColumn(final int index);


   public GroupT getGroup(final int index);


   public List<GroupT> getGroups();


   public IStructuredVertexContainer<VectorT, VertexT, GroupT, ?> getGroupVertices(final GroupT group);


   public boolean hasRowColumn();


   public boolean storageAsRawData();


   public MutableT newEmptyContainer(final int initialCapacity,
                                     final boolean storageAsRawData);


   public MutableT newEmptyContainer(final int initialCapacity,
                                     final GProjection projection,
                                     final boolean storageAsRawData);


   public int addPoint(final VectorT point,
                       final float intensity,
                       final VectorT normal,
                       final IColor color,
                       final long userData,
                       final IVectorI2 rowColumn,
                       final GroupT group);


   public int addPoint(final VectorT point,
                       final float intensity,
                       final VectorT normal,
                       final IColor color,
                       final long userData,
                       final IVectorI2 rowColumn);


   public int addPoint(final VectorT point,
                       final float intensity,
                       final VectorT normal,
                       final IColor color,
                       final long userData,
                       final GroupT group);


   public int addPoint(final VectorT point,
                       final float intensity,
                       final IColor color,
                       final IVectorI2 rowColumn,
                       final GroupT group);


   public int addPoint(final VectorT point,
                       final IVectorI2 rowColumn,
                       final GroupT group);


   public int addPoint(final VectorT point,
                       final IVectorI2 rowColumn);


   public int addPoint(final VectorT point,
                       final GroupT group);


   //   public void addGroup(final GroupT group);
   //
   //
   //   public void addGroups(final List<GroupT> groups);


   public VertexT getRawVertex(final int index);


   public VectorT getRawPoint(final int index);


   public VertexT getTransformedVertex(final int index);


   public VectorT getTransformedPoint(final int index);


   public void setRowColumn(final int index,
                            final IVectorI2 rowColumn);


   public void setGroup(final int index,
                        final GroupT group);


   public void setPoint(final int index,
                        final VectorT point,
                        final float intensity,
                        final VectorT normal,
                        final IColor color,
                        final long userData,
                        final IVectorI2 rowColumn,
                        final GroupT group);


   public GVectorPrecision rowColumnPrecision();


   /**
    * Creates an empty container with the same layout than the receiver
    * 
    * @param initialCapacity
    * @return
    */
   //   @Override
   //   public IStructuredVertexContainer<VectorT, VertexT, GroupT, ?> newEmptyContainer(final int initialCapacity);


   /**
    * Creates a new container with the same, but sorted vertices
    * 
    * @param comparator
    * @return
    */
   //   @Override
   //   public IStructuredVertexContainer<VectorT, VertexT, GroupT, ?> asSortedContainer(final Comparator<VertexT> comparator);


   //   public IStructuredVertexContainer<VectorT, VertexT, GroupT, ?> select(final IPredicate<VertexT> predicate);


   //   public IStructuredVertexContainer<VectorT, VertexT, GroupT, ?> collect(final ITransformer<VertexT, VertexT> predicate);


   public IStructuredVertexContainer<VectorT, VertexT, GroupT, ?> composedWith(final IStructuredVertexContainer<VectorT, VertexT, GroupT, ?> container);


   /**
    * Creates a new container with the same, but raw point data
    * 
    * @param comparator
    * @return
    */
   public IStructuredVertexContainer<VectorT, VertexT, GroupT, ?> asRawContainer();


   /**
    * Creates a new container with the same, but transformed point data
    * 
    * @param comparator
    * @return
    */
   public IStructuredVertexContainer<VectorT, VertexT, GroupT, ?> asTransformedContainer();


   public IStructuredVertexContainer<VectorT, VertexT, GroupT, ?> selectAsSubContainer(final IPredicate<VertexT> predicate);


   //   @Override
   //   public IVertexContainer<VectorT, VertexT, ?> asSubContainer(final int[] subIndices);

   // @Override
   //public GStructuredSubVertexContainer<VectorT, GroupT> asSubContainer(final int[] subIndices);

   // @Override
   //public GStructuredSubVertexContainer<VectorT, GroupT> asSortedSubContainer(final Comparator<VertexT> comparator);

   //   @Override
   //   public IStructuredVertexContainer<VectorT, VertexT, GroupT, ?> asMutableCopy();

}
