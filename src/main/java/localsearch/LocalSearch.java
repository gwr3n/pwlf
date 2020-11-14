/**
 * pwlf: Piecewise Linearization of Arbitrary First Order Loss Functions
 * 
 * MIT License
 * 
 * Copyright (c) 2020 Roberto Rossi
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy 
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package localsearch;

import java.util.ArrayList;
import java.util.Arrays;

import lossfunction.PiecewiseComplementaryFirstOrderLossFunction;
import umontreal.ssj.randvar.UniformIntGen;
import umontreal.ssj.rng.MRG32k3aL;

public class LocalSearch {
	
	public static double[] uniformPartitioning(PiecewiseComplementaryFirstOrderLossFunction[] pwcfolfs, int partitions, int nbSamples){
		double[] probabilityMass = new double[partitions];
		Arrays.fill(probabilityMass, 1.0/partitions);
		double maxApproxError = 0;
		for(int i = 0; i < pwcfolfs.length; i++){
			maxApproxError = Math.max(maxApproxError,pwcfolfs[i].getMaxApproximationError(probabilityMass, nbSamples));
		}
		System.out.println("Minimax (UP): "+maxApproxError);
		return probabilityMass;
	}
	
	public static double[] coordinateDescent(MRG32k3aL randomGenerator, int nbSamples, 
			PiecewiseComplementaryFirstOrderLossFunction[] pwcfolfs, int partitions, int population){

		double[] probabilityMass = simpleRandomSampling(randomGenerator, nbSamples, pwcfolfs, partitions, population);
		
		double[] bestMass = hillClimbing(randomGenerator, probabilityMass, nbSamples, pwcfolfs, partitions, population);
		
		return bestMass;
	}
	
	public static double[] hillClimbing(MRG32k3aL randomGenerator, double[] probabilityMass, int nbSamples, 
			PiecewiseComplementaryFirstOrderLossFunction[] pwcfolfs, int partitions, int population){
		double precision = 1.0/nbSamples;

		double currentApproxError = 0;
		for(int i = 0; i < pwcfolfs.length; i++){
			currentApproxError = Math.max(currentApproxError,pwcfolfs[i].getMaxApproximationError(probabilityMass, nbSamples));
		}
		
		boolean stop = false;
		do{
			System.out.print(".");
			for(int k = 0; k < probabilityMass.length-1; k++){
				double[] probabilityMassL = new double[partitions];
				System.arraycopy(probabilityMass, 0, probabilityMassL, 0, partitions);
				if(probabilityMassL[k] > precision){
					probabilityMassL[k] -= precision;
					probabilityMassL[k+1] += precision;
				}
				double maxApproxErrorL = 0;
				for(int i = 0; i < pwcfolfs.length; i++){
					maxApproxErrorL = Math.max(maxApproxErrorL,pwcfolfs[i].getMaxApproximationError(probabilityMassL, nbSamples));
				}	
				double[] probabilityMassR = new double[partitions];
				System.arraycopy(probabilityMass, 0, probabilityMassR, 0, partitions);
				if(probabilityMassR[k] < 1.0-precision){
					probabilityMassR[k] += precision;
					probabilityMassR[k+1] -= precision;
				}
				double maxApproxErrorR = 0;
				for(int i = 0; i < pwcfolfs.length; i++){
					maxApproxErrorR = Math.max(maxApproxErrorR,pwcfolfs[i].getMaxApproximationError(probabilityMassR, nbSamples));
				}
				if(maxApproxErrorL<currentApproxError){
					System.arraycopy(probabilityMassL, 0, probabilityMass, 0, partitions);
					currentApproxError = maxApproxErrorL;
					break;
				}else if(maxApproxErrorR<currentApproxError){
					System.arraycopy(probabilityMassR, 0, probabilityMass, 0, partitions);
					currentApproxError = maxApproxErrorR;
					break;
				}else{
					if(k == probabilityMass.length-2) stop = true;
				}
			}
			/*System.out.print(currentApproxError+": ");
			for(int j = 0; j < partitions; j++){
				System.out.print(probabilityMass[j]+"\t");
			}
			System.out.println();*/
		}while(!stop && probabilityMass.length > 1);

		System.out.println("Minimax (HC): "+currentApproxError);
		for(int j = 0; j < partitions; j++){
			System.out.print(probabilityMass[j]+"\t");
		}
		System.out.println();
		
		return probabilityMass;
	}
	
	public static double[] simpleRandomSampling(MRG32k3aL randomGenerator, int nbSamples, 
			PiecewiseComplementaryFirstOrderLossFunction[] pwcfolfs,
			int partitions, int population){

		ArrayList<double[]> probabilityMassPool = new ArrayList<double[]>();
		//double[][] masses = LHSampling.latin_random(population, partitions, randomGenerator);
		for(int i = 0; i < population; i++){
			if(i % 100 == 0)System.out.print(".");
			double[] probabilityMass = new double[partitions];
			double totalMass = 0;
			for(int j = 0; j < partitions; j++){
				probabilityMass[j] = UniformIntGen.nextInt(randomGenerator, 1, nbSamples);
				//probabilityMass[j] = masses[i][j];
				totalMass += probabilityMass[j];
			}
			for(int j = 0; j < partitions; j++){
				probabilityMass[j] /= totalMass;
			}
			
			probabilityMassPool.add(probabilityMass);
		}
		
		System.out.println();

		int counter = 0;
		double minMaxApproxError = Double.MAX_VALUE;
		double[] bestMass = null;
		for(double[] probabilityMass : probabilityMassPool){
			if(++counter % 100 == 0) System.out.print(counter+"..");
			double maxApproxError = 0;
			for(int i = 0; i < pwcfolfs.length; i++){
				maxApproxError = Math.max(maxApproxError,pwcfolfs[i].getMaxApproximationError(probabilityMass, nbSamples));
			}
			if(maxApproxError<minMaxApproxError){
				bestMass = probabilityMass;
				minMaxApproxError = maxApproxError;
			}
		}

		System.out.println("Minimax (SRS): "+minMaxApproxError);
		for(int j = 0; j < partitions; j++){
			System.out.print(bestMass[j]+"\t");
		}
		System.out.println();
		
		return bestMass;
	}
	
}
