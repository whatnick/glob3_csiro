

package es.igosoftware.euclid.experimental.tests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import es.igosoftware.euclid.experimental.algorithms.tests.GLinesToEquispacedPointsTest;


public class GAllTests
         extends
            TestCase {

   public static Test suite() {
      final TestSuite suite = new TestSuite("Euclid tests");
      suite.addTestSuite(GEuclidTest.class);
      suite.addTestSuite(GLinesToEquispacedPointsTest.class);
      return suite;
   }
}
