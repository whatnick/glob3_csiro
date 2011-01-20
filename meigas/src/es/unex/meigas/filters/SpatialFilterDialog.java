package es.unex.meigas.filters;

import info.clearthought.layout.TableLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import es.unex.meigas.dataObjects.IVectorLayer;
import es.unex.meigas.gui.SelectGeometryDialog;

public class SpatialFilterDialog extends FilterDialog {

	private JLabel jLabelTitle;
	private JPanel jPanelCoords;
	private JTextField jTextFieldYMax;
	private JTextField jTextFieldYMin;
	private JTextField jTextFieldXMax;
	private JTextField jTextFieldSelectedShape;
	private JButton jButtonSelectShape;
	private JPanel jPanelShapeSelection;
	private JTextField jTextFieldXMin;
	private JRadioButton jRadioButtonUseGeometry;
	private JRadioButton jRadioButtonUseRect;

	public SpatialFilterDialog(){

		super();
		initGUI();

	}

	public  void initGUI() {

		try {
			{
				TableLayout thisLayout = new TableLayout(new double[][] {{29.0, 20.0, TableLayout.FILL, TableLayout.FILL, TableLayout.FILL, 161.0, 25.0}, {10.0, 20.0, 10.0, 20.0, 20.0, 20.0, TableLayout.FILL, 5.0}});
				thisLayout.setHGap(5);
				thisLayout.setVGap(5);
				getMainPane().setLayout(thisLayout);
				//this.setPreferredSize(new java.awt.Dimension(620, 337));
				//this.setSize(620, 337);
				{
					jLabelTitle = new JLabel();
					this.add(jLabelTitle, "1, 1, 5, 1");
					jLabelTitle.setText("Seleccione la extension geográfica a aplicar en este filtro");
					jLabelTitle.setFont(new java.awt.Font("Tahoma",1,11));
				}
				{
					jPanelCoords = new JPanel();
					TableLayout jPanelCoordsLayout = new TableLayout(new double[][] {
							{TableLayout.FILL, TableLayout.FILL, TableLayout.FILL, TableLayout.FILL, TableLayout.FILL},
							{5.0, TableLayout.FILL, TableLayout.MINIMUM, TableLayout.MINIMUM, TableLayout.MINIMUM,TableLayout.FILL}});
					jPanelCoordsLayout.setHGap(5);
					jPanelCoordsLayout.setVGap(5);
					jPanelCoords.setLayout(jPanelCoordsLayout);
					getMainPane().add(jPanelCoords, "2, 6, 5, 6");
					jPanelCoords.setBorder(BorderFactory.createTitledBorder("Coordenadas del marco"));
					{
						jTextFieldYMax = new JTextField();
						jPanelCoords.add(jTextFieldYMax, "2, 2");
						jTextFieldYMax.setText("Y máxima");
					}
					{
						jTextFieldYMin = new JTextField();
						jPanelCoords.add(jTextFieldYMin, "2, 4");
						jTextFieldYMin.setText("Y mínima");
					}
					{
						jTextFieldXMax = new JTextField();
						jPanelCoords.add(jTextFieldXMax, "3, 3");
						jTextFieldXMax.setText("X máxima");
					}
					{
						jTextFieldXMin = new JTextField();
						jPanelCoords.add(jTextFieldXMin, "1, 3");
						jTextFieldXMin.setText("X mínima");
					}
					jPanelCoords.setEnabled(false);
				}
				{
					jRadioButtonUseGeometry = new JRadioButton();
					getMainPane().add(jRadioButtonUseGeometry, "2, 3, 4, 3");
					jRadioButtonUseGeometry.setText("Restringir a los límites de cada canton");
					jRadioButtonUseGeometry.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							enableCoordsFields(false);
						}
					});
				}
				{
					jRadioButtonUseRect = new JRadioButton();
					getMainPane().add(jRadioButtonUseRect, "2, 5, 4, 5");
					jRadioButtonUseRect	.setText("Establecer un marco manualmente");
					jRadioButtonUseRect.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							enableCoordsFields(true);
						}
					});
				}
				{
					ButtonGroup group = new ButtonGroup();
					group.add(jRadioButtonUseRect);
					{
						jPanelShapeSelection = new JPanel();
						TableLayout jPanelShapeSelectionLayout = new TableLayout(new double[][] {{TableLayout.FILL, 40.0}, {TableLayout.FILL}});
						jPanelShapeSelectionLayout.setHGap(5);
						jPanelShapeSelectionLayout.setVGap(5);
						jPanelShapeSelection.setLayout(jPanelShapeSelectionLayout);
						getMainPane().add(jPanelShapeSelection, "2, 4, 5, 4");
						{
							jButtonSelectShape = new JButton();
							jPanelShapeSelection.add(jButtonSelectShape, "1, 0");
							jButtonSelectShape.setText("...");
							jButtonSelectShape.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent evt) {
									SelectGeometryDialog dialog = new SelectGeometryDialog(IVectorLayer.SHAPE_TYPE_POLYGON);
								}
							});
						}
						{
							jTextFieldSelectedShape = new JTextField();
							jPanelShapeSelection.add(jTextFieldSelectedShape, "0, 0");
							jTextFieldSelectedShape.setEnabled(false);
							jTextFieldSelectedShape.setEditable(false);
						}
					}
					group.add(jRadioButtonUseGeometry);

				}
				enableCoordsFields(false);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	protected void enableCoordsFields(boolean b) {

		jTextFieldXMin.setEnabled(b);
		jTextFieldXMax.setEnabled(b);
		jTextFieldYMin.setEnabled(b);
		jTextFieldYMax.setEnabled(b);
		jButtonSelectShape.setEnabled(!b);

	}

	//@Override
	protected boolean checkValuesAndSetFilter() {

		return false;

	}

}
