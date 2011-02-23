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


package es.igosoftware.io;

import java.io.File;

import es.igosoftware.util.GAssert;


public class GFileLoader
         implements
            ILoader {

   protected final File _rootDirectory;


   public GFileLoader(final String rootDirectoryName) {
      GAssert.notNull(rootDirectoryName, "rootDirectoryName");

      _rootDirectory = new File(rootDirectoryName);
      validateRootDirectory();
   }


   private void validateRootDirectory() {
      if (!_rootDirectory.exists()) {
         throw new IllegalArgumentException("Root directory not found (" + _rootDirectory.getAbsolutePath() + ")");
      }

      if (!_rootDirectory.isDirectory()) {
         throw new IllegalArgumentException(_rootDirectory.getAbsolutePath() + " is not a directory");
      }

   }


   public GFileLoader(final File rootDirectory) {
      GAssert.notNull(rootDirectory, "rootDirectory");

      _rootDirectory = rootDirectory;
      validateRootDirectory();
   }


   @Override
   public ILoader.LoadID load(final String fileName,
                              final long bytesToLoad,
                              final int priority,
                              final ILoader.IHandler handler) {

      final File file = new File(_rootDirectory, fileName);

      if (!file.exists()) {
         handler.loadError(ILoader.ErrorType.NOT_FOUND, null);
      }
      else if (!file.canRead()) {
         handler.loadError(ILoader.ErrorType.CANT_READ, null);
      }
      else {
         try {
            // in files, all the needed bytes are available in a shot 
            final long bytesLoaded = file.length();
            handler.loaded(file, bytesLoaded, true);
         }
         catch (final ILoader.AbortLoading e) {
            // do nothing
         }
      }

      return null;
   }


   @Override
   public void cancelLoad(final ILoader.LoadID id) {
      // do nothing, the loads in GFileLoader are not asynchronous
   }


   @Override
   public void cancelAllLoads(final String fileName) {
      // do nothing, the loads in GFileLoader are not asynchronous
   }


}
