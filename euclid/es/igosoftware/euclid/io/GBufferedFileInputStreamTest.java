package es.igosoftware.euclid.io;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import es.igosoftware.io.GIOUtils;


public class GBufferedFileInputStreamTest {
   //   public static void main(String[] args) throws IOException {
   //      //      FileInputStream in = null;
   //      //      FileOutputStream out = null;
   //      InputStreamReader inStream = null;
   //      OutputStreamWriter outStream = null;
   //      try {
   //         //in = new FileInputStream("xanadu.txt");
   //         inStream = new InputStreamReader(new FileInputStream("xanadu.txt"));
   //         //out = new FileOutputStream("outagain.txt");
   //         outStream = new OutputStreamWriter(new FileOutputStream("outagain.txt"));
   //         int c;
   //
   //         while ((c = inStream.read()) != -1) {
   //            outStream.write(c);
   //         }
   //
   //
   //      }
   //      finally {
   //         if (inStream != null) {
   //            inStream.close();
   //         }
   //         if (outStream != null) {
   //            outStream.close();
   //         }
   //      }
   //   }

   //   public static void main(String[] args) throws IOException {
   //      FileReader input = null;
   //      FileWriter output = null;
   //
   //      try {
   //         input = new FileReader("xanadu.txt");
   //         output = new FileWriter("outPutAsChars.txt");
   //
   //         int c = 0;
   //
   //         while ((c = input.read()) != -1) {
   //            
   //            output.write(c);
   //            System.out.println(Integer.toString(c));
   //
   //         }
   //
   //      }
   //      finally {
   //         if (input != null) {
   //            input.close();
   //         }
   //         if (output != null) {
   //            output.close();
   //         }
   //      }
   //   }
   public static void main(final String[] args) throws IOException {
      OutputStream out = null;
      GBufferedFileInputStream in = null;

      try {
         in = new GBufferedFileInputStream("/home/dgd/Escritorio/FileBuffered/xanadu.txt", 4);
         out = new FileOutputStream("/home/dgd/Escritorio/FileBuffered/outAsBuffered.txt");


         int c;
         while ((c = in.read()) != -1) {
            out.write(c);
         }
         //                  String line;
         //         while ((line = in.readLine()) != null) {
         //            System.out.println(line);
         //         }
      }
      finally {
         GIOUtils.gentlyClose(in);
         GIOUtils.gentlyClose(out);
      }
   }

}
