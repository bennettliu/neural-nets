/*
 * Authored by Bennett Liu on Nov 18th, 2019
 */

import java.io.*;

public class BMPNet 
{
   public static void main(String[] args) 
   {
      int inputNodes = 100;
      double outputs[];                // The outputs for each test case
      double trainingInputs[][];             // The inputs for each training case
      double trainingOutputs[][];            // The outputs for each training case

      String fileName = "networks/Network5_10x10.txt";
      Network network = new Network(new File(fileName));

      PelGetter pelGetter = new PelGetter("10x10.bmp", "10x10out2.bmp");
      double d[] = pelGetter.getPels();
      trainingInputs = new double[1][inputNodes];
      trainingOutputs = new double[1][inputNodes];
      trainingInputs[0] = d;
      trainingOutputs[0] = d;

      NetworkTrainer trainer = new NetworkTrainer(network, trainingInputs, trainingOutputs);    // Initialize trainer

      // Train the network with the given parameters
      trainer.train(1.0, 1.0001, 1000000000, 0, 0, 10000, 100000);
      network = trainer.getNetwork();                    // Retrieve trained network

      outputs = network.eval(d);

      pelGetter.makeBMP(outputs);

      return;
   } // public static void main(String[] args)
 } // public class Main