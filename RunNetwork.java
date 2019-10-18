/*
 * Authored by Bennett Liu on October 15th, 2019
 * The RunNetwork contains a main function imports a Network from a file, containing an exported Network. 
 * It then takes in test cases, which are run on the imported Network.
 */

import java.util.*;
import java.io.*;

public class RunNetwork {
   public static void main(String[] args) {
      // Network and test case parameters
      int inputNodes;                        // The number of inputs
      int outputNodes;                       // The number of outputs
      int testCases;                         // The number of test cases
      double testInputs[][];                 // The inputs for each test case
      double testOutputs[][];                // The outputs for each test case

      Scanner in = new Scanner(System.in);   // Create scanner to take input from console 

      // Import network from file
      System.out.println("Enter the file that you'd like to import your network from: ");
      String fileName = in.next();
      Network network = new Network(new File(fileName));

      inputNodes = network.inputs;
      outputNodes = network.outputs;

      /*
       * Read test case inputs, namely:
       * 
       * The number of test cases
       * Each test case's inputs
       * Each test case's outputs
       */
      System.out.println("How many test cases: ");
      testCases = in.nextInt();

      testInputs = new double[testCases][inputNodes];
      testOutputs = new double[testCases][outputNodes];
      for (int i = 1; i <= testCases; i++) 
      {
         System.out.println(String.format("Test Case %d", i));
         for (int j = 1; j <= inputNodes; j++)
         {
            System.out.println(String.format("Input %d:", j));
            testInputs[i - 1][j - 1] = in.nextDouble();
         }
         for (int j = 1; j <= outputNodes; j++)
         {
            System.out.println(String.format("Output %d:", j));
            testOutputs[i - 1][j - 1] = in.nextDouble();
         }
      }  // for (int i = 1; i <= testCases; i++)

      // Initialize trainer and evaluate the initial network for all test cases
      NetworkTrainer trainer = new NetworkTrainer(network, testInputs, testOutputs);
      trainer.printResults();

      in.close();          // Close scanner

      return;
    } // public static void main(String[] args)
 } // public class RunNetwork