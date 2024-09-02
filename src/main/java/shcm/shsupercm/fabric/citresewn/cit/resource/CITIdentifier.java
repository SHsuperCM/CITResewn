package shcm.shsupercm.fabric.citresewn.cit.resource;

import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackInfo;
import net.minecraft.util.Identifier;

public record CITIdentifier(ResourcePack pack, String root, Identifier path) {
    public CITIdentifier(Identifier path, String root, Resource resource) {
        this(resource.getPack(), root, path);
    }
}
