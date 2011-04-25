

package es.igosoftware.euclid.experimental.tests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import es.igosoftware.euclid.experimental.algorithms.GLinesToEquispacedPointsTest;
import es.igosoftware.euclid.experimental.scripting.GScriptTest;


public class GAllTests
         extends
            TestCase {

   public static Test suite() {
      final TestSuite suite = new TestSuite("Test de la guía de estudios geotécnicos");
      suite.addTestSuite(GEuclidTest.class);
      suite.addTestSuite(GLinesToEquispacedPointsTest.class);
      suite.addTestSuite(GScriptTest.class);
      return suite;
   }
}
