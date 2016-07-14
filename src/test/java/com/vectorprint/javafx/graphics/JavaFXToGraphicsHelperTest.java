/*
 * Copyright 2016 VectorPrint.
 *
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
 */
package com.vectorprint.javafx.graphics;

import java.awt.image.BufferedImage;
import java.io.File;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javax.imageio.ImageIO;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Eduard Drenth at VectorPrint.nl
 */
public class JavaFXToGraphicsHelperTest {

   public JavaFXToGraphicsHelperTest() {
   }

   @Test
   public void testDraw() throws Exception {
      Scene scene = new Scene(new Group(), 1280, 960);

      new JFXPanel(); // needed because of "Toolkit not initialized"

      //defining the axes
      final NumberAxis xAxis = new NumberAxis();
      final NumberAxis yAxis = new NumberAxis();
      xAxis.setLabel("Number of Month");
      //creating the chart
      final LineChart<Number, Number> lineChart
          = new LineChart<Number, Number>(xAxis, yAxis);

      lineChart.setTitle("Stock Monitoring, 2010");
      //defining a series
      XYChart.Series series = new XYChart.Series();
      series.setName("My portfolio");
      //populating the series with data
      series.getData().add(new XYChart.Data(1, 23));
      series.getData().add(new XYChart.Data(2, 14));
      series.getData().add(new XYChart.Data(3, 15));
      series.getData().add(new XYChart.Data(4, 24));
      series.getData().add(new XYChart.Data(5, 34));
      series.getData().add(new XYChart.Data(6, 36));
      series.getData().add(new XYChart.Data(7, 22));
      series.getData().add(new XYChart.Data(8, 45));
      series.getData().add(new XYChart.Data(9, 43));
      series.getData().add(new XYChart.Data(10, 17));
      series.getData().add(new XYChart.Data(11, 29));
      series.getData().add(new XYChart.Data(12, 25));

      lineChart.getData().add(series);
      lineChart.setPrefSize(1200, 900);

      ((Group) scene.getRoot()).getChildren().add(lineChart);

      BufferedImage img = new JavaFXSceneToGraphics2D().transform(scene, BufferedImage.TYPE_INT_ARGB);

      File file = new File("target/scene.png");
      Assert.assertTrue("deleting " + file.getPath() + " failed", !file.exists() || file.delete());

      ImageIO.write(img, "png", file);

      Assert.assertTrue("writing scene to " + file.getPath() + " failed", file.length() > 10);

   }

}
