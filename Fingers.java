/*
 * Authored by Bennett Liu on December 11th, 2019
 */

import java.io.*;

public class Fingers 
{
   public static void main(String[] args) 
   {
      // String fileName = "networks/Finger.txt";
      // Network network = new Network(new File(fileName));
      Network network = new Network(2500, new int[]{5000, 5000}, 1, -1, 1);

      int numbers = 5;
      int cases = 1;
      int totalCases = 4;
      String inputImages[] = new String[numbers * cases];
      double trainingOutputs[][] = new double[numbers * cases][1];
      for (int i = 1; i <= cases; i++)
      {
         for (int j = 1; j <= numbers; j++)
         {
            inputImages[((i - 1) * numbers) + (j - 1)] = "SmallGrayBMP/" + Integer.toString(j) + "_" + Integer.toString(i) + ".bmp";
            // trainingOutputs[((i - 1) * cases) + (j - 1)] = new double[]{0.375, 0.375, 0.375, 0.375, 0.375};
            // trainingOutputs[((i - 1) * cases) + (j - 1)][j - 1] = 1.0;
            trainingOutputs[((i - 1) * numbers) + (j - 1)][0] = (j/5);
         }
      }

      NetworkTrainer trainer = new NetworkTrainer(network, inputImages, trainingOutputs);    // Initialize trainer
      trainer.printResults();

      trainer.train(1.0, 4.0, 100000, 0, 0.0001, 1, 10);
      network = trainer.getNetwork();                    // Retrieve trained network

      for (int i = 1; i <= numbers; i++)
      {
         for (int j = cases + 1; j <= totalCases; j++)
         {
            double results[] = network.eval("SmallGrayBMP/" + Integer.toString(i) + "_" + Integer.toString(j) + ".bmp");
            int best = 0;
            for (int k = 1; k < 5; k++) {
               if(results[best] < results[k]) best = k;
            }
            System.out.println(String.format("%d %d %f", i, (best + 1), results[best]));
         }
      }

      return;
   } // public static void main(String[] args)
 } // public class Main