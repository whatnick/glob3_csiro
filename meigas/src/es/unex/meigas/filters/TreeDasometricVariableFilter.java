package es.unex.meigas.filters;

import es.unex.meigas.core.DasocraticElement;
import es.unex.meigas.core.Tree;

public class TreeDasometricVariableFilter extends DasocraticVariableFilter {

	public final static int VOLUME = 0;
	public final static int VOLUME_WITHOUT_BARK = 1;
	public final static int HEIGHT = 2;
	public final static int LOG_HEIGHT = 3;
	public final static int AGE = 4;
	public final static int DBH = 5;

	public static final String[] VARIABLES = new String[] {"Volumen", "Volumen sin corteza", "Altura",
										"Altura de fuste", "Edad", "Diámetro normal"};
	public static final String[] CRITERIA = new String[] {"Igual a", "Mayor que", "Menor que"};
	public static final String[] CRITERIA2 = new String[] {"=", ">", "<"};

	public TreeDasometricVariableFilter(int variable, int criteria,
									double threshold) {

		super(variable, criteria, threshold);

	}

	public boolean accept(DasocraticElement element) {

		if (!(element instanceof Tree)){
			return true;
		}

		Tree tree = (Tree) element;
		double dValue;

		switch(this.m_iVariable){
		case VOLUME:
			dValue = tree.getVolumeWithBark().getValue();
			break;
		case VOLUME_WITHOUT_BARK:
			dValue = tree.getVolumeWithoutBark().getValue();
			break;
		case HEIGHT:
			dValue = tree.getHeight().getValue();
			break;
		case LOG_HEIGHT:
			dValue = tree.getLogHeight().getValue();
			break;
		case AGE:
			dValue = tree.getAge().getValue();
			break;
		case DBH:
			dValue = tree.getDBH().getValue();
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

		return "Por variable dasocrática: "  + VARIABLES[m_iVariable] + " "
					+ CRITERIA2[m_iCriteria] + " " + Double.toString(m_dThreshold);
	}

}
