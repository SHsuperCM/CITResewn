package io.shcm.fabric.citresewn.client_component_changes;

import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentHolder;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.Optional;

public class ClientComponentChanges {
    public static final ClientComponentChanges EMPTY = new ClientComponentChanges(new Reference2ObjectArrayMap<>()) {
        @Override
        public <T> T getOrDefault(ComponentType<? extends T> type, T original) {
            return original;
        }
    };

    private final Reference2ObjectMap<ComponentType<?>, Optional<?>> changedComponents;

    public ClientComponentChanges(Reference2ObjectMap<ComponentType<?>, Optional<?>> changedComponents) {
        this.changedComponents = changedComponents;
    }

    public static <T extends ComponentHolder> ClientComponentChanges fromHolder(T holder) {
        ClientComponentChanges componentChanges = new ClientComponentChanges(new Reference2ObjectArrayMap<>());

        if (holder instanceof ItemStack stack)
            ClientComponentChangesEvents.ITEMSTACK.invoker().resolveClientComponents(stack, componentChanges);

        return componentChanges;
    }

    public <T> T getOrDefault(ComponentType<? extends T> type, T original) {
        Optional<?> clientComponent = this.changedComponents.get(type);
        if (clientComponent != null)
            return (T) clientComponent.orElse(null);
        return original;
    }
}
