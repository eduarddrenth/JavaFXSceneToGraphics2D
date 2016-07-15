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
import java.awt.image.BufferedImageOp;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;

/**
 * Helper that can plot a JavaFX Scene or Node on a Graphics2D, uses {@link Scene#snapshot(javafx.scene.image.WritableImage)
 * }
 * or {@link Node#snapshot(javafx.scene.SnapshotParameters, javafx.scene.image.WritableImage) }. Before creating scenes
 * and nodes you may need to instantiate a {@link JFXPanel} if you run into "Toolkit not initialized", or apply some
 * other solution to solve this issue. This class is ThreadSafe.
 *
 * @author Eduard Drenth at VectorPrint.nl
 */
public class JavaFXToGraphics2D {

   /**
    * Draws a scene or a node on the provided graphics2D.
    *
    * @param graphics2D
    * @param sceneOrNode
    * @throws InterruptedException
    * @throws ExecutionException
    */
   public void draw(Graphics2D graphics2D, Object sceneOrNode) throws InterruptedException, ExecutionException {
      doDraw(graphics2D, sceneOrNode, null, null);
   }

   /**
    * Draws a scene on the provided graphics2D using BufferedImageOp when provided.
    *
    * @param graphics2D
    * @param scene
    * @param options optional options
    * @throws InterruptedException
    * @throws ExecutionException
    */
   public void draw(Graphics2D graphics2D, Scene scene, BufferedImageOp options) throws InterruptedException, ExecutionException {
      doDraw(graphics2D, scene, null, options);
   }

   /**
    * Draws a node on the provided graphics2D using BufferedImageOp and/or SnapshotParameters when provided.
    *
    * @param graphics2D
    * @param node
    * @param snapshotParameters optional parameters
    * @param options optional options
    * @throws InterruptedException
    * @throws ExecutionException
    */
   public void draw(Graphics2D graphics2D, Node node, SnapshotParameters snapshotParameters, BufferedImageOp options) throws InterruptedException, ExecutionException {
      doDraw(graphics2D, node, snapshotParameters, options);
   }

   /**
    * Last two parameters are optional, snapshotParameters only for drawing a Node.
    *
    * @param graphics2D
    * @param sceneOrNode
    * @param snapshotParameters
    * @param options
    * @throws InterruptedException
    * @throws ExecutionException
    */
   private void doDraw(Graphics2D graphics2D, Object sceneOrNode, SnapshotParameters snapshotParameters, BufferedImageOp options) throws InterruptedException, ExecutionException {

      RunnableFuture<Void> rf = new FutureTask<Void>(getFor(graphics2D, sceneOrNode, snapshotParameters, options));

      Platform.runLater(rf);

      rf.get();

   }

   private Callable<Void> getFor(Graphics2D graphics2D, Object sceneOrNode, SnapshotParameters snapshotParameters, BufferedImageOp options) {
      if (sceneOrNode instanceof Scene || sceneOrNode instanceof Node) {
         if (sceneOrNode instanceof Node && snapshotParameters != null) {
            throw new IllegalArgumentException(String.format("snapshotParameters Node", sceneOrNode));
         }
         return new Callable<Void>() {
            @Override
            public Void call() throws Exception {
               BufferedImage fromFXImage = SwingFXUtils.fromFXImage(
                   sceneOrNode instanceof Scene ? ((Scene) sceneOrNode).snapshot(null) : ((Node) sceneOrNode).snapshot(snapshotParameters, null), null);

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
    */
   public static BufferedImage fromScene(Scene scene, int bufferedImageType) {
      return new BufferedImage(Math.round((float) scene.getWidth()), Math.round((float) scene.getHeight()), bufferedImageType);
   }

   /**
    * creates an image of a scene using {@link Node#getBoundsInLocal() }.
    *
    * @param node
    * @param bufferedImageType
    * @return
    */
   public static BufferedImage fromNode(Node node, int bufferedImageType) {
      return new BufferedImage(
          Math.round((float) node.boundsInLocalProperty().get().getWidth()),
          Math.round((float) node.boundsInLocalProperty().get().getHeight()),
          bufferedImageType);
   }

   /**
    * draws a scene on the provided BufferedImage
    *
    * @param scene
    * @param img
    * @throws InterruptedException
    * @throws ExecutionException
    */
   public void draw(Scene scene, BufferedImage img) throws InterruptedException, ExecutionException {
      draw(img.createGraphics(), scene);
   }

   /**
    * draws a node on the provided BufferedImage
    *
    * @param node
    * @param img
    * @throws InterruptedException
    * @throws ExecutionException
    */
   public void draw(Node node, BufferedImage img) throws InterruptedException, ExecutionException {
      draw(img.createGraphics(), node);
   }
}
