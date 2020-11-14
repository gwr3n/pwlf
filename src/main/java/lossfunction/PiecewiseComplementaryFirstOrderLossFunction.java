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

package lossfunction;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import umontreal.ssj.charts.XYLineChart;
import umontreal.ssj.probdist.Distribution;
import umontreal.ssj.probdist.EmpiricalDist;
import umontreal.ssj.probdist.NormalDist;
import umontreal.ssj.probdist.PoissonDist;
import umontreal.ssj.probdist.ExponentialDist;

public class PiecewiseComplementaryFirstOrderLossFunction extends
ComplementaryFirstOrderLossFunction {

	public PiecewiseComplementaryFirstOrderLossFunction(Distribution[] distributions, long[] seed){
		super(distributions, seed);
	}

	public double[] getConditionalExpectations(double[] probabilityMasses, int nbSamples){
		double[] conditionalExpectations = new double[probabilityMasses.length];
		EmpiricalDist empDistribution = this.getEmpiricalDistribution(nbSamples);
		double probabilityMass = 0;
		int conditionalExpectationIndex = 0;
		for(int i = 0; i < empDistribution.getN(); i++){
			if(probabilityMass < 1 && probabilityMass < probabilityMasses[conditionalExpectationIndex]){
				conditionalExpectations[conditionalExpectationIndex] += empDistribution.getObs(i)/empDistribution.getN();
				probabilityMass += 1.0/empDistribution.getN();
			}else{
				conditionalExpectations[conditionalExpectationIndex] /= probabilityMasses[conditionalExpectationIndex];
				probabilityMass = 0;
				conditionalExpectationIndex++;
			}
		}
		conditionalExpectations[conditionalExpectationIndex] /= probabilityMasses[conditionalExpectationIndex];
		return conditionalExpectations;
	}

	public XYSeries getLossFunctionXYSeriesForSegment(int segmentIndex, 
			double[] probabilityMasses, 
			double[] conditionalExpectations, 
			double min, 
			double max, 
			double minYValue,
			double precision){
		XYSeries series = new XYSeries("Piecewise complementary loss function");
		for(double x = min; x <= max; x+= precision){
			double value = getPiecewiseLossFunctionValue(segmentIndex, x, probabilityMasses, conditionalExpectations);
			if(value >= minYValue) series.add(x, value);
		}
		return series;
	}

	public double getPiecewiseLossFunctionValue(int segmentIndex, double x, double[] probabilityMasses, double[] conditionalExpectations){
		double value = 0;
		for(int i = 1; i <= segmentIndex; i++){
			value += (x-conditionalExpectations[i-1])*probabilityMasses[i-1];
		}
		return value;
	}
	
	public XYSeries getPiecewiseErrorXYSeries(double min, double max, int nbSamples, double[] probabilityMasses, double[] conditionalExpectations, double precision){
		XYSeries series = new XYSeries("Piecewise complementary loss function error");
		for(double x = min; x <= max; x+= precision){
			series.add(x, getPiecewiseErrorValue(x, nbSamples, probabilityMasses, conditionalExpectations));
		}
		return series;
	}
	
	public double getPiecewiseErrorValue(double x, int nbSamples, double[] probabilityMasses, double[] conditionalExpectations){
		double lossFunctionValue = this.getLossFunctionValue(x, nbSamples);

		double maxValue = 0;
		for(int j = 0; j <= probabilityMasses.length; j++){
			double value = 0;
			for(int i = 1; i <= j; i++){
				value += (x-conditionalExpectations[i-1])*probabilityMasses[i-1];
			}
			maxValue = Math.max(maxValue, value);
		}
		
		return lossFunctionValue-maxValue;
	}

	public void plotPiecewiseLossFunction(double min, double max, double minYValue, double[] probabilityMasses, int nbSamples, double precision, boolean saveToDisk){
		int segments = probabilityMasses.length + 1;
		double[] conditionalExpectations = this.getConditionalExpectations(probabilityMasses, nbSamples);

		XYSeriesCollection xyDataset = new XYSeriesCollection();

		xyDataset.addSeries(this.getLossFunctionXYSeries(min, max, nbSamples, precision));

		for(int i = 0; i < segments; i++)
			xyDataset.addSeries(this.getLossFunctionXYSeriesForSegment(i, probabilityMasses, conditionalExpectations, min, max, minYValue, precision));

		xyDataset.addSeries(this.getPiecewiseErrorXYSeries(min, max, nbSamples, probabilityMasses, conditionalExpectations, precision));
		
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

	public double[] getApproximationErrors(double[] probabilityMasses, int nbSamples){
		double[] conditionalExpectations = this.getConditionalExpectations(probabilityMasses, nbSamples);
		double[] approximationErrors = new double[conditionalExpectations.length];

		for(int i = 0; i < probabilityMasses.length; i++){
			approximationErrors[i] = getLossFunctionValue(conditionalExpectations[i], nbSamples)-
					getPiecewiseLossFunctionValue(i, conditionalExpectations[i], probabilityMasses, conditionalExpectations);
		}

		return approximationErrors;
	}

	public double getMaxApproximationError(double[] probabilityMasses, int nbSamples){
		double[] approximationErrors = this.getApproximationErrors(probabilityMasses, nbSamples);
		double maxApproximationError = 0;

		for(int i = 0; i < probabilityMasses.length; i++){
			maxApproximationError = Math.max(maxApproximationError, approximationErrors[i]);
		}

		return maxApproximationError;
	}
}
