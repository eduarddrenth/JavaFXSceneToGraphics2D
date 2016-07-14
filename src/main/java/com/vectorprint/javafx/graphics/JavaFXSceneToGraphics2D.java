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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;

/**
 * Helper that can plot a JavaFX Scenes on a Graphics2D, uses {@link Scene#snapshot(javafx.scene.image.WritableImage) }.
 *
 * @author Eduard Drenth at VectorPrint.nl
 */
public class JavaFXSceneToGraphics2D {

   public void draw(Graphics2D graphics2D, Scene scene) throws InterruptedException, ExecutionException {

      Callable<Boolean> c = new Callable<Boolean>() {
         @Override
         public Boolean call() throws Exception {
            BufferedImage fromFXImage = SwingFXUtils.fromFXImage(scene.snapshot(null), null);

            graphics2D.drawImage(fromFXImage, null, 0, 0);

            graphics2D.dispose();

            return true;
         }
      };

      RunnableFuture<Boolean> rf = new FutureTask<Boolean>(c);

      Platform.runLater(rf);

      rf.get();

   }

   public BufferedImage transform(Scene scene, int bufferedImageType) throws InterruptedException, ExecutionException {
      BufferedImage img = new BufferedImage(Math.round((float) scene.getWidth()), Math.round((float) scene.getHeight()), bufferedImageType);
      draw(img.createGraphics(), scene);
      return img;
   }

}
