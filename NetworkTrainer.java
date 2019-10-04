/*
 * Authored by Bennett Liu on September 20th, 2019
 * NetworkTrainer.java defines a class which trains a feed-forward multi-layer neural network on a training set
 */

public class NetworkTrainer
{
   Network network;           // The network to be trained
   double testInputs[][];
   double testOutputs[][];
   double weights[][][];
   double error;
   double trainingFactor;

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
      trainingFactor = 0.1;
      return;
   }

   public double calcError() {
      double totalError = 0;
      for (int i = 0; i < testInputs.length; i++)
      {
         double result = network.eval(testInputs[i])[0];

         totalError += (testOutputs[i][0] - result) * (testOutputs[i][0] - result);
      }
      totalError /= 2;
      return totalError;
   }

   public double[][][] getDTotalError() {
      double DTotalweights[][][] = new double[network.layers - 1][network.maxNodes][network.maxNodes];
      for (int testcase = 0; testcase < testInputs.length; testcase++) {
         double Dweights[][][] = network.getDError(testInputs[testcase], testOutputs[testcase][0]);
         for (int n = 0; n < Dweights.length; n++) {
            for (int i = 0; i < Dweights[0].length; i++) {
               for (int j = 0; j < Dweights[0][0].length; j++) {
                  DTotalweights[n][i][j] += Dweights[n][i][j];
               }
            }
         }
      }
      return DTotalweights;
   }

   public void train(int maxSteps, double minError) {
      int steps = 0;
      while (steps < maxSteps && error > minError)
      {
         improve();
         steps++;
      }
   }

   public void improve() {
      double Dweights[][][] = getDTotalError();
      double oldWeights[][][] = new double[network.layers - 1][network.maxNodes][network.maxNodes];
      for (int n = 0; n < Dweights.length; n++) {
         for (int i = 0; i < Dweights[0].length; i++) {
            for (int j = 0; j < Dweights[0][0].length; j++) {
               oldWeights[n][i][j] = network.weights[n][i][j];
            }
         }
      }

      network.weights.clone();

      double newWeights[][][] = new double[network.layers - 1][network.maxNodes][network.maxNodes];
      double newError = 1 << 29;
      while (newError > error)
      {
         for (int n = 0; n < Dweights.length; n++) {                             // create new set of test weights
            for (int i = 0; i < Dweights[0].length; i++) {
               for (int j = 0; j < Dweights[0][0].length; j++) {
                  newWeights[n][i][j] = oldWeights[n][i][j] + trainingFactor * Dweights[n][i][j];
               }
            }
         }
         network.setWeights(newWeights);

         newError = calcError();
         if (newError < error) trainingFactor *= 2;
         else trainingFactor /= 2;
      }
      if(newError < error) 
      {
         error = newError;
      }
      else 
      {
         network.setWeights(oldWeights);
      }
      System.out.println(trainingFactor + " " + error);
      return;
   }

   public void printTest() 
   {
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
      System.out.println(String.format("Total Error: %.15f", error));
      System.out.println();
   }

   public Network getNetwork() 
   {
      return network;
   }
};