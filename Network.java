/*
 * Authored by Bennett Liu on September 18th, 2019
 * Network.java implements a multi-layer feed-forward perceptron neural network customizable for any positive number 
 * of layers and nodes.
 * 
 * Contains the following methods:
 * 
 * Method                  Description
 * Network                 A constructor for creating a Network with randomized weights.
 * Network                 A constructor for loading a Network from a file.
 * exportNet               Exports the Network to a specified file.
 * calcMaxNodes            Calculates the most nodes a layer has.
 * initRandomizedWeights   Randomly initializes the weights matrix, given bounds.
 * initActivationVals      Creates the activations matrix.
 * loadTrainingCase        Loads a given training case into the input activations.
 * thresholdF              The threshold function applied to a node's input values.
 * dThresholdF             The derivative of the threshold function.
 * dotProduct              Calculates the dot product for node (n, i)'s input values.
 * eval                    Evaluates the network, given a training case.
 * getDErrors              Finds the partial derivatives relative to the weights of a given case's total error.
 * setWeights              Sets the weights to a given matrix.
 */
import java.util.*;
import java.io.*;

/*
 * The Network class defines the feed-forward multi-layer neural network and provides methods to evaluate it.
 */
public class Network 
{
   int layers;                // The total number of layers
   int nodesInLayer[];        // The number of nodes per layer
   int maxNodes;              // The maximum number of nodes in a layer

   int inputs;                // The number of input nodes
   int inputLayer;            // The index of the input layer
   int outputs;               // The number of output nodes
   int outputLayer;           // The index of the output layer

   double weights[][][];      // Weight in weight layer m connecting left row to right row: [m][left][right]
   double activationVals[][]; // Value in model at layer n, row r: [n][r]

   /*
    * The Network constructor creates a new Network with randomized weights, given the number of input nodes, 
    * nodes in each hidden layer, output nodes, and the bounds of randomization.
    */
   public Network(int inputNodes, int hiddenLayerNodes[], int outputNodes, double minWeight, double maxWeight) 
   { 
      layers = hiddenLayerNodes.length + 2;                    // Total layers is hidden layers + input + output layers

      inputLayer = 0;                                          // Input layer index is always 0
      inputs = inputNodes;                                     // Get nodes in input layer
      outputLayer = layers - 1;                                // Output layer index is always last index
      outputs = outputNodes;                                   // Get nodes in output layer

      nodesInLayer = new int[layers];                          // Create nodesInLayer, the number of nodes in each layer
      nodesInLayer[inputLayer] = inputs;
      for (int i = 1; i < layers - 1; i++) 
         nodesInLayer[i] = hiddenLayerNodes[i - 1];
      nodesInLayer[outputLayer] = outputs;

      calcMaxNodes();                                          // Calculates the maximum nodes in each layer

      initRandomizedWeights(minWeight, maxWeight);             // Initialize weights matrix
      initActivationVals();                                    // Initialize activation matrix

      return;
   }
   
   /*
    * The Network constructor loads a Network from a file describing its structure.
    */
   public Network(File file) 
   {
      try 
      {
         Scanner scanner = new Scanner(file);

         layers = scanner.nextInt();                              // Number of layers

         nodesInLayer = new int[layers];                          // Parse the number of nodes in each layer
         for (int i = 0; i < layers; i++)
         {
            nodesInLayer[i] = scanner.nextInt();
         }
         
         inputLayer = 0;                                          // Input layer index is always 0
         inputs = nodesInLayer[inputLayer];                       // Get nodes in input layer
         outputLayer = layers - 1;                                // Output layer index is always last index
         outputs = nodesInLayer[outputLayer];                     // Get nodes in output layer

         calcMaxNodes();                                          // Calculates the maximum nodes in each layer

         weights = new double[layers - 1][maxNodes][maxNodes];    // Initialize weights matrix
         for (int n = 0; n < layers - 1; n++) 
         {
            for (int i = 0; i < nodesInLayer[n]; i++) 
            {
               for (int j = 0; j < nodesInLayer[n + 1]; j++) 
               {
                  weights[n][i][j] = scanner.nextDouble();
               }
            }
         }

         initActivationVals();                                    // Initialize activation matrix
         scanner.close();
      } 
      catch (Exception e)
      {
         System.out.println(String.format("Exception: Network could not be intialized with file %s", file.getName()));
      }

      return;
   }
   
