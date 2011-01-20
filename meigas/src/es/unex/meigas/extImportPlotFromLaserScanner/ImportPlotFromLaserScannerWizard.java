	package es.unex.meigas.extImportPlotFromLaserScanner;

import es.igosoftware.invforestal.Tree3D;
import es.unex.meigas.extBase.BaseWizardPanel;
import es.unex.meigas.extBase.MainWizardWindow;
import es.unex.meigas.gui.MeigasPanel;

public class ImportPlotFromLaserScannerWizard extends MainWizardWindow {

	private MeigasPanel meigasPanel = null;

	public ImportPlotFromLaserScannerWizard(MeigasPanel panel) {

		super(panel);
		meigasPanel = panel;
		setName("Importar parcela de Laser-Scanner");

		pack();
	}

	@Override
	protected void finish() {

		String folder = ((FileSelectionPanel)m_Panels[0]).getFolder();
		Tree3D.main(new String[]{folder});

	}

	protected void setPanels() {

		m_Panels = new BaseWizardPanel[1];
		m_Panels[0] = new FileSelectionPanel(this);

	}

}
