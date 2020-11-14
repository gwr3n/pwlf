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

package lossfunction;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import umontreal.ssj.charts.XYLineChart;
import umontreal.ssj.probdist.Distribution;
import umontreal.ssj.probdist.EmpiricalDist;
import umontreal.ssj.probdist.ExponentialDist;
import umontreal.ssj.probdist.NormalDist;
import umontreal.ssj.probdist.PoissonDist;
import umontreal.ssj.randvar.UniformGen;
import umontreal.ssj.rng.MRG32k3aL;

public class ComplementaryFirstOrderLossFunction {
	Distribution[] distributions;
	long[] seed;
	MRG32k3aL randGenerator;
	
	public ComplementaryFirstOrderLossFunction(Distribution[] distributions, long[] seed){
		this.distributions = distributions;
		this.randGenerator = new MRG32k3aL();
		this.randGenerator.setSeed(seed);
	}
	
	private double[][] sample(int nbSamples){
		this.randGenerator.resetStartStream();
		double[][] sampleMatrix = new double[nbSamples][this.distributions.length];
		for(int i = 0; i < sampleMatrix.length; i++){
			for(int j = 0; j < sampleMatrix[i].length; j++){
				sampleMatrix[i][j] = distributions[j].inverseF(UniformGen.nextDouble(this.randGenerator, 0, 1));
			}
		}
		return sampleMatrix;
	}
	
	public EmpiricalDist getEmpiricalDistribution(int nbSamples){
		double[][] sampleMatrix = this.sample(nbSamples);
		double[] observations = new double[nbSamples];
		for(int i = 0; i < sampleMatrix.length; i++){
			for(int j = 0; j < sampleMatrix[i].length; j++){
				observations[i] += sampleMatrix[i][j];
			}
		}
		Arrays.sort(observations);
		EmpiricalDist empDistribution = new EmpiricalDist(observations);
		return empDistribution;
	}
	
	public XYSeries getDistributionXYSeries(int nbSamples, double precision){
		XYSeries series = new XYSeries("Empirical distribution");
		EmpiricalDist empDistribution = this.getEmpiricalDistribution(nbSamples);
		for(int i = 0; i < empDistribution.getN(); i++){
			while(i>0 && i<empDistribution.getN() && empDistribution.getObs(i)==empDistribution.getObs(i-1))i++;
			series.add(empDistribution.getObs(i),empDistribution.cdf(empDistribution.getObs(i)));
		}
		return series;
	}
	
	public void plotEmpiricalDistribution(int nbSamples, double precision){
		XYDataset xyDataset = new XYSeriesCollection(this.getDistributionXYSeries(nbSamples, precision));
		JFreeChart chart = ChartFactory.createXYLineChart("Empirical distribution", "Support", "Frequency",
				 xyDataset, PlotOrientation.VERTICAL, false, true, false);
		ChartFrame frame = new ChartFrame("Empirical distribution",chart);
		frame.setVisible(true);
		frame.setSize(500,400);
	}
	
	public double getLossFunctionValue(double x, int nbSamples){
		EmpiricalDist empDistribution = this.getEmpiricalDistribution(nbSamples);
		double value = 0;
		for(int i = 0; i < empDistribution.getN(); i++){
			value += Math.max(x-empDistribution.getObs(i),0)/empDistribution.getN();
		}
		return value;
	}
	
	public XYSeries getLossFunctionXYSeries(double min, double max, int nbSamples, double precision){
		XYSeries series = new XYSeries("Empirical complementary loss function");
		for(double x = min; x <= max; x+= precision){
			series.add(x, getLossFunctionValue(x, nbSamples));
		}
		return series;
	}
	
	public void plotEmpiricalLossFunction(double min, double max, int nbSamples, double precision, boolean saveToDisk){
		XYSeriesCollection xyDataset = new XYSeriesCollection(this.getLossFunctionXYSeries(min, max, nbSamples, precision));
		JFreeChart chart = ChartFactory.createXYLineChart("Empirical complementary loss function", "x", "CL(x)",
				 xyDataset, PlotOrientation.VERTICAL, false, true, false);
		ChartFrame frame = new ChartFrame("Empirical complementary loss function",chart);
		frame.setVisible(true);
		frame.setSize(500,400);
		
		if(saveToDisk){

			XYLineChart lc = new XYLineChart("Piecewise linearization", "x", "CL(x)", xyDataset);

			try {
				File latexFolder = new File("./latex");
				if(!latexFolder.exists()){
					latexFolder.mkdir();
				}
				Writer file = new FileWriter("./latex/graph.tex");
				file.write(lc.toLatex(8, 5));
				file.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
