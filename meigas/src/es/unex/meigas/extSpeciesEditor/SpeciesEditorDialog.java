package es.unex.meigas.extSpeciesEditor;

import info.clearthought.layout.TableLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.border.LineBorder;

import es.unex.meigas.gui.MeigasPanel;

public class SpeciesEditorDialog
         extends
            JDialog {

   private final MeigasPanel m_MeigasPanel;
   private JScrollPane       jScrollPane;
   private JTree             jTree;
   private JPanel            jPanelDescription;
   private JButton           jButtonPrevious;
   private JButton           jButtonRemoveSpecie;
   private JButton           jButtonAddSpecie;
   private JButton           jButtonNext;
   private JPanel            jPanelButtons;
   private JPanel            jPanelImage;
   private JButton           jButtonRemoveImage;
   private JButton           jButtonAddImage;
   private JTextArea         jTextAreaDescription;
   private JPanel            jPanelImages;
   private JTabbedPane       jTabbedPane;


   public SpeciesEditorDialog(final MeigasPanel panel) {

      super();
      m_MeigasPanel = panel;
      initGUI();
      setLocationRelativeTo(null);

   }


   private void initGUI() {

      final TableLayout thisLayout = new TableLayout(new double[][] {
               { 3.0, TableLayout.FILL, TableLayout.FILL, TableLayout.FILL, 3.0 },
               { 3.0, TableLayout.FILL, TableLayout.FILL, TableLayout.MINIMUM, 3.0 } });
      thisLayout.setHGap(5);
      thisLayout.setVGap(5);
      getContentPane().setLayout(thisLayout);
      {
         jScrollPane = new JScrollPane();
         getContentPane().add(jScrollPane, "1, 1, 1, 2");
         {
            jTree = new JTree();
            jScrollPane.setViewportView(jTree);
         }
      }
      {
         jTabbedPane = new JTabbedPane();
         getContentPane().add(jTabbedPane, "2, 1, 3, 3");
         {
            jPanelDescription = new JPanel();
            final TableLayout jPanelDescriptionLayout = new TableLayout(new double[][] { { 3.0, TableLayout.FILL, 3.0 },
                     { 3.0, TableLayout.FILL, 3.0 } });
            jPanelDescriptionLayout.setHGap(5);
            jPanelDescriptionLayout.setVGap(5);
            jPanelDescription.setLayout(jPanelDescriptionLayout);
            jTabbedPane.addTab("Descripci�n", null, jPanelDescription, null);
            {
               jTextAreaDescription = new JTextArea();
               jPanelDescription.add(jTextAreaDescription, "1, 1");
               jTextAreaDescription.setBorder(new LineBorder(new java.awt.Color(0, 0, 0), 1, false));
            }
         }
         {
            jPanelImages = new JPanel();
            final TableLayout jPanelImagesLayout = new TableLayout(new double[][] {
                     { 3.0, TableLayout.FILL, TableLayout.FILL, TableLayout.FILL, TableLayout.FILL, TableLayout.FILL,
                              TableLayout.FILL, 3.0 }, { 3.0, TableLayout.FILL, TableLayout.FILL, TableLayout.MINIMUM, 3.0 } });
            jPanelImagesLayout.setHGap(5);
            jPanelImagesLayout.setVGap(5);
            jPanelImages.setLayout(jPanelImagesLayout);
            jTabbedPane.addTab("Fotograf�as", null, jPanelImages, null);
            jPanelImages.setPreferredSize(new java.awt.Dimension(466, 364));
            {
               jButtonPrevious = new JButton();
               jPanelImages.add(jButtonPrevious, "1, 3");
               jButtonPrevious.setText("<");
            }
            {
               jButtonNext = new JButton();
               jPanelImages.add(jButtonNext, "6, 3");
               jButtonNext.setText(">");
            }
            {
               jButtonAddImage = new JButton();
               jPanelImages.add(jButtonAddImage, "3, 3");
               jButtonAddImage.setText("+");
            }
            {
               jButtonRemoveImage = new JButton();
               jPanelImages.add(jButtonRemoveImage, "4, 3");
               jButtonRemoveImage.setText("-");
            }
            {
               jPanelImage = new JPanel();
               jPanelImages.add(jPanelImage, "1, 1, 6, 2");
               jPanelImage.setBorder(new LineBorder(new java.awt.Color(0, 0, 0), 1, false));
            }
         }
      }
      {
         jPanelButtons = new JPanel();
         final TableLayout jPanelButtonsLayout = new TableLayout(new double[][] { { TableLayout.FILL, TableLayout.FILL },
                  { TableLayout.FILL } });
         jPanelButtonsLayout.setHGap(5);
         jPanelButtonsLayout.setVGap(5);
         jPanelButtons.setLayout(jPanelButtonsLayout);
         getContentPane().add(jPanelButtons, "1, 3");
         {
            jButtonAddSpecie = new JButton();
            jPanelButtons.add(jButtonAddSpecie, "0, 0");
            jButtonAddSpecie.setText("+");
         }
         {
            jButtonRemoveSpecie = new JButton();
            jPanelButtons.add(jButtonRemoveSpecie, "1, 0");
            jButtonRemoveSpecie.setText("-");
         }
      }
      // TODO Auto-generated method stub

      pack();
   }

}
