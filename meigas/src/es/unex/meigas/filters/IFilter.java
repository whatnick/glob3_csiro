package es.unex.meigas.filters;

import es.unex.meigas.core.DasocraticElement;

public interface IFilter extends Cloneable{

	public boolean accept(DasocraticElement element);

	public Object clone();

}
