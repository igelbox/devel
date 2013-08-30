#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <math.h>
#include <GL/glut.h>
#include <GL/glu.h>

#define SZ 256
float FTEMP = 0.15f;	//Minimal temperature for fire
float FFSPD = 9.00f;	//Gaz burn speed
float FFENG = 2.00f;	//Fire energy multiplicator
float FFESP = 3.00f;	//Fire energy to near particle speed factor
float FFRCT = 0.01f;	//Friction
float FDENS = 0.33f;	//Heat density
float FTURB = 0.70f;	//Turbulency factor
int   FTRRD = 32;	//Turbulency search radius
int vis = 0;
int bq = 2;

unsigned char firec[SZ][SZ][3];

float time;
int cnt;

float vfx[SZ][SZ];
float vfy[SZ][SZ];
float  ft[SZ][SZ];
float  ff[SZ][SZ];
float  fo[SZ][SZ];

inline float clamp( float x, float min, float max ) {
    if (x < min) return min;
    if (x > max) return max;
    return x;
};

inline float frand() {
    return (float)rand()/(float)RAND_MAX*2.0f-1.0f;
};

inline float getFixel( float *f, int x, int y ) {
    if ((x >= 0) && (y >= 0) && (x < SZ) && (y < SZ)) return f[y*SZ+x]; else return 0.0f;
}

inline void setFixel( float *f, int x, int y, float v ) {
    if ((x >= 0) && (y >= 0) && (x < SZ) && (y < SZ)) f[y*SZ+x] = v;
}

inline void addFixel( float *f, int x, int y, float v ) {
    if ((x >= 0) && (y >= 0) && (x < SZ) && (y < SZ)) f[y*SZ+x] += v;
}

void init( int px, int py, float rr, int cnt) {
    for ( int i = 0; i < cnt; i++ ) {
	float a = frand()*M_PI;
	float r = (frand()/2.0f+0.5f);
	float rca = r*cosf(a);
	float rsa = r*sinf(a);
	addFixel( vfx[0], rr*rca*(float)SZ+px, rr*rsa*(float)SZ+py, rca*0.5f);
	addFixel( vfy[0], rr*rca*(float)SZ+px, rr*rsa*(float)SZ+py, rsa*0.5f);
	if (frand() > 0.5f)
	    addFixel( ft[0], rr*rca*(float)SZ+px, rr*rsa*(float)SZ+py, 0.33f);
	addFixel( ff[0], rr*rca*(float)SZ+px, rr*rsa*(float)SZ+py, 1.0f);
	addFixel( fo[0], rr*rca*(float)SZ+px, rr*rsa*(float)SZ+py, 0.5f);
    }
};

float fturb( float *f, int x, int y, int r ) {
    if (!r) return 0.0f;
    float rs = 0.0f;
    float dc = 0.0f;
    for ( int i = 1; i < r; i++ ) {
	float k = pow(1.0f-(float)i/(float)r, 0.33f);
	rs += k*(getFixel(f, x-i, y-i)+getFixel(f, x+i, y+i) - getFixel(f, x+i, y-i)-getFixel(f, x-i, y+i))/4.0f;
	dc += k;
    }
    if (!dc) return 0.0f;
    return rs/dc;
}

