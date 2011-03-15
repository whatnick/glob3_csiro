/*

 IGO Software SL  -  info@igosoftware.es

 http://www.glob3.org

-------------------------------------------------------------------------------
 Copyright (c) 2010, IGO Software SL
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
     * Redistributions of source code must retain the above copyright
       notice, this list of conditions and the following disclaimer.
     * Redistributions in binary form must reproduce the above copyright
       notice, this list of conditions and the following disclaimer in the
       documentation and/or other materials provided with the distribution.
     * Neither the name of the IGO Software SL nor the
       names of its contributors may be used to endorse or promote products
       derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL IGO Software SL BE LIABLE FOR ANY
 DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
-------------------------------------------------------------------------------

*/


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


public class GLocationManagerDialog
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


   public GLocationManagerDialog(final IGlobeApplication app) {

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
         final GNamedLocation[] locations = GLocations.getLocations();
         final GNamedLocation[] allLocations = new GNamedLocation[locations.length + 1];
         System.arraycopy(locations, 0, allLocations, 0, locations.length);
         allLocations[allLocations.length - 1] = GLocations.getDefaultLocation();
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
                  final GNamedLocation location = (GNamedLocation) dlm.getElementAt(index);
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
      if ((sel != null) && !sel.equals(GLocations.getDefaultLocation())) {
         GLocations.removeLocation((GNamedLocation) sel);
         model.removeElement(sel);
      }

   }


   private void createNewLocation() {

      final Position position = m_View.getCurrentEyePosition();
      final double elevation = m_View.getCurrentEyePosition().elevation;
      final String sName = JOptionPane.showInputDialog(this, "Location _name", "Enter location _name",
               JOptionPane.INFORMATION_MESSAGE);

      if (sName != null) {
         final GNamedLocation location = new GNamedLocation(sName, position, elevation);
         GLocations.addLocation(location);
         ((DefaultComboBoxModel) jList.getModel()).addElement(location);
      }

   }
}
