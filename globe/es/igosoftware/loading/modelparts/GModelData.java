package es.igosoftware.loading.modelparts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import es.igosoftware.euclid.bounding.GAxisAlignedBox;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.util.Logger;

public class GModelData
         implements
            Serializable {


   private static final Logger logger = Logger.instance();


   public static enum FaceCullingMode {
      BACK,
      FRONT,
      FRONT_AND_BACK;
   }


   private static final long       serialVersionUID   = 1L;

   private final String            _name;

   private final List<IVector3<?>> _vertices          = new ArrayList<IVector3<?>>();
   private final List<IVector3<?>> _normals           = new ArrayList<IVector3<?>>();
   private final List<IVector2<?>> _texCoords         = new ArrayList<IVector2<?>>();

   private final List<GMaterial>   _materials         = new ArrayList<GMaterial>();
   private final List<GModelMesh>  _meshes            = new ArrayList<GModelMesh>();

   //   private final List<GModelMesh>  _transparentMeshes = new ArrayList<GModelMesh>();

   private GAxisAlignedBox         _bounds            = null;

   private boolean                 _useTexture        = true;
   private boolean                 _renderAsWireframe = false;
   private boolean                 _useLighting       = true;

   private Boolean                 _isOpaque          = null;

   private FaceCullingMode         _faceCullingMode   = FaceCullingMode.BACK;


   // Constructor
   public GModelData(final String name) {
      _name = name;
   }


   //Materials
   public void addMaterial(final GMaterial mat) {
      _materials.add(mat);
   }


   public List<GMaterial> getMaterials() {
      return Collections.unmodifiableList(_materials);
   }


   //Meshes
   public void addmesh(final GModelMesh mesh) {
      _isOpaque = null;
      mesh.setModel(this);
      _meshes.add(mesh);
   }


   //   public void addTransparentMesh(final GModelMesh mesh) {
   //      _isOpaque = false;
   //      mesh.setModel(this);
   //      _transparentMeshes.add(mesh);
   //   }


   //   public void sortMeshes() {
   //      final List<GModelMesh> meshesToRemove = new ArrayList<GModelMesh>();
   //      for (final GModelMesh mesh : _meshes) {
   //         if (!mesh.isOpaque()) {
   //            addTransparentMesh(mesh);
   //            meshesToRemove.add(mesh);
   //         }
   //      }
   //      for (final GModelMesh mesh : meshesToRemove) {
   //         _meshes.remove(mesh);
   //      }
   //
   //
   //   }


   public List<GModelMesh> getMeshes() {
      return Collections.synchronizedList(_meshes);

   }


   //   public List<GModelMesh> getTransparentMeshes() {
   //      return Collections.synchronizedList(_transparentMeshes);
   //      //return _transparentMeshes;
   //   }


   public void addVertex(final IVector3<?> vertex) {
      _bounds = null; // invalidate bounds to force recalculation
      _vertices.add(vertex);
   }


   public void addNormal(final IVector3<?> normal) {
      _normals.add(normal);
   }


   public void addTexCoord(final IVector2<?> uv) {
      _texCoords.add(uv);
   }


   public String getName() {
      return _name;
   }


   public GAxisAlignedBox getBounds() {
      if (_bounds == null) {
         _bounds = GAxisAlignedBox.minimumBoundingBox(_vertices);
      }
      return _bounds;
   }


   public boolean isUsingLighting() {
      return _useLighting;
   }


   public boolean isUsingTexture() {
      return _useTexture;
   }


   public void setUseTexture(final boolean useTexture) {
      _useTexture = useTexture;
   }


   public void setUseLighting(final boolean useLighting) {
      _useLighting = useLighting;
   }


   public boolean isRenderAsWireframe() {
      return _renderAsWireframe;
   }


   public void setRenderAsWireframe(final boolean renderAsWireframe) {
      _renderAsWireframe = renderAsWireframe;
   }


   public List<IVector3<?>> getVertices() {
      return Collections.unmodifiableList(_vertices);
   }


   public List<IVector3<?>> getNormals() {
      return Collections.unmodifiableList(_normals);
   }


   public List<IVector2<?>> getTexCoords() {
      return Collections.unmodifiableList(_texCoords);
   }


   public boolean isOpaque() {
      if (_isOpaque == null) {
         _isOpaque = Boolean.TRUE;
         for (final GModelMesh mesh : _meshes) {
            if (!mesh.isOpaque()) {
               _isOpaque = Boolean.FALSE;
               return false;
            }
         }
      }

      return _isOpaque.booleanValue();
   }


   @Override
   public String toString() {
      return "GModelData [name=" + _name + "]";
   }


   public FaceCullingMode getFaceCullingMode() {
      return _faceCullingMode;
   }


   public void setFaceCullingMode(final FaceCullingMode faceCullingMode) {
      _faceCullingMode = faceCullingMode;
   }


   public void showStatistics() {
      logger.increaseIdentationLevel();

      logger.info("Name: " + _name);

      logger.info("Vertices: " + _vertices.size());
      logger.info("Normals: " + _normals.size());
      logger.info("TexCoords: " + _texCoords.size());

      logger.info("Meshes: " + _meshes.size());
      logger.info("Materials: " + _materials.size());

      logger.info("Bound: " + getBounds());
      logger.info("Use Texture: " + _useTexture);

      logger.info("Render as Wireframe: " + _renderAsWireframe);
      logger.info("Use Lighting: " + _useLighting);

      logger.info("Opaque: " + isOpaque());

      logger.info("Face Culling: " + _faceCullingMode);

      int facesCount = 0;
      int verticesIndicesCount = 0;
      for (final GModelMesh mesh : _meshes) {
         //         mesh.showStatistics();

         final List<GFace> faces = mesh.getFaces();
         facesCount += faces.size();
         for (final GFace face : faces) {
            verticesIndicesCount += face._vertexIndices.length;
         }
      }

      logger.info("Faces: " + facesCount);
      logger.info("Total Faces VerticesIndices: " + verticesIndicesCount);
      logger.info("VerticesIndices per Face (average): " + ((float) verticesIndicesCount / facesCount));

      logger.decreaseIdentationLevel();
   }


}
