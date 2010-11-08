package es.igosoftware.globe.modules.locationManager;

import es.igosoftware.globe.IGlobeApplication;
import gov.nasa.worldwind.View;
import gov.nasa.worldwind.geom.Position;
import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;

public class LocationManagerDialog
         extends
            JDialog {

   /**
    * 
    */
   private static final long serialVersionUID = 1L;

   private JButton           jButtonNew;
   private JList             jList;
   private JButton           jButtonRemove;
   private final View        m_View;


   //private JButton           jButtonSetAsDefault;


   public LocationManagerDialog(final IGlobeApplication app) {

      super(app.getFrame(), "Location manager", true);

      m_View = app.getView();

      initGUI();

      setLocationRelativeTo(null);

   }


   private void initGUI() {

      final TableLayout thisLayout = new TableLayout(new double[][] {
               { 6.0, TableLayoutConstants.FILL, TableLayoutConstants.FILL, TableLayoutConstants.FILL, TableLayoutConstants.FILL,
                        3.0, TableLayoutConstants.FILL, 6.0 },
               { 6.0, TableLayoutConstants.FILL, 6.0, TableLayoutConstants.MINIMUM, 6.0 } });
      thisLayout.setHGap(5);
      thisLayout.setVGap(5);
      getContentPane().setLayout(thisLayout);
      {
         jButtonNew = new JButton();
         getContentPane().add(jButtonNew, "6, 3");
         jButtonNew.setText("New...");
         jButtonNew.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
               createNewLocation();
            }
         });
      }
      {
         jButtonRemove = new JButton();
         getContentPane().add(jButtonRemove, "4, 3");
         jButtonRemove.setText("Remove");
         jButtonRemove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
               removeLocation();
            }
         });
      }
      {
         final NamedLocation[] locations = Locations.getLocations();
         final NamedLocation[] allLocations = new NamedLocation[locations.length + 1];
         System.arraycopy(locations, 0, allLocations, 0, locations.length);
         allLocations[allLocations.length - 1] = Locations.getDefaultLocation();
         final ListModel model = new DefaultComboBoxModel(allLocations);
         jList = new JList();
         getContentPane().add(jList, "1, 1, 6, 1");
         jList.setModel(model);
         jList.setBorder(new LineBorder(new java.awt.Color(0, 0, 0), 1, false));
         jList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
         jList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent e) {
               if (e.getClickCount() == 2) {
                  final int index = jList.locationToIndex(e.getPoint());
                  final ListModel dlm = jList.getModel();
                  final NamedLocation location = (NamedLocation) dlm.getElementAt(index);
                  m_View.goTo(location._position, location._elevation);
               }
            }
         });
      }
      {
         //         jButtonSetAsDefault = new JButton();
         //         getContentPane().add(jButtonSetAsDefault, "1, 3");
         //         jButtonSetAsDefault.setText("Set as default");
         //         jButtonSetAsDefault.addActionListener(new ActionListener() {
         //            public void actionPerformed(final ActionEvent evt) {
         //               setAsDefaultLocation();
         //            }
         //         });
      }
      {
         this.setSize(516, 290);
      }

   }


   //   protected void setAsDefaultLocation() {
   //
   //      final Object sel = jList.getSelectedValue();
   //      if (sel != null) {
   //         Locations.setDefaultLocation((NamedLocation) sel);
   //      }
   //
   //   }


   protected void removeLocation() {

      final DefaultComboBoxModel model = ((DefaultComboBoxModel) jList.getModel());
      final Object sel = jList.getSelectedValue();
      if ((sel != null) && !sel.equals(Locations.getDefaultLocation())) {
         Locations.removeLocation((NamedLocation) sel);
         model.removeElement(sel);
      }

   }


   private void createNewLocation() {

      final Position position = m_View.getCurrentEyePosition();
      final double elevation = m_View.getCurrentEyePosition().elevation;
      final String sName = JOptionPane.showInputDialog(this, "Location _name", "Enter location _name",
               JOptionPane.INFORMATION_MESSAGE);

      if (sName != null) {
         final NamedLocation location = new NamedLocation(sName, position, elevation);
         Locations.addLocation(location);
         ((DefaultComboBoxModel) jList.getModel()).addElement(location);
      }

   }
}
