package es.igosoftware.loading.modelparts;

public class GFace {
   public final int _vertexIndices[];
   public final int _texCoordIndices[];
   public final int _normalIndices[];


   public GFace(final int numVertices) {
      _vertexIndices = new int[numVertices];
      _texCoordIndices = new int[numVertices];
      _normalIndices = new int[numVertices];
   }

}
