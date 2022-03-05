package shcm.shsupercm.fabric.citresewn.mixin;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.NoticeScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import shcm.shsupercm.fabric.citresewn.CITResewnCommand;
import shcm.shsupercm.fabric.citresewn.config.CITResewnConfigScreenFactory;

import static shcm.shsupercm.fabric.citresewn.CITResewnCommand.openConfig;

/**
 * Opens the config screen when running the "/citresewn config" command.
 * @see CITResewnCommand#openConfig
 */
@Mixin(ChatScreen.class)
public class ChatScreenMixin {
    /**
     * If {@link CITResewnCommand#openConfig} is true, changes the screen that's opened when the chat is closed to the config screen.
     * @see CITResewnCommand#openConfig
     */
    @ModifyArg(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;setScreen(Lnet/minecraft/client/gui/screen/Screen;)V"))
    public Screen citresewn$redirectConfigScreen(Screen original) {
        if (openConfig) {
            openConfig = false;
            return FabricLoader.getInstance().isModLoaded("cloth-config2") ?
                    CITResewnConfigScreenFactory.create(null) :
                    new NoticeScreen(() -> MinecraftClient.getInstance().setScreen(null), Text.of("CIT Resewn"), Text.of("CIT Resewn requires Cloth Config to be able to show the config."));
        }

        return original;
    }
}
