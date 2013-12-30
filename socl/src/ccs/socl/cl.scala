package ccs.socl

/**Low-level OpenCL access*/
object CL {
  val CONTEXT_PLATFORM        = 0x1084
  val DEVICE_TYPE_DEFAULT     = 1 << 0
  val DEVICE_TYPE_CPU         = 1 << 1
  val DEVICE_TYPE_GPU         = 1 << 2
  val DEVICE_TYPE_ACCELERATOR = 1 << 3
  val DEVICE_TYPE_ALL         = 0xFFFFFFFF
  val CONTEXT_DEVICES         = 0x1081
  val MEM_READ_WRITE          = 1 << 0
  val MEM_WRITE_ONLY          = 1 << 1
  val MEM_READ_ONLY           = 1 << 2
  val MEM_USE_HOST_PTR        = 1 << 3
  val MEM_ALLOC_HOST_PTR      = 1 << 4
  //val MEM_COPY_HOST_PTR       = 1 << 5

  @native
  def getPlatformIDs( platforms: Array[Long], offset: Int, length: Int ): Int

  def getPlatformIDs( platforms: Array[Long] ): Int =
    getPlatformIDs(platforms, 0, platforms.length)

  @native
  def createContextFromType( properties: Array[Long], deviceType: Int ): Long
  @native
  def releaseContext( context: Long )

  @native
  def getContextInfo( context: Long, paramName: Int, dst: Array[Byte], offset: Int, length: Int ): Int
  def getContextInfo( context: Long, paramName: Int, dst: Array[Byte] ): Int =
    getContextInfo( context, paramName, dst, 0, dst.length )
  def getContextInfo( context: Long, paramName: Int, dst: Buffer ): Int =
    getContextInfo( context, paramName, dst.buff )

  @native
  def createCommandQueue( context: Long, device: Long ): Long
  @native
  def releaseCommandQueue( queue: Long )

  @native
  def createProgramWithSource( context: Long, source: Array[Byte] ): Long
  @native
  def buildProgram( program: Long )
  @native
  def releaseProgram( program: Long )

  @native
  def createKernel( program: Long, name: Array[Byte] ): Long
  @native
  def setKernelArg( kernel: Long, idx: Int, memObj: Long )
  @native
  def releaseKernel( kernel: Long )

  @native
  def createBuffer( context: Long, flags: Int, data: Array[Byte] ): Long
  @native
  def createBuffer( context: Long, flags: Int, size: Int ): Long
  def releaseBuffer( buffer: Long ) =
    releaseMemObj( buffer )
  @native
  def releaseMemObj( memObj: Long )

  @native
  def enqueueNDRangeKernel( queue: Long, kernel: Long, workSize: Int )

  @native
  def enqueueReadBuffer( queue: Long, buffer: Long, sync: Boolean, dst: Array[Byte] )
}
