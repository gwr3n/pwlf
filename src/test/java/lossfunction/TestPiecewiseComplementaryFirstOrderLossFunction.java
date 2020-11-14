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
