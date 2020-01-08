/*
 * Authored by Bennett Liu on September 18th, 2019
 * 
 * Network.java implements a configurable multi-layer feed-forward perceptron neural network. The network is configurable 
 * for any positive number of layers, nodes, and any weight values.
 * 
 * Contains the following methods:
 * 
 * Method                  |  Description
 * ------------------------|---------------------
 * Network                 |  A constructor for creating a Network with randomized weights.
 * Network                 |  A constructor for loading a Network from a file.
 * exportNet               |  Exports the Network to a specified file.
 * calcMaxNodes            |  Calculates the most nodes a layer has.
 * initRandomizedWeights   |  Randomly initializes the weights matrix, given bounds.
 * initActivationVals      |  Creates the activations matrix.
 * loadInputs              |  Loads a given array of inputs into the input activations.
 * thresholdF              |  The threshold function applied to a node's input values.
 * dThresholdF             |  The derivative of the threshold function.
 * dotProduct              |  Calculates the dot product for node (n, i)'s input values.
 * eval                    |  Evaluates the network, given an array of inputs.
 * getDErrors              |  Finds the partial derivatives relative to the weights of a given case's total error.
 * setWeights              |  Sets the weights to a given matrix.
 */

import java.util.*;
import java.io.*;

/*
 * The Network class defines the feed-forward multi-layer neural network and provides methods to evaluate it.
 */
public class Network 
{
   int layers;                // The total number of layers
   int nodesInLayer[];        // The number of nodes for [layer]
   int maxNodes;              // The maximum number of nodes in a layer

   int inputs;                // The number of input nodes
   int inputIndex;            // The index of the input layer
   int outputs;               // The number of output nodes
   int outputIndex;           // The index of the output layer

   double weights[][][];      // Weight in model for [layer][leftNode][rightNode]
   double dotVals[][];        // Dot product values in model for [layer][node]
   double activationVals[][]; // Activation value in model for [layer][node]

   /*
    * The Network constructor creates a new Network with randomized weights, given the number of input nodes, 
    * nodes in each hidden layer, output nodes, and the bounds of randomization.
    */
   public Network(int inputNodes, int hiddenLayerNodes[], int outputNodes, double minWeight, double maxWeight)
   { 
      layers = hiddenLayerNodes.length + 2;                    // Total layers is hidden layers + input + output layers

      inputIndex = 0;                                          // Input layer index is always 0
      inputs = inputNodes;                                     // Get nodes in input layer
      outputIndex = layers - 1;                                // Output layer index is always last index
      outputs = outputNodes;                                   // Get nodes in output layer

      nodesInLayer = new int[layers];                          // Create nodesInLayer, the number of nodes in each layer
      nodesInLayer[inputIndex] = inputs;
      for (int i = 1; i < layers - 1; i++)
         nodesInLayer[i] = hiddenLayerNodes[i - 1];
      nodesInLayer[outputIndex] = outputs;

      calcMaxNodes();                                          // Calculates the maximum nodes in each layer
      initRandomizedWeights(minWeight, maxWeight);             // Initialize weights matrix
      initActivationVals();                                    // Initialize activation matrix

      return;
   }  // public Network(int inputNodes, int hiddenLayerNodes[], int outputNodes, double minWeight, double maxWeight)
   
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
            nodesInLayer[i] = scanner.nextInt();
         
         inputIndex = 0;                                          // Input layer index is always 0
         inputs = nodesInLayer[inputIndex];                       // Get nodes in input layer
         outputIndex = layers - 1;                                // Output layer index is always last index
         outputs = nodesInLayer[outputIndex];                     // Get nodes in output layer

         calcMaxNodes();                                          // Calculates the maximum nodes in each layer

         weights = new double[layers - 1][maxNodes][maxNodes];    // Initialize weights matrix
         for (int layer = 0; layer < layers - 1; layer++)
         {
            for (int i = 0; i < nodesInLayer[layer]; i++)
            {
               for (int j = 0; j < nodesInLayer[layer + 1]; j++)
               {
                  weights[layer][i][j] = scanner.nextDouble();
               }
            }
         }

