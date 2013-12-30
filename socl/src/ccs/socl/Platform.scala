package ccs.socl

object Platform {
  def incontext[B]( f: Long => B ): B = {
    val pids = new Array[Long](1)
    val c = CL.getPlatformIDs( pids )
    val props = new Array[Long](2)
    props(0) = CL.CONTEXT_PLATFORM
    props(1) = pids(0)
    val context = CL.createContextFromType( props, CL.DEVICE_TYPE_ALL )
    try {
      f( context )
    } finally {
      CL.releaseContext( context )
    }
  }
}
