/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vectorprint.javafx.graphics;

import java.awt.Graphics;
import java.util.concurrent.Semaphore;
import javafx.embed.swing.JFXPanel;

/**
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
