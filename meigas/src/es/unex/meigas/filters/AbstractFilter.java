package es.unex.meigas.filters;

public abstract class AbstractFilter implements IFilter {

	@Override
	public Object clone() {

		Object obj=null;
        try{
            obj=super.clone();
        }catch(CloneNotSupportedException ex){}
        return obj;

	}

}
