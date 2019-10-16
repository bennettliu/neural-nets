/*
 * Authored by Bennett Liu on September 20th, 2019
 * NetworkTrainer.java defines a class which trains a feed-forward multi-layer neural network 
 * defined in Network.java on a training set
 */
import java.util.*;

public class NetworkTrainer
{
   Network network;           // The network to be trained
   double testInputs[][];
   double testOutputs[][];
   double weights[][][];
   double error;
   double trainingFactor;
   double adaptFactor;

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

         improved = false;
         for (int i = 0; i < testInputs.length; i++) 
         {
            improved = improved || improve(i);
         }

         if (improved) 
            trainingFactor *= adaptFactor;
         else 
            trainingFactor /= adaptFactor;

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
         System.out.println(String.format("Error fell below %f", minError));
      if (!improved) 
         System.out.println("Was not able to improve error.");
      if (trainingFactor <= 0) 
         System.out.println("Training factor (lambda) reached 0");
      System.out.println();
   }

   /*
    * improve changes the network's weights to decrease the total error. Modifies weights by the training 
    * factor (lambda) multiplied by the partial derivatives of total error.
    * Adapts lambda by factors of 2 to ensure that it does not overstep. Returns whether error was successfully improved.
    */
   public boolean improve(int testcase) 
   {
      double Dweights[][][] = network.getDErrors(testInputs[testcase], testOutputs[testcase]);
      double oldWeights[][][] = new double[network.layers - 1][network.maxNodes][network.maxNodes];
      for (int n = 0; n < Dweights.length; n++) 
      {
         for (int i = 0; i < Dweights[0].length; i++) 
         {
            for (int j = 0; j < Dweights[0][0].length; j++) 
            {
               oldWeights[n][i][j] = network.weights[n][i][j];
            }
         }
      }

      double newWeights[][][] = new double[network.layers - 1][network.maxNodes][network.maxNodes];
      for (int n = 0; n < Dweights.length; n++) 
      {
         for (int i = 0; i < Dweights[0].length; i++) 
         {
            for (int j = 0; j < Dweights[0][0].length; j++) 
            {
               newWeights[n][i][j] = oldWeights[n][i][j] - trainingFactor * Dweights[n][i][j];
            }
         }
      }
      network.setWeights(newWeights);
      double newError = calcError();

      boolean improved;
      if (newError < error) 
      {
         error = newError;
         improved = true;
      }
      else 
      {
         network.setWeights(oldWeights);
         improved = false;
      }

      return improved;
   }

   /*
    * printTest prints the network's results for each input-output pair and other debug information.
    */
   public void printTest() 
   {
      System.out.println();
      // Evaluate the network for all test cases
      for (int i = 0; i < testInputs.length; i++) 
      {
         System.out.println(String.format("Case %d:", i + 1));

         System.out.print("Inputs:");
         for (int j = 0; j < testInputs[0].length; j++)
         {
            System.out.print(String.format(" %.15f", testInputs[i][j]));
         }
         System.out.println();

         System.out.print("Results:");
         double results[] = network.eval(testInputs[i]);
         for (int j = 0; j < results.length; j++)
         {
            System.out.print(String.format(" %.15f", results[j]));
         }
         System.out.println();

         System.out.print("Answers:");
         for (int j = 0; j < testOutputs[0].length; j++)
         {
            System.out.print(String.format(" %.15f", testOutputs[i][j]));
         }
         System.out.println();

         System.out.println();
      }
      System.out.println(String.format("Lambda: %f", trainingFactor));
      System.out.println(String.format("Total Error: %.15f", error));
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