/*
 * Authored by Bennett Liu on September 20th, 2019
 * 
 * NetworkTrainer.java implements a trainer of the feed-forward multi-layer perceptron network defined in Network.java. 
 * Given a training set and training parameters, NetworkTrainer minimizes the error of the perceptron on the training set.
 * 
 * Contains the following methods:
 * 
 * Method                  |  Description
 * ------------------------|---------------------
 * NetworkTrainer          |  A constructor for creating a Network, given a network and a training set of doubles.
 * NetworkTrainer          |  A constructor for creating a Network, given a network and a training set of image inputs and double outputs.
 * NetworkTrainer          |  A constructor for creating a Network, given a network and a training set of images.
 * calcError               |  Calculates the total error for the whole training set.
 * train                   |  Runs training steps while certain conditions are met.
 * adaptiveImprove         |  Runs adaptive training
 * printResults            |  Prints information on each training case.
 * getNetwork              |  Returns the current network.
 */

import java.util.*;

public class NetworkTrainer
{
   Network network;              // The network to be trained
   int trainingCases;            // The number of training cases
   double trainingInputs[][];    // The inputs to be trained on
   double trainingOutputs[][];   // The outputs to be trained on

   double error;                 // The network's current error
   double trainingFactor;        // The training factor (lambda)
   double adaptConstant;         // The adaptive factor, used to modify lambda

   /*
    * The Network constructor creates a new NetworkTrainer, given a network and double training inputs/outputs.
    */
   public NetworkTrainer(Network initialNetwork, double inputs[][], double outputs[][])
   {
      network = initialNetwork;
      trainingCases = inputs.length;
      trainingInputs = inputs;
      trainingOutputs = outputs;
      error = calcError();

      return;
   }

   /*
    * The Network constructor creates a new NetworkTrainer, given a network, image file name training inputs, and double training outputs.
    */
   public NetworkTrainer(Network initialNetwork, String inputFilenames[], double outputs[][])
   {
      PelGetter pelGetter = new PelGetter();

      network = initialNetwork;
      trainingCases = inputFilenames.length;
      trainingInputs = new double[trainingCases][network.inputs];
      for (int i = 0; i < trainingCases; i++)
      {
         trainingInputs[i] = pelGetter.getPels(inputFilenames[i]);
      }
      trainingOutputs = outputs;
      error = calcError();

      return;
   }

   /*
    * The Network constructor creates a new NetworkTrainer, given a network and image file name training inputs/outputs.
    */
   public NetworkTrainer(Network initialNetwork, String inputFilenames[], String outputFilenames[])
   {
      PelGetter pelGetter = new PelGetter();

      network = initialNetwork;
      trainingCases = inputFilenames.length;

      trainingInputs = new double[trainingCases][network.inputs];
      trainingOutputs = new double[trainingCases][network.inputs];
      for (int i = 0; i < trainingCases; i++)
      {
         trainingInputs[i] = pelGetter.getPels(inputFilenames[i]);
         trainingOutputs[i] = pelGetter.getPels(outputFilenames[i]);
      }
      error = calcError();
      
      return;
   }

   /*
    * calcError returns the total error when the network is run for all input-output pairs.
    */
   private double calcError()
   {
      double totalError = 0.0;
      double diff;
      for (int i = 0; i < trainingCases; i++)
      {
         double[] results = network.eval(trainingInputs[i]);         // Get results

         for (int j = 0; j < network.outputs; j++)                   // Calculate error for given training case
         {
            diff = (trainingOutputs[i][j] - results[j]);
            totalError += (diff * diff);
         }
      }
      totalError /= 2.0;                                             // This halving of error is specified in design doc 1.

      return totalError;
   }  // private double calcError()

