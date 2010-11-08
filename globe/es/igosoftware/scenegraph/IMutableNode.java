package es.igosoftware.scenegraph;

import es.igosoftware.euclid.mutability.IMutable;
import es.igosoftware.euclid.vector.IVector3;

public interface IMutableNode<MutableT extends IMutableNode<MutableT>>
         extends
            INode,
            IMutable<MutableT> {


   public void setHeading(final double heading);


   public void setPitch(final double pitch);


   public void setRoll(final double roll);


   public void setScale(final double scale);


   public void setTranslation(final IVector3<?> translation);

}
