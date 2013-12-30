import std.file;
import std.regex;
import std.conv;

struct Process {
    uint	pid;
    string	cmd, cmdline;
    this( uint pid, string cmd, string cmdline ) {
	this.pid = pid;
	this.cmd = cmd;
	this.cmdline = cmdline;
    }
    string toString() {
	return to!string(pid) ~ ":" ~ cmd;
    }
}

Process[] userProcesses() {
    string read( string fname ) {
	char[] _ = cast(char[]) std.file.read(fname);
	return _ ? to!string(_[0..$-1]) : null;
    }
    enum rex = ctRegex!( "/proc/(\\d+)" );
    Process[] result = [];
    foreach ( DirEntry e; dirEntries("/proc", SpanMode.shallow) )
	if ( auto m = match(e.name, rex) ) {
	    string comm = read( e.name ~ "/comm" );
	    if ( !comm )
		continue;
	    string cmdline = read( e.name ~ "/cmdline" );
	    result ~= Process(to!uint(m.captures[1]), comm, cmdline);
	}
    return result;
}