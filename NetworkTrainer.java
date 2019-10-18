/*
 * Authored by Bennett Liu on September 20th, 2019
 * NetworkTrainer.java defines a class which trains a feed-forward multi-layer neural network 
 * defined in Network.java on a training set
 */
import java.util.*;

public class NetworkTrainer
{
   Network network;              // The network to be trained
   double testInputs[][];
   double testOutputs[][];

   double error;
   double trainingFactor;        // The training factor (lambda)
   double adaptFactor;           // The adaptive factor, used to modify lambda

   /*
    * The Network constructor creates a new Network, given the number of input nodes, nodes in each hidden layer, and output nodes.
    */
   public NetworkTrainer(Network initialNetwork, double inputs[][], double[][] outputs) 
   {
      // validation needed
      network = initialNetwork;
      testInputs = inputs;
      testOutputs = outputs;
      error = calcError();
      return;
   }

   /*
    * calcError returns the total error when the network is run for each input-output pair.
    */
   public double calcError() 
   {
      double totalError = 0;
      for (int i = 0; i < testInputs.length; i++)
      {
         double[] results = network.eval(testInputs[i]);

         for (int j = 0; j < results.length; j++)
         {
            totalError += (testOutputs[i][j] - results[j]) * (testOutputs[i][j] - results[j]);
         }
      }
      totalError /= 2;

      return totalError;
   }

   /*
    * train runs multiple steps while some conditions are still met.
    */
   public void train(double initialLambda, double adaptiveConstant, int maxSteps, double minError, int savePeriod) 
   {
      trainingFactor = initialLambda;
      adaptFactor = adaptiveConstant;

      int step = 0;
      boolean improved = true;
      while (step < maxSteps && error > minError && trainingFactor > 0 && (adaptFactor != 1 || improved))
      {
         step++;

         improved = adaptiveImprove();

         if (savePeriod != 0 && step % savePeriod == 0)
         {
            printTest();
            network.exportNet("logs/" + (new Date()).getTime() + ".txt");      
         }
      }

      System.out.println();
      System.out.println(String.format("Terminated after %d steps", step));
      if (step >= maxSteps) 
         System.out.println(String.format("Steps passed limit of %d", maxSteps));
      if (error <= minError) 
         System.out.println(String.format("Error fell below %.15f", minError));
      if (adaptFactor == 1 && !improved) 
         System.out.println("Was not able to improve error.");
      if (trainingFactor <= 0) 
         System.out.println("Training factor (lambda) reached 0");
      System.out.println();
   }

   public boolean adaptiveImprove()
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

      for (int testcase = 0; testcase < testInputs.length; testcase++)  // Improve for each test case
         improve(testcase);
         
      newError = calcError();                // Calculate the new error
      if (newError < error)                  // If steps improved error
      {
         error = newError;                   // Update error
         trainingFactor *= adaptFactor;      // Make a bigger step next time
         improved = true;
      }
      else                                   // If steps worsened error
      {
         network.setWeights(oldWeights);     // Roll back weights
         trainingFactor /= adaptFactor;      // Make a smaller step next time
         improved = false;
      }

      return improved;                       // Return whether the error improved
   }

   /*
    * improve changes the network's weights to decrease the total error. Modifies weights by the training 
    * factor (lambda) multiplied by the partial derivatives of total error.
    * Adapts lambda by factors of 2 to ensure that it does not overstep.
    */
   public void improve(int testcase) 
   {
      double Dweights[][][] = network.getDErrors(testInputs[testcase], testOutputs[testcase]);

      double newWeights[][][] = new double[network.layers - 1][network.maxNodes][network.maxNodes];
      for (int n = 0; n < Dweights.length; n++) 
      {
         for (int i = 0; i < Dweights[0].length; i++) 
         {
            for (int j = 0; j < Dweights[0][0].length; j++) 
            {
               newWeights[n][i][j] = network.weights[n][i][j] - trainingFactor * Dweights[n][i][j];
            }
         }
      }
      network.setWeights(newWeights);

      return;
   }

   /*
    * printTest prints the network's results for each input-output pair and other debug information.
    */
   public void printTest() 
   {
      System.out.println();
      for (int i = 0; i < testInputs.length; i++)                          // For each test case
      {
         System.out.println(String.format("Case %d:", i + 1));             // Print the number

         System.out.print("Inputs:");                                      // Print the inputs
         for (int j = 0; j < testInputs[0].length; j++)
         {
            System.out.print(String.format(" %.15f", testInputs[i][j]));
         }
         System.out.println();

         System.out.print("Results:");                                     // Calculate and print the network's outputs
         double results[] = network.eval(testInputs[i]);
         for (int j = 0; j < results.length; j++)
         {
            System.out.print(String.format(" %.15f", results[j]));
         }
         System.out.println();

         System.out.print("Answers:");                                     // Calculate and print the correct outputs
         for (int j = 0; j < testOutputs[0].length; j++)
         {
            System.out.print(String.format(" %.15f", testOutputs[i][j]));
         }
         System.out.println();

         System.out.println();
      }
      System.out.println(String.format("Lambda: %.15f", trainingFactor));  // Print the training factor
      System.out.println(String.format("Total Error: %.15f", error));      // Print the total error
      System.out.println();
   }

   /*
    * getNetwork returns the trained network
    */
   public Network getNetwork() 
   {
      return network;
   }
};