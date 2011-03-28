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


package es.igosoftware.globe;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EventListener;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.miginfocom.swing.MigLayout;
import es.igosoftware.globe.actions.GButtonGenericAction;
import es.igosoftware.globe.actions.GLayerAction;
import es.igosoftware.globe.actions.IGenericAction;
import es.igosoftware.globe.actions.ILayerAction;
import es.igosoftware.globe.attributes.GBooleanLayerAttribute;
import es.igosoftware.globe.attributes.ILayerAttribute;
import es.igosoftware.util.GCollections;
import es.igosoftware.util.GPair;
import es.igosoftware.util.GTriplet;
import es.igosoftware.utils.GSwingUtils;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.layers.LayerList;


public class GLayersManagerModule
         extends
            GAbstractGlobeModule {

   private JList                                                                                  _layersJList;

   private final JPanel                                                                           _layerPropertiesPanel;
   private final List<GTriplet<IGlobeLayer, ILayerAttribute<?>, GPair<Component, EventListener>>> _widgetsInLayerPropertiesPanel;

   private final boolean                                                                          _autoAddSingleLayer;


   public GLayersManagerModule() {
      this(true);
   }


   public GLayersManagerModule(final boolean autoAddSingleLayer) {
      _layerPropertiesPanel = new JPanel(new MigLayout("fillx, insets 0 0 0 0"));
      _layerPropertiesPanel.setBackground(Color.WHITE);
      _widgetsInLayerPropertiesPanel = new ArrayList<GTriplet<IGlobeLayer, ILayerAttribute<?>, GPair<Component, EventListener>>>();

      _autoAddSingleLayer = autoAddSingleLayer;
   }


   @Override
   public String getName() {
      return "Layers Manager";
   }


   @Override
   public String getVersion() {
      return "0.1";
   }


   @Override
   public String getDescription() {
      return "Layers Manager";
   }


   @Override
   public List<? extends IGenericAction> getGenericActions(final IGlobeApplication application) {
      final IGenericAction addLayer = new GButtonGenericAction("Add a layer", 'A', application.getIcon("add.png"),
               IGenericAction.MenuArea.FILE, true) {

         @Override
         public boolean isVisible() {
            return !getAllLayerInfos(application).isEmpty();
         }


         @Override
         public void execute() {
            addNewLayer(application);
         }
      };

      return Collections.singletonList(addLayer);
   }


   private static class ModuleAndLayerInfo {
      private final ILayerFactoryModule _module;
      private final ILayerInfo          _layerInfo;


      private ModuleAndLayerInfo(final ILayerFactoryModule module,
                                 final ILayerInfo layerInfo) {
         _module = module;
         _layerInfo = layerInfo;
      }


      @Override
      public String toString() {
         return _module.getName() + ": " + _layerInfo.getName();
      }
   }


   private void addNewLayer(final IGlobeApplication application) {
      final ArrayList<ModuleAndLayerInfo> allLayerInfos = getAllLayerInfos(application);

      if (allLayerInfos.isEmpty()) {
         return;
      }

      if ((allLayerInfos.size() == 1) && _autoAddSingleLayer) {
         final ModuleAndLayerInfo first = allLayerInfos.get(0);
         createNewLayer(application, first._module, first._layerInfo);
         return;
      }


      final ModuleAndLayerInfo moduleAndLayerInfo = (ModuleAndLayerInfo) JOptionPane.showInputDialog(application.getFrame(),
               application.getTranslation("Select a layer"), application.getTranslation("Add a layer"),
               JOptionPane.PLAIN_MESSAGE, application.getIcon("add.png", 32, 32), allLayerInfos.toArray(), null);

      if (moduleAndLayerInfo != null) {
         createNewLayer(application, moduleAndLayerInfo._module, moduleAndLayerInfo._layerInfo);
      }

   }


   private ArrayList<ModuleAndLayerInfo> getAllLayerInfos(final IGlobeApplication application) {
      final ArrayList<ModuleAndLayerInfo> result = new ArrayList<ModuleAndLayerInfo>();

      for (final IGlobeModule module : application.getModules()) {
         if (!(module instanceof ILayerFactoryModule)) {
            continue;
         }

         final ILayerFactoryModule layerFactoryModule = (ILayerFactoryModule) module;

         final List<? extends ILayerInfo> moduleLayerInfos = layerFactoryModule.getAvailableLayers(application);
         if (moduleLayerInfos != null) {
            for (final ILayerInfo moduleLayerInfo : moduleLayerInfos) {
               result.add(new ModuleAndLayerInfo(layerFactoryModule, moduleLayerInfo));
            }
         }
      }

      return result;
   }


   private void createNewLayer(final IGlobeApplication application,
                               final ILayerFactoryModule module,
                               final ILayerInfo layerInfo) {
      final Cursor currentCursor = Cursor.getDefaultCursor();
      application.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

      try {
         final IGlobeLayer newLayer = module.addNewLayer(application, layerInfo);

         if (newLayer != null) {
            newLayer.doDefaultAction(application);
            selectLayer(newLayer);
         }
      }
      finally {
         application.getFrame().setCursor(currentCursor);
      }
   }


   public void selectLayer(final IGlobeLayer layer) {
      if ((_layersJList != null) && (layer != null)) {
         _layersJList.setSelectedValue(layer, true);
      }
   }


   @Override
   public List<? extends ILayerAction> getLayerActions(final IGlobeApplication application,
                                                       final IGlobeLayer layer) {

      final ILayerAction addLayer = new GLayerAction("Add a layer", 'A', application.getIcon("add.png"), false) {
         @Override
         public boolean isVisible() {
            return !getAllLayerInfos(application).isEmpty();
         }


         @Override
         public void execute() {
            addNewLayer(application);
         }
      };


      final ILayerAction zoomToLayer = new GLayerAction("Zoom to layer", 'Z', application.getIcon("zoom.png"), true) {
         @Override
         public boolean isVisible() {
            return (layer != null) && (layer.getExtent() != null);
         }


         @Override
         public void execute() {
            application.zoomToSector(layer.getExtent());
         }
      };


      final ILayerAction removeLayer = new GLayerAction("Remove layer", 'R', application.getIcon("remove.png"), true) {
         @Override
         public boolean isVisible() {
            return (layer != null);
         }


         @Override
         public void execute() {
            application.getLayerList().remove(layer);
         }
      };


      final ILayerAction moveUp = new GLayerAction("Move up", 'U', application.getIcon("up.png"), false) {
         @Override
         public boolean isVisible() {
            return (layer != null);
         }


         @Override
         public boolean isEnabled() {
            final List<? extends IGlobeLayer> layers = application.getGlobeLayers();
            final int layerPosition = layers.indexOf(layer);
            return (layerPosition > 0);
         }


         @Override
         public void execute() {
            final LayerList wwLayersList = application.getLayerList();

            final List<? extends IGlobeLayer> layers = application.getGlobeLayers();
            final int layerPosition = layers.indexOf(layer);

            final IGlobeLayer previousLayer = layers.get(layerPosition - 1);
            final int previousLayerIndexInWWLayerList = wwLayersList.indexOf(previousLayer);

            wwLayersList.remove(layer);
            wwLayersList.add(previousLayerIndexInWWLayerList, layer);

            if (_layersJList != null) {
               _layersJList.setSelectedIndex(layerPosition - 1);
            }
         }
      };


      final ILayerAction moveDown = new GLayerAction("Move down", 'D', application.getIcon("down.png"), false) {
         @Override
         public boolean isVisible() {
            return (layer != null);
         }


         @Override
         public boolean isEnabled() {
            final List<? extends IGlobeLayer> layers = application.getGlobeLayers();
            final int layerPosition = layers.indexOf(layer);

            return (layerPosition < (layers.size() - 1));
         }


         @Override
         public void execute() {
            final LayerList wwLayersList = application.getLayerList();

            final List<? extends IGlobeLayer> layers = application.getGlobeLayers();
            final int layerPosition = layers.indexOf(layer);

            final IGlobeLayer nextLayer = layers.get(layerPosition + 1);
            final int nextLayerIndexInWWLayerList = wwLayersList.indexOf(nextLayer);

            wwLayersList.remove(layer);
            wwLayersList.add(nextLayerIndexInWWLayerList, layer);

            if (_layersJList != null) {
               _layersJList.setSelectedIndex(layerPosition + 1);
            }
         }
      };

      return GCollections.createList(addLayer, zoomToLayer, removeLayer, moveUp, moveDown);
   }


   @Override
   public List<GPair<String, Component>> getPanels(final IGlobeApplication application) {
      final ArrayList<GPair<String, Component>> panels = new ArrayList<GPair<String, Component>>();

      panels.add(new GPair<String, Component>("Layers", createLayersPanel(application)));

      return panels;
   }


   private Component createLayersPanel(final IGlobeApplication application) {
      final JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
      splitPane.setBorder(BorderFactory.createEmptyBorder());
      splitPane.setContinuousLayout(true);
      splitPane.setOneTouchExpandable(true);
      splitPane.setDividerLocation(Math.round(application.initialDimension().height / 6f) + splitPane.getInsets().left);

      splitPane.setTopComponent(wrapInJScrollPane(createLayersJList(application)));
      splitPane.setBottomComponent(wrapInJScrollPane(_layerPropertiesPanel));

      return splitPane;
   }


   private JScrollPane wrapInJScrollPane(final Component component) {
      final JScrollPane scrollPane = new JScrollPane(component);
      scrollPane.setBorder(BorderFactory.createEmptyBorder());
      return scrollPane;
   }


   private static class GlobeLayersListModel
            extends
               AbstractListModel {
      private static final long                 serialVersionUID = 1L;

      private final List<? extends IGlobeLayer> _globeLayers;


      private GlobeLayersListModel(final List<? extends IGlobeLayer> globeLayers) {
         _globeLayers = globeLayers;
      }


      @Override
      public IGlobeLayer getElementAt(final int index) {
         return _globeLayers.get(index);
      }


      @Override
      public int getSize() {
         return _globeLayers.size();
      }
   }


   private static class LayerRenderer
            extends
               DefaultListCellRenderer {
      private static final long       serialVersionUID = 1L;

      private final IGlobeApplication _application;


      private LayerRenderer(final IGlobeApplication application) {
         _application = application;
      }


      @Override
      public Component getListCellRendererComponent(final JList list,
                                                    final Object value,
                                                    final int index,
                                                    final boolean isSelected,
                                                    final boolean hasFocus) {
         final IGlobeLayer layer = (IGlobeLayer) value;
         final Icon icon = layer.getIcon(_application);

         final JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, hasFocus);
         label.setIcon(icon);
         label.setText(layer.getName());

         return label;
      }
   }


   private JList createLayersJList(final IGlobeApplication application) {
      final LayerList layerList = application.getLayerList();

      //      final List<IGlobeLayer> layers = getGlobeLayers(layerList);


      _layersJList = new JList(new GlobeLayersListModel(application.getGlobeLayers()));
      _layersJList.setBackground(Color.WHITE);

      layerList.addPropertyChangeListener(AVKey.LAYERS, new PropertyChangeListener() {
         @Override
         public void propertyChange(final PropertyChangeEvent evt) {
            _layersJList.setModel(new GlobeLayersListModel(application.getGlobeLayers()));
         }
      });

      _layersJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      _layersJList.setBorder(BorderFactory.createEmptyBorder());

      _layersJList.setCellRenderer(new LayerRenderer(application));

      _layersJList.addListSelectionListener(new ListSelectionListener() {
         @Override
         public void valueChanged(final ListSelectionEvent e) {
            if (e.getValueIsAdjusting() == false) {
               final List<? extends IGlobeLayer> layers = ((GlobeLayersListModel) _layersJList.getModel())._globeLayers;

               final IGlobeLayer selectedLayer;
               final int layerPosition = _layersJList.getSelectedIndex();
               if (layerPosition == -1) {
                  selectedLayer = null;
               }
               else {
                  selectedLayer = layers.get(layerPosition);
               }
               changedSelectedLayer(application, selectedLayer);
            }
         }
      });


      _layersJList.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(final MouseEvent mouseEvent) {
            final int button = mouseEvent.getButton();
            final int clickCount = mouseEvent.getClickCount();

            // right click
            if ((button == MouseEvent.BUTTON3) && (clickCount == 1)) {
               final int layerPosition = selectBasedOnMousePosition(mouseEvent);

               final List<? extends IGlobeLayer> layers = ((GlobeLayersListModel) _layersJList.getModel())._globeLayers;
               final IGlobeLayer layer = (layerPosition < 0) ? null : layers.get(layerPosition);

               popupContextMenu(application, layer, _layersJList, mouseEvent);
            }

            // double click
            if ((button == MouseEvent.BUTTON1) && (clickCount == 2)) {
               final int layerPosition = selectBasedOnMousePosition(mouseEvent);

               final List<? extends IGlobeLayer> layers = ((GlobeLayersListModel) _layersJList.getModel())._globeLayers;

               if (layerPosition < 0) {
                  return;
               }

               final IGlobeLayer layer = layers.get(layerPosition);
               layer.doDefaultAction(application);
            }
         }


         private int selectBasedOnMousePosition(final MouseEvent mouseEvent) {
            final int layerPosition = _layersJList.locationToIndex(mouseEvent.getPoint());

            if (layerPosition != _layersJList.getSelectedIndex()) {
               _layersJList.setSelectedIndex(layerPosition);
            }

            return layerPosition;
         }
      });

      return _layersJList;
   }


   protected void popupContextMenu(final IGlobeApplication application,
                                   final IGlobeLayer layer,
                                   final JList list,
                                   final MouseEvent event) {
      final JPopupMenu menu = new JPopupMenu();
      menu.setLightWeightPopupEnabled(false);


      for (final IGlobeModule module : application.getModules()) {
         if (module != null) {
            createLayerActionsMenuItems(application, menu, module.getLayerActions(application, layer));
         }
      }

      final List<? extends ILayerAction> layerActions = (layer == null) ? null : layer.getLayerActions(application);
      createLayerActionsMenuItems(application, menu, layerActions);


      final Point pt = SwingUtilities.convertPoint(event.getComponent(), event.getPoint(), list);
      menu.show(list, pt.x, pt.y);
   }


   private void createLayerActionsMenuItems(final IGlobeApplication application,
                                            final JPopupMenu menu,
                                            final List<? extends ILayerAction> layersActions) {
      if (layersActions == null) {
         return;
      }

      boolean firstActionOnMenu = true;
      for (final ILayerAction layerAction : layersActions) {
         if (layerAction.isVisible()) {
            final JMenuItem menuItem = new JMenuItem(application.getTranslation(layerAction.getLabel()), layerAction.getIcon());

            menuItem.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(final ActionEvent e) {
                  layerAction.execute();
               }
            });
            menuItem.setEnabled(layerAction.isEnabled());

            if (firstActionOnMenu) {
               firstActionOnMenu = false;
               if (menu.getComponents().length > 0) {
                  menu.addSeparator();
               }
            }

            menu.add(menuItem);
         }
      }
   }


   protected void changedSelectedLayer(final IGlobeApplication application,
                                       final IGlobeLayer selectedLayer) {
      cleanLayerPropertiesPanel();

      if (selectedLayer != null) {
         createLayerPropertiesWidgets(application, selectedLayer);
      }

      // force redraw
      _layerPropertiesPanel.invalidate();
      _layerPropertiesPanel.validate();
      _layerPropertiesPanel.repaint();

      _layerPropertiesPanel.requestFocus();
      _layerPropertiesPanel.requestFocusInWindow();
   }


   private void cleanLayerPropertiesPanel() {
      _layerPropertiesPanel.removeAll();

      for (final GTriplet<IGlobeLayer, ILayerAttribute<?>, GPair<Component, EventListener>> layerAttributeAndWidget : _widgetsInLayerPropertiesPanel) {
         final IGlobeLayer layer = layerAttributeAndWidget._first;
         final ILayerAttribute<?> attribute = layerAttributeAndWidget._second;
         final GPair<Component, EventListener> widget = layerAttributeAndWidget._third;

         attribute.cleanupWidget(layer, widget);
      }
      _widgetsInLayerPropertiesPanel.clear();
   }


   private void createLayerPropertiesWidgets(final IGlobeApplication application,
                                             final IGlobeLayer layer) {

      final JToolBar toolbar = new JToolBar();
      toolbar.setBorder(BorderFactory.createEmptyBorder());
      toolbar.setFloatable(false);


      for (final IGlobeModule module : application.getModules()) {
         if (module != null) {
            createLayerActionsToolbarItems(application, toolbar, module.getLayerActions(application, layer));
         }
      }

      createLayerActionsToolbarItems(application, toolbar, layer.getLayerActions(application));

      if (toolbar.getComponentCount() > 0) {
         _layerPropertiesPanel.add(toolbar, "growx, wrap, span 2");
      }


      for (final IGlobeModule module : application.getModules()) {
         if (module != null) {
            createAttributesWidgets(application, layer, module.getLayerAttributes(application, layer));
         }
      }

      createAttributesWidgets(application, layer, layer.getLayerAttributes(application));
   }


   private void createLayerActionsToolbarItems(final IGlobeApplication application,
                                               final JToolBar toolbar,
                                               final List<? extends ILayerAction> layersActions) {
      if (layersActions == null) {
         return;
      }

      for (final ILayerAction layerAction : layersActions) {
         if (layerAction.isShowOnToolBar() && layerAction.isVisible()) {
            final JButton button = GSwingUtils.createToolbarButton(layerAction.getIcon(),
                     application.getTranslation(layerAction.getLabel()), new ActionListener() {
                        @Override
                        public void actionPerformed(final ActionEvent e) {
                           layerAction.execute();
                        }
                     });

            button.setEnabled(layerAction.isEnabled());
            toolbar.add(button);
         }
      }
   }


   private void createAttributesWidgets(final IGlobeApplication application,
                                        final IGlobeLayer layer,
                                        final List<? extends ILayerAttribute<?>> layerAttributes) {
      if (layerAttributes == null) {
         return;
      }

      boolean firstAttributeOnPanel = true;

      for (final ILayerAttribute<?> attribute : layerAttributes) {
         if (!attribute.isVisible()) {
            continue;
         }

         final GPair<Component, EventListener> widget = attribute.createWidget(application, layer);
         if (widget == null) {
            continue;
         }


         if (firstAttributeOnPanel) {
            firstAttributeOnPanel = false;
            final Component[] components = _layerPropertiesPanel.getComponents();
            if ((components.length > 0) && (!(components[components.length - 1] instanceof JToolBar))) {
               _layerPropertiesPanel.add(new JSeparator(SwingConstants.HORIZONTAL), "growx, wrap, span 2");
            }
         }

         _widgetsInLayerPropertiesPanel.add(new GTriplet<IGlobeLayer, ILayerAttribute<?>, GPair<Component, EventListener>>(layer,
                  attribute, widget));
         final String label = attribute.getLabel();
         if (label == null) {
            //            _layerPropertiesPanel.add(widget._first, "wrap, gap 3, span 2");
            _layerPropertiesPanel.add(widget._first, "growx, wrap, span 2");
         }
         else {
            _layerPropertiesPanel.add(new JLabel(application.getTranslation(label)), "gap 3");
            _layerPropertiesPanel.add(widget._first, "left, wrap");
         }
      }

   }


   @Override
   public List<ILayerAttribute<?>> getLayerAttributes(final IGlobeApplication application,
                                                      final IGlobeLayer layer) {
      final GBooleanLayerAttribute visible = new GBooleanLayerAttribute("Visible", "Enabled") {
         @Override
         public boolean isVisible() {
            return true;
         }


         @Override
         public Boolean get() {
            return layer.isEnabled();
         }


         @Override
         public void set(final Boolean value) {
            layer.setEnabled(value);
         }
      };

      //      final GSelectionLayerAttribute crs = new GSelectionLayerAttribute("CRS", "CRS", GProjection.getEPSGProjections()) {
      //
      //         @Override
      //         public boolean isVisible() {
      //            return true;
      //         }
      //
      //
      //         @Override
      //         public GProjection get() {
      //            return layer.getProjection();
      //         }
      //
      //
      //         @Override
      //         public void set(final Object value) {
      //            layer.setProjection((GProjection) value);
      //         }
      //      };

      return GCollections.createList(visible/*, crs*/);

   }


   @Override
   public void initializeTranslations(final IGlobeApplication application) {
      application.addTranslation("es", "Layers", "Capas");
      application.addTranslation("es", "Add a layer", "Agregar una capa");
      application.addTranslation("es", "Zoom to layer", "Zoom a la capa");
      application.addTranslation("es", "Remove layer", "Remover la capa");
      application.addTranslation("es", "Move up", "Mover hacia arriba");
      application.addTranslation("es", "Move down", "Mover hacia abajo");
      application.addTranslation("es", "Visible", "Visible");
      application.addTranslation("es", "CRS", "CRS");
      application.addTranslation("es", "Select a layer", "Elija una capa");
   }

}
