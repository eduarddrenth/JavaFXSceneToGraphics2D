package com.vectorprint.javafx.graphics;

import java.awt.Graphics;
import java.util.concurrent.Semaphore;
import javafx.embed.swing.JFXPanel;

/**
 * Subclass of JFXPanel that will draw on a Graphics object you provide. After drawing {@link Semaphore#release() }
 * will be called to let you know drawing has finished.
 *
 * @author Eduard Drenth at VectorPrint.nl
 */
class GraphicsPanel extends JFXPanel {

   private final Graphics graphics;
   private final Semaphore rendered;

   public GraphicsPanel(Graphics graphics, Semaphore rendered) {
      this.graphics = graphics;
      this.rendered = rendered;
   }

   @Override
   protected void paintComponent(Graphics g) {
      super.paintComponent(graphics);
      rendered.release();
   }

}
