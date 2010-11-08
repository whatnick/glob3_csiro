package es.igosoftware.io;


public class XSerializer {

   //
   //   public String objectToSerialize(final String string) {
   //      return string;
   //   }
   //
   //
   //   public Boolean objectToSerialize(final Boolean bool) {
   //      return bool;
   //   }
   //
   //
   //   public Number objectToSerialize(final Number number) {
   //      return number;
   //   }
   //
   //
   //   public float[] objectToSerialize(final Tuple3f tuple) {
   //      if (tuple == null) {
   //         return null;
   //      }
   //      return new float[] { tuple.x, tuple.y, tuple.z };
   //   }
   //
   //
   //   public double[] objectToSerialize(final Tuple3d tuple) {
   //      return new double[] { tuple.x, tuple.y, tuple.z };
   //   }
   //
   //
   //   public double[] objectToSerialize(final Tuple2d tuple) {
   //      return new double[] { tuple.x, tuple.y };
   //   }
   //
   //
   //   public double[][] objectToSerialize(final BoundingBox box) {
   //      final Point3d lower = new Point3d();
   //      box.getLower(lower);
   //
   //      final Point3d upper = new Point3d();
   //      box.getUpper(upper);
   //
   //      return new double[][] { objectToSerialize(lower), objectToSerialize(upper) };
   //   }
   //
   //
   //   public String objectToSerialize(final Enum<?> en) {
   //      return en.name();
   //   }
   //
   //
   //   //   public <T extends XSerializable> Set<Map<String, Object>> objectsToSerialize(final Set<T> set) {
   //   //      final HashSet<Map<String, Object>> result = new HashSet<Map<String, Object>>(set.size());
   //   //
   //   //      for (final T each : set) {
   //   //         result.add(objectToSerialize(each));
   //   //      }
   //   //
   //   //      return result;
   //   //   }
   //
   //
   //   //   public <T extends XSerializable> List<Map<String, Object>> objectsToSerialize(final List<T> list) {
   //   //      final List<Map<String, Object>> result = new ArrayList<Map<String, Object>>(list.size());
   //   //
   //   //      for (final T each : list) {
   //   //         result.add(objectToSerialize(each));
   //   //      }
   //   //
   //   //      return result;
   //   //
   //   //      //      return objectsToSerialize(list, new Converter<T, Map<String, Object>>() {
   //   //      //         @Override
   //   //      //         public Map<String, Object> convert(final T object) {
   //   //      //            return objectToSerialize(object);
   //   //      //         }
   //   //      //      });
   //   //   }
   //
   //
   //   //   public <T extends Tuple3d> List<double[]> objectsToSerializeTuples3d(final List<T> set) {
   //   //      final List<double[]> result = new ArrayList<double[]>(set.size());
   //   //
   //   //      for (final T each : set) {
   //   //         result.add(objectToSerialize(each));
   //   //      }
   //   //
   //   //      return result;
   //   //   }
   //
   //
   //   //   public <T extends BoundingBox> List<double[][]> objectsToSerializeBoundingBox(final List<T> list) {
   //   //      final List<double[][]> result = new ArrayList<double[][]>(list.size());
   //   //
   //   //      for (final T each : list) {
   //   //         result.add(objectToSerialize(each));
   //   //      }
   //   //
   //   //      return result;
   //   //   }
   //
   //
   //   //   public <T extends XSerializable> Collection<Map<String, Object>> objectsToSerialize(final Collection<T> collection) {
   //   //      final List<Map<String, Object>> result = new ArrayList<Map<String, Object>>(collection.size());
   //   //
   //   //      for (final T each : collection) {
   //   //         result.add(objectToSerialize(each));
   //   //      }
   //   //
   //   //      return result;
   //   //   }
   //
   //
   //   //   public Map<String, Object> objectToSerialize(final XSerializable object) {
   //   //      if (object == null) {
   //   //         return null;
   //   //      }
   //   //      return object.serialize(this);
   //   //   }
   //
   //
   //   //   public void serialize(final XSerializable object,
   //   //                         final String fileName) {
   //   //
   //   //      ObjectOutputStream oos = null;
   //   //      try {
   //   //         oos = new ObjectOutputStream(new FileOutputStream(fileName));
   //   //
   //   //         oos.writeObject(objectToSerialize(object));
   //   //      }
   //   //      catch (final IOException e) {
   //   //         e.printStackTrace();
   //   //      }
   //   //      finally {
   //   //         GIOUtils.gentlyClose(oos);
   //   //      }
   //   //   }
   //
   //
   //   //-------------------------------------------------------------------------
   //   public interface Converter<T> {
   //      public Object convert(final T object);
   //   }
   //
   //
   //   public <T> List<Object> objectsToSerialize(final List<T> list,
   //                                              final XSerializer.Converter<T> converter) {
   //      final List<Object> result = new ArrayList<Object>(list.size());
   //      for (final T each : list) {
   //         result.add(converter.convert(each));
   //      }
   //      return result;
   //   }
   //
   //
   //   public <T> Collection<Object> objectsToSerialize(final Collection<T> list,
   //                                                    final XSerializer.Converter<T> converter) {
   //      final List<Object> result = new ArrayList<Object>(list.size());
   //      for (final T each : list) {
   //         result.add(converter.convert(each));
   //      }
   //      return result;
   //   }
   //
   //
   //   public <T> Set<Object> objectsToSerialize(final Set<T> list,
   //                                             final XSerializer.Converter<T> converter) {
   //      final Set<Object> result = new HashSet<Object>(list.size());
   //      for (final T each : list) {
   //         result.add(converter.convert(each));
   //      }
   //      return result;
   //   }
   //
   //
   //   //-------------------------------------------------------------------------
   //   //-------------------------------------------------------------------------
   //
   //   private final Map<XSerializable, Map<String, Object>> _objectsToSerialize = new HashMap<XSerializable, Map<String, Object>>();
   //
   //
   //   private Map<String, Object> getObjectToSerialize(final XSerializable serializable) {
   //      Map<String, Object> objectToSerialize = _objectsToSerialize.get(serializable);
   //      if (objectToSerialize == null) {
   //         objectToSerialize = new HashMap<String, Object>();
   //         _objectsToSerialize.put(serializable, objectToSerialize);
   //      }
   //      return objectToSerialize;
   //   }
   //
   //
   //   public Map<String, Object> serialize(final XSerializable serializable,
   //                                        final String name,
   //                                        final XSerializable value) {
   //      final Map<String, Object> objectToSerialize = getObjectToSerialize(serializable);
   //      objectToSerialize.put(name, getObjectToSerialize(value));
   //      value.serialize(this);
   //      return objectToSerialize;
   //   }
   //
   //
   //   public Map<String, Object> serialize(final XSerializable serializable,
   //                                        final String name,
   //                                        final Number value) {
   //      final Map<String, Object> objectToSerialize = getObjectToSerialize(serializable);
   //      objectToSerialize.put(name, value);
   //      return objectToSerialize;
   //   }
   //
   //
   //   public Map<String, Object> serialize(final XSerializable serializable,
   //                                        final String name,
   //                                        final String value) {
   //      final Map<String, Object> objectToSerialize = getObjectToSerialize(serializable);
   //      objectToSerialize.put(name, value);
   //      return objectToSerialize;
   //   }
   //
   //
   //   public Map<String, Object> serialize(final XSerializable serializable,
   //                                        final String name,
   //                                        final Boolean value) {
   //      final Map<String, Object> objectToSerialize = getObjectToSerialize(serializable);
   //      objectToSerialize.put(name, value);
   //      return objectToSerialize;
   //   }
   //
   //
   //   public Map<String, Object> serialize(final XSerializable serializable,
   //                                        final String name,
   //                                        final Enum<?> value) {
   //      final Map<String, Object> objectToSerialize = getObjectToSerialize(serializable);
   //      objectToSerialize.put(name, value.name());
   //      return objectToSerialize;
   //   }
   //
   //
   //   public Map<String, Object> serialize(final XSerializable serializable,
   //                                        final String name,
   //                                        final Tuple2d value) {
   //      final Map<String, Object> objectToSerialize = getObjectToSerialize(serializable);
   //      objectToSerialize.put(name, new double[] { value.x, value.y });
   //      return objectToSerialize;
   //   }
   //
   //
   //   public Map<String, Object> serialize(final XSerializable serializable,
   //                                        final String name,
   //                                        final Tuple3d value) {
   //      final Map<String, Object> objectToSerialize = getObjectToSerialize(serializable);
   //      objectToSerialize.put(name, new double[] { value.x, value.y, value.z });
   //      return objectToSerialize;
   //   }
   //
   //
   //   public Map<String, Object> serialize(final XSerializable serializable,
   //                                        final String name,
   //                                        final Tuple3f value) {
   //      final Map<String, Object> objectToSerialize = getObjectToSerialize(serializable);
   //      objectToSerialize.put(name, new float[] { value.x, value.y, value.z });
   //      return objectToSerialize;
   //   }


}
