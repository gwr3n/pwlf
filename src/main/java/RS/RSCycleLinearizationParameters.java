/**
 * pwlf: Piecewise Linearization of the First-Order Loss Function
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

package RS;

import localsearch.LocalSearch;
import lossfunction.PiecewiseComplementaryFirstOrderLossFunction;
import umontreal.ssj.probdist.Distribution;
import umontreal.ssj.probdist.EmpiricalDist;
import umontreal.ssj.probdist.ExponentialDist;
import umontreal.ssj.probdist.NormalDist;
import umontreal.ssj.probdist.PoissonDist;
import umontreal.ssj.probdist.UniformDist;
import umontreal.ssj.rng.MRG32k3aL;

public class RSCycleLinearizationParameters {
	Distribution[] demand;
	double[] probabilityMasses;
	double[][][] conditionalExpectation;
	double[][] maximumApproximationError;
	
	long[] seed;
	int nbSamples;
	int population;
	int partitions;
	
	/**
	 * This class can be used to produce piecewise linearisation parameters for the first order loss function 
	 * of an arbitrarily distributed demand over a given planning horizon.
	 * 
	 * @param demand the demand distribution in each period
	 * @param seed the random seed
	 * @param nbSamples the number of samples to use in the computation of the probability masses
	 * @param population the number of samples to use in the coordinate descent (only used if coordinate descent is adopted)
	 * @param partitions the number of partitions of the random variable support (number of segments = number of partitions + 1) 
	 */
	public RSCycleLinearizationParameters(Distribution[] demand, long[] seed, int nbSamples, int population, int partitions){
		this.demand = demand;
		this.seed = seed;
		this.nbSamples = nbSamples;
		this.population = population;
		this.partitions = partitions;
		
		initialize();
	}
	
	private void initialize(){
		probabilityMasses = new double[partitions];
		conditionalExpectation = new double[demand.length][demand.length][partitions];
		maximumApproximationError = new double[demand.length][demand.length]; 
		
		PiecewiseComplementaryFirstOrderLossFunction[][] cycleLossFunction = new PiecewiseComplementaryFirstOrderLossFunction[demand.length][demand.length];
		PiecewiseComplementaryFirstOrderLossFunction[] lossFunctionArray = new PiecewiseComplementaryFirstOrderLossFunction[(int)Math.round((demand.length)*(demand.length+1)/2.0)]; 
		
		MRG32k3aL randomGenerator = new MRG32k3aL();
		randomGenerator.setSeed(seed);
		
		int counter = 0;
		for(int i = 0; i < demand.length; i++){
			for(int j = i; j < demand.length; j++){
				Distribution[] cycleDistribution = new Distribution[j-i+1];
				System.arraycopy(demand, i, cycleDistribution, 0, j-i+1);
				cycleLossFunction[i][j] = new PiecewiseComplementaryFirstOrderLossFunction(cycleDistribution, seed);
				lossFunctionArray[counter++] = cycleLossFunction[i][j];
			}
		}
		
		//probabilityMasses = LocalSearch.coordinateDescent(randomGenerator, nbSamples, lossFunctionArray, partitions, population);
		probabilityMasses = LocalSearch.uniformPartitioning(lossFunctionArray, partitions, nbSamples);
		
		for(int i = 0; i < demand.length; i++){
			for(int j = i; j < demand.length; j++){
				conditionalExpectation[i][j] = cycleLossFunction[i][j].getConditionalExpectations(probabilityMasses, nbSamples);
				maximumApproximationError[i][j] = cycleLossFunction[i][j].getMaxApproximationError(probabilityMasses, nbSamples);
				
				//cycleLossFunction[i][j].plotPiecewiseLossFunction(0, 100, probabilityMasses, nbSamples, 0.1);
			}
		}
	}
	
	public double[] getProbabilityMasses(){
		return probabilityMasses;
	}
	
	public double[] getConditionalExpectation(int i, int j){
		return conditionalExpectation[i][j];
	}
	
	public double getMaximumApproximationError(int i, int j){
		return maximumApproximationError[i][j];
	}
	
	public String getProbabilityMassesTable(){
		String table = "";
		table += "[";
		for(int k = 0; k < partitions-1; k++){
			table += probabilityMasses[k];
			if(k < partitions-1)
				table += ",";
		}
		table += probabilityMasses[partitions-1];
		table += "];";
		return table;
	}
	
	public String getConditionalExpectationTable(){
		String table = "";
		table += "[";
		for(int i = 0; i < demand.length; i++){
			table += "[";
			for(int j = 0; j < demand.length; j++){
				if(j >= i){
					table += "[";
					for(int k = 0; k < partitions-1; k++){
						table += conditionalExpectation[i][j][k];
						if(k < partitions-1)
							table += ",";
					}
					table += conditionalExpectation[i][j][partitions-1];
					table += "]";
					if(j < demand.length-1)
						table += ",";
				}else{
					table += "[";
					for(int k = 0; k < partitions; k++){
						table += "0";
						if(k < partitions-1)
							table += ",";
					}
					table += "],";
				}
			}
			table += "]";
			if(i < demand.length-1)
				table += ",\n";
		}
		table += "];";
		return table;
	}
	
	public String getMaximumApproximationErrorTable(){
		String table = "";
		table += "[";
		for(int i = 0; i < demand.length; i++){
			table += "[";
			for(int j = 0; j < demand.length; j++){
				table += maximumApproximationError[i][j];
				if(j < demand.length-1)
					table += ",";
			}
			table += "]";
			if(i<demand.length-1)
				table += ",\n";
		}
		table += "];";
		return table;
	}
}
