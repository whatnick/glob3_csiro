

package es.igosoftware.protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


public abstract class GArrayProtocolField<T extends IProtocolObject>
         extends
            GProtocolField<T[]> {


   private GProtocolMultiplexor _multiplexor;


   public GArrayProtocolField(final boolean isNullable) {
      super(isNullable);
   }


   public void setMultiplexor(final GProtocolMultiplexor multiplexor) {
      _multiplexor = multiplexor;
   }


   @SuppressWarnings("unchecked")
   @Override
   protected void doRead(final DataInputStream input) throws IOException {
      final int lenght = input.readInt();
      if (lenght < 0) {
         set(null);
      }
      else {
         final T[] value = createArray(lenght);
         for (int i = 0; i < lenght; i++) {
            //            final FT child = (FT) _childrenField.clone();
            //            child.read(input);
            //            value[i] = child.get();

            //value[i] = _multiplexor.createObject(bytes);
            value[i] = (T) _multiplexor.createObject(input);
         }
         set(value);
      }
   }


   //   private T[] createArray(final int lenght) {
   //      return (T[]) new IProtocolObject[lenght];
   //   }
   protected abstract T[] createArray(final int lenght);


   @Override
   protected void doWrite(final DataOutputStream output) throws IOException {
      final T[] value = get();
      if (value == null) {
         output.writeInt(-1);
      }
      else {
         output.writeInt(value.length);
         for (final T child : value) {
            output.write(_multiplexor.getProtocolBytes(child));
         }
      }
   }


}
