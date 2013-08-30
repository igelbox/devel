package ccs.jrack.functions;

import ccs.jrack.Function;

/**
 *
 * @author igel
 */
public class Overdrive extends Function {

    private final Arg in, drive;

    public Overdrive( Arg in, Arg drive ) {
        this.in = in;
        this.drive = drive;
    }

    @Override
    public float apply() {
        float v = in.get(), a = Math.abs( v ), s = Math.signum( v ), d = drive.get();
        return s * (float) (Math.log( 1.0 + a * d ) / Math.log( 1.0 + d ));
    }
}
