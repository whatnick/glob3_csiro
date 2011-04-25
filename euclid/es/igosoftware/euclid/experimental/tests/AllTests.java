

package es.igosoftware.euclid.experimental.tests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import es.igosoftware.euclid.experimental.algorithms.GLinesToEquispacedPointsTest;


public class AllTests
         extends
            TestCase {

   public static Test suite() {
      final TestSuite suite = new TestSuite("Test de la guía de estudios geotécnicos");
      suite.addTestSuite(EuclidTest.class);
      suite.addTestSuite(GLinesToEquispacedPointsTest.class);
      return suite;
   }
}
