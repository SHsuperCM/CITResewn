package shcm.shsupercm.fabric.citresewn;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import shcm.shsupercm.fabric.citresewn.config.CITResewnConfig;

@Environment(EnvType.CLIENT)
public class CITResewn implements ClientModInitializer {
    public static final Logger LOG = LogManager.getLogger("CITResewn");
    public static CITResewn INSTANCE;

    public ActiveCITs activeCITs = null;

    public CITResewnConfig config = null;


    public boolean processingBrokenPaths = false;

    @Override
    public void onInitializeClient() {
        INSTANCE = this;

        config = CITResewnConfig.read();
    }

    public static void info(String message) {
        LOG.info(message);
    }

    public static void logErrorLoading(String message) {
        if (CITResewnConfig.INSTANCE().mute_errors)
            return;
        LOG.error(message);
    }

    public static void logWarnLoading(String message) {
        if (CITResewnConfig.INSTANCE().mute_warns)
            return;
        LOG.error(message);
    }
}
