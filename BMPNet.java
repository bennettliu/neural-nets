/*
 * Authored by Bennett Liu on Nov 18th, 2019
 */

import java.util.*;
import java.io.*;

public class BMPNet 
{
   public static void main(String[] args) 
   {
      double outputs[];                // The outputs for each test case

      String fileName = "networks/Network_10x10.txt";
      Network network = new Network(new File(fileName));

      PelGetter pelGetter = new PelGetter();
      double d[] = pelGetter.getPels();

      outputs = network.eval(d);

      pelGetter.makeBMP(outputs);

      return;
   } // public static void main(String[] args)
 } // public class Main