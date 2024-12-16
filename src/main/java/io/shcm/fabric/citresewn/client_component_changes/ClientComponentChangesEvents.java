package io.shcm.fabric.citresewn.client_component_changes;

import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.item.ItemStack;
import net.fabricmc.fabric.api.event.Event;

public interface ClientComponentChangesEvents {
    public static final Event<ItemStackClientComponentChangesHandler> ITEMSTACK = EventFactory.createArrayBacked(ItemStackClientComponentChangesHandler.class, (stack, componentChanges) -> {}, callbacks -> (stack, componentChanges) -> {
        for (ItemStackClientComponentChangesHandler callback : callbacks)
            callback.resolveClientComponents(stack, componentChanges);
    });

    @FunctionalInterface
    interface ItemStackClientComponentChangesHandler {
        void resolveClientComponents(ItemStack stack, ClientComponentChanges componentChanges);
    }
}
