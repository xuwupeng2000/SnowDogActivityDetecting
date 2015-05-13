
package com.wu.wavelet;

// Lifting Scheme Base abstract class
public abstract class LiftingSchemeBase {

  protected final int forward = 1;
  protected final int inverse = 2;

  // split vector {1,2,3,4,5,6} as {1,3,5,2,4,6}
  protected void split( double[] vec, int N )
  {
    
    int start = 1;
    int end = N - 1;

    while (start < end) {
      for (int i = start; i < end; i = i + 2) {
	double tmp = vec[i];
	vec[i] = vec[i+1];
	vec[i+1] = tmp;
      }
      start = start + 1;
      end = end - 1;
    }
  }

  // merge {1,3,5,2,4,6} back to {1,2,3,4,5,6}
  protected void merge( double[] vec, int N )
  {
    int half = N >> 1;
    int start = half-1;
    int end = half;
    
    while (start > 0) {
      for (int i = start; i < end; i = i + 2) {
	double tmp = vec[i];
	vec[i] = vec[i+1];
	vec[i+1] = tmp;
      }
      start = start - 1;
      end = end + 1;
    }
  }



  protected abstract void predict( double[] vec, int N, int direction );

  
  protected abstract void update(  double[] vec, int N, int direction );


  // forwardTrans  
  // vec size must be a power of two
  // vec[0] is the final average of the input data set vec
  // vec[1]-vec[n] is the set of wavelet coefficients ordered by increasing frequency
  // vec[1]-vec[n] is ordered as (1,2,4,8,16...)
  public void forwardTrans( double[] vec )
  {
    final int N = vec.length;

    for (int n = N; n > 1; n = n >> 1) {
      split( vec, n );
      predict( vec, n, forward );
      update( vec, n, forward );
    }
  } 



  // inverse transform
  // get everything back
  public void inverseTrans( double[] vec )
  {
    final int N = vec.length;

    for (int n = 2; n <= N; n = n << 1) {
      update( vec, n, inverse );
      predict( vec, n, inverse );
      merge( vec, n );
    }
  } 


} 
