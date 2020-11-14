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

package sampling.latinhypercube;

import umontreal.ssj.rng.MRG32k3aL;

public class LHSampling {

    public static double[][] latin_random ( int dim_num, int point_num, MRG32k3aL randomGenerator){
		
        double x[][] = new double[dim_num][point_num];
        int [] perm;

        for (int i = 0; i < dim_num; i++ )
        {
            perm = perm_random ( point_num, randomGenerator );

        for (int j = 0; j < point_num; j++ )
        {
          x[i][j] = ( ( ( double ) ( perm[j] - 1 ) ) + randomGenerator.nextDouble() ) / ( ( double ) point_num );
        }
      }
      return x;
    }

    static int getUniform(int a, int b, MRG32k3aL randomGenerator) {
        double randDbl = randomGenerator.nextDouble();
        randDbl = (1.0 - randDbl) * ((double) (Math.min(a, b)) - 0.5)
                  + randDbl * ((double) (Math.max(a, b)) + 0.5);

        randDbl = Math.round(randDbl);

        randDbl = Math.max(randDbl, Math.min(a, b));
        randDbl = Math.min(randDbl, Math.max(a, b));

        return (int)randDbl;
    }

    static int[] perm_random ( int point_num, MRG32k3aL randomGenerator){
        int[] perm = new int[point_num];

        for (int i = 0; i < point_num; i++) {
            perm[i] = i + 1;
        }

        for (int i = 1; i <= point_num; i++) {
            int j = getUniform(i, point_num, randomGenerator);
            int swap = perm[i - 1];
            perm[i - 1] = perm[j - 1];
            perm[j - 1] = swap;
        }

        return perm;
    }

    static void test(){
        int dimNumber = 12;
        int pointNumber = 10;
        long[] seed = {1,2,3,4,5,6};

    	MRG32k3aL randomGenerator = new MRG32k3aL();
		randomGenerator.setSeed(seed);
        double[][] latin = latin_random(dimNumber, pointNumber, randomGenerator);

        for(int j = 0; j < pointNumber; j++){
            for(int i = 0; i < dimNumber; i++){
                System.out.print((""+latin[i][j]).substring(0,5)+ "\t");
            }
            System.out.println();
        }
    }

    public static void main(String args[]){
        LHSampling.test();
    }
}