void process( float dTime ) {
    float ovfx[SZ][SZ];
    float ovfy[SZ][SZ];
    memcpy(ovfx, vfx, sizeof(vfx));
    memcpy(ovfy, vfy, sizeof(vfy));
    memset(vfx, 0, sizeof(vfx));
    memset(vfy, 0, sizeof(vfy));
    float oft[SZ][SZ];
    memcpy(oft, ft, sizeof(ft));
    memset(ft, 0, sizeof(ft));
    float off[SZ][SZ];
    memcpy(off, ff, sizeof(ff));
    memset(ff, 0, sizeof(ff));
    float ofo[SZ][SZ];
    memcpy(ofo, fo, sizeof(fo));
    memset(fo, 0, sizeof(fo));
    for ( int j = 0; j < SZ; j++ )
	for ( int i = 0; i < SZ; i++ ) {
	    float px = ((float)i/(float)SZ + ovfx[j][i]*dTime)*(float)SZ;
	    float py = ((float)j/(float)SZ + ovfy[j][i]*dTime)*(float)SZ;
	    float kx = px-(float)((int)px);  float ky = py-(float)((int)py);

	    float ovx = (1.0f-FTURB)*getFixel(ovfx[0], i, j) + FTURB*fturb(ovfy[0], i, j, FTRRD);
	    float ovy = (1.0f-FTURB)*getFixel(ovfy[0], i, j) + FTURB*fturb(ovfx[0], i, j, FTRRD);

	    addFixel( vfx[0], px+0, py+0, ovx*(1.0f-kx)*(1.0f-ky) );
	    addFixel( vfx[0], px+0, py+1, ovx*(1.0f-kx)*(     ky) );
	    addFixel( vfx[0], px+1, py+1, ovx*(     kx)*(     ky) );
	    addFixel( vfx[0], px+1, py+0, ovx*(     kx)*(1.0f-ky) );

	    addFixel( vfy[0], px+0, py+0, ovy*(1.0f-kx)*(1.0f-ky) );
	    addFixel( vfy[0], px+0, py+1, ovy*(1.0f-kx)*(     ky) );
	    addFixel( vfy[0], px+1, py+1, ovy*(     kx)*(     ky) );
	    addFixel( vfy[0], px+1, py+0, ovy*(     kx)*(1.0f-ky) );

	    addFixel( ft[0], px+0, py+0, oft[j][i]*(1.0f-kx)*(1.0f-ky) );
	    addFixel( ft[0], px+0, py+1, oft[j][i]*(1.0f-kx)*(     ky) );
	    addFixel( ft[0], px+1, py+1, oft[j][i]*(     kx)*(     ky) );
	    addFixel( ft[0], px+1, py+0, oft[j][i]*(     kx)*(1.0f-ky) );

	    addFixel( ff[0], px+0, py+0, off[j][i]*(1.0f-kx)*(1.0f-ky) );
	    addFixel( ff[0], px+0, py+1, off[j][i]*(1.0f-kx)*(     ky) );
	    addFixel( ff[0], px+1, py+1, off[j][i]*(     kx)*(     ky) );
	    addFixel( ff[0], px+1, py+0, off[j][i]*(     kx)*(1.0f-ky) );

	    addFixel( fo[0], px+0, py+0, ofo[j][i]*(1.0f-kx)*(1.0f-ky) );
	    addFixel( fo[0], px+0, py+1, ofo[j][i]*(1.0f-kx)*(     ky) );
	    addFixel( fo[0], px+1, py+1, ofo[j][i]*(     kx)*(     ky) );
	    addFixel( fo[0], px+1, py+0, ofo[j][i]*(     kx)*(1.0f-ky) );

	}

}

void grav( float dTime ) {
    float f = pow(FDENS, dTime);
    for ( int j = 0; j < SZ; j++ )
	for ( int i = 0; i < SZ; i++ ) vfy[j][i] += f*ft[j][i];
}

void frict( float dTime ) {
    float f = pow(FFRCT, dTime);
    for ( int j = 0; j < SZ; j++ )
	for ( int i = 0; i < SZ; i++ ) {
//	    vfx[j][i] *= f;
//	    vfy[j][i] *= f;
	    ft[j][i] *= f;
	}
}

void bblur( float *f, int r ) {
    float of[SZ*SZ];
    memcpy( of, f, sizeof(of) );
    memset( f, 0, sizeof(vfx) );
    for ( int j = 0; j < SZ; j++ )
	for ( int i = 0; i < SZ; i++ ) {
	    float ar = getFixel( of, i, j );
	    float dc = 1.0f;
	    for ( int jj = j-r; jj <= j+r; jj++ )
		for ( int ii = i-r; ii <= i+r; ii++ ) {
		    ar += getFixel( of, ii, jj )*1.0f;
		    dc += 1.0f;
		}
	    f[j*SZ+i] = ar / dc;
	}
}

void fire( float dTime ) {
    float fto[SZ][SZ];
    memcpy( fto, ft, sizeof(ft) );
    for ( int j = 1; j < SZ-1; j++ )
	for ( int i = 1; i < SZ-1; i++ ) {
	    float t = fmax(0, fto[j][i]);
	    t = fmax(t, fto[j][i-1]);
	    t = fmax(t, fto[j][i+1]);
	    t = fmax(t, fto[j-1][i]);
	    t = fmax(t, fto[j+1][i]);
	    if ((t >= FTEMP) && (fo[j][i] > 0.0f)) {
		float e = fmin(ff[j][i], fo[j][i])*fmin(dTime*FFSPD, 1.0f);
		ff[j][i] -= e; fo[j][i] -= e;
		e *= FFENG;
		addFixel( ft[0]	, i, j, e );
		addFixel( vfx[0], i-1, j, -e*FFESP);
		addFixel( vfx[0], i+1, j, +e*FFESP);
		addFixel( vfy[0], i, j-1, -e*FFESP);
		addFixel( vfy[0], i, j+1, +e*FFESP);
	    }
	}
}

void drawQuad() {
    glEnable(GL_TEXTURE_2D);
    glBegin(GL_QUADS);
	glTexCoord2f( 0.0f, 0.0f ); glVertex2f( -1.0f, -1.0f);
	glTexCoord2f( 1.0f, 0.0f ); glVertex2f(  1.0f, -1.0f);
	glTexCoord2f( 1.0f, 1.0f ); glVertex2f(  1.0f,  1.0f);
	glTexCoord2f( 0.0f, 1.0f ); glVertex2f( -1.0f,  1.0f);
    glEnd();
};