   /*
    * exportNet writes the fundamental structure of the network to a given file. This includes the number 
    * of layers, nodes in each layer and weights.
    */
    public void exportNet(String fileName) 
    {
      try {
         FileWriter fw = new FileWriter(fileName);
         BufferedWriter writer = new BufferedWriter(fw);

         writer.append(String.format("%d\n", layers));                           // Print number of layers

         for (int i = 0; i < layers; i++)                                        // Print nodes per layer
            writer.append(String.format("%d ", nodesInLayer[i]));
         writer.append("\n");

         for (int m = 0; m < layers - 1; m++)                                    // Print all weights
         {
            writer.append("\n");
            for (int i = 0; i < nodesInLayer[m]; i++)                            // Prints weights connecting layer m - 1 to m
            {
               for (int j = 0; j < nodesInLayer[m + 1]; j++) 
               {
                  writer.append(String.format("%.15f ", weights[m][i][j]));
               }
               writer.append("\n");
            }
         }

         writer.close();
      } 
      catch (IOException e) 
      {
         e.printStackTrace();
      }

      return;
    }

   /*
    * calcMaxNodes calculates the maximum nodes in each layer and updates the related instance variable.
    */
   private void calcMaxNodes()
   {
      maxNodes = 0;
      for (int i = 0; i < nodesInLayer.length; i++) maxNodes = Math.max(maxNodes, nodesInLayer[i]);
      return;
   }

   /*
    * initRandomizedWeights creates a new weight matrix and fills in randomized weight values, given a range.
    */
   void initRandomizedWeights(double minWeight, double maxWeight) 
   {
      weights = new double[layers - 1][maxNodes][maxNodes];

      Random random = new Random();                            // initialize all weights as random
      for (int n = 0; n < layers - 1; n++) 
      {
         for (int i = 0; i < nodesInLayer[n]; i++) 
         {
            for (int j = 0; j < nodesInLayer[n + 1]; j++) 
            {
               weights[n][i][j] = minWeight + (maxWeight - minWeight) * random.nextDouble();
            }
         }
      }

      return;
   }

   /*
    * initActivationVals creates a new activation values matrix.
    */
   void initActivationVals() 
   {
      activationVals = new double[layers][maxNodes];
      return;
   }

   /*
    * loadTrainingCase initializes the input nodes' activation values, given a training case.
    */
   void loadTrainingCase(double trainingCase[]) 
   {
      for (int i = 0; i < inputs; i++) 
         activationVals[inputLayer][i] = trainingCase[i];
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
    * dThresholdF returns the derivative of the function thresholdF
    */
   public double dThresholdF(double x) 
   {
      return thresholdF(x) * (1.0 - thresholdF(x));
   }
   
   /*
    * dotProduct calculates the dot product of node (n, i)'s input activation values and weights, given n and i.
    */
   double dotProduct(int n, int i)
   {
      int m = n - 1;                                  // m is the weight index connecting layer n - 1 to n, given by m = n - 1 
      double dotProduct = 0;

      for (int j = 0; j < nodesInLayer[n - 1]; j++)   // Calculates dot product of activationVals[n-1][] and weights[m][][i]
         dotProduct += activationVals[n - 1][j] * weights[m][j][i];

      return dotProduct;
   }

   /*
    * eval evaluates and returns the output of the network, given a training case.
    */
   public double[] eval(double trainingCase[]) 
   {
      loadTrainingCase(trainingCase);

      for (int n = 1; n <= outputLayer; n++)                                  // For each non-input layer n
      {
         for (int i = 0; i < nodesInLayer[n]; i++)                            // For each node i in layer n
         {
            activationVals[n][i] = thresholdF(dotProduct(n, i));              // Calculate activation value
         }
      }
      
      return Arrays.copyOfRange(activationVals[outputLayer], 0, outputs);     // Return output values
   }

   /*
    * getDErrors returns the partial derivative of the total error of each output of a given training case 
    * relative to each weight.
    */
   public double[][][] getDErrors(double trainingCase[], double[] truths) 
   {
      double Dweights[][][] = new double[weights.length][weights[0].length][weights[0][0].length];
      double[] results = eval(trainingCase);
      
      for (int output = 0; output < truths.length; output++)
      {
         double dp1 = dotProduct(2, output);
         for (int i = 0; i < nodesInLayer[1]; i++)          // Calculates partial derivatives of second layer
         {
            Dweights[1][i][output] = (results[output] - truths[output]) * dThresholdF(dp1) * activationVals[1][i];
         }

         for (int i = 0; i < nodesInLayer[0];i++)           // Calculates partial derivatives of first layer
         {
            for (int j = 0; j < nodesInLayer[1]; j++)
            {
               double dp2 = dotProduct(1, j);
               Dweights[0][i][j] = (results[output] - truths[output]) * dThresholdF(dp2) * dThresholdF(dp1) * activationVals[0][i] * weights[1][j][output];
            }
         }
      }
      
      return Dweights;
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
