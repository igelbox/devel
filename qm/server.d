import std.socket;

class Server {
    this( uint port ) {
	ss = new Socket( AddressFamily.INET, SocketType.STREAM, ProtocolType.IP );
	try {
	    ss.bind( new InternetAddress(6666) );
	    ss.listen( 4 );
	} catch ( Throwable e ) {
	    ss.close();
	    throw e;
	}
    }
    ~this() {
	ss.close();
    }
    void process() {
    }
private:
    Socket	ss;
}