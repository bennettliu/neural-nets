/*
 * Authored by Bennett Liu on September 18th, 2019
 * The main class runs 
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
      double testset[][] = 
      {
         {0, 0},
         {0, 1},
         {1, 0},
         {1, 1}
      };
      double truthset[] = {0, 1, 1, 0};

      // Initialize trainer
      NetworkTrainer trainer = new NetworkTrainer(network, testset, truthset);
      System.out.println(trainer.calcError());

      // Evaluate the initial network for all test cases
      trainer.printTest();
      network.exportNet("startPoint.txt");

      // Train the network
      trainer.train(100000, 0.001);

      // Evaluate the final network for all test cases
      trainer.printTest();
      network = trainer.getNetwork();
      network.exportNet("endPoint.txt");
      
      return;
    }
 }