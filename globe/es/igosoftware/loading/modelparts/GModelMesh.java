package es.igosoftware.loading.modelparts;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import es.igosoftware.euclid.mutability.GMutableAbstract;
import es.igosoftware.euclid.mutability.IMutable;
import es.igosoftware.euclid.vector.GVector3D;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.utils.GWWUtils;
import gov.nasa.worldwind.geom.Matrix;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.render.DrawContext;

public class GModelMesh
         extends
            GMutableAbstract<GModelMesh>
         implements
            Serializable,
            IMutable.ChangeListener {

   private static final long serialVersionUID = 1L;

   private final String      _name;

   //   public int                _materialID      = 0;
   private GMaterial         _material;
   private final List<GFace> _faces           = new ArrayList<GFace>();

   public boolean            _hasTexCoords    = false;
   public boolean            _hasNormals      = false;

   public boolean            _smoothShadeMode = true;

   private GModelData        _model;

   private Vec4              _centroid;
   private Vec4              _centroidVec4;

   private Matrix            _lastModelMatrix;


   public GModelMesh(final String name) {
      _name = name;
   }


   public void addFace(final GFace face) {
      _faces.add(face);

      _centroidVec4 = null;
   }


   public List<GFace> getFaces() {
      return Collections.unmodifiableList(_faces);
   }


   public String getName() {
      return _name;
   }


   public GMaterial getMaterial() {
      return _material;
   }


   public void setMaterial(final GMaterial material) {
      if (_material != null) {
         _material.removeChangeListener(this);
      }

      _material = material;

      if (_material != null) {
         _material.addChangeListener(this);
      }
   }


   public GModelData getModel() {
      return _model;
   }


   public void setModel(final GModelData model) {
      _model = model;
   }


   public boolean isOpaque() {
      if (_material != null) {
         final Color color = _material._diffuseColor;
         if (color != null) {
            if (color.getAlpha() < 255) {
               return false;
            }
         }

         final String textureFileName = _material.getTextureFileName();
         if ((textureFileName != null) && !textureFileName.trim().isEmpty()) {

            final String textureFileNameLowerCase = textureFileName.trim().toLowerCase();

            if (textureFileNameLowerCase.endsWith(".png")) {
               return false;
            }
            if (textureFileNameLowerCase.endsWith(".tga")) {
               return false;
            }

         }
      }

      return true;
   }


   public Vec4 getCentroid(final DrawContext dc,
                           final Matrix modelMatrix) {

      if ((modelMatrix != _lastModelMatrix) || (_centroidVec4 == null)) {
         //         System.out.println("Calculating centroid for " + this);
         _lastModelMatrix = modelMatrix;

         final Vec4 centroid = getCentroid();

         final Vec4 transformed = centroid.transformBy4(modelMatrix);

         _centroidVec4 = GWWUtils.toVec3(transformed);
      }

      return _centroidVec4;
   }


   private Vec4 getCentroid() {
      if (_centroid == null) {
         final List<IVector3<?>> vertices = _model.getVertices();

         IVector3<?> min = GVector3D.POSITIVE_INFINITY;
         IVector3<?> max = GVector3D.NEGATIVE_INFINITY;

         for (final GFace face : _faces) {
            for (final int vertexIndex : face._vertexIndices) {

               final IVector3<?> vertex = vertices.get(vertexIndex);
               min = min.min(vertex);
               max = max.max(vertex);
            }
         }

         _centroid = GWWUtils.toVec4(min.add(max).scale(0.5));
      }

      return _centroid;
   }


   @Override
   public String toString() {
      return "GModelMesh [name=" + _name + ", model=" + _model.getName() + ", faces=" + _faces.size() + "]";
   }


   @Override
   public void mutableChanged() {
      changed();
   }


}
