/*
 * Authored by Bennett Liu on September 18th, 2019
 * Network.java implements a multi-layer perceptron neural network customizable for any dimensions.
 */
import java.util.*; 
import java.io.*;

/*
 * The Network class defines the feed-forward multi-layer neural network and provides methods to evaluate it.
 */
public class Network 
{
   int inputs;                // The number of input nodes
   int inputLayer;            // The index of the input layer
   int hiddenLayers;          // The number of hidden layers
   int outputs;               // The number of output nodes
   int outputLayer;           // The index of the output layer
   int layers;                // The total number of layers
                              
   int nodesInLayer[];        // The number of nodes per layer
   int maxNodes;              // The maximum number of nodes in a layer

   double weights[][][];      // Weight in weight layer m connecting left row to right row: [m][left][right]
   double activationVals[][]; // Value in model at layer n, row r: [n][r]

   /*
    * The Network constructor creates a new Network, given the number of input nodes, nodes in each hidden layer, and output nodes.
    */
   public Network(int inputNodes, int hiddenLayerNodes[], int outputNodes) 
   { 
      inputs = inputNodes;
      inputLayer = 0;                                 // input layer index is always 0
      hiddenLayers = hiddenLayerNodes.length;
      outputs = outputNodes;
      outputLayer = hiddenLayers + 1;                 // output layer index
      layers = hiddenLayers + 2;                      // total layers is hidden layers + input + output layers

      // create array of nodes in each layer
      nodesInLayer = new int[layers];
      nodesInLayer[inputLayer] = inputs;
      for (int i = 1; i <= hiddenLayers; i++) 
      {
         nodesInLayer[i] = hiddenLayerNodes[i - 1];
      }
      nodesInLayer[outputLayer] = outputs;

      // calculate the maximum number of nodes in any layer
      maxNodes = 0;
      for (int i = 0; i < nodesInLayer.length; i++) maxNodes = Math.max(maxNodes, nodesInLayer[i]);

      // initialize weight and activation matrices
      initWeights();
      initActivationVals();
      return;
   }

   /*
    * initWeights creates a new weight matrix and fills in weight values.
    */
   void initWeights() 
   {
      weights = new double[layers - 1][maxNodes][maxNodes];

      // initialize all weights as random
      Random random = new Random();
      double rangeMin = 0;
      double rangeMax = 1;
      for (int n = 0; n < layers - 1; n++) {
         for (int i = 0; i < nodesInLayer[n]; i++) {
            for (int j = 0; j < nodesInLayer[n + 1]; j++) {
               weights[n][i][j] = rangeMin + (rangeMax - rangeMin) * random.nextDouble();
            }
         }
      }
      return;
   }

   /*
    * initActivationVals creates a new activation values matrix
    */
   void initActivationVals() 
   {
      activationVals = new double[layers][maxNodes];
      return;
   }

   /*
    * loadTestcase initializes the input nodes' activations, given a testcase
    */
   void loadTestcase(double testcase[]) 
   {
      for (int i = 0; i < inputs; i++) activationVals[inputLayer][i] = testcase[i];
      return;
   }

   /*
    * thresholdF returns the result of the threshold function used to determine a node's activation state. 
    */
   public double thresholdF(double x) 
   {
      return 1.0 / (1.0 + Math.exp(-x));
   }

   /*
    * dF returns the derivative of the function thresholdF
    */
   public double dF(double x) 
   {
      return thresholdF(x) * (1 - thresholdF(x));
   }
   
   /*
    * dotProduct calculates the dot product of node (n, i)'s input activation values and weights, given n and i
    */
   double dotProduct(int n, int i)
   {
      // m: weight layer index connecting layer n - 1 to n, given by m = n - 1 
      int m = n - 1;

      // Calculate dot product of activationVals[n-1][] and weights[m][][i]
      double dotProduct = 0;                                          
      for (int j = 0; j < nodesInLayer[n - 1]; j++)
      {
         // System.out.printf(String.format("a[%d][%d]w[%d][%d][%d] +", n-1, j, m, j, i));
         dotProduct += activationVals[n - 1][j] * weights[m][j][i];
      }

      return dotProduct;
   }

   /*
    * eval evaluates and returns the output of the network, given a test case
    */
   public double[] eval(double testcase[]) 
   {
      loadTestcase(testcase);

      // evaluate network
      for (int n = 1; n <= outputLayer; n++)                         // for each non-input layer
      {
         for (int i = 0; i < nodesInLayer[n]; i++)                   // for each node (row i) in layer n
         {
            activationVals[n][i] = thresholdF(dotProduct(n, i));     // Calculate activation value
         }
      }
      
      return Arrays.copyOfRange(activationVals[outputLayer], 0, outputs);     // return output value
   }

   /*
    * getDErrors returns the partial derivative of the total error of each output of a given test case 
    * relative to each weight.
    */
   public double[][][] getDErrors(double testcase[], double[] truths) {
      // this might initialize hella random vals but we'll see ig
      double Dweights[][][] = new double[weights.length][weights[0].length][weights[0][0].length];
      double[] results = eval(testcase);
      
      for (int output = 0; output < truths.length; output++)
      {
         // second layer
         for (int i = 0; i < nodesInLayer[1]; i++)                   // for each node (row i) in layer n
         {
            double dp = dotProduct(2, output);
            Dweights[1][i][output] += -(results[output] - truths[output]) * dF(dp) * activationVals[1][i];
         }

         // first layer
         for (int i = 0; i < nodesInLayer[0];i++) {
            for (int j = 0; j < nodesInLayer[1]; j++)                   // for each node (row i) in layer n
            {
               double dp1 = dotProduct(1, j);
               double dp2 = dotProduct(2, output);
               Dweights[0][i][j] = -(results[output] - truths[output]) * dF(dp1) * dF(dp2) * activationVals[0][i] * weights[1][j][output];
            }
         }
      }
      
      return Dweights;
   }

   /*
    * exportNet writes the fundamental structure of the network to a given file.
    */
   public void exportNet(String fileName) {
      try {
         FileWriter fw = new FileWriter(fileName);
         BufferedWriter writer = new BufferedWriter(fw);
         writer.append(String.format("layers: %d\n", layers));
         writer.append("nodesInLayer: ");
         for (int i = 0; i < layers; i++) {
            writer.append(String.format("%d ", nodesInLayer[i]));
         }
         writer.append("\n");

         writer.append(String.format("maxNodes: %d\n", maxNodes));

         writer.append("weights: \n");
         for (int n = 0; n < layers - 1; n++) {
            for (int i = 0; i < nodesInLayer[n]; i++) {
               for (int j = 0; j < nodesInLayer[n + 1]; j++) {
                  writer.append(String.format("%15.8f ", weights[n][i][j]));
               }
               writer.append("\n");
            }
            writer.append("\n");
         }

         writer.close();
      } catch (IOException e) {
         e.printStackTrace();
      }
      return;
   }

   /*
    * setWeights changes the network's weights to a given set of weights 
    */
   public void setWeights(double newWeights[][][]) 
   {
      weights = newWeights;
      return;
   }
};
