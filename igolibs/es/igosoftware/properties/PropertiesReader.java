package es.igosoftware.properties;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import es.igosoftware.io.GIOUtils;


public class PropertiesReader {

   private static final String        CONFIGURATION_FILE = "../webapps/SenderosSistema/config/config.properties";
   private static Map<Object, Object> propiedades;


   static {
      FileInputStream fis = null;
      try {
         fis = new FileInputStream(PropertiesReader.CONFIGURATION_FILE);
         final Properties properties = new Properties();
         properties.load(fis);
         PropertiesReader.propiedades = new HashMap<Object, Object>(properties);
      }
      catch (final Exception e) {
         e.printStackTrace();
      }
      finally {
         GIOUtils.gentlyClose(fis);
      }
   }


   private PropertiesReader() {}


   public static String getStringProperty(final String nombre) {
      return (String) PropertiesReader.propiedades.get(nombre);
   }


   public static Object getProperty(final String name) {
      return PropertiesReader.propiedades.get(name);
   }

}
