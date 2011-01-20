package es.unex.meigas.filters;

import info.clearthought.layout.TableLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

import es.unex.meigas.core.DasocraticElement;
import es.unex.meigas.core.DasocraticElement.FilterAndDasocraticElement;
import es.unex.meigas.gui.DasocraticInfoPanel;
import es.unex.meigas.gui.MeigasPanel;

public class FiltersPanel extends DasocraticInfoPanel {

	private static final String[] FILTER_NAMES = {"Filtro por variable dasométrica (árbol)",
													//"Filtro por variable dasométrica (unidad)",
													"Filtro espacial",
													"Filtro por especie"};

	private static final int TREE_DASOMETRIC_VARIABLE_FILTER = 0;
	//private static final int NON_TREE_DASOMETRIC_VARIABLE_FILTER = 1;
	private static final int SPATIAL_FILTER = 1;
	private static final int SPECIES_FILTER = 2;

	private JScrollPane jScrollPane;
	private JList jListFilters;
	private JPanel jPanel1;
	private JScrollPane jScrollPaneParentFilters;
	private JList jListParentFilters;
	private JButton jButtonRemove;
	private JButton jButtonAdd;

	public FiltersPanel(DasocraticElement element, MeigasPanel panel) {

		super(element, panel);
		setName("Filtros");

	}

	@Override
	protected boolean checkDataAndUpdate() {

		return true;

	}

	@Override
	protected void initGUI() {

		TableLayout thisLayout = new TableLayout(new double[][] {
				{6.0, TableLayout.FILL, TableLayout.FILL, 50.0, 6.0},
				{6.0, TableLayout.FILL, TableLayout.FILL, 7.0}});
		thisLayout.setHGap(5);
		thisLayout.setVGap(5);
		this.setLayout(thisLayout);
		this.setPreferredSize(new java.awt.Dimension(433, 243));
		{
			jScrollPane = new JScrollPane();
			this.add(jScrollPane, "1, 1, 2, 1");
			{
				IFilter[] filters = m_MeigasPanel.getActiveElement().getFilters();
				jListFilters = new JList(new DefaultListModel());
				for (int i = 0; i < filters.length; i++) {
					((DefaultListModel)jListFilters.getModel()).addElement(filters[i]);
				}
				jScrollPane.setViewportView(jListFilters);
			}
		}
		{
			jPanel1 = new JPanel();
			TableLayout jPanel1Layout = new TableLayout(new double[][] {{TableLayout.FILL}, {TableLayout.FILL, TableLayout.MINIMUM, TableLayout.MINIMUM, TableLayout.FILL}});
			jPanel1Layout.setHGap(5);
			jPanel1Layout.setVGap(5);
			this.add(jPanel1, "3, 1");
			jPanel1.setLayout(jPanel1Layout);
			{
				jButtonAdd = new JButton();
				jPanel1.add(jButtonAdd, "0, 1");
				jButtonAdd.setText("+");
				jButtonAdd.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e) {
						showFiltersPopup();
					}
				});
			}
			{
				jButtonRemove = new JButton();
				jPanel1.add(jButtonRemove, "0, 2");
				jButtonRemove.setText("-");
				jButtonRemove.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e) {
						IFilter filter = (IFilter) jListFilters.getSelectedValue();
						if (filter != null){
							m_MeigasPanel.getActiveElement().removeFilter(filter);
							((DefaultListModel)jListFilters.getModel()).removeElement(filter);
						}
					}
				});
			}
		}
		{
			jScrollPaneParentFilters = new JScrollPane();
			this.add(jScrollPaneParentFilters, "1, 2, 2, 2");
			{
				jListParentFilters = new JList(new DefaultListModel());
				jListParentFilters.setEnabled(false);
				FilterAndDasocraticElement[] filters = m_MeigasPanel.getActiveElement().getParentFilters();
				for (int i = 0; i < filters.length; i++) {
					((DefaultListModel)jListParentFilters.getModel()).addElement(filters[i]);
				}
				jScrollPaneParentFilters.setViewportView(jListParentFilters);
			}
		}

	}

	protected void showFiltersPopup() {

			JMenuItem menuItem;
			JPopupMenu popup = new JPopupMenu( "Menu" );

			for (int i = 0; i < FILTER_NAMES.length; i++) {
				final int iType = i;
				menuItem = new JMenuItem(FILTER_NAMES[i]);
				menuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						addFilter(iType);
					}
				});
//				if (m_MeigasPanel.getActiveElement() instanceof Plot
//						&& i== NON_TREE_DASOMETRIC_VARIABLE_FILTER){
//					menuItem.setEnabled(false);
//				}
				popup.add(menuItem);
			}
			popup.show(jButtonAdd, jButtonAdd.getWidth() / 2,
					jButtonAdd.getHeight() / 2);

	}

	protected void addFilter(int iType) {

		IFilter filter = null;
		FilterDialog dialog = null;

		switch(iType){
		case SPATIAL_FILTER:
			dialog = new SpatialFilterDialog();
			break;
		case SPECIES_FILTER:
			dialog = new SpecieFilterDialog();
			break;
		case TREE_DASOMETRIC_VARIABLE_FILTER:
			dialog = new TreeDasometricVariableFilterDialog();
			break;
//		case NON_TREE_DASOMETRIC_VARIABLE_FILTER:
//			dialog = new NonTreeDasometricVariableFilterDialog();
//			break;
		}

		if (dialog != null){
			dialog.pack();
			dialog.setVisible(true);
			filter = dialog.getFilter();
		}
		if (filter != null){
			((DefaultListModel)jListFilters.getModel()).addElement(filter);
			m_MeigasPanel.getActiveElement().addFilter(filter);
		}

	}

	@Override
	protected void initializeContent() {}

	@Override
	protected void updateContent() {
		// TODO Auto-generated method stub

	}

}