void redisplay() {
    glMatrixMode(GL_PROJECTION);
    glLoadIdentity();
    glMatrixMode(GL_MODELVIEW);
    glLoadIdentity();
    float f[SZ][SZ];
    glClearColor(0.0, 0.0, 0.0, 0.0);
    glClear(GL_COLOR_BUFFER_BIT);

    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

    switch (vis) {
	case 0: {
		glColorMask(1, 1, 1, 0);
//		glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE, SZ, SZ, 0, GL_LUMINANCE, GL_FLOAT, ft);
		for ( int j = 0; j < SZ; j++ )
		    for ( int i = 0; i < SZ; i++ ) {
			firec[j][i][0] = fmin(255.0f, fmax(0.0f, 255.0f*pow(ft[j][i], 0.3f)*1.5f-0.05f));
			firec[j][i][1] = fmin(255.0f, fmax(0.0f, 255.0f*pow(ft[j][i], 0.8f)*0.9f-0.10f));
			firec[j][i][2] = fmin(255.0f, fmax(0.0f, 255.0f*pow(ft[j][i], 1.2f)*0.5f-0.15f));
		    }
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, SZ, SZ, 0, GL_RGB, GL_UNSIGNED_BYTE, firec);
		drawQuad();
	    }; break;
	case 1: {
		glColorMask(1, 0, 0, 0);
		for ( int j = 0; j < SZ; j++ )
		    for ( int i = 0; i < SZ; i++ ) f[j][i] = fabs(vfx[j][i])*2.0f;
		glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE, SZ, SZ, 0, GL_LUMINANCE, GL_FLOAT, f);
		drawQuad();
		glColorMask(0, 0, 1, 0);
		for ( int j = 0; j < SZ; j++ )
		    for ( int i = 0; i < SZ; i++ ) f[j][i] = fabs(vfy[j][i])*2.0f;
		glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE, SZ, SZ, 0, GL_LUMINANCE, GL_FLOAT, f);
		drawQuad();
	    }; break;
	case 2: {
		glColorMask(1, 0, 0, 0);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE, SZ, SZ, 0, GL_LUMINANCE, GL_FLOAT, ff);
		drawQuad();
		glColorMask(0, 0, 1, 0);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE, SZ, SZ, 0, GL_LUMINANCE, GL_FLOAT, fo);
		drawQuad();
	    }; break;
    }
    glColorMask(1, 1, 1, 1);
    glutSwapBuffers();
};

void idle() {
    float ctime = (float)glutGet(GLUT_ELAPSED_TIME)/1000.0f;
    float dTime = (ctime-time);
    dTime = 0.01f;
    grav( dTime );
    frict( dTime );
    process( dTime );
    glutPostRedisplay();
    bblur(vfx[0], 1 );
    bblur(vfy[0], 1 );
    bblur( ft[0], 1 );
    bblur( ff[0], 1 );
    bblur( fo[0], 1 );
//    init(1*SZ/3, SZ/8, 0.1f, 64);
//    init(2*SZ/3, SZ/8, 0.1f, 64);
//    for ( int i = SZ/10; i < 9*SZ/10; i++ ) init( i, 1, 0.03, 1);
    for ( int i = -SZ/4; i <= SZ/4; i++ ) {
	addFixel( vfx[0], SZ/2+i, 3, frand()*2.0f);
	addFixel( vfy[0], SZ/2+i, 3, (frand()*0.3f+0.7f)*2.0f);
	addFixel(  fo[0], SZ/2+i, 3, 1.0f);
	addFixel(  ff[0], SZ/2+i, 3, 1.0f);
	addFixel(  ft[0], SZ/2+i, 3, 0.3f);
    }
    fire( dTime );
    time = ctime;
//    if (cnt < 4) init( SZ/2, SZ/10, 0.1f, 2048 );
    cnt++;
};

void keyboard( unsigned char key, int x, int y ) {
    switch (key) {
	case '1': vis = 0; break;
	case '2': vis = 1; break;
	case '3': vis = 2; break;
	case ' ': init( 2*SZ/3, 10, 0.05f, 512 ); break;
	case 'T': FTURB = fmin(FTURB+0.05f, 1.0f); printf("FTURB: %f\n", FTURB); break;
	case 't': FTURB = fmax(FTURB-0.05f, 0.0f); printf("FTURB: %f\n", FTURB); break;
	case 'R': FTRRD += 1; printf("FTRD: %d\n", FTRRD); break;
	case 'r': FTRRD = fmax(FTRRD-1, 0); printf("FTRRD: %d\n", FTRRD); break;
    }
};

int main( int argc, char *argv[] ) {
    for ( int j = 0; j < SZ; j++ )
	for ( int i = 0; i < SZ; i++ ) fo[j][i] = 1.0f;
    glutInit(&argc, argv);
    glutInitDisplayMode(GLUT_DOUBLE);
    glutCreateWindow("fLAME!");
    glutDisplayFunc(&redisplay);
    glutIdleFunc(&idle);
    glutKeyboardFunc(&keyboard);
    glutMainLoop();
    return 0;
};
