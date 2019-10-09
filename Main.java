/*
 * Authored by Bennett Liu on September 18th, 2019
 * 
 * The main class takes inputs defining the structure of the perceptron model, 
 * as found in Network.java and specifying a test set. It then trains the model, 
 * provided it has one hidden layer and one output node.
 */
import java.util.*;

public class Main {
   public static void main(String[] args) {
      // Network and test case parameters
      int inputNodes;                        // The number of inputs
      int hiddenLayers;                      // The number of hidden layers
      int hiddenLayerNodes[];                // How many nodes are in each layer
      int outputNodes;                       // The number of outputs
      int testcases;                         // The number of test cases
      double testInputs[][];                 // The inputs for each test case
      double testOutputs[][];                // The outputs for each test case

      // Take network inputs
      Scanner in = new Scanner(System.in);      
      System.out.println("How many input nodes: ");
      inputNodes = in.nextInt();

      System.out.println("How many hidden layers: ");
      hiddenLayers = in.nextInt();

      hiddenLayerNodes = new int[hiddenLayers];
      for (int  i = 1; i <= hiddenLayers; i++) 
      {
         System.out.println(String.format("How many nodes in hidden layer %d: ", i));
         hiddenLayerNodes[i - 1] = in.nextInt();
      }

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
      Network network = new Network(inputNodes, hiddenLayerNodes, outputNodes, -1, 1);

      // Initialize trainer and evaluate the initial network for all test cases
      NetworkTrainer trainer = new NetworkTrainer(network, testInputs, testOutputs, 0.1, 2);
      trainer.printTest();
      network.exportNet("logs/startPoint.txt");

      if (hiddenLayers == 1)
      {
         trainer.train(10000000, 0.0001, 50000);            // Train the network

         trainer.printTest();                               // Evaluate the final network for all test cases
         network = trainer.getNetwork();
         network.exportNet("logs/endPoint.txt");
      }
      else
      {
         System.out.println("Can't train for that case");
      }
      return;
    }
 }