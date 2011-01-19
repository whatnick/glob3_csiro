

package es.igosoftware.protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import es.igosoftware.dmvc.GDUtils;


public class GObjectProtocolField<T>
         extends
            GProtocolField<T> {

   public GObjectProtocolField() {
      super(false);
   }


   @SuppressWarnings("unchecked")
   @Override
   protected void doRead(final DataInputStream input) throws IOException {
      final int lenght = input.readInt();
      if (lenght < 0) {
         set(null);
      }
      else {
         final byte[] bytes = new byte[lenght];
         for (int i = 0; i < lenght; i++) {
            bytes[i] = input.readByte();
         }
         set((T) GDUtils.getObject(bytes));
      }
   }


   @Override
   protected void doWrite(final DataOutputStream output) throws IOException {
      final Object value = get();
      if (value == null) {
         output.writeInt(-1);
      }
      else {
         final byte[] bytes = GDUtils.getSerializedBytes(value);
         output.writeInt(bytes.length);
         output.write(bytes);
      }
   }

}
