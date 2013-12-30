package ccs.socl

object Main {
  System.loadLibrary( "socl" )

  def main( args: Array[String] ) {
    val count = 16;
    Platform incontext {
      c => 
      Device inqueue ( c, {
          q => {
            val p = CL.createProgramWithSource( c,
                                               ("__kernel void x(__global int *o) {"+
                                                "  int idx = get_global_id(0);"+
                                                "  o[idx] = idx*idx;"+
                                                "}").getBytes )
            try {
              CL.buildProgram( p )
              val k = CL.createKernel( p, "x".getBytes )
              try {
                val b = CL.createBuffer( c, CL.MEM_WRITE_ONLY, 4*count )
                try {
                  CL.setKernelArg( k, 0, b )
                  CL.enqueueNDRangeKernel( q, k, count )
                  val bb = new Buffer( 4*count );
                  CL.enqueueReadBuffer( q, b, true, bb.buff )
                  for( i <- 0 until count )
                    println( bb.int(i*4) );
                } finally {
                  CL.releaseMemObj( b );
                }
              } finally {
                CL.releaseKernel( k )
              }
            } finally {
              CL.releaseProgram( p )
            }
          }
        })
    }
  }
}
