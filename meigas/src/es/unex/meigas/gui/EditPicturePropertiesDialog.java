package es.unex.meigas.gui;

import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;

import es.unex.meigas.core.Meigas;
import es.unex.meigas.core.Picture;
import info.clearthought.layout.TableLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JLabel;

public class EditPicturePropertiesDialog extends JDialog{

	private JLabel jLabelDescription;
	private JLabel jLabelCoords;
	private JButton jButtonCancel;
	private JButton jButtonOK;
	private JPanel jPanelButtons;
	private JTextField jTextFieldFile;
	private JButton jButtonFile;
	private JPanel jPanelFile;
	private JFormattedTextField jTextFieldX;
	private JFormattedTextField jTextFieldY;
	private JTextField jTextFieldDescription;
	private JLabel jLabelOrientation;
	private JLabel jLabelFile;

	Picture m_Picture;
	private File m_File;
	private JFormattedTextField jTextFieldOrientation;
	private boolean m_bIsOK;

	public EditPicturePropertiesDialog(Picture pic){

		super(Meigas.getMainFrame(), true);

		setLocationRelativeTo(null);

		m_Picture = pic;

		initGUI();

	}

	private void initGUI() {

		this.setSize(new java.awt.Dimension(456, 228));
		try {
			{
				TableLayout thisLayout = new TableLayout(new double[][] {
						{ 5.0, TableLayout.FILL, TableLayout.FILL,
								TableLayout.FILL, 5.0 },
						{ 5.0, 20.0, TableLayout.FILL, 20.0, TableLayout.FILL,
								20.0, TableLayout.FILL, 20.0, TableLayout.FILL,
								30.0, 5.0 } });
				thisLayout.setHGap(5);
				thisLayout.setVGap(5);
				this.setLayout(thisLayout);

				{
					jLabelDescription = new JLabel();
					this.add(jLabelDescription, "1, 1");
					jLabelDescription.setText("Descripción");
				}
				{
					jLabelFile = new JLabel();
					this.add(jLabelFile, "1, 3");
					jLabelFile.setText("Archivo");
				}
				{
					jLabelCoords = new JLabel();
					this.add(jLabelCoords, "1, 5");
					jLabelCoords.setText("Coordenadas");
				}
				{
					jLabelOrientation = new JLabel();
					this.add(jLabelOrientation, "1, 7");
					jLabelOrientation.setText("Rumbo");
				}
				{
					jTextFieldDescription = new JTextField();
					jTextFieldDescription.setText(m_Picture.getDescription());
					this.add(jTextFieldDescription, "2, 1, 3, 1");
				}
				{
					jTextFieldX = new JFormattedTextField(new CustomTextFormatterDouble());
					jTextFieldX.setValue(new Double(m_Picture.getCoords().getX()));
					this.add(jTextFieldX, "2, 5");
				}
				{
					jTextFieldY = new JFormattedTextField(new CustomTextFormatterDouble());
					jTextFieldY.setValue(new Double(m_Picture.getCoords().getY()));
					this.add(jTextFieldY, "3, 5");
				}
				{
					jTextFieldOrientation = new JFormattedTextField(new CustomTextFormatterDouble());
					jTextFieldOrientation.setValue(new Double(m_Picture.getOrientation()));
					this.add(jTextFieldOrientation, "2, 7, 3, 7");
				}
				{
					jPanelFile = new JPanel();
					TableLayout jPanelFileLayout = new TableLayout(
						new double[][] { { TableLayout.FILL, 25.0 }, {TableLayout.FILL} });
					jPanelFileLayout.setHGap(5);
					jPanelFileLayout.setVGap(5);
					jPanelFile.setLayout(jPanelFileLayout);
					this.add(jPanelFile, "2, 3, 3, 3");
					{
						jButtonFile = new JButton();
						jPanelFile.add(jButtonFile, "1, 0");
						jButtonFile.setText("...");
						jButtonFile.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								selectFile();
							}
						});
					}
					{
						jTextFieldFile = new JTextField();
						jPanelFile.add(jTextFieldFile, "0, 0");
						m_File = m_Picture.getFile();
						if (m_File != null){
							jTextFieldFile.setText(m_File.getAbsolutePath());
						}
						jTextFieldFile.setEnabled(false);
					}
				}
				{
					jPanelButtons = new JPanel();
					FlowLayout jPanelButtonsLayout = new FlowLayout();
					jPanelButtonsLayout.setAlignment(FlowLayout.RIGHT);
					jPanelButtons.setLayout(jPanelButtonsLayout);
					this.add(jPanelButtons, "2, 9, 3, 9");
					{
						jButtonOK = new JButton();
						jPanelButtons.add(jButtonOK);
						jButtonOK.setText("Aceptar");
						jButtonOK.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								m_bIsOK = true;
								updatePictureInfo();
								cancel();
							}
						});
					}
					{
						jButtonCancel = new JButton();
						jPanelButtons.add(jButtonCancel);
						jButtonCancel.setText("Cancelar");
						jButtonCancel.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								m_bIsOK = false;
								cancel();
							}
						});
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void selectFile() {

		JFileChooser fc = new JFileChooser();
		fc.setMultiSelectionEnabled(false);
        int iReturn = fc.showOpenDialog(this);

        if (iReturn == JFileChooser.APPROVE_OPTION) {
        	m_File = fc.getSelectedFile();
        	jTextFieldFile.setText(m_File.getAbsolutePath());
        }

	}

	protected void updatePictureInfo() {

		Double dX = ((Double)jTextFieldX.getValue());
		Double dY = ((Double)jTextFieldY.getValue());
		m_Picture.setCoords(new Point2D.Double(dX.doubleValue(), dY.doubleValue()));
		m_Picture.setDescription(jTextFieldDescription.getText());
		m_Picture.setFile(m_File);
		m_Picture.setOrientation(((Double)jTextFieldOrientation.getValue()).doubleValue());

	}

	protected void cancel() {

		this.dispose();
		this.setVisible(false);

	}

	public boolean isOK(){

		return m_bIsOK;

	}

}
