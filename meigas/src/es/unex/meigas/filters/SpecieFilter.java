package es.unex.meigas.filters;

import es.unex.meigas.core.DasocraticElement;
import es.unex.meigas.core.Tree;

public class SpecieFilter extends AbstractFilter{

	public static final int CRITERIA_ACCEPT = 0;
	public static final int CRITERIA_REJECT = 1;

	private static final String[] sCriteria = {"Aceptar", "Rechazar"};

	private String m_sSpecie;
	private int m_iCriteria;

	public SpecieFilter(String sSpecie, int iCriteria){

		m_sSpecie = sSpecie;
		m_iCriteria = iCriteria;

	}

	public boolean accept(DasocraticElement element) {

		if (element instanceof Tree){
			Tree tree = (Tree) element;
			if (m_iCriteria == CRITERIA_ACCEPT){
				return tree.getSpecie().equals(m_sSpecie);
			}
			else{
				return !tree.getSpecie().equals(m_sSpecie);
			}
		}
		else{
			return true;
		}

	}

	public String toString(){

		return "Por especie: " + m_sSpecie + "/" + sCriteria[m_iCriteria];

	}

}
