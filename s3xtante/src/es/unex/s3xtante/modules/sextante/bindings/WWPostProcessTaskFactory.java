

package es.unex.s3xtante.modules.sextante.bindings;

import es.unex.sextante.core.GeoAlgorithm;
import es.unex.sextante.gui.core.IPostProcessTaskFactory;


public class WWPostProcessTaskFactory
         implements
            IPostProcessTaskFactory {

   @Override
   public Runnable getPostProcessTask(final GeoAlgorithm alg) {

      return new WWPostProcessTask(alg);

   }

}
