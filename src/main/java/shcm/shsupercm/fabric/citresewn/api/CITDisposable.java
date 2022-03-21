package shcm.shsupercm.fabric.citresewn.api;

/**
 * @see #dispose()
 */
@FunctionalInterface
public interface CITDisposable {
    /**
     * Entrypoint for any disposing method that is not covered by CIT Resewn automatically.
	 * @see #dispose()
     */
    String ENTRYPOINT = "citresewn:dispose";

    /**
     * Invoked just before reloading CITs. Use to clean up effects and changes that CIT loading made.
     */
    void dispose();
}
