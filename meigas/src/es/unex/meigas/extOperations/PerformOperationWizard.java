	package es.unex.meigas.extOperations;

import java.util.Date;

import es.unex.meigas.core.DasocraticElement;
import es.unex.meigas.extBase.BaseWizardPanel;
import es.unex.meigas.extBase.MainWizardWindow;
import es.unex.meigas.gui.MeigasPanel;

public class PerformOperationWizard extends MainWizardWindow {

	private MeigasPanel meigasPanel = null;

	private int m_iParameter;


	public PerformOperationWizard(MeigasPanel panel) {

		super(panel);
		meigasPanel = panel;
		setName("Realizar operación selvícola");

		pack();
	}

	@Override
	protected void finish() {

		//TODO: create operation
		Operation operation = new Operation();
		Date date = ((SelectDatePanel)m_Panels[4]).getDate();
		operation.setDate(date);

		boolean bCreateNewData = ((CreateNewDataPanel)m_Panels[0]).getCreateNewData();

		DasocraticElement element = meigasPanel.getActiveElement();

		if (bCreateNewData){
			DasocraticElement newElement = element.getNewInstance();
			element.setName(element.getName() + "[intervenido]");
			operation.applyOperation(newElement);
			element.getParent().addElement(newElement);
		}

		meigasPanel.fillTree();

	}

	protected void setPanels() {

		m_Panels = new BaseWizardPanel[5];
		m_Panels[0] = new CreateNewDataPanel(this);
		m_Panels[1] = new ParameterSelectionPanel(this);
		m_Panels[2] = new ObjectiveValueSelectionPanel(this);
		m_Panels[3] = new MethodSelectionPanel(this);
		m_Panels[4] = new SelectDatePanel(this);

	}

	public int getParameter() {

		return m_iParameter;

	}

	public void setParameter(int parameter) {

		m_iParameter = parameter;

	}



}
