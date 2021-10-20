package shcm.shsupercm.fabric.citresewn.mixin.core;

import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import shcm.shsupercm.fabric.citresewn.OptionalCompat;

import static shcm.shsupercm.fabric.citresewn.CITResewnCommand.openConfig;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {
    @ModifyArg(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;setScreen(Lnet/minecraft/client/gui/screen/Screen;)V"))
    public Screen redirectConfigScreen(Screen original) {
        if (original == null && openConfig) {
            openConfig = false;
            return OptionalCompat.getModConfigScreenFactory().apply(null);
        }

        return original;
    }
}
