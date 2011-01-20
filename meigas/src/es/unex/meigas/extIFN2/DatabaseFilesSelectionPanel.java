package es.unex.meigas.extIFN2;

import info.clearthought.layout.TableLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;

import es.unex.meigas.extBase.BaseWizardPanel;

public class DatabaseFilesSelectionPanel extends BaseWizardPanel{

	private JPanel jPanelButtons;
	private JLabel jLabelText;
	private JLabel jLabelFiles;
	private JButton jButtonRemove;
	private JButton jButtonAdd;
	private JList jListFiles;
	private JScrollPane jScrollPane;

	public DatabaseFilesSelectionPanel(IFN2Panel panel){

		super(panel);

		initGUI();

	}

	public boolean hasEnoughInformation() {

		return (jListFiles.getModel().getSize() > 0);

	}

	public void initGUI() {

		try {
			{
				TableLayout thisLayout = new TableLayout(new double[][] {
						{ 5.0, 60.0, TableLayout.FILL, 70.0, 5.0 },
						{ TableLayout.FILL, 25.0, TableLayout.FILL,
								TableLayout.FILL, TableLayout.FILL,
								TableLayout.FILL } });
				thisLayout.setHGap(5);
				thisLayout.setVGap(5);
				this.setLayout(thisLayout);
				this.setPreferredSize(new java.awt.Dimension(498, 201));
				{
					jPanelButtons = new JPanel();
					TableLayout jPanelButtonsLayout = new TableLayout(
						new double[][] {
								{ TableLayout.FILL },
								{ TableLayout.FILL, TableLayout.FILL,
										TableLayout.FILL } });
					jPanelButtonsLayout.setHGap(5);
					jPanelButtonsLayout.setVGap(5);
					jPanelButtons.setLayout(jPanelButtonsLayout);
					this.add(jPanelButtons, "3, 3, 3, 4");
					{
						jButtonAdd = new JButton();
						jPanelButtons.add(jButtonAdd, "0, 0");
						jButtonAdd.setText("Añadir");
						jButtonAdd.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								addFile();
							}
						});
					}
					{
						jButtonRemove = new JButton();
						jPanelButtons.add(jButtonRemove, "0, 1");
						jButtonRemove.setText("Eliminar");
						jButtonRemove.setEnabled(false);
						jButtonRemove.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								removeFile();
							}
						});
					}
				}
				{
					jLabelText = new JLabel();
					this.add(jLabelText, "1, 1, 2, 1");
					jLabelText.setText("Seleccione los archivos del IFN2 a utilizar (datestXX.dbf)");
					jLabelText.setFont(new java.awt.Font("Tahoma",1,11));
				}
				{
					jLabelFiles = new JLabel();
					this.add(jLabelFiles, "1, 3");
					jLabelFiles.setText("Archivos");
				}
				{
					jScrollPane = new JScrollPane();
					this.add(jScrollPane, "2, 3, 2, 4");
					{
						jListFiles = new JList(new DefaultListModel());
						jScrollPane.setViewportView(jListFiles);
						jListFiles
							.addListSelectionListener(new ListSelectionListener() {
							public void valueChanged(ListSelectionEvent evt) {
								selectionChanged();
							}
							});
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addFile() {

		int i;
		JFileChooser fc = new JFileChooser();
		fc.setMultiSelectionEnabled(true);
		fc.setFileFilter(new DBFFilter());
        int iReturn = fc.showOpenDialog(m_ParentPanel);

        DefaultListModel model = (DefaultListModel) jListFiles.getModel();
        if (iReturn == JFileChooser.APPROVE_OPTION) {
        	File files[] = fc.getSelectedFiles();
            for (i = 0; i < files.length; i++){
            	model.addElement(files[i]);
            }
            jButtonRemove.setEnabled(true);
        }

        m_ParentPanel.updateButtons();

	}

	private void removeFile(){

		int i;
		int[] indices = jListFiles.getSelectedIndices();
		DefaultListModel model = (DefaultListModel)jListFiles.getModel();

		for (i = indices.length - 1; i > -1; i--){
			model.removeElementAt(indices[i]);
		}

		m_ParentPanel.updateButtons();

	}

	private void selectionChanged(){

		jButtonRemove.setEnabled(jListFiles.getSelectedIndices().length > 0);

	}

	public File[] getFiles(){

		int i;
		DefaultListModel model = (DefaultListModel)jListFiles.getModel();
		File[] files = new File[model.getSize()];

		for (i = 0; i < model.getSize(); i++){
			files[i] = (File) model.get(i);

		}

		return files;

	}

	private class DBFFilter extends FileFilter{

		public boolean accept(File f) {

			if (f.isDirectory()) {
				return true;
			}

			String extension = getExtension(f);
			String name = getName(f);
			if (extension != null && name != null) {
				if (extension.equals("dbf") && name.equals("datest")) {
					return true;
				} else {
					return false;
				}
			}


			return false;
		}


		private String getName(File f) {

			String name;
	        String s = f.getName();

	        try{
	        	name = s.substring(0, 6).toLowerCase();
	        }
	        catch(Exception e){
	        	return null;
	        }

	        return name;

		}


		public String getDescription() {

			return "Archivos DBase (*.dbf)";

		}

	    private String getExtension(File f) {

	        String ext = null;
	        String s = f.getName();
	        int i = s.lastIndexOf('.');

	        if (i > 0 &&  i < s.length() - 1) {
	            ext = s.substring(i+1).toLowerCase();
	        }

	        return ext;

	    }


	}

	@Override
	public boolean isFinish() {
		// TODO Auto-generated method stub
		return false;
	}

}
