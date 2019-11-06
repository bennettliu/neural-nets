# Multi-layer Perceptron Model
This project implements a feed-forward multi-layer perceptron model. 

It contains two main classes for training and running. 

Class Name       | Purpose
---------------- | -------------
Main             | Training and Exporting Networks
RunNetwork       | Importing and Running Networks

# General Usage
For neatness, all log files will be placed in the `logs` folder. While all valid file paths are supported, please export networks to the `networks` folder. 

# Compilation
To compile this project, navigate into the project and execute `javac *.java`. 

# Training and Exporting Networks
To run, execute `java Main`. Enter the requested values. Only networks with one hidden layer will train.

To compile, run, and delete all class files afterwards, run `javac *.java && java Main && rm *.class`

Reads inputs from console. Sample training inputs can be found in the trainingCases folder.

In these files, all values are separated by spaces or carriage returns. These files are formatted as follows.

*  Network
   *  The number of input nodes (integer)
   *  The number of hidden layers (integer)
   *  The number of nodes in each hidden layer (integers)
      *  For 3 hidden layers with 2, 4, and 6 nodes (from the input side to output side), this would be `2 4 6`
   *  The number of output nodes (integer)
   *  The range in which weights will be randomized (two decimals)
*  Training Cases
   *  The number of training cases (integer)
   *  For each case:
      *  The case's inputs (decimals)
      *  The case's outputs (decimals)
*  **If 1 hidden layer:** Training Parameters
   *  The initial training factor, lambda (decimal)
   *  The adaptive training constant (decimal)
   *  The maximum number of steps (integer)
   *  The ceiling of the desired error (decimal)
   *  The period of steps at which user updates and the network is saved (integer)
      *  If set ≤ 0.0, no updates/saves will occur
*  Exporting
   *  The file where the network should be exported to. Usually `networks/DESCRIPTIVE_NAME.txt`.

# Importing and Running Networks
After running training, networks will be exported to files. Run these files with the `java RunNetwork`.

To compile, run, and delete all class files afterwards, run `javac *.java && java RunNetwork && rm *.class`

# To submit to Dr. Nelson:
Run `rm -f *.zip && rm -f *.class && zip -u -r bennett_liu_perceptron.zip * && zip -d -r bennett_liu_perceptron.zip networks/*` to create a zip of only Java code, folders, and `README.md`