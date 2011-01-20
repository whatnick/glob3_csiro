package es.unex.meigas.filters;

import es.unex.meigas.core.DasocraticElement;
import es.unex.meigas.core.Tree;

public class NonTreeDasocraticVariableFilter extends DasocraticVariableFilter {


	public static final String[] VARIABLES = new String[] { "Volumen", "Volumen sin corteza", "Área basimétrica"};
	public static final String[] CRITERIA = new String[] { "Igual a", "Mayor que", "Menor que"};
	public static final String[] CRITERIA2 = new String[] {"=", ">", "<"};

	private static final int VOLUME = 0;
	private static final int VOLUME_WITHOUT_BARK = 1;
	private static final int BASIMETRIC_AREA = 2;

	public NonTreeDasocraticVariableFilter(int variable, int criteria,
			double threshold) {

		super(variable, criteria, threshold);

	}

	public boolean accept(DasocraticElement element) {

		if (element instanceof Tree){
			return true;
		}

		double dValue;

		switch(this.m_iVariable){
			case VOLUME:
				dValue = element.getTotalVolumeWithBark();
				break;
			case VOLUME_WITHOUT_BARK:
				dValue = element.getTotalVolumeWithoutBark();
				break;
			case BASIMETRIC_AREA:
				dValue = element.getTotalBasimetricAreaByHa();
				break;
			default:
				return false;
		}

		if (dValue != DasocraticElement.NO_DATA){
			switch(m_iCriteria){
				case DasocraticVariableFilter.EQUAL:
					return (dValue == m_dThreshold);
				case DasocraticVariableFilter.GREATERHAN:
					return (dValue > m_dThreshold);
				case DasocraticVariableFilter.LOWERTHAN:
					return (dValue < m_dThreshold);
				default:
					return false;
			}
		}
		else{
			return false;
		}

	}

	public String toString(){

		return "Por variable dasocrática: "  + VARIABLES[m_iVariable] + " " +
					CRITERIA2[m_iCriteria] + " " + Double.toString(m_dThreshold);

	}

}
