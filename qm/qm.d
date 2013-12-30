import std.stdio;
import std.socket;

import qemu;
import server;
import iostream;

void main() {
//    Qemu q = Qemu.launch();
    Socket server = new Socket( AddressFamily.INET, SocketType.STREAM, ProtocolType.IP );
    try {
	server.bind( new InternetAddress(6666) );
	server.listen( 4 );
	Socket client = server.accept();
	try {
	    client.send( "hello".dup );
	} finally {
	    client.close();
	}
    } finally {
	server.close();
    }
/*    auto pid = fork();
    if ( pid ) {
	//self
	writeln( "pid:", pid );
    } else {
	//child
	auto args = ["qemu"];
	args ~= "-qmp";
	args ~= "tcp:localhost:4444,server";
	writeln( "execvp" );
	auto r = execvp( "qemu", args );
	writeln("r:", r);
    }*/
//    writeln( pid, errno );
}