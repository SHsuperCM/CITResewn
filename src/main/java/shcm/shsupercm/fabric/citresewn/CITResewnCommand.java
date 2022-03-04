package shcm.shsupercm.fabric.citresewn;

import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.loader.api.FabricLoader;
import shcm.shsupercm.fabric.citresewn.cit.ActiveCITs;
import shcm.shsupercm.fabric.citresewn.cit.CITRegistry;
import shcm.shsupercm.fabric.citresewn.config.CITResewnConfig;

import java.util.Collection;

import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.literal;
import static net.minecraft.text.Text.of;

public class CITResewnCommand {
    public static boolean openConfig = false;

    public static void register() {
        ClientCommandManager.DISPATCHER.register(literal("citresewn")
                .executes(context -> {
                    context.getSource().sendFeedback(of("CIT Resewn v" + FabricLoader.getInstance().getModContainer("citresewn").orElseThrow().getMetadata().getVersion() + ":"));
                    context.getSource().sendFeedback(of("  Registered: " + CITRegistry.TYPES.values().stream().distinct().count() + " types and " + CITRegistry.CONDITIONS.values().stream().distinct().count() + " conditions"));

                    final boolean active = CITResewnConfig.INSTANCE.enabled && ActiveCITs.isActive();
                    context.getSource().sendFeedback(of("  Active: " + (active ? "yes" : ("no, " + (CITResewnConfig.INSTANCE.enabled ? "no cit packs loaded" : "disabled in config")))));
                    if (active) {
                        context.getSource().sendFeedback(of("   Loaded: " + ActiveCITs.getActive().cits.values().stream().mapToLong(Collection::size).sum() + " CITs from " + ActiveCITs.getActive().cits.values().stream().flatMap(Collection::stream).map(cit -> cit.packName).distinct().count() + " resourcepacks"));
                    }
                    context.getSource().sendFeedback(of(""));

                    return 1;
                })
                .then(literal("config")
                        .executes(context -> {
                            openConfig = true;

                            return 1;
                        })));
    }
}
