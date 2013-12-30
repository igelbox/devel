package findup;

/**
 *
 * @author igel
 */
class AppError extends RuntimeException {

    AppError( String message ) {
        super( message );
    }
}