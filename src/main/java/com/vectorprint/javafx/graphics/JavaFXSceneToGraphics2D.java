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
import javafx.embed.swing.JFXPanel;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.Scene;

/**
 * Helper that can plot a JavaFX Scene or Node on a Graphics2D, uses {@link Scene#snapshot(javafx.scene.image.WritableImage)
 * }
 * or {@link Node#snapshot(javafx.scene.SnapshotParameters, javafx.scene.image.WritableImage) }. Before creating scenes
 * and nodes you may need to instantiate a {@link JFXPanel} if you run into "Toolkit not initialized", or apply some
 * other solution to solve this issue.
 *
 * @author Eduard Drenth at VectorPrint.nl
 */
public class JavaFXSceneToGraphics2D {

   public void draw(Graphics2D graphics2D, Object sceneOrNode) throws InterruptedException, ExecutionException {

      RunnableFuture<Void> rf = new FutureTask<Void>(getFor(graphics2D, sceneOrNode));

      Platform.runLater(rf);

      rf.get();

   }

   private Callable<Void> getFor(Graphics2D graphics2D, Object sceneOrNode) {
      if (sceneOrNode instanceof Scene || sceneOrNode instanceof Node) {
         return new Callable<Void>() {
            @Override
            public Void call() throws Exception {
               BufferedImage fromFXImage = SwingFXUtils.fromFXImage(
                   sceneOrNode instanceof Scene ? ((Scene) sceneOrNode).snapshot(null) : ((Node) sceneOrNode).snapshot(null, null), null);

               graphics2D.drawImage(fromFXImage, null, 0, 0);

               graphics2D.dispose();

               return null;
            }
         };
      } else {
         throw new IllegalArgumentException(String.format("%s is not a Scene or Node", sceneOrNode));
      }
   }

   /**
    * creates an image of a scene using {@link Scene#getWidth() } and {@link Scene#getHeight() }.
    *
    * @param scene
    * @param bufferedImageType
    * @return
    * @throws InterruptedException
    * @throws ExecutionException
    */
   public BufferedImage transform(Scene scene, int bufferedImageType) throws InterruptedException, ExecutionException {
      BufferedImage img = new BufferedImage(Math.round((float) scene.getWidth()), Math.round((float) scene.getHeight()), bufferedImageType);
      draw(img.createGraphics(), scene);
      return img;
   }

   /**
    * creates an image of a scene using {@link Node#getBoundsInLocal() }.
    *
    * @param node
    * @param bufferedImageType
    * @return
    * @throws InterruptedException
    * @throws ExecutionException
    */
   public BufferedImage transform(Node node, int bufferedImageType) throws InterruptedException, ExecutionException {
      BufferedImage img = new BufferedImage(
          Math.round((float) node.boundsInLocalProperty().get().getWidth()),
          Math.round((float) node.boundsInLocalProperty().get().getHeight()),
          bufferedImageType);
      draw(img.createGraphics(), node);
      return img;
   }
}
