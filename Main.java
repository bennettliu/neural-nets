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
      double testset[][] = 
      {
         {0, 0},
         {0, 1},
         {1, 0},
         {1, 1}
      };
      double truthset[][] = 
      {
         {0}, 
         {1}, 
         {1}, 
         {0}
      };

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