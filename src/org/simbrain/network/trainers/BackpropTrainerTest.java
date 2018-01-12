/*
 * Part of Simbrain--a java-based neural network kit
 * Copyright (C) 2005,2007 The Authors.  See http://www.simbrain.net/credits
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.simbrain.network.trainers;

import java.io.File;
import java.util.Arrays;

import org.jblas.DoubleMatrix;
import org.simbrain.network.core.Network;
import org.simbrain.network.groups.SynapseGroup;
import org.simbrain.network.subnetworks.BackpropNetwork;
import org.simbrain.network.trainers.BackpropTrainer2.UpdateMethod;
import org.simbrain.util.Utils;
import org.simbrain.util.math.ProbDistribution;

/**
 * Test backprop trainer.
 * 
 * Future test methods. But for now just run them with a main.
 */
public class BackpropTrainerTest {

    public static void main(String[] args) {
       testXor();
       //testAssociator();
       //testAssociator5();
       //testMazur();
       //testSineWave();
       //testDumb();
    }

    /**
     * Classic XOR Test.
     */
    public static void testXor() {

        BackpropNetwork network = new BackpropNetwork(new Network(),
                new int[] { 2, 2, 1 });

        network.getTrainingSet().setInputData(
                new double[][] { { -1, -1 }, { -1, 1 }, { 1, -1 }, { 1, 1 } });
        network.getTrainingSet()
                .setTargetData(new double[][] { { .2 }, { .8 }, { .8 }, { .2 } });

        //BackpropTrainer trainer = new BackpropTrainer(network);
        BackpropTrainer2 trainer = new BackpropTrainer2(network);
        trainer.initData();
        trainer.setLearningRate(.1);
        trainer.setMomentum(.9);
        //trainer.setUpdateMethod(UpdateMethod.STOCHASTIC);
        //trainer.rand.setPdf(ProbDistribution.UNIFORM);
        //trainer.rand.setParam1(-.95);
        //trainer.rand.setParam2(.95);
        for (int i = 0; i < 10000; i++) {
            trainer.apply();
            trainer.commitChanges();
        	for(int jj=0; jj<2; jj++) {
            	System.out.println(Arrays.deepToString(trainer.getWeightMatrices().get(jj).toArray2()));
        	}
            System.out.println(trainer.getError());
        }
    }

    /**
     * Simple 2-2-2 auto-associator.
     */
    public static void testAssociator() {

        BackpropNetwork network = new BackpropNetwork(new Network(),
                new int[] { 2, 2, 2 });

        network.getTrainingSet().setInputData(
                new double[][] { { 0, 0 }, { 0, 1 }, { 1, 0 }, { 1, 1 } });
        network.getTrainingSet().setTargetData(
                new double[][] { { 0, 0 }, { 0, 1 }, { 1, 0 }, { 1, 1 } });

        //BackpropTrainer trainer = new BackpropTrainer(network);
        BackpropTrainer2 trainer = new BackpropTrainer2(network);
        
        trainer.initData();
        trainer.setLearningRate(.15);
        trainer.setMomentum(.8);
//        trainer.rand.setPdf(ProbDistribution.UNIFORM);
//        trainer.rand.setParam1(0);
//        trainer.rand.setParam2(.5);
        
        trainer.randomize();
        
        for (int i = 0; i < 10000; i++) {
            trainer.apply();
            if (i % 100 == 0) {
                System.out.println(trainer.getError());                
            }
        }
    }
    
    /**
     * 5-5-5 auto-associator. Currently failing on the new array backed system.
     * But decent performance on the 
     */
    public static void testAssociator5() {

        BackpropNetwork network = new BackpropNetwork(new Network(),
                new int[] { 5, 5, 5 });

        network.getTrainingSet().setInputData(Utils.getDoubleMatrix(
                new File("./simulations/tables/5_binary.csv")));
        network.getTrainingSet().setTargetData(Utils.getDoubleMatrix(
                new File("./simulations/tables/5_binary.csv")));

        //BackpropTrainer trainer = new BackpropTrainer(network);
        BackpropTrainer2 trainer = new BackpropTrainer2(network);
        trainer.initData();
        //trainer.setUpdateMethod(BackpropTrainer2.UpdateMethod.EPOCH);
        trainer.setLearningRate(.1);
        trainer.setMomentum(.9);
        //trainer.rand.setPdf(ProbDistribution.UNIFORM);
        //trainer.rand.setParam1(1);
        //trainer.rand.setParam2(1.1);
        trainer.randomize();
        
        for (int i = 0; i < 10_000; i++) {
            trainer.apply();
            if (i % 1000 == 0) {
            	System.out.println(trainer.getError());                
            }
        }
        
    }

