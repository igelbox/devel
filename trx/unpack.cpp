#include <stdio.h>

typedef unsigned int uint32;

typedef struct {
    uint32	magic;
    uint32	len;
    uint32	crc32;
    uint32	fv;
    uint32	offsets[3];
} trx;

int main( int argc, const char** argv ) {
    const char* fn = argv[1];
    printf( "file: %s\n", fn );
    FILE *f = fopen( fn, "r" );
    trx h;
    fread( &h, sizeof h, 1, f );
    printf( "magic: %04X\n", h.magic );
    printf( "length: %d\n", h.len );
    for ( int i = 2; i < 3; i++ )
	printf( "offset[%d]: %d\n", i, h.offsets[i] );
    for ( int i = 2; i < 3; i++ ) {
	printf( "unpacking[%d]\n", i );
	int left = i == 2 ? 0x0FFFFFFF : h.offsets[i+1] - h.offsets[i];
	fseek( f, h.offsets[i], SEEK_SET );
	char buff[4096];
	sprintf( buff, "part_%d.raw", i );
	FILE *fo = fopen( buff, "w" );
	int c;
	while ( (left > 0) && ((c = fread(buff, 1, sizeof buff, f)) > 0) )
	{
	    fwrite( buff, 1, c, fo );
	    left -= c;
	}
	fclose( fo );
    }
    fclose( f );
    return 0;
}
