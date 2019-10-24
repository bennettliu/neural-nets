/*
 * Authored by Bennett Liu on September 18th, 2019
 * 
 * The main class takes console inputs defining the structure of the perceptron model, as found in Network.java to create 
 * a perceptron with randomized weights. Takes console inputs specifying a training set. Then runs and prints the results of 
 * the model on the training set. 
 * 
 * If the model has exactly one hidden layer, it takes inputs specifying training parameters and trains the model accordingly. 
 * When training is completed, it runs the model on the training set and prints the result.
 * 
 * Whether or not the model is trained, the resulting model is written to a specified file.
 */

import java.util.*;

public class Main 
{
   public static void main(String[] args) 
   {
      int inputNodes;                        // The number of inputs in the new network
      int hiddenLayers;                      // The number of hidden layers in the new network
      int hiddenLayerNodes[];                // The number of nodes in each layer of the new network
      int outputNodes;                       // The number of outputs in the new network
      double minWeight;                      // The minimum value for the new network's randomized weights
      double maxWeight;                      // The maximum value for the new network's randomized weights

      int trainingCases;                     // The number of training cases
      double trainingInputs[][];             // The inputs for each training case
      double trainingOutputs[][];            // The outputs for each training case
      
      double trainingFactor;                 // The initial training factor, lambda
      double adaptiveConstant;               // The adaptive lambda factor. Adaptive training can be disabled by setting to 1
      int stepLimit;                         // The maximum number of steps the training algorithm will take
      double errorLimit;                     // The desired error, training will stop if/when this target is met
      double trainingFactorLimit;            // The minimum training factor (lambda) for training to run
      int updateSavePeriod;                  // The period of steps at which progress will be reported and the network saved

      Scanner in = new Scanner(System.in);   // Create scanner to take input from console

      /*
       * Read network inputs, namely:
       * 
       * The number of input nodes
       * The number of hidden layers
       * The number of nodes in each hidden layer
       * The number of output nodes
       * The range in which weights will be randomized
       */
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

      System.out.println("Weights will be randomized according to the following values.");
      System.out.println("Minimum initial weight value: ");
      minWeight = in.nextDouble();

      System.out.println("Maximum initial weight value: ");
      maxWeight = in.nextDouble();

      /*
       * Read training case inputs, namely:
       * 
       * The number of training cases
       * Each training case's inputs
       * Each training case's outputs
       */
      System.out.println("How many training cases: ");
      trainingCases = in.nextInt();

      trainingInputs = new double[trainingCases][inputNodes];
      trainingOutputs = new double[trainingCases][outputNodes];
      for (int i = 1; i <= trainingCases; i++)
      {
         System.out.println(String.format("Training Case %d", i));
         for (int j = 1; j <= inputNodes; j++)
         {
            System.out.println(String.format("Input %d:", j));
            trainingInputs[i - 1][j - 1] = in.nextDouble();
         }
         for (int j = 1; j <= outputNodes; j++)
         {
            System.out.println(String.format("Output %d:", j));
            trainingOutputs[i - 1][j - 1] = in.nextDouble();
         }
      }  // for (int i = 1; i <= trainingCases; i++)

      // Create network with given parameters
      Network network = new Network(inputNodes, hiddenLayerNodes, outputNodes, minWeight, maxWeight);    

      NetworkTrainer trainer = new NetworkTrainer(network, trainingInputs, trainingOutputs);    // Initialize trainer
      trainer.printResults();                                        // Evaluate initial network for all training cases

      if (hiddenLayers == 1)                                         // Train if contains exactly one hidden layer
      {
         /*
          * Read training case inputs, namely:
          *
          * The initial training factor
          * The adaptive training constant
          * The maximum number of steps
          * The ceiling of the desired error
          * The period of steps at which user is updated and saves will be executed
          */
         System.out.println("Initial Training Factor (Lambda): ");
         trainingFactor = in.nextDouble();

         System.out.println("Adaptive Training Constant: ");
         adaptiveConstant = in.nextDouble();

         System.out.println("Training Step Limit: ");
         stepLimit = in.nextInt();

         System.out.println("Training Error Limit: ");
         errorLimit = in.nextDouble();

         System.out.println("Training Factor Limit: ");
         trainingFactorLimit = in.nextDouble();

         System.out.println("Training Update and Save Period: ");
         updateSavePeriod = in.nextInt();

         // Train the network with the given parameters
         trainer.train(trainingFactor, adaptiveConstant, stepLimit, errorLimit, trainingFactorLimit, updateSavePeriod);

         trainer.printResults();                            // Evaluate the final network for all training cases
         network = trainer.getNetwork();                    // Retrieve trained network
      }  // if (hiddenLayers == 1)
      else
      {
         System.out.println("Can only train for networks with one hidden layer.");
      }

      // Save network to file
      System.out.println("Enter the file that you'd like the resulting network to be printed in: ");
      String outputFileName = in.next();
      network.exportNet(outputFileName);
      
      in.close();          // Close scanner

      return;
   } // public static void main(String[] args)
 } // public class Main