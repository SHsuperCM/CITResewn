package io.shcm.fabric.citresewn.client_component_changes;

import net.minecraft.component.ComponentHolder;

public interface MergedClientComponentMap {
    ClientComponentChanges citresewn$getClientChanges();

    void citresewn$updateFromHolder(ComponentHolder holder);

    void citresewn$invalidateClientChanges();
}
