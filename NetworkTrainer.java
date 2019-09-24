/*
 * Authored by Bennett Liu on September 20th, 2019
 * NetworkTrainer.java defines a class which trains a feed-forward multi-layer neural network on a training set
 */

public class NetworkTrainer
{
   Network network;           // The network to be trained
   double testcases[][];
   double truths[];
   double weights[][][];
   double error;
   double trainingFactor;

   /*
    * The Network constructor creates a new Network, given the number of input nodes, nodes in each hidden layer, and output nodes.
    */
   public NetworkTrainer(Network initialNetwork, double testset[][], double[] truthset) 
   {
      // validation needed
      network = initialNetwork;
      testcases = testset;
      truths = truthset;
      error = calcError();
      trainingFactor = 0.1;
      return;
   }

   public double calcError() {
      double totalError = 0;
      for (int i = 0; i < testcases.length; i++)
      {
         double result = network.eval(testcases[i]);

         totalError += (truths[i] - result) * (truths[i] - result);
      }
      totalError /= 2;
      return totalError;
   }

   public double[][][] getDTotalError() {
      double DTotalweights[][][] = new double[network.layers - 1][network.maxNodes][network.maxNodes];
      for (int testcase = 0; testcase < testcases.length; testcase++) {
         double Dweights[][][] = network.getDError(testcases[testcase], truths[testcase]);
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
         for (int n = 0; n < Dweights.length; n++) {                             // copy oldWeights into newWeights
            for (int i = 0; i < Dweights[0].length; i++) {
               for (int j = 0; j < Dweights[0][0].length; j++) {
                  newWeights[n][i][j] = oldWeights[n][i][j];
               }
            }
         }

         for (int n = 0; n < Dweights.length; n++) {
            for (int i = 0; i < Dweights[0].length; i++) {
               for (int j = 0; j < Dweights[0][0].length; j++) {
                  newWeights[n][i][j] += trainingFactor * Dweights[n][i][j];
               }
            }
         }
         network.setWeights(newWeights);

         newError = calcError();
         if(newError < error) trainingFactor *= 2;
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

   public Network getNetwork() 
   {
      return network;
   }
};