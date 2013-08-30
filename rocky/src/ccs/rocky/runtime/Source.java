package ccs.rocky.runtime;

/**
 *
 * @author igel
 */
public interface Source {

    float[] get( int samples, int samplerate, float time );
}
