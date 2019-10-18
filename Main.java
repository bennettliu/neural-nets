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
      int inputNodes;                        // The number of inputs in the new network
      int hiddenLayers;                      // The number of hidden layers in the new network
      int hiddenLayerNodes[];                // The number of nodes in each layer of the new network
      int outputNodes;                       // The number of outputs in the new network
      double minWeight;                      // The minimum value for the new network's randomized weights
      double maxWeight;                      // The maximum value for the new network's randomized weights

      int testcases;                         // The number of test cases
      double testInputs[][];                 // The inputs for each test case
      double testOutputs[][];                // The outputs for each test case
      
      double trainingFactor;                 // The initial training factor, lambda
      double adaptFactor;                    // The adaptive lambda factor. Adaptive training can be disabled by setting to 1
      int stepLimit;                         // The maximum number of steps the training algorithm will take
      double errorLimit;                     // The desired error, training will stop if/when this target is met
      int updateSavePeriod;                  // The period of steps at which progress will be reported and the network saved

      Scanner in = new Scanner(System.in);   // Initialize scanner to take input from console

      // Read network inputs
      System.out.println("How many input nodes: ");
      inputNodes = in.nextInt();

      System.out.println("How many hidden layers: ");
      hiddenLayers = in.nextInt();

      hiddenLayerNodes = new int[hiddenLayers];
      for (int i = 1; i <= hiddenLayers; i++) 
      {
         System.out.println(String.format("How many nodes in hidden layer %d: ", i));
         hiddenLayerNodes[i - 1] = in.nextInt();
      }

      System.out.println("How many output nodes: ");
      outputNodes = in.nextInt();

      System.out.println("Minimum initial weight value: ");
      minWeight = in.nextDouble();

      System.out.println("Maximum initial weight value: ");
      maxWeight = in.nextDouble();

      // Read testcases as inputs
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

      // Initialize network with given parameters
      Network network = new Network(inputNodes, hiddenLayerNodes, outputNodes, minWeight, maxWeight);    

      NetworkTrainer trainer = new NetworkTrainer(network, testInputs, testOutputs);   // Initialize trainer
      trainer.printTest();                                                             // Evaluate initial network for all test cases

      if (hiddenLayers == 1)                                                           // Train if contains exactly one hidden layer
      {
         // Read training parameter inputs
         System.out.println("Initial Training Factor (Lambda): ");
         trainingFactor = in.nextDouble();

         System.out.println("Adaptive Training Factor: ");
         adaptFactor = in.nextDouble();

         System.out.println("Training Step Limit: ");
         stepLimit = in.nextInt();

         System.out.println("Training Error Limit: ");
         errorLimit = in.nextDouble();

         System.out.println("Training Update and Save Period: ");
         updateSavePeriod = in.nextInt();

         // Run training with given parameters
         trainer.train(trainingFactor, adaptFactor, stepLimit, errorLimit, updateSavePeriod);

         trainer.printTest();                               // Evaluate the final network for all test cases
         network = trainer.getNetwork();                    // Retrieve trained network                 

         // Save network to file
         System.out.println("Enter the file that you'd like the resulting network to be printed in: ");
         String outputFileName = in.next();
         network.exportNet(outputFileName);
      }
      else
      {
         System.out.println("Can only train for networks with one hidden layer.");
      }
      return;
    }
 }