         initActivationVals();                                    // Initialize activation matrix
         scanner.close();
      }  // try
      catch (Exception e)
      {
         System.out.println(String.format("Exception: Network could not be intialized with file %s", file.getName()));
      }

      return;
   }  // public Network(File file)
   
   /*
    * exportNet writes the fundamental structure of the network to a provided file name. This includes the number 
    * of layers, nodes in each layer and weights.
    */
    public void exportNet(String fileName)
    {
      try 
      {
         FileWriter fw = new FileWriter(fileName);
         BufferedWriter writer = new BufferedWriter(fw);

         writer.append(String.format("%d\n", layers));                           // Print number of layers

         for (int i = 0; i < layers; i++)                                        // Print nodes per layer
            writer.append(String.format("%d ", nodesInLayer[i]));
         writer.append("\n");

         for (int layer = 0; layer < layers - 1; layer++)                      // Print all weights
         {
            writer.append("\n");
            for (int i = 0; i < nodesInLayer[layer]; i++)                        // Prints weights connecting layer m to m + 1
            {
               for (int j = 0; j < nodesInLayer[layer + 1]; j++)
               {
                  writer.append(String.format("%.15f ", weights[layer][i][j]));
               }
               writer.append("\n");
            }
         }  // for (int layer = 0; layer < layers - 1; layer++)

         writer.close();
      }  // try
      catch (IOException e)
      {
         e.printStackTrace();
      }

      return;
    } // public void exportNet(String fileName)

   /*
    * calcMaxNodes calculates the maximum number of nodes in any layer and updates the related instance variable.
    */
   private void calcMaxNodes()
   {
      maxNodes = 0;
      for (int i = 0; i < layers; i++)
         maxNodes = Math.max(maxNodes, nodesInLayer[i]);
      
      return;
   }

   /*
    * initRandomizedWeights creates a new weight matrix and fills in randomized weight values, given a range.
    */
   private void initRandomizedWeights(double minWeight, double maxWeight)
   {
      weights = new double[layers - 1][maxNodes][maxNodes];

      Random random = new Random();                            // initialize all weights as random
      for (int layer = 0; layer < layers - 1; layer++)
      {
         for (int i = 0; i < nodesInLayer[layer]; i++)
         {
            for (int j = 0; j < nodesInLayer[layer + 1]; j++)
            {
               weights[layer][i][j] = minWeight + (maxWeight - minWeight) * random.nextDouble();
            }
         }
      }

      return;
   }  // private void initRandomizedWeights(double minWeight, double maxWeight)

   /*
    * initActivationVals creates a new activation values matrix.
    */
   private void initActivationVals()
   {
      dotVals = new double[layers][maxNodes];
      activationVals = new double[layers][maxNodes];
      return;
   }

   /*
    * loadInputs initializes the input nodes' activation values, given an input array.
    */
   private void loadInputs(double inputArray[])
   {
      for (int i = 0; i < inputs; i++)
         activationVals[inputIndex][i] = inputArray[i];
      return;
   }

   /*
    * thresholdF returns the result of the threshold function used to determine a node's activation state. 
    */
   private double thresholdF(double x)
   {
      return 1.0 / (1.0 + Math.exp(-x));
   }

   /*
    * dThresholdF returns the derivative of the function thresholdF
    */
   private double dThresholdF(double x)
   {
      return thresholdF(x) * (1.0 - thresholdF(x));
   }
   
   /*
    * dotProduct calculates the dot product of node (n, i)'s input activation values and weights, given n and i.
    */
   private double dotProduct(int n, int i)
   {
      double dotProduct = 0.0;

      for (int j = 0; j < nodesInLayer[n - 1]; j++)       // Calculates dot product of activationVals[n-1][] and weights[m][][i]
         dotProduct += activationVals[n - 1][j] * weights[n - 1][j][i];

      return dotProduct;
   }

   /*
    * eval evaluates and returns the output of the network, given an array of inputs.
    */
   public double[] eval(double inputArray[])
   {
      loadInputs(inputArray);

      for (int layer = 1; layer <= outputIndex; layer++)
      {
         for (int i = 0; i < nodesInLayer[layer]; i++)
         {
            dotVals[layer][i] = dotProduct(layer, i);
            activationVals[layer][i] = thresholdF(dotVals[layer][i]);      // Calculate activation value
         }
      }
      
      return Arrays.copyOfRange(activationVals[outputIndex], 0, outputs);     // Return output values
   }  // public double[] eval(double inputArray[])

   public double[] eval(String filename)
   { 
      PelGetter pelGetter = new PelGetter();
      double inputArray[] = pelGetter.getPels(filename);
      return eval(inputArray);
   } 

   /*
    * getDErrors returns the partial derivative of the total error relative to each weight.
    */
   public double[][][] getDErrors(double inputArray[], double expectedOutputs[], double lambda)
   {
      double results[] = eval(inputArray);
      
      double psi[] = new double[maxNodes];
      double omega[][] = new double[layers][maxNodes];
      double dWeights[][][] = new double[layers - 1][maxNodes][maxNodes];

      // Initialize last layer omega values
      for (int i = 0; i < nodesInLayer[layers - 1]; i++)
         omega[layers - 1][i] = (results[i] - expectedOutputs[i]);
         
      // Evaluate all other weight layers
      for (int layer = layers - 2; layer >= 0; layer--)                          // Calculate dWeight with backpropagation
      {
         for (int j = 0; j < nodesInLayer[layer + 1]; j++)                       // Current weight's destination node
         {
            psi[j] = omega[layer + 1][j] * dThresholdF(dotVals[layer + 1][j]);   // Calculate psi
            for (int i = 0; i < nodesInLayer[layer]; i++)                        // Current weight's source node
            {
               dWeights[layer][i][j] = activationVals[layer][i] * psi[j];        // Set dWeights for current weight layer
               omega[layer][i] += psi[j] * weights[layer][i][j];                 // Set omega for next round
               weights[layer][i][j] -= lambda * dWeights[layer][i][j];
            }
         }
      }  // for (int layer = layers - 2; layer >= 0; layer--)

      return dWeights;
   }  // public double[][][] getDErrors(double inputArray[], double expectedOutputs[], double lambda)
 
   /*
    * setWeights changes the network's weights to a given set of weights 
    */
   public void setWeights(double newWeights[][][])
   {
      weights = newWeights;
      return;
   }
}  // public class Network 
