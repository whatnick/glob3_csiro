

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

   @Override
   protected void setUp() throws Exception {
      super.setUp();
      if (!GScriptManager.isInitialized()) {
         GScriptManager.initialize();
      }
   }


   public void testJythonNormalCase() throws Exception {
      final IAlgorithm alg = GScriptManager.getPythonAlgorithm(getScriptStream("normalCase.py"));
      assertTrue(alg.getName().equals("name"));
      assertTrue(alg.getDescription().equals("description"));
      @SuppressWarnings("unchecked")
      final Object result = alg.apply(new Integer(2));
      assertTrue(result.equals(3.14159));
   }


   public void testJythonNoAlgorithmVarSet() throws Exception {
      try {
         GScriptManager.getPythonAlgorithm(getScriptStream("noAlgorithmVarSet.py"));
         fail();
      }
      catch (final GIllegalScriptException e) {}
   }


   public void testJythonNoIAlgorithmInstance() throws Exception {
      try {
         GScriptManager.getPythonAlgorithm(getScriptStream("noIAlgorithmInstance.py"));
         fail();
      }
      catch (final GIllegalScriptException e) {}
   }


   public void testBeanshellNormalCase() throws Exception {
      final IAlgorithm alg = GScriptManager.getBeanshellAlgorithm(getScriptStream("normalCase.bsh"));
      assertTrue(alg.getName().equals("name"));
      assertTrue(alg.getDescription().equals("description"));
      @SuppressWarnings("unchecked")
      final Object result = alg.apply(new Integer(2));
      assertTrue(result.equals(3.14159));
   }


   public void testBeanshellNoAlgorithmVarSet() throws Exception {
      try {
         GScriptManager.getBeanshellAlgorithm(getScriptStream("noAlgorithmVarSet.bsh"));
         fail();
      }
      catch (final GIllegalScriptException e) {
         System.out.println(e.getMessage());
      }
   }


   public void testBeanshellNoIAlgorithmInstance() throws Exception {
      try {
         GScriptManager.getBeanshellAlgorithm(getScriptStream("noIAlgorithmInstance.bsh"));
         fail();
      }
      catch (final GIllegalScriptException e) {
      }
   }


   private InputStream getScriptStream(final String fileName) throws FileNotFoundException {
      return new BufferedInputStream(new FileInputStream(new File("es/igosoftware/euclid/scripting/" + fileName)));
   }

}
