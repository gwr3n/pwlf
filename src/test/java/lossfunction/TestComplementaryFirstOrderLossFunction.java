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
