package es.unex.meigas.gui;

import info.clearthought.layout.TableLayout;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import es.unex.meigas.core.DasocraticElement;
import es.unex.meigas.core.Picture;

public class PicturesPanel
         extends
            DasocraticInfoPanel
         implements
            ListSelectionListener {

   private JSplitPane  jSplitPane;
   private JPanel      jPanel, jPanelImage;
   private JButton     jButtonRemove;
   private JButton     jButtonEdit;
   private JList       jList;
   private JScrollPane jScrollPane;
   private JButton     jButtonAdd;
   private JLabel      jLabel;
   private PanelCanvas jPanelCanvas;

   private Picture     m_ActivePicture;


   public PicturesPanel(final DasocraticElement element,
                        final MeigasPanel meigasPanel) {

      super(element, meigasPanel);

      setName("Imágenes");

      m_ActivePicture = null;

   }


   protected void initializeContent() {


   }


   @Override
   protected void updateContent() {


   }


   @Override
   protected boolean checkDataAndUpdate() {

      return true;

   }


   @Override
   protected void initGUI() {

      final BorderLayout thisLayout = new BorderLayout();
      this.setLayout(thisLayout);
      this.setPreferredSize(new java.awt.Dimension(426, 262));
      {
         jSplitPane = new JSplitPane();
         this.add(jSplitPane, BorderLayout.CENTER);
         {
            jPanel = new JPanel();
            final TableLayout jPanelLayout = new TableLayout(new double[][] { { 5.0, TableLayout.FILL, TableLayout.FILL, 5.0 },
                     { 5.0, TableLayout.FILL, 25.0, 25.0, 5.0 } });
            jPanelLayout.setHGap(5);
            jPanelLayout.setVGap(5);
            jPanel.setLayout(jPanelLayout);
            jSplitPane.add(jPanel, JSplitPane.LEFT);
            jPanel.setPreferredSize(new java.awt.Dimension(114, 260));
            {
               jButtonAdd = new JButton();
               jPanel.add(jButtonAdd, "1, 2");
               jButtonAdd.setText("A�adir");
               jButtonAdd.addActionListener(new ActionListener() {
                  public void actionPerformed(final ActionEvent evt) {
                     addPicture();
                  }
               });
            }
            {
               jButtonRemove = new JButton();
               jPanel.add(jButtonRemove, "2, 2");
               jButtonRemove.setText("Eliminar");
               jButtonRemove.addActionListener(new ActionListener() {
                  public void actionPerformed(final ActionEvent evt) {
                     removePicture();
                  }
               });
            }
            {
               jButtonEdit = new JButton();
               jPanel.add(jButtonEdit, "1, 3, 2, 3");
               jButtonEdit.setText("Editar");
               jButtonEdit.addActionListener(new ActionListener() {
                  public void actionPerformed(final ActionEvent evt) {
                     editPicture();
                  }
               });
            }
            {
               jScrollPane = new JScrollPane();
               jPanel.add(jScrollPane, "1, 1, 2, 1");
               {
                  final ListModel jListModel = new DefaultListModel();
                  jList = new JList();
                  jScrollPane.setViewportView(jList);
                  jList.setModel(jListModel);
                  jList.addListSelectionListener(this);
                  jList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                  //Renderer renderer = new Renderer();
                  //jList.setCellRenderer(renderer);
                  addPicturesToList();
               }
            }
            jPanelImage = new JPanel();
            final TableLayout jPanelImageLayout = new TableLayout(new double[][] { { 5.0, TableLayout.FILL, 5.0 },
                     { 5.0, 20.0, TableLayout.FILL, 5.0 } });
            jPanelImageLayout.setHGap(5);
            jPanelImageLayout.setVGap(5);
            jPanelImage.setLayout(jPanelImageLayout);
            jPanelLayout.setHGap(5);
            jPanelLayout.setVGap(5);
            jSplitPane.add(jPanelImage, JSplitPane.RIGHT);
            jPanelImage.setPreferredSize(new java.awt.Dimension(114, 260));
            jLabel = new JLabel("");
            jPanelImage.add(jLabel, "1, 1");
            {
               jPanelCanvas = new PanelCanvas();
               jPanelImage.add(jPanelCanvas, "1, 2");
               //jPanelCanvas.setBorder(new LineBorder(new java.awt.Color(0,0,0), 1, false));
            }

         }
      }

   }


   protected void addPicture() {

      final Picture pic = new Picture();
      final EditPicturePropertiesDialog dialog = new EditPicturePropertiesDialog(pic);
      dialog.setVisible(true);
      if (dialog.isOK()) {
         final DefaultListModel model = ((DefaultListModel) jList.getModel());
         model.addElement(pic);
         jList.setSelectedValue(pic, true);
         m_ActivePicture = pic;
      }
      //updateSelection();

   }


   protected void editPicture() {

      final EditPicturePropertiesDialog dialog = new EditPicturePropertiesDialog(m_ActivePicture);
      dialog.setVisible(true);
      updateSelection();

   }


   protected void removePicture() {

      m_Element.removePicture(m_ActivePicture);
      ((DefaultListModel) jList.getModel()).removeElement(m_ActivePicture);
      m_ActivePicture = null;
      updateSelection();

   }


   private void addPicturesToList() {

      int i;
      final ArrayList pics = m_Element.getPictures();
      final DefaultListModel model = ((DefaultListModel) jList.getModel());

      for (i = 0; i < pics.size(); i++) {
         model.addElement(pics.get(i));
      }

   }


   private void updateSelection() {

      if (m_ActivePicture != null) {
         final DecimalFormat df = new DecimalFormat("##.##");
         final String s = m_ActivePicture.getDescription() + " [" + df.format(m_ActivePicture.getCoords().getX()) + ","
                          + df.format(m_ActivePicture.getCoords().getY()) + ", rumbo: "
                          + df.format(m_ActivePicture.getOrientation()) + "]";
         jLabel.setText(s);
         final File file = m_ActivePicture.getFile();
         BufferedImage img = null;
         if (file != null) {

            try {
               img = ImageIO.read(file);
            }
            catch (final IOException e) {
               img = null;
            }

         }
         jPanelCanvas.setImage(img);
         jButtonRemove.setEnabled(true);
         jButtonEdit.setEnabled(true);
      }
      else {
         jLabel.setText("");
         jPanelCanvas.setImage(null);
         jButtonRemove.setEnabled(false);
         jButtonEdit.setEnabled(false);
      }

   }


   public void valueChanged(final ListSelectionEvent e) {

      if (!e.getValueIsAdjusting()) {
         m_ActivePicture = (Picture) jList.getSelectedValue();
         updateSelection();
      }

   }

   public class PanelCanvas
            extends
               JPanel {

      BufferedImage m_Image = null;
      int           x, y;


      @Override
      public void paint(final Graphics g) {

         super.paint(g);

         if (m_Image != null) {
            final double dRatioImg = (double) m_Image.getWidth() / (double) m_Image.getHeight();
            final double dRatioCanvas = (double) this.getWidth() / (double) this.getHeight();
            if (dRatioImg > dRatioCanvas) {
               x = 0;
               y = (int) (this.getHeight() - this.getWidth() / dRatioImg);
            }
            else {
               x = (int) (this.getWidth() - this.getHeight() * dRatioImg);
               y = 0;
            }

            g.drawImage(m_Image, x, y, this.getWidth() - x, this.getHeight() - y, 0, 0, m_Image.getWidth(), m_Image.getHeight(),
                     null);
            g.drawRect(x, y, this.getWidth() - 2 * x - 1, this.getHeight() - 2 * y - 1);
         }

      }


      public void setImage(final BufferedImage image) {

         m_Image = image;
         this.repaint();

      }

   }

   class Renderer
            extends
               JLabel
            implements
               ListCellRenderer {

      public Renderer() {

         setOpaque(true);
         setHorizontalAlignment(CENTER);
         setVerticalAlignment(CENTER);

      }


      public Component getListCellRendererComponent(final JList list,
                                                    final Object value,
                                                    final int index,
                                                    final boolean isSelected,
                                                    final boolean cellHasFocus) {
         ImageIcon icon;

         if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
         }
         else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
         }

         final Picture pic = (Picture) value;
         final File file = pic.getFile();

         if (file != null) {
            icon = new ImageIcon(file.getAbsolutePath());
         }
         else {
            icon = null;
         }
         setIcon(icon);

         setText(pic.getDescription());
         if (icon != null) {
            setFont(list.getFont());
         }
         else {
            setFont(list.getFont().deriveFont(Font.ITALIC));
         }

         return this;
      }

   }

}
