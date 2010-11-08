/*
 * Cáceres 3D
 * 
 * Copyright (c) 2008 Junta de Extremadura.
 * 
 * Permission is hereby granted, free of charge, to any
 * person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the
 * Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice
 * shall be included in all copies or substantial portions of
 * the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
 * OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 * 
 * Funded by European Union. FEDER program.
 * Developed by: IGO SOFTWARE, S.L.
 * 
 * For more information, contact: 
 * 
 *    Junta de Extremadura
 *    Consejería de Cultura y Turismo
 *    C/ Almendralejo 14 Mérida
 *    06800 Badajoz
 *    SPAIN
 * 
 *    Tel: +34 924007009
 *    http://www.culturaextremadura.com
 * 
 *   or
 * 
 *    IGO SOFTWARE, S.L.
 *    Calle Santiago Caldera Nro 4
 *    Cáceres
 *    Spain
 *    Tel: +34 927 629 436
 *    e-mail: support@igosoftware.es
 *    http://www.igosoftware.es
 */

package es.igosoftware.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public final class SoundManager {
   private final static Logger logger = Logger.instance();


   public interface SoundPlayingHandler {
      public void soundStopped();
   }

   private final static SoundManager instance = new SoundManager();


   public static SoundManager instance() {
      return instance;
   }


   // private constructor, use singleton
   private SoundManager() {

   }

   private Clip    clipLine;
   private boolean isPlaying  = false;
   private boolean isStopping = false;


   public void playSound(final String soundName) {
      playSounds(new String[] { soundName });
   }


   public synchronized void playSoundInBackground(final String soundName,
                                                  final boolean loop) {
      isPlaying = true;
      isStopping = false;

      if (!tryToPlaySoundInBackground(soundName, loop)) {
         stopSound();
      }
   }


   public void playSounds(final String[] soundsNames) {
      final List<String> soundsNamesList = new ArrayList<String>();

      for (final String eachSoundName : soundsNames) {
         soundsNamesList.add(eachSoundName);
      }

      playSounds(soundsNamesList);
   }


   public void playSounds(final List<String> soundsNames) {
      playSounds(soundsNames, null);
   }

   private Runnable onFinishHandler;


   public synchronized void playSounds(final List<String> soundsNames,
                                       final Runnable onFinish) {
      isPlaying = true;
      isStopping = false;
      onFinishHandler = onFinish;

      if (soundsNames.isEmpty()) {
         stopSound();
         return;
      }

      final Iterator<String> soundsNamesIterator = soundsNames.iterator();

      final SoundPlayingHandler handler = new SoundPlayingHandler() {
         @Override
         public void soundStopped() {

            if (isStopping) {
               return;
            }

            if (soundsNamesIterator.hasNext()) {
               GUtils.delay(250);

               if (!tryToPlaySound(soundsNamesIterator.next(), this)) {
                  stopSound();
               }
            }
            else {
               stopSound();
            }
         }
      };

      if (!tryToPlaySound(soundsNamesIterator.next(), handler)) {
         stopSound();
      }
   }


   private boolean tryToPlaySound(final String soundFileName,
                                  final SoundPlayingHandler handler) {
      Exception ex = null;
      try {
         rawPlaySound(soundFileName, handler);
         return true;
      }
      catch (final UnsupportedAudioFileException e) {
         ex = e;
      }
      catch (final IOException e) {
         ex = e;
      }
      catch (final LineUnavailableException e) {
         ex = e;
      }
      catch (final IllegalArgumentException e) {
         ex = e;
      }

      logger.severe("Error trying to play: " + soundFileName);
      logger.severe(ex);

      stopSound();
      return false;
   }


   private boolean tryToPlaySoundInBackground(final String soundFileName,
                                              final boolean loop) {
      Exception ex = null;
      try {
         rawPlaySoundInBackground(soundFileName, loop);
         return true;
      }
      catch (final UnsupportedAudioFileException e) {
         ex = e;
      }
      catch (final IOException e) {
         ex = e;
      }
      catch (final LineUnavailableException e) {
         ex = e;
      }
      catch (final IllegalArgumentException e) {
         ex = e;
      }

      logger.severe("Error trying to play: " + soundFileName);
      logger.severe(ex);

      stopSound();
      return false;
   }


   private class SoundBackgroundThread
            extends
               Thread {
      private final AudioInputStream audioInputStream;
      private SourceDataLine         source;
      private int                    total;
      private final boolean          loop;

      final private String           soundFileName;


      // private boolean forceStop = false;

      private SoundBackgroundThread(final String soundFileName1,
                                    final boolean loop1) throws IOException, LineUnavailableException,
               UnsupportedAudioFileException {

         setName("Background SoundManager Thread");
         setDaemon(true);
         loop = loop1;
         soundFileName = soundFileName1;
         final File soundFile = new File(soundFileName);

         audioInputStream = AudioSystem.getAudioInputStream(soundFile);
         final AudioFormat format = audioInputStream.getFormat(); // This need's for reconstruct the audiofile
         final DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

         try {
            if (AudioSystem.isLineSupported(info)) {
               source = (SourceDataLine) AudioSystem.getLine(info);
               total = audioInputStream.available();
               source.open(format);

            }
            else {
               logger.severe("DataLine not supported");
            }
         }
         catch (final IllegalArgumentException e) {
            logger.severe(e);
            stopSound();
         }
      }


      @Override
      public void run() {

         if (source == null) {
            return;
         }

         if (source.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            final FloatControl masterFC = (FloatControl) source.getControl(FloatControl.Type.MASTER_GAIN);
            masterFC.setValue(masterFC.getMaximum());
         }

         final int buffersize = source.getBufferSize() /* 10240 */;
         final byte[] data = new byte[buffersize];
         source.start();

         int read = buffersize;
         // Well, now play!!!
         while (!isStopping && (read < total)) {
            try {
               read = audioInputStream.read(data, 0, data.length);
            }
            catch (final IOException e) {
               e.printStackTrace();
               read = -1;
            }
            if (read == -1) {
               break; // End of audiostream.
            }
            source.write(data, 0, read);
            GUtils.delay(25);
         }

         if (!isStopping && loop) {
            playSoundInBackground(soundFileName, true);
         }
      }


      private void forceStop() {
         if (source == null) {
            return;
         }

         if (source.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            final FloatControl masterFC = (FloatControl) source.getControl(FloatControl.Type.MASTER_GAIN);
            masterFC.setValue(masterFC.getMinimum());
         }

         source.stop();
      }
   }

   private SoundBackgroundThread backgroundSoundThread;


   private synchronized void rawPlaySoundInBackground(final String soundFileName,
                                                      final boolean loop) throws UnsupportedAudioFileException, IOException,
                                                                         LineUnavailableException {

      final File soundFile = new File(soundFileName);

      final AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
      final AudioFormat format = audioInputStream.getFormat(); // This need's for reconstruct the audiofile
      final DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

      try {
         if (AudioSystem.isLineSupported(info)) {
            backgroundSoundThread = new SoundBackgroundThread(soundFileName, loop);
            backgroundSoundThread.start();
         }
         else {
            logger.severe("DataLine not supported");
         }
      }
      catch (final IllegalArgumentException e) {
         logger.severe(e);
         stopSound();
      }
   }


   private synchronized void rawPlaySound(final String soundFileName,
                                          final SoundPlayingHandler handler) throws UnsupportedAudioFileException, IOException,
                                                                            LineUnavailableException {

      final File soundFile = new File(soundFileName);

      final AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
      final AudioFormat format = audioInputStream.getFormat();
      final DataLine.Info info = new DataLine.Info(Clip.class, format);

      try {
         clipLine = (Clip) AudioSystem.getLine(info);

         if (clipLine.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            final FloatControl masterFC = (FloatControl) clipLine.getControl(FloatControl.Type.MASTER_GAIN);
            masterFC.setValue(masterFC.getMaximum());
         }

         clipLine.addLineListener(new LineListener() {
            @Override
            public void update(final LineEvent event) {
               if (event.getType().equals(LineEvent.Type.STOP)) {
                  clipLine.close();
                  clipLine = null;
               }
               else if (event.getType().equals(LineEvent.Type.CLOSE)) {
                  handler.soundStopped();
               }
            }
         });

         clipLine.start();
         clipLine.open(audioInputStream);
         clipLine.loop(0);

      }
      catch (final IllegalArgumentException e) {
         logger.severe(e);
         stopSound();
         handler.soundStopped();
      }
   }


   public synchronized boolean isPlaying() {
      return isPlaying;
   }


   public synchronized void stopSound() {
      isPlaying = false;
      isStopping = true;

      if (backgroundSoundThread != null) {
         backgroundSoundThread.forceStop();
         backgroundSoundThread = null;
      }

      if (clipLine != null) {
         clipLine.stop();
         clipLine = null;
      }

      if (onFinishHandler != null) {
         onFinishHandler.run();
         onFinishHandler = null;
      }

      GUtils.delay(100);
   }
}
