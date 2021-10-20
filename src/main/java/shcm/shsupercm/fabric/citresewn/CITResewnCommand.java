package shcm.shsupercm.fabric.citresewn;

import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.loader.api.FabricLoader;
import shcm.shsupercm.fabric.citresewn.config.CITResewnConfig;

import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.literal;
import static net.minecraft.text.Text.of;

public class CITResewnCommand {
    public static boolean openConfig = false;

    public static void register() {
        ClientCommandManager.DISPATCHER.register(literal("citresewn")
                .executes(context -> {
                    context.getSource().sendFeedback(of("CIT Resewn v" + FabricLoader.getInstance().getModContainer("citresewn").get().getMetadata().getVersion() + ":"));
                    boolean active = CITResewnConfig.INSTANCE().enabled && CITResewn.INSTANCE.activeCITs != null;
                    context.getSource().sendFeedback(of("  Active: " + (active ? "yes" : "no")));
                    if (active) {
                        context.getSource().sendFeedback(of("  Loaded: " + CITResewn.INSTANCE.activeCITs.cits.size() + " CITs from " + CITResewn.INSTANCE.activeCITs.packs.size() + " resourcepacks"));
                    }
                    context.getSource().sendFeedback(of("  "));

                    return 1;
                })
                .then(literal("config")
                    .executes(context -> {
                        openConfig = true;

                        return 1;
                    })));
    }
}
