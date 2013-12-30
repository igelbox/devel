package ccs.socl

object Device {
  def inqueue[B]( context: Long, f: Long => B ): B = {
    val devs = new Buffer( 8 )
    CL.getContextInfo( context, CL.CONTEXT_DEVICES, devs )
    val queue = CL.createCommandQueue( context, devs.long(0) )
    try {
      f( queue )
    } finally {
      CL.releaseCommandQueue( queue )
    }
  }
}
