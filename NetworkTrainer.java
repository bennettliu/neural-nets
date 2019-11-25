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
 * NetworkTrainer          |  A constructor for creating a Network, given a network and a training set.
 * calcError               |  Calculates the total error for the whole training set.
 * train                   |  Runs training steps while certain conditions are met.
 * adaptiveImprove         |  Runs adaptive training
 * improve                 |  Makes a step for a given training case.
 * printResults            |  Prints information on each training case.
 * getNetwork              |  Returns the current network.
 */

import java.util.*;

public class NetworkTrainer
{
   Network network;              // The network to be trained
   double trainingInputs[][];    // The inputs to be trained on
   double trainingOutputs[][];   // The outputs to be trained on

   double error;                 // The network's current error
   double trainingFactor;        // The training factor (lambda)
   double adaptConstant;         // The adaptive factor, used to modify lambda

   /*
    * The Network constructor creates a new NetworkTrainer, given a network and training inputs/outputs.
    */
   public NetworkTrainer(Network initialNetwork, double inputs[][], double[][] outputs) 
   {
      network = initialNetwork;
      trainingInputs = inputs;
      trainingOutputs = outputs;
      error = calcError();

      return;
   }

   /*
    * calcError returns the total error when the network is run for all input-output pairs.
    */
   private double calcError() 
   {
      double totalError = 0.0;
      double diff = 0.0;
      for (int i = 0; i < trainingInputs.length; i++)
      {
         double[] results = network.eval(trainingInputs[i]);         // Get results

         for (int j = 0; j < results.length; j++)                    // Calculate error for given training case
         {
            diff = (trainingOutputs[i][j] - results[j]);
            totalError += diff * diff;
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

         improved = adaptiveImprove();                                        // Run an adaptive step and save the result

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
   private boolean adaptiveImprove()
   {
      boolean improved;
      double newError;
      double oldWeights[][][] = new double[network.layers - 1][network.maxNodes][network.maxNodes];

      for (int n = 0; n < network.weights.length; n++)                  // Save old weights in case of roll back
      {
         for (int i = 0; i < network.weights[0].length; i++) 
         {
            for (int j = 0; j < network.weights[0][0].length; j++) 
            {
               oldWeights[n][i][j] = network.weights[n][i][j];
            }
         }
      }

      for (int trainingCase = 0; trainingCase < trainingInputs.length; trainingCase++)  // Improve for each training case
         improve(trainingCase);
         
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
   }  // private boolean adaptiveImprove()

   /*
    * improve decreases the network's weights by the training factor (lambda) multiplied by the partial derivatives 
    * of total error.
    */
   private void improve(int trainingCase) 
   {
      double Dweights[][][] = network.getDErrors(trainingInputs[trainingCase], trainingOutputs[trainingCase]);

      double newWeights[][][] = new double[network.layers - 1][network.maxNodes][network.maxNodes];
      for (int n = 0; n < Dweights.length; n++) 
      {
         for (int i = 0; i < Dweights[0].length; i++) 
         {
            for (int j = 0; j < Dweights[0][0].length; j++) 
            {
               newWeights[n][i][j] = network.weights[n][i][j] - (trainingFactor * Dweights[n][i][j]);
            }
         }
      }
      network.setWeights(newWeights);

      return;
   }  // private void improve(int trainingCase) 

   /*
    * printResults prints the network's results for each input-output pair, the training factor, and total error.
    */
   public void printResults() 
   {
      System.out.println();
      for (int i = 0; i < trainingInputs.length; i++)                      // For each training case
      {
         System.out.println(String.format("Case %d:", i + 1));             // Print the number

         System.out.println("Inputs:");                                      // Print the inputs
         for (int j = 0; j < trainingInputs[0].length; j++)
         {
            System.out.print(String.format(" %.15f", trainingInputs[i][j]));
         }
         System.out.println();

         System.out.println("Results:");                                     // Calculate and print the network's outputs
         double results[] = network.eval(trainingInputs[i]);
         for (int j = 0; j < results.length; j++)
         {
            System.out.print(String.format(" %.15f", results[j]));
         }
         System.out.println();

         System.out.println("Answers:");                                     // Calculate and print the correct outputs
         for (int j = 0; j < trainingOutputs[0].length; j++)
         {
            System.out.print(String.format(" %.15f", trainingOutputs[i][j]));
         }
         System.out.println();

         System.out.println();
      }  // for (int i = 0; i < trainingInputs.length; i++)

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