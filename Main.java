/*
 * Authored by Bennett Liu on September 18th, 2019
 * The main class runs 
 */
import java.util.*;

public class Main {
   public static void main(String[] args) {
      //Network parameters
      int inputNodes;
      int hiddenLayers;
      int hiddenLayerNodes[];
      int outputNodes;

      // Initialize test cases
      int testcases;
      double testset[][];
      double truthset[][];

      Scanner in = new Scanner(System.in);
      System.out.println("How many input nodes: ");
      inputNodes = in.nextInt();

      System.out.println("How many hidden layers: ");
      hiddenLayers = in.nextInt();

      hiddenLayerNodes = new int[hiddenLayers];
      for (int  i = 1; i <= hiddenLayers; i++) 
      {
         System.out.println(String.format("How many nodes in hidden layer %d: ", i));
         hiddenLayerNodes[i - 1] = in.nextInt();
      }

      System.out.println("How many output nodes: ");
      outputNodes = in.nextInt();

      System.out.println("How many test cases: ");
      testcases = in.nextInt();

      testset = new double[testcases][inputNodes];
      truthset = new double[testcases][outputNodes];
      for (int i = 1; i <= testcases; i++) 
      {
         System.out.println(String.format("Test Case %d", i));
         for (int j = 1; j <= inputNodes; j++)
         {
            System.out.println(String.format("Input %d:", j));
            testset[i - 1][j - 1] = in.nextDouble();
         }
         for (int j = 1; j <= outputNodes; j++)
         {
            System.out.println(String.format("Output %d:", j));
            truthset[i - 1][j - 1] = in.nextDouble();
         }
      }

      // Create network
      Network network = new Network(inputNodes, hiddenLayerNodes, outputNodes);

      // Initialize trainer and evaluate the initial network for all test cases
      NetworkTrainer trainer = new NetworkTrainer(network, testset, truthset);
      trainer.printTest();
      network.exportNet("startPoint.txt");

      if (inputNodes == 2 && outputNodes == 1)
      {
         // Train the network
         trainer.train(100000, 0.001);

         // Evaluate the final network for all test cases
         trainer.printTest();
         network = trainer.getNetwork();
         network.exportNet("endPoint.txt");
      }
      else
      {
         System.out.println("Can't train for that case");
      }
      return;
    }
 }