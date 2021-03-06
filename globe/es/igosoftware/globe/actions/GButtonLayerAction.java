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


package es.igosoftware.globe.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JMenuItem;

import es.igosoftware.globe.IGlobeApplication;


public abstract class GButtonLayerAction
         extends
            GLayerAction {


   protected GButtonLayerAction(final String label,
                                final char mnemonic,
                                final Icon icon,
                                final boolean showOnToolBar) {
      super(label, mnemonic, icon, showOnToolBar);
   }


   protected GButtonLayerAction(final String label,
                                final Icon icon,
                                final boolean showOnToolBar) {
      super(label, icon, showOnToolBar);
   }


   @Override
   public Component createToolbarWidget(final IGlobeApplication application) {
      final JButton button = GSwingFactory.createToolbarButton(getIcon(), application.getTranslation(getLabel()),
               new ActionListener() {
                  @Override
                  public void actionPerformed(final ActionEvent e) {
                     execute();
                  }
               });

      button.setEnabled(isEnabled());

      return button;
   }


   @Override
   public JMenuItem createMenuWidget(final IGlobeApplication application) {
      final JMenuItem menuItem = new JMenuItem(application.getTranslation(getLabel()), getIcon());

      menuItem.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(final ActionEvent e) {
            execute();
         }
      });
      menuItem.setEnabled(isEnabled());

      return menuItem;
   }


}
