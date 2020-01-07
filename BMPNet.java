/*
 * Authored by Bennett Liu on November 18th, 2019
 */

import java.io.*;

public class BMPNet 
{
   public static void main(String[] args) 
   {
      String fileName = "networks/Network5_10x10.txt";
      Network network = new Network(new File(fileName));

      String inputImages[] = new String[]{"10x10.bmp"};
      String outputImages[] = new String[]{"10x10.bmp"};

      NetworkTrainer trainer = new NetworkTrainer(network, inputImages, outputImages);    // Initialize trainer

      // Train the network with the given parameters
      trainer.train(1.0, 1.0001, 1000000000, 0, 0, 10000, 100000);
      network = trainer.getNetwork();                    // Retrieve trained network

      return;
   } // public static void main(String[] args)
 } // public class Main