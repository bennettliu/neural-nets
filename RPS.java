/*
 * Authored by Bennett Liu on November 11th, 2019
 * 
 * Uses a neural network to train a network to play rock, paper, scissors.
 */

import java.util.*;

public class RPS 
{
   public static void main(String[] args) 
   {
      int inputNodes = 8;                        // The number of inputs in the new network
      int hiddenLayerNodes[] = {4};                // The number of nodes in each layer of the new network
      int outputNodes = 3;                       // The number of outputs in the new network
      double minWeight = -1;                      // The minimum value for the new network's randomized weights
      double maxWeight = 1;                      // The maximum value for the new network's randomized weights

      double inputs[];
      double results[];

      double trainingInputs[][];             // The inputs for each training case
      double trainingOutputs[][];            // The outputs for each training case
      
      double trainingFactor;                 // The initial training factor, lambda
      double adaptiveConstant;               // The adaptive lambda factor. Adaptive training can be disabled by setting to 1
      int stepLimit;                         // The maximum number of steps the training algorithm will take
      double errorLimit;                     // The desired error, training will stop if/when this target is met
      double trainingFactorLimit;            // The minimum training factor (lambda) for training to run
      int updateSavePeriod;                  // The period of steps at which progress will be reported and the network saved

      double[] rock = {0.5, 1, 0};
      double[] paper = {0, 0.5, 1};
      double[] scissors = {1, 0, 0.5};

      String[] words= {"Rock", "Paper", "Scissors"};
      // Rock 0
      // Paper 1
      // Scissors 2
      Scanner in = new Scanner(System.in);   // Create scanner to take input from console
      Network network = new Network(inputNodes, hiddenLayerNodes, outputNodes, minWeight, maxWeight);
      trainingInputs = new double[0][inputNodes];
      trainingOutputs = new double[0][outputNodes];

      int wins = 0;
      int ties = 0;
      int losses = 0;
      for (int rounds = 0; true; rounds++) 
      {
         inputs =  new double[inputNodes];
         results = network.eval(inputs);

         System.out.println(String.format("Won %d/%d", wins, rounds));
         System.out.println(String.format("Tied %d/%d", ties, rounds));
         System.out.println(String.format("Lost %d/%d", losses, rounds));
         System.out.println();

         // Figure out choice
         int choice = 0;
         for (int j = 0; j < 3; j++) 
         {
            if (results[j] > results[choice])
               choice = j;
         }
         System.out.println(String.format("Pick %s", words[choice]));

         // Get answer
         String s = in.next();
         s = s.toLowerCase();

         int response = 0;
         double[] result = new double[3];
         if (s.charAt(0) == 'r') 
         {
            response = 0;
            result = rock;
         }
         else if (s.charAt(0) == 'p') 
         {
            response = 1;
            result = paper;
         }
         else if (s.charAt(0) == 's') 
         {
            response = 2;
            result = scissors;
         }
         else {
            network.exportNet("networks/" + (new Date()).getTime() + "RPS.txt");
            break;
         }

         // Result
         if (result[choice] == 0) 
         {
            System.out.println("You won");
            losses++;
         }
         else if (result[choice] == 0.5) 
         {
            System.out.println("We tied");
            ties++;
         }
         else if (result[choice] == 1) 
         {
            System.out.println("You lost");
            wins++;
         }

         // Set up training
         double[][] tmp = new double[rounds + 1][inputNodes];
         System.arraycopy(trainingInputs, 0, tmp, 0, trainingInputs.length);
         trainingInputs = tmp;
         trainingInputs[rounds] = inputs;

         tmp = new double[rounds + 1][3];
         System.arraycopy(trainingOutputs, 0, tmp, 0, trainingOutputs.length);
         trainingOutputs = tmp;
         trainingOutputs[rounds] = result;

         // Train
         network = new Network(inputNodes, hiddenLayerNodes, outputNodes, minWeight, maxWeight);
         NetworkTrainer trainer = new NetworkTrainer(network, trainingInputs, trainingOutputs);    // Initialize trainer
         trainingFactor = 1;
         adaptiveConstant = 2;
         stepLimit = 1000;
         errorLimit = 0.01;
         trainingFactorLimit = 0;
         updateSavePeriod = 0;
         trainer.train(trainingFactor, adaptiveConstant, stepLimit, errorLimit, trainingFactorLimit, updateSavePeriod, updateSavePeriod);
         network = trainer.getNetwork();                    // Retrieve trained network

         // Change inputs

         for (int j = inputNodes - 1; j >= 4; j--)
            inputs[j] = inputs[j - 4];
         inputs[0] = result[choice];
         inputs[1] = 0;
         inputs[2] = 0;
         inputs[3] = 0;
         inputs[response+1] = 1;
      }
      in.close();          // Close scanner

      return;
   } // public static void main(String[] args)
 } // public class Main