    public static void testSineWave() {
    	BackpropNetwork network = new BackpropNetwork(new Network(),
                new int[] { 1, 5, 1 });
    	
    	double[][] inpData = new double[100][1];
    	double[][] targData = new double[100][1];
    	for(int ii=0; ii<100; ++ii) {
    		inpData[ii][0] = ii * (2 * Math.PI / 100);
    		targData[ii][0] = Math.sin(inpData[ii][0])/10 + 0.5;
    		//System.out.println(inpData[ii][0] + "     " + 10*(targData[ii][0]-0.5));
    	}
    	network.getTrainingSet().setInputData(inpData);
    	network.getTrainingSet().setTargetData(targData);
    	
        BackpropTrainer2 trainer = new BackpropTrainer2(network);
        //BackpropTrainer trainer = new BackpropTrainer(network);
        trainer.initData();
        
        trainer.setLearningRate(0.05);
        trainer.setMomentum(0.5);
      // trainer.setUpdateMethod(UpdateMethod.EPOCH);
     //   int plorp = 0;
        for (int i = 0; i <= 10_000; i++) {
            trainer.apply();
//            if (i % (1000) == 0) {
//            	DoubleMatrix bob = trainer.getWeightMatrices().get(1);
//            	for(int jj=0; jj<5; jj++) {
//               	System.out.println(Arrays.toString(bob.data));
//            	}
            	//plorp++;
            	System.out.println(trainer.getError()); //+ "      " + i + "     " + 10*(trainer.layers.get(1).data[0]-0.5));  
            	
//            }
        }
        
    	
    }
    
    public static void testDumb() {
    	DoubleMatrix bob = DoubleMatrix.zeros(5);
    	bob.addi(2);
    	DoubleMatrix jim = DoubleMatrix.zeros(5);
    	jim.addi(1);
    	DoubleMatrix tom = DoubleMatrix.zeros(5);
    	
    	bob.subi(jim, tom);
    	
    	System.out.println(Arrays.toString(tom.data));
    	
    }
    
    /**
     * Test based on this discussion.
     * https://mattmazur.com/2015/03/17/a-step-by-step-backpropagation-example/
     */
    public static void testMazur() {

        BackpropNetwork network = new BackpropNetwork(new Network(),
                new int[] { 2, 2, 2 });

        network.getTrainingSet().setInputData(new double[][] { { .05, .10 } });
        network.getTrainingSet().setTargetData(new double[][] { { .01, .99 } });

        // BackpropTrainer trainer = new BackpropTrainer(network);
        BackpropTrainer2 trainer = new BackpropTrainer2(network);
        trainer.initData();
        trainer.setMomentum(0);
        trainer.setLearningRate(.5);

        DoubleMatrix weightLayer1 = new DoubleMatrix(
                new double[][] { { .15, .25 }, { .2, .3 } });
        trainer.getWeightMatrices().set(0, weightLayer1);
        DoubleMatrix weightLayer2 = new DoubleMatrix(
                new double[][] { { .4, .5 }, { .45, .55 } });
        trainer.getWeightMatrices().set(1, weightLayer2);

        DoubleMatrix biases1 = new DoubleMatrix(new double[] { .35, .35 });
        trainer.getBiases().set(0, biases1);

        DoubleMatrix biases2 = new DoubleMatrix(new double[] { .6, .6 });
        trainer.getBiases().set(1, biases2);
        
        for (int i = 0; i < 1; i++) {
            trainer.apply();
            System.out.println("Error:" + trainer.getError());
        }
    }

}