   /*
    * train runs multiple steps while some conditions are still met.
    */
   public void train(double initLambda, double adaptConst, int maxSteps, double minError, double minLambda, int updatePeriod, int writePeriod)
   {
      trainingFactor = initLambda;                                   // Set training factors
      adaptConstant = adaptConst;

      int step = 0;
      boolean improved = true;
      while ((step < maxSteps) && (error >= minError) && (trainingFactor >= minLambda) && (adaptConstant != 1 || improved))
      {
         step++;

         improved = adaptiveImprove(minLambda);                                        // Run an adaptive step and save the result

         if ((updatePeriod > 0) && ((step % updatePeriod) == 0))              // Saves and prints output every updatePeriod steps
            printResults();
         if ((writePeriod > 0) && ((step % writePeriod) == 0))                // Saves and prints output every updatePeriod steps
            network.exportNet("logs/" + (new Date()).getTime() + ".txt");
      }  // while ((step < maxSteps) && (error >= minError) && (trainingFactor >= minLambda) && (adaptConstant != 1 || improved))

      System.out.println();                                                   // Print the reason(s) for termination
      System.out.println(String.format("Terminated after %d steps", step));
      if (step >= maxSteps)
         System.out.println(String.format("Steps passed limit of %d", maxSteps));
      if (error < minError)
         System.out.println(String.format("Error fell below %.15f", minError));
      if (adaptConstant == 1 && !improved)
         System.out.println("Was not able to improve error.");
      if (trainingFactor < minLambda)
         System.out.println(String.format("Training factor (lambda) fell below %.15f", minLambda));
      System.out.println();
   }  // public void train(double initLambda, double adaptConst, int maxSteps, double minError, double minLambda, int updatePeriod, int writePeriod)

   /*
    * adaptiveImprove runs a single adaptive training step for each training case. It saves initial weights 
    * and attempts these steps simultaneously. If the step improves error, the training factor is increased, 
    * otherwise the weights are rolled back and training factor is decreased. Returns whether error was improved.
    */
   private boolean adaptiveImprove(double minLambda)
   {
      boolean improved;
      double newError = 0.0;
      double oldWeights[][][] = new double[network.layers - 1][network.maxNodes][network.maxNodes];

      for (int layer = 0; layer < network.layers - 1; layer++)                   // Save old weights in case of roll back
      {
         for (int i = 0; i < network.maxNodes; i++)
         {
            for (int j = 0; j < network.maxNodes; j++)
            {
               oldWeights[layer][i][j] = network.weights[layer][i][j];
            }
         }
      }
      for (int trainingCase = 0; trainingCase < trainingCases; trainingCase++)  // Improve for each training case
         network.step(trainingInputs[trainingCase], trainingOutputs[trainingCase], trainingFactor);

      newError = calcError();                // Calculate the new error
      if (newError < error)                  // If steps improved error
      {
         error = newError;                   // Update error
         trainingFactor *= adaptConstant;    // Make a bigger step next time
         improved = true;
      }
      else                                   // If steps worsened error
      {
         network.setWeights(oldWeights);     // Roll back weights
         trainingFactor /= adaptConstant;    // Make a smaller step next time
         improved = false;
      }

      return improved;                       // Return whether the error improved
   }  // private boolean adaptiveImprove(double minLambda)

   /*
    * printResults prints the network's results for each input-output pair, the training factor, and total error.
    */
   public void printResults()
   {
      System.out.println();
      // for (int i = 0; i < trainingCases; i++)                              // For each training case
      // {
      //    System.out.println(String.format("Case %d:", i + 1));             // Print the number

      //    System.out.println("Inputs:");                                    // Print the inputs
      //    for (int j = 0; j < network.inputs; j++)
      //    {
      //       System.out.print(String.format(" %.15f", trainingInputs[i][j]));
      //    }
      //    System.out.println();

      //    System.out.println("Results:");                                   // Calculate and print the network's outputs
      //    double results[] = network.eval(trainingInputs[i]);
      //    for (int j = 0; j < network.outputs; j++)
      //    {
      //       System.out.print(String.format(" %.15f", results[j]));
      //    }
      //    System.out.println();

      //    // PelGetter pelGetter = new PelGetter();
      //    // pelGetter.makeBMP(results, "_.bmp");

      //    System.out.println("Answers:");                                   // Calculate and print the correct outputs
      //    for (int j = 0; j < network.outputs; j++)
      //    {
      //       System.out.print(String.format(" %.15f", trainingOutputs[i][j]));
      //    }
      //    System.out.println();

      //    System.out.println();
      // }  // for (int i = 0; i < trainingCases; i++)

      System.out.println(String.format("Lambda: %.15f", trainingFactor));  // Print the training factor
      System.out.println(String.format("Total Error: %.15f", error));      // Print the total error
      System.out.println();

      return;
   }  // public void printResults()

   /*
    * getNetwork returns the current network.
    */
   public Network getNetwork()
   {
      return network;
   }
}  // public class NetworkTrainer