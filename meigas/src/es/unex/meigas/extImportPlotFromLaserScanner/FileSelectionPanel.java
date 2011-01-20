package es.unex.meigas.extImportPlotFromLaserScanner;

import info.clearthought.layout.TableLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import es.unex.meigas.extBase.BaseWizardPanel;

public class FileSelectionPanel extends BaseWizardPanel{

	private JTextField fileTextField = null;
	private JPanel jPanelImage;
	private ImportPlotFromLaserScannerWizard wizard;

	public FileSelectionPanel(ImportPlotFromLaserScannerWizard wizard) {

		super(wizard);
		this.wizard = wizard;
		initGUI();

	}

	public void initGUI(){

		TableLayout thisLayout = new TableLayout(new double[][] {
				{TableLayout.FILL, TableLayout.PREFERRED, 5.0, TableLayout.PREFERRED, 5.0, TableLayout.PREFERRED, TableLayout.FILL},
				{TableLayout.FILL, TableLayout.FILL, TableLayout.PREFERRED, 10.0, TableLayout.PREFERRED, TableLayout.FILL}});

		this.setLayout(thisLayout);
		this.setPreferredSize(new java.awt.Dimension(550, 220));

		JLabel shpLabel = new JLabel("Directorio");
		this.add(shpLabel, "1, 4");
		fileTextField = new JTextField(20);
		fileTextField.setText("");
		fileTextField.addKeyListener(new KeyListener(){

			public void keyPressed(KeyEvent e) {}
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub

			}

			public void keyTyped(KeyEvent e) {
				hasEnoughInformation();
				m_ParentPanel.updateButtons();
			}

		});
		fileTextField.getDocument().addDocumentListener(new DocumentListener(){
			public void changedUpdate(DocumentEvent e) {}
			public void insertUpdate(DocumentEvent e) {
				if (hasEnoughInformation()){;
					m_ParentPanel.updateButtons();
				}
			}
			public void removeUpdate(DocumentEvent e) {
				if (hasEnoughInformation()){;
					m_ParentPanel.updateButtons();
				}
			}
		});
		this.add(fileTextField, "3, 4");
		JButton fileChooserButton = new JButton("...");
		fileChooserButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				//fileChooser.setFileFilter(new SHPFilter());
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int returnValue = fileChooser.showOpenDialog(null);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					java.io.File file = fileChooser.getSelectedFile();
					fileTextField.setText(file.getAbsolutePath());
					//wizard.setFilename(file.getAbsolutePath());
				}
			}
		});
		this.add(fileChooserButton, "5, 4");
		{
			jPanelImage = new ImagePanel();
			this.add(jPanelImage, "1, 1, 5, 1");
		}
		//setVisible(true);

	}

	public boolean hasEnoughInformation() {

		return new File(fileTextField.getText()).exists();

	}

	public String getFolder(){

		return fileTextField.getText();

	}

	@Override
	public boolean isFinish() {

		return true;

	}

}

