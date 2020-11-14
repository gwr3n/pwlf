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
import umontreal.ssj.probdist.NormalDist;

public class TestPiecewiseComplementaryFirstOrderLossFunction {
   public static void main(String[] args){
      testPiecewiseLossFunction();
      testApproximationErrors();
   }

   private static void testPiecewiseLossFunction(){
      long[] seed = {1,2,3,4,5,6};
      Distribution[] distributions = new Distribution[1];
      distributions[0] = new NormalDist(0,1);
      //distributions[1] = new ExponentialDist(0.1);
      //distributions[0] = new PoissonDist(20);
      PiecewiseComplementaryFirstOrderLossFunction pwcfolf = new PiecewiseComplementaryFirstOrderLossFunction(distributions, seed);
      double[] probabilityMasses = {0.5,0.5};
      int nbSamples = 1000;
      pwcfolf.plotPiecewiseLossFunction(-2, 2, -1, probabilityMasses, nbSamples, 0.1, true);
   }
     
   private static void testApproximationErrors(){
      long[] seed = {1,2,3,4,5,6};
      Distribution[] distributions = new Distribution[1];
      distributions[0] = new NormalDist(0,1);
      //distributions[1] = new ExponentialDist(0.1);
      PiecewiseComplementaryFirstOrderLossFunction pwcfolf = new PiecewiseComplementaryFirstOrderLossFunction(distributions, seed);
      double[] probabilityMasses = {0.5,0.5};
      int nbSamples = 1000;
      double[] approximationErrors = pwcfolf.getApproximationErrors(probabilityMasses, nbSamples);
      for(int i = 0; i < probabilityMasses.length; i++){
         System.out.print(approximationErrors[i]+"\t");
      }
   }
}
