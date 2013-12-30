import std.stdio;
import std.c.linux.linux;
import std.process;
import std.conv;

class Pipe {
    const int	r, w;
    this() {
	int[2] tmp;
	pipe( tmp );
	this.r = tmp[0];
	this.w = tmp[1];
    }
    ~this() {
	close( this.r );
	close( this.w );
    }
    const string read() {
	char[256] buff;
	auto c = core.sys.posix.unistd.read( this.r, buff.ptr, buff.length );
	return to!string(buff[0..c]);
    }
}

class Qemu {
    static Qemu launch( string[] args ... ) {
	const Pipe stdinp = new Pipe, stdout = new Pipe, stderr = new Pipe;
	auto pid = fork();
	if ( !pid )
	{
	    string[] _args = ["qemu", "-qmp", "stdio", "-vnc", "none"];
	    _args ~= args;
	    //writeln( _args, args );
	    close( stdinp.w ); dup2( stdinp.r, STDIN_FILENO  ); close( stdinp.r );
	    close( stdout.r ); dup2( stdout.w, STDOUT_FILENO ); close( stdout.r );
	    close( stderr.r ); dup2( stderr.w, STDERR_FILENO ); close( stderr.r );
	    auto r = execvp( _args[0], _args );
	}
	close( stdinp.r ); close( stdout.w ); close( stderr.w );
	writeln( "|", stdout.read(), "|" );
	return new Qemu( pid, stdinp, stdout, stderr );
    }
private:
    const int	pid;
    const Pipe	stdinp, stdout, stderr;
    this( int pid, in Pipe stdinp, in Pipe stdout, in Pipe stderr ) {
	this.pid = pid;
	this.stdinp = stdinp;
	this.stdout = stdout;
	this.stderr = stderr;
    }
}
