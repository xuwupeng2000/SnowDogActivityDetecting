

package com.wu.wavelet;




public class DaubechiesTranform extends LiftingSchemeBase {
  final static double sqrt3 = Math.sqrt( 3 );
  final static double sqrt2 = Math.sqrt( 2 );

  protected void normalize( double[] S, int N, int direction )
  {
    int half = N >> 1;

    for (int n = 0; n < half; n++) {
      if (direction == forward) {
	S[n] = ((sqrt3-1.0)/sqrt2) * S[n];
	S[n+half] = ((sqrt3+1.0)/sqrt2) * S[n+half];
      }
      else if (direction == inverse) {
	S[n] = ((sqrt3+1.0)/sqrt2) * S[n];
	S[n+half] = ((sqrt3-1.0)/sqrt2) * S[n+half];
      }
      else {
	System.out.println("daubechies::normalize: bad direction value");
	break;
      }
    }
  }


  protected void predict( double[] S, int N, int direction )
  {
    int half = N >> 1;

    if (direction == forward) {
      S[half] = S[half] - (sqrt3/4.0)*S[0] - (((sqrt3-2)/4.0)*S[half-1]);
    }
    else if (direction == inverse) {
      S[half] = S[half] + (sqrt3/4.0)*S[0] + (((sqrt3-2)/4.0)*S[half-1]);
    }
    else {
      System.out.println("daubechies::predict: bad direction value");
    }

    

    for (int n = 1; n < half; n++) {
      if (direction == forward) {
	S[half+n] = S[half+n] - (sqrt3/4.0)*S[n] - (((sqrt3-2)/4.0)*S[n-1]);
      }
      else if (direction == inverse) {
	S[half+n] = S[half+n] + (sqrt3/4.0)*S[n] + (((sqrt3-2)/4.0)*S[n-1]);
      }
      else {
	break;
      }
    }

  }  


  protected void updateOne(  double[] S, int N, int direction )
  {
    int half = N >> 1;

    for (int n = 0; n < half; n++) {
      double updateVal = sqrt3 * S[half+n];

      if (direction == forward) {
	S[n] = S[n] + updateVal;
      }
      else if (direction == inverse) {
	S[n] = S[n] - updateVal;
      }
      else {
	System.out.println("daubechies::updateOne: bad direction value");
	break;
      }
    }
  } 



  protected void update(  double[] S, int N, int direction )
  {
    int half = N >> 1;

    for (int n = 0; n < half-1; n++) {
      if (direction == forward) {
	S[n] = S[n] - S[half+n+1];
      }
      else if (direction == inverse) {
	S[n] = S[n] + S[half+n+1];
      }
      else {
	System.out.println("daubechies::update: bad direction value");
	break;
      }
    }

    if (direction == forward) {
      S[half-1] = S[half-1] - S[half];
    }
    else if (direction == inverse) {
      S[half-1] = S[half-1] + S[half];
    }
  } 


  public void forwardTrans( double[] vec )
  {
    final int N = vec.length;

    for (int n = N; n > 1; n = n >> 1) {
      split( vec, n );
      updateOne( vec, n, forward );  
      predict( vec, n, forward );
      update( vec, n, forward );      
      normalize( vec, n, forward );
    }
  } 



 
  public void inverseTrans( double[] vec )
  {
    final int N = vec.length;

    for (int n = 2; n <= N; n = n << 1) {
      normalize( vec, n, inverse );
      update( vec, n, inverse );
      predict( vec, n, inverse );
      updateOne(vec, n, inverse );
      merge( vec, n );
    }
  } 
}
