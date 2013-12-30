//import std.socket;

interface InputStream {
    bool has( uint n );
    void read( void[] data, uint size );
}

struct DataInputStream: InputStream {
    ubyte readUByte();
    uint readUInt();
}