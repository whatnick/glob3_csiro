/*

 IGO Software SL  -  info@igosoftware.es

 http://www.glob3.org

-------------------------------------------------------------------------------
 Copyright (c) 2010, IGO Software SL
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
     * Redistributions of source code must retain the above copyright
       notice, this list of conditions and the following disclaimer.
     * Redistributions in binary form must reproduce the above copyright
       notice, this list of conditions and the following disclaimer in the
       documentation and/or other materials provided with the distribution.
     * Neither the name of the IGO Software SL nor the
       names of its contributors may be used to endorse or promote products
       derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL IGO Software SL BE LIABLE FOR ANY
 DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
-------------------------------------------------------------------------------

*/

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
