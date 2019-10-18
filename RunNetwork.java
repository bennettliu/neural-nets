/*
 * Authored by Bennett Liu on October 15th, 2019
 */
import java.util.*;
import java.io.*;

public class RunNetwork {
   public static void main(String[] args) {
      Network n = new Network(new File("logs/startPoint.txt"));
      // Network and test case parameters
      int inputNodes;                        // The number of inputs
      int outputNodes;                       // The number of outputs
      int testcases;                         // The number of test cases
      double testInputs[][];                 // The inputs for each test case
      double testOutputs[][];                // The outputs for each test case

      // Take network inputs
      Scanner in = new Scanner(System.in);      
      System.out.println("How many input nodes: ");
      inputNodes = in.nextInt();

      System.out.println("How many output nodes: ");
      outputNodes = in.nextInt();

      // Take testcase as inputs
      System.out.println("How many test cases: ");
      testcases = in.nextInt();

      testInputs = new double[testcases][inputNodes];
      testOutputs = new double[testcases][outputNodes];
      for (int i = 1; i <= testcases; i++) 
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
      }

      // Create network
      Network network = new Network(new File("logs/endPoint.txt"));

      // Initialize trainer and evaluate the initial network for all test cases
      NetworkTrainer trainer = new NetworkTrainer(network, testInputs, testOutputs);
      trainer.printTest();
      return;
    }
 }