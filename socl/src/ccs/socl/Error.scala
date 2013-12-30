package ccs.socl

class Error(val code: Int) extends Throwable(Error.err2srt(code)) {
}

object Error {
  val CL_DEVICE_NOT_FOUND       = -01
  val CL_BUILD_PROGRAM_FAILURE  = -11
  val CL_INVALID_VALUE          = -30
  val CL_INVALID_PLATFORM       = -32
  val CL_INVALID_MEM_OBJECT     = -38
  val CL_INVALID_PROGRAM        = -44
  val CL_INVALID_KERNEL_NAME    = -46
  val CL_INVALID_BUFFER_SIZE    = -61

  def err2srt( code: Int ): String = code match {
    case CL_DEVICE_NOT_FOUND      => "CL_DEVICE_NOT_FOUND"
    case CL_BUILD_PROGRAM_FAILURE => "CL_BUILD_PROGRAM_FAILURE"
    case CL_INVALID_VALUE         => "CL_INVALID_VALUE"
    case CL_INVALID_PLATFORM      => "CL_INVALID_PLATFORM"
    case CL_INVALID_MEM_OBJECT    => "CL_INVALID_MEM_OBJECT"
    case CL_INVALID_PROGRAM       => "CL_INVALID_PROGRAM"
    case CL_INVALID_KERNEL_NAME   => "CL_INVALID_KERNEL_NAME"
    case CL_INVALID_BUFFER_SIZE   => "CL_INVALID_BUFFER_SIZE"
    case _ => "E" + code
  }
}
