package es.unex.meigas.filters;


public abstract class DasocraticVariableFilter extends AbstractFilter {

	protected double m_dThreshold;
	protected int m_iCriteria;
	protected int m_iVariable;

	public static final int EQUAL = 0;
	public static final int GREATERHAN = 1;
	public static final int LOWERTHAN = 2;

	public DasocraticVariableFilter(int iVariable, int iCriteria, double dThreshold){

		m_iVariable = iVariable;
		m_iCriteria = iCriteria;
		m_dThreshold = dThreshold;

	}

}
