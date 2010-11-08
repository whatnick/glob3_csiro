package es.igosoftware.loading.modelparts;

import java.awt.Color;
import java.io.Serializable;

import es.igosoftware.euclid.mutability.GMutableAbstract;
import es.igosoftware.util.GUtils;

public class GMaterial
         extends
            GMutableAbstract<GMaterial>
         implements
            Serializable {


   private static final long serialVersionUID = 1L;


   public final String       _name;

   private String            _textureFileName;

   public Color              _diffuseColor    = new Color(0.8f, 0.8f, 0.8f);
   public Color              _ambientColor    = Color.BLACK;
   public Color              _specularColor   = Color.WHITE;
   public Color              _emissiveColor   = Color.BLACK;

   public float              _shininess       = 1;

   public boolean            _mipmap          = true;


   public GMaterial(final String name) {
      _name = name;
   }


   public GMaterial(final String name,
                    final String textureName) {

      _name = name;
      _textureFileName = textureName;
   }


   public String getTextureFileName() {
      return _textureFileName;
   }


   public void setTextureFileName(final String textureFileName) {
      if (GUtils.equals(_textureFileName, textureFileName)) {
         return;
      }
      //      System.out.println("\n======> changed texture from " + _textureFileName + " to " + textureFileName + " (" + this + ")");
      _textureFileName = textureFileName;
      changed();
   }


   @Override
   public String toString() {
      return "GMaterial [ambient=" + _ambientColor + ", diffuse=" + _diffuseColor + ",_emissive=" + _emissiveColor + ", name="
             + _name + ", shininess=" + _shininess + ", specular=" + _specularColor + ", texture=" + _textureFileName
             + ", mipmap=" + _mipmap + "]";
   }


   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((_ambientColor == null) ? 0 : _ambientColor.hashCode());
      result = prime * result + ((_diffuseColor == null) ? 0 : _diffuseColor.hashCode());
      result = prime * result + ((_emissiveColor == null) ? 0 : _emissiveColor.hashCode());
      result = prime * result + (_mipmap ? 1231 : 1237);
      result = prime * result + Float.floatToIntBits(_shininess);
      result = prime * result + ((_specularColor == null) ? 0 : _specularColor.hashCode());
      result = prime * result + ((_textureFileName == null) ? 0 : _textureFileName.hashCode());
      return result;
   }


   @Override
   public boolean equals(final Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      final GMaterial other = (GMaterial) obj;
      if (_ambientColor == null) {
         if (other._ambientColor != null) {
            return false;
         }
      }
      else if (!_ambientColor.equals(other._ambientColor)) {
         return false;
      }
      if (_diffuseColor == null) {
         if (other._diffuseColor != null) {
            return false;
         }
      }
      else if (!_diffuseColor.equals(other._diffuseColor)) {
         return false;
      }
      if (_emissiveColor == null) {
         if (other._emissiveColor != null) {
            return false;
         }
      }
      else if (!_emissiveColor.equals(other._emissiveColor)) {
         return false;
      }
      if (_mipmap != other._mipmap) {
         return false;
      }
      if (Float.floatToIntBits(_shininess) != Float.floatToIntBits(other._shininess)) {
         return false;
      }
      if (_specularColor == null) {
         if (other._specularColor != null) {
            return false;
         }
      }
      else if (!_specularColor.equals(other._specularColor)) {
         return false;
      }
      if (_textureFileName == null) {
         if (other._textureFileName != null) {
            return false;
         }
      }
      else if (!_textureFileName.equals(other._textureFileName)) {
         return false;
      }
      return true;
   }


}
