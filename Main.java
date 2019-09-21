/*
 * Authored by Bennett Liu on September 18th, 2019
 * 
 */

public class Main {
   public static void main(String[] args) {
      //Network parameters
      int inputNodes = 2;
      int hiddenLayerNodes[] = {2};
      int outputNodes = 1;

      // Create network
      Network network = new Network(inputNodes, hiddenLayerNodes, outputNodes);

      // Initialize test cases
      double testset[][] = {
         {0, 0},
         {0, 1},
         {1, 0},
         {1, 1}
      };
      double truthset[] = {0, 0, 0, 1};

      // Evaluate the network for all test cases
      for (int i = 0; i < testset.length; i++) 
      {
         System.out.println(String.format("Case %d", i));
         System.out.println(String.format("Input 1: %f", testset[i][0]));
         System.out.println(String.format("Input 2: %f", testset[i][1]));
         System.out.println("Result: " + network.eval(testset[i]));
         System.out.println("Answer: " + truthset[i]);
         System.out.println();
      }
      network.exportNet("og.txt");


      NetworkTrainer trainer = new NetworkTrainer(network, testset, truthset);
      System.out.println(trainer.calcError());
      for(int i = 0; i < 1000; i++)
      {
         trainer.improve();
      }

      network = trainer.getNetwork();

      for (int i = 0; i < testset.length; i++) 
      {
         System.out.println(String.format("Case %d", i));
         System.out.println(String.format("Input 1: %f", testset[i][0]));
         System.out.println(String.format("Input 2: %f", testset[i][1]));
         System.out.println("Result: " + network.eval(testset[i]));
         System.out.println("Answer: " + truthset[i]);
         System.out.println();
      }
      System.out.println(trainer.calcError());
      // Export the network to config.txt
      network.exportNet("config.txt");
      
      return;
    }
 }