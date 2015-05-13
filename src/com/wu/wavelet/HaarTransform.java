

package com.wu.wavelet;

public class HaarTransform extends LiftingSchemeBase {

  // Harr Transform predict stage
  protected void predict( double[] vec, int N, int direction )
  {
    int half = N >> 1;
    int cnt = 0;

    for (int i = 0; i < half; i++) {
      double predictVal = vec[i];
      int j = i + half;

      if (direction == forward) {
	vec[j] = vec[j] - predictVal;
      }
      else if (direction == inverse) {
	vec[j] = vec[j] + predictVal;
      }
      else {
	System.out.println("haar::predict: bad direction value");
      }
    }
  }


  // Update
  protected void update( double[] vec, int N, int direction )
  {
    int half = N >> 1;

    for (int i = 0; i < half; i++) {
      int j = i + half;
      double updateVal = vec[j] / 2.0;

      if (direction == forward) {
	vec[i] = vec[i] + updateVal;
      }
      else if (direction == inverse) {
	vec[i] = vec[i] - updateVal;
      }
      else {
	System.out.println("update: bad direction value");
      }
    }
  }


} 
