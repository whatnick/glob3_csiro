package es.igosoftware.globe;

public class GField {

   private final String   _name;
   private final Class<?> _type;


   public GField(final String name,
                 final Class<?> type) {
      _name = name;
      _type = type;
   }


   public String getName() {
      return _name;
   }


   public Class<?> getType() {
      return _type;
   }


   @Override
   public String toString() {
      return "GField [name=" + _name + ", type=" + _type + "]";
   }

}
