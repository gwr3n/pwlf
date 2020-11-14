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

import umontreal.ssj.probdist.Distribution;
import umontreal.ssj.probdist.ExponentialDist;
import umontreal.ssj.probdist.NormalDist;
import umontreal.ssj.probdist.PoissonDist;

public class TestComplementaryFirstOrderLossFunction {
   
   public static void main(String[] args){
      //testDistributionPlot1();
      //testDistributionPlot2();
      testLossFunctionPlot();
   }
   
   public static void testDistributionPlot1(){
      long[] seed = {1,2,3,4,5,6};
      Distribution[] distributions = new Distribution[3];
      double lambda[] = {20,5,50};
      distributions[0] = new PoissonDist(lambda[0]);
      distributions[1] = new PoissonDist(lambda[1]);
      distributions[2] = new PoissonDist(lambda[2]);
      ComplementaryFirstOrderLossFunction cfolf = new ComplementaryFirstOrderLossFunction(distributions, seed);
      cfolf.plotEmpiricalDistribution(1000, 1);
   }
   
   public static void testDistributionPlot2(){
      long[] seed = {1,2,3,4,5,6};
      Distribution[] distributions = new Distribution[2];
      distributions[0] = new ExponentialDist(0.1);
      distributions[1] = new NormalDist(10,2);
      ComplementaryFirstOrderLossFunction cfolf = new ComplementaryFirstOrderLossFunction(distributions, seed);
      cfolf.plotEmpiricalDistribution(1000, 1);
   }
   
   private static void testLossFunctionPlot(){
      long[] seed = {1,2,3,4,5,6};
      Distribution[] distributions = new Distribution[3];
      double lambda[] = {20,5,50};
      distributions[0] = new PoissonDist(lambda[0]);
      distributions[1] = new PoissonDist(lambda[1]);
      distributions[2] = new PoissonDist(lambda[2]);
      ComplementaryFirstOrderLossFunction cfolf = new ComplementaryFirstOrderLossFunction(distributions, seed);
      cfolf.plotEmpiricalLossFunction(50, 90, 1000, 1, true);
   }
}
