

package es.igosoftware.euclid.experimental.scripting;

import java.io.InputStream;

import org.python.core.PyObject;
import org.python.util.PythonInterpreter;

import es.igosoftware.euclid.experimental.algorithms.IAlgorithm;


public class ScriptManager {

   private static final String      PYTHON_VAR_NAME = "ALGORITHM";
   private static PythonInterpreter interpreter     = new PythonInterpreter();


   /**
    * Returns an instance of the IAlgorithm instance. The instance is passed in python in the InputStream parameter. The python
    * code must have a statement that sets a {@value #PYTHON_VAR_NAME} local variable to the python instance.
    * 
    * @param <ParametersVectorT>
    * @param <ParametersT>
    * @param <ResultVectorT>
    * @param <ResultT>
    * @param is
    * @return
    * @throws IllegalScriptException
    *            If the instance does not implement the IAlgorithm instance or the {@value #PYTHON_VAR_NAME} is not set
    */
   public static IAlgorithm<?, ?, ?, ?> getPythonAlgorithm(final InputStream is) throws IllegalScriptException {
      interpreter.set(PYTHON_VAR_NAME, null);
      interpreter.execfile(is);
      final PyObject pyObject = interpreter.get(PYTHON_VAR_NAME);
      if (pyObject == null) {
         throw new IllegalScriptException("There is no \"" + PYTHON_VAR_NAME + " = "
                                          + "[algorithm instance]\" statement in the script");
      }
      final Object javaObject = pyObject.__tojava__(IAlgorithm.class);
      try {
         return (IAlgorithm<?, ?, ?, ?>) javaObject;
      }
      catch (final ClassCastException e) {
         throw new IllegalScriptException("The returned python " + "object does not implement the IAlgorithm interface");
      }
   }
}
