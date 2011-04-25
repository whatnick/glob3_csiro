

package es.igosoftware.euclid.scripting;

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
<<<<<<< HEAD:euclid/es/igosoftware/euclid/experimental/scripting/ScriptTest.java
      catch (final IllegalScriptException e) {}
=======
      catch (final GIllegalScriptException e) {
      }
>>>>>>> ef8c4971ab295e2d462754644a989ec2a440276b:euclid-scripting/es/igosoftware/euclid/scripting/GScriptTest.java
   }


   public void testNoIAlgorithmInstance() throws Exception {
      try {
         GScriptManager.getPythonAlgorithm(getScriptStream("noIAlgorithmInstance.py"));
         fail();
      }
<<<<<<< HEAD:euclid/es/igosoftware/euclid/experimental/scripting/ScriptTest.java
      catch (final IllegalScriptException e) {}
=======
      catch (final GIllegalScriptException e) {
      }
>>>>>>> ef8c4971ab295e2d462754644a989ec2a440276b:euclid-scripting/es/igosoftware/euclid/scripting/GScriptTest.java
   }


   private InputStream getScriptStream(final String fileName) throws FileNotFoundException {
      return new BufferedInputStream(new FileInputStream(new File("es/igosoftware/euclid/scripting/" + fileName)));
   }

}
