package com.vectorprint.javafx.graphics;

/*
 * #%L
 * VectorPrintReport4.0
 * %%
 * Copyright (C) 2012 - 2013 VectorPrint
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
//~--- non-JDK imports --------------------------------------------------------
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.concurrent.Semaphore;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javax.swing.JFrame;

/**
 * Helper that can plot a JavaFX Scenes (with known width and height) on a Graphics2D. A JFrame with a JFXPanel are used
 * to accomplish this, you will see the JFrame flashing by. Note that scene is drawn as a bitmap, not as vectors, so for
 * sharp images you may want to scale up before drawing. If you use this functionality outside a JavaFX application you
 * may get a "Toolkit not initialized" exception, instantiating a JFXPanel before creating nodes and scenes solves this.
 *
 * @author Eduard Drenth at VectorPrint.nl
 */
public class JavaFXToGraphicsHelper {

   public static void draw(Graphics2D graphics2D, Scene scene) throws
       InterruptedException {

      float w = (float) scene.getWidth();
      float h = (float) scene.getHeight();

      final Semaphore rendered = new Semaphore(1);
      rendered.acquire();

      JFXPanel panel = new GraphicsPanel(graphics2D, rendered);
      panel.setScene(scene);

      JFrame frame = new JFrame("closes when rendering is complete");
      frame.setSize(Math.round(w), Math.round(h));
      frame.setEnabled(false);
      frame.add(panel);
      frame.setVisible(true);

      rendered.acquire();

      frame.setVisible(false);

      graphics2D.dispose();    // always dispose this

   }

   public static BufferedImage transform(Scene scene, int bufferedImageType) throws InterruptedException {
      BufferedImage img = new BufferedImage(Math.round((float) scene.getWidth()), Math.round((float) scene.getHeight()), bufferedImageType);
      draw(img.createGraphics(), scene);
      return img;
   }

}
