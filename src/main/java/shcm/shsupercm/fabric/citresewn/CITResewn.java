package shcm.shsupercm.fabric.citresewn;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import shcm.shsupercm.fabric.citresewn.config.CITResewnConfig;

/**
 * Main initializer for CIT Resewn. Contains various internal utilities(just logging for now).
 */
public class CITResewn {
    public static final Logger LOG = LogManager.getLogger("CITResewn");

    /**
     * Logs an info line in CIT Resewn's name.
     * @param message log message
     */
    public static void info(String message) {
        LOG.info("[citresewn] " + message);
    }

    /**
     * Logs a warning line in CIT Resewn's name if enabled in config.
     * @see CITResewnConfig#mute_warns
     * @param message warn message
     */
    public static void logWarnLoading(String message) {
        if (CITResewnConfig.INSTANCE.mute_warns)
            return;
        LOG.error("[citresewn] " + message);
    }

    /**
     * Logs an error line in CIT Resewn's name if enabled in config.
     * @see CITResewnConfig#mute_errors
     * @param message error message
     */
    public static void logErrorLoading(String message) {
        if (CITResewnConfig.INSTANCE.mute_errors)
            return;
        LOG.error("{citresewn} " + message);
    }
}
