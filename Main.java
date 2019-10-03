/*
 * Authored by Bennett Liu on September 18th, 2019
 * The main class runs 
 */
import java.util.*;

public class Main {
   public static void main(String[] args) {
      //Network parameters
      int inputNodes = 2;
      int hiddenLayers = 1;
      int hiddenLayerNodes[] = {2};
      int outputNodes = 1;

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

      // Create network
      Network network = new Network(inputNodes, hiddenLayerNodes, outputNodes);

      // Initialize trainer and evaluate the initial network for all test cases
      NetworkTrainer trainer = new NetworkTrainer(network, testset, truthset);
      trainer.printTest();
      network.exportNet("startPoint.txt");

      if (inputNodes == 2 && hiddenLayerNodes[0] == 2 && outputNodes == 1)
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