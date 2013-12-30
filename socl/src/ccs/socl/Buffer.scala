package ccs.socl

class Buffer( size: Int ) {
  val buff = new Array[Byte]( size );

  def byte( idx: Int ) =
    buff(idx)

  def ubyte( idx: Int ) =
    (byte(idx).toShort & 0xFF).toShort

  def short( idx: Int ) =
    ubyte(idx) | (ubyte(idx+1) << 8)

  def ushort( idx: Int ) =
    (short(idx).toInt & 0xFFFF).toInt

  def int( idx: Int ) =
    ushort(idx) | (ushort(idx+2) << 16)

  def uint( idx: Int ) =
    (int(idx).toLong & 0xFFFFFFFFL).toLong

  def long( idx: Int ) =
    uint(idx) | (uint(idx+4) << 32)

  override def toString =
    buff.mkString( "[", ",", "]" );
}
