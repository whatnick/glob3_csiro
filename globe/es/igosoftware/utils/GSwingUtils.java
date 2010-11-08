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


package es.igosoftware.utils;

import java.awt.Component;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JToggleButton;

import org.pushingpixels.substance.api.SubstanceLookAndFeel;


public class GSwingUtils {
   private GSwingUtils() {
   }


   public static JButton createToolbarButton(final Icon icon,
                                             final String label,
                                             final ActionListener actionListener) {

      final JButton button;
      if (icon == null) {
         button = new JButton(label);
      }
      else {
         button = new JButton(icon);
         button.setToolTipText(label);
      }

      button.putClientProperty(SubstanceLookAndFeel.FLAT_PROPERTY, Boolean.TRUE);
      if (actionListener != null) {
         button.addActionListener(actionListener);
      }

      return button;
   }


   public static Component createToolbarCheckBox(final Icon icon,
                                                 final String label,
                                                 final boolean initState,
                                                 final ActionListener actionListener) {

      final JToggleButton button;
      if (icon == null) {
         button = new JToggleButton(label);
      }
      else {
         button = new JToggleButton(icon);
         button.setToolTipText(label);
      }

      button.putClientProperty(SubstanceLookAndFeel.FLAT_PROPERTY, Boolean.TRUE);
      button.addActionListener(actionListener);

      return button;

      //
      //      final JCheckBoxMenuItem result = new JCheckBoxMenuItem(label, icon, initState);
      //
      //      result.addActionListener(actionListener);
      //
      //      return result;
   }


   public static JMenuItem createMenuItem(final String label,
                                          final Icon icon,
                                          final char mnemonic,
                                          final ActionListener actionListener) {
      final JMenuItem item = new JMenuItem(label, icon);
      if (mnemonic != ' ') {
         item.setMnemonic(mnemonic);
      }
      item.addActionListener(actionListener);
      return item;
   }

}
