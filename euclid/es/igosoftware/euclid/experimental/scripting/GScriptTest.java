

package es.igosoftware.euclid.experimental.scripting;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import junit.framework.TestCase;
import es.igosoftware.euclid.experimental.algorithms.IAlgorithm;


public class GScriptTest
         extends
            TestCase {

   public void testNormalCase() throws Exception {
      final IAlgorithm alg = GScriptManager.getPythonAlgorithm(getScriptStream("normalCase.py"));
      assertTrue(alg.getName().equals("name"));
      assertTrue(alg.getDescription().equals("description"));
      @SuppressWarnings("unchecked")
      final Object result = alg.apply(new Integer(2));
      assertTrue(result.equals(3.14159));
   }


   public void testNoAlgorithmVarSet() throws Exception {
      try {
         GScriptManager.getPythonAlgorithm(getScriptStream("noAlgorithmVarSet.py"));
         fail();
      }
      catch (final GIllegalScriptException e) {
      }
   }


   public void testNoIAlgorithmInstance() throws Exception {
      try {
         GScriptManager.getPythonAlgorithm(getScriptStream("noIAlgorithmInstance.py"));
         fail();
      }
      catch (final GIllegalScriptException e) {
      }
   }


   private InputStream getScriptStream(final String fileName) throws FileNotFoundException {
      return new BufferedInputStream(new FileInputStream(new File("es/igosoftware/euclid/experimental/scripting/" + fileName)));
   }

}
