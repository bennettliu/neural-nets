# Multi-layer Perceptron Model
This project implements a feed-forward multi-layer perceptron model. 

It contains two main classes for training and running. 

Main Class       | Purpose
---------------- | -------------
Main.java        | Training and Exporting Networks
RunNetwork.java  | Importing and Running Networks

# General Usage
For neatness, all log files will be placed in the logs folder. While all valid file paths are supported, please export networks to the networks folder. 

# Compilation
To compile this project, navigate into the project and execute `javac *.java`. 

# Training and Exporting Networks
To run, execute `java Main`.

To compile, run, and delete all class files afterwards, run `javac *.java && java Main && rm *.class`

# Importing and Running Networks
After running training, networks will be exported to files. Run these files with the `java RunNetwork`.

To compile, run, and delete all class files afterwards, run `javac *.java && java RunNetwork && rm *.class`

# To submit to Dr. Nelson
Run `rm -f *.zip && zip -u bennett_liu_perceptron.zip *.java && zip -u bennett_liu_perceptron.zip README.md && zip -u bennett_liu_perceptron.zip logs && zip -u bennett_liu_perceptron.zip networks` to create a zip of only Java code and `README.md`