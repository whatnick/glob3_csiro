package es.igosoftware.euclid.verticescontainer;

import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.util.IPredicate;

public interface IUnstructuredVertexContainer<

VectorT extends IVector<VectorT, ?>,

VertexT extends IVertexContainer.Vertex<VectorT>,

//MutableT extends IMutable<MutableT>
MutableT extends IUnstructuredVertexContainer<VectorT, VertexT, MutableT>

>
         extends
            IVertexContainer<VectorT, VertexT, MutableT> {


   public VectorT getReferencePoint();


   //   public IVertexContainer<VectorT, VertexT, ?> composedWith(final IUnstructuredVertexContainer<VectorT, VertexT, ?> container);
   //
   //
   //   public IVertexContainer<VectorT, VertexT, ?> select(final IPredicate<VertexT> predicate);
   //
   //
   //   public IVertexContainer<VectorT, VertexT, ?> collect(final ITransformer<VertexT, VertexT> predicate,
   //                                                        final VectorT referencePoint);
   //
   //
   //   public IVertexContainer<VectorT, VertexT, ?> newEmptyContainer(final int initialCapacity,
   //                                                                  final VectorT referencePoint);


   public IUnstructuredVertexContainer<VectorT, VertexT, ?> composedWith(final IUnstructuredVertexContainer<VectorT, VertexT, ?> container);


   public MutableT newEmptyContainer(final int initialCapacity,
                                     final VectorT referencePoint);


   public MutableT newEmptyContainer(final int initialCapacity,
                                     final GProjection projection,
                                     final VectorT referencePoint);


   public IUnstructuredVertexContainer<VectorT, VertexT, ?> selectAsSubContainer(final IPredicate<VertexT> predicate);


   //   @Override
   //   public IUnstructuredVertexContainer<VectorT, VertexT, ?> newEmptyContainer(final int initialCapacity);


   //   @Override
   //   public IVertexContainer<VectorT, VertexT, ?> asSubContainer(final int[] subIndices);

   //    @Override
   //   public GSubVertexContainer<VectorT> asSubContainer(final int[] subIndices);

   // @Override
   //public GSubVertexContainer<VectorT> asSortedSubContainer(final Comparator<VertexT> comparator);

   //   @Override
   //   public IUnstructuredVertexContainer<VectorT, VertexT, ?> asMutableCopy();

}
