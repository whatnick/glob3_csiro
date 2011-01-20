package es.unex.meigas.extOperations;

import java.util.Date;

import info.clearthought.layout.TableLayout;

import javax.swing.JLabel;

import org.freixas.jcalendar.JCalendarCombo;

import es.unex.meigas.extBase.BaseWizardPanel;
import es.unex.meigas.extBase.MainWizardWindow;

public class SelectDatePanel extends BaseWizardPanel {

	private JLabel jLabelDate;
	private JCalendarCombo jCalendar;

	public SelectDatePanel(MainWizardWindow panel) {

		super(panel);
		initGUI();

	}

	@Override
	public boolean hasEnoughInformation() {

		return true;

	}

	@Override
	public void initGUI() {

		TableLayout thisLayout = new TableLayout(new double[][] {
				{TableLayout.FILL, TableLayout.FILL, TableLayout.FILL, TableLayout.FILL},
				{TableLayout.FILL, TableLayout.MINIMUM, TableLayout.FILL}});
		thisLayout.setHGap(5);
		thisLayout.setVGap(5);
		this.setLayout(thisLayout);
		this.setPreferredSize(new java.awt.Dimension(374, 302));
		{
			jLabelDate = new JLabel();
			this.add(jLabelDate, "1, 1");
			jLabelDate.setText("Fecha");
		}
		{
			jCalendar = new JCalendarCombo(JCalendarCombo.DISPLAY_DATE, false);
			this.add(jCalendar, "2, 1");
		}

	}

	@Override
	public boolean isFinish() {

		return true;

	}

	public Date getDate(){

		return jCalendar.getDate();

	}

}
