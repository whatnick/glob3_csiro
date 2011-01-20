package es.unex.meigas.filters;
import info.clearthought.layout.TableLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import es.unex.meigas.core.Meigas;

public abstract class FilterDialog extends JDialog {

	private JButton jButtonOk;
	private JButton jButtonCancel;
	private JPanel jPanelMain;

	protected IFilter m_Filter;

	public FilterDialog(){

		super(Meigas.getMainFrame(), "Filtro", true);

		initGUI();

		setLocationRelativeTo(null);

	}

	private void initGUI() {

		try {
			TableLayout thisLayout = new TableLayout(new double[][] {{TableLayout.FILL, TableLayout.FILL, TableLayout.FILL, TableLayout.MINIMUM}, {TableLayout.FILL, 3.0, 25.0, TableLayout.MINIMUM}});
			thisLayout.setHGap(5);
			thisLayout.setVGap(5);
			getContentPane().setLayout(thisLayout);
			{
				jButtonOk = new JButton();
				getContentPane().add(jButtonOk, "1, 2");
				jButtonOk.setText("Aceptar");
				jButtonOk.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						if (checkValuesAndSetFilter()){
							close();
						}
						else{
							JOptionPane.showMessageDialog(Meigas.getMainFrame(),
								    "Datos incorrectos o insuficientes",
								    "Aviso",
								    JOptionPane.WARNING_MESSAGE);
						}
					}
				});
			}
			{
				jButtonCancel = new JButton();
				getContentPane().add(jButtonCancel, "2, 2");
				jButtonCancel.setText("Cancelar");
				jButtonCancel.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						m_Filter = null;
						close();
					}
				});
			}
			{
				jPanelMain = new JPanel();
				getContentPane().add(jPanelMain, "0, 0, 2, 0");
			}
			{
				this.setSize(366, 236);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	protected abstract boolean checkValuesAndSetFilter();

	protected void close() {

		this.dispose();
		this.setVisible(false);

	}

	public IFilter getFilter(){

		return m_Filter;

	}

	protected JPanel getMainPane() {

		return jPanelMain;
	}


}
