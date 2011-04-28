

package es.igosoftware.euclid.experimental.vectorial.rendering;

import java.util.List;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.experimental.vectorial.rendering.styledgeometries.GStyled2DGeometry;


public class GRenderUnitResult {


   private final List<GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> _symbols;
   private final boolean                                                                             _hasGroupableSymbols;


   GRenderUnitResult(final List<GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> symbols,
                     final boolean hasGroupableSymbols) {
      _symbols = symbols;
      _hasGroupableSymbols = hasGroupableSymbols;
   }


   public List<GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> getSymbols() {
      return _symbols;
   }


   public boolean hasGroupableSymbols() {
      return _hasGroupableSymbols;
   }


}
