package shcm.shsupercm.fabric.citresewn.cit;

import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import shcm.shsupercm.fabric.citresewn.CITResewn;
import shcm.shsupercm.fabric.citresewn.api.CITTypeContainer;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyGroup;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyKey;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyValue;

import java.util.*;

/**
 * Instanced parent for CIT Types that are applied to items when conditions pass.
 * @see CITTypeContainer
 * @see CIT
 */
public abstract class CITType {
    /**
     * Used to determine which property keys are not conditions.
     * @return a set of property keys used by this type
     */
    public abstract Set<PropertyKey> typeProperties();

    /**
     * Loads the given property group into the type.
     * @param conditions conditions that were parsed out of the property group
     * @param properties group of properties to be read into this type
     * @param resourceManager the CIT's containing resource manager
     * @throws CITParsingException if errored while parsing the type
     */
    public abstract void load(List<CITCondition> conditions, PropertyGroup properties, ResourceManager resourceManager) throws CITParsingException;

    protected void warn(String message, PropertyValue value, PropertyGroup properties) {
        CITResewn.logWarnLoading("Warning: " + properties.messageWithDescriptorOf(message, value == null ? -1 : value.position()));
    }

    /**
     * ///// PORTED FROM BETA \\\\\
     * This shit was ported from the
     * beta and will be rewritten at
     * some point!
     * \\\\\                  /////
     *
     * Takes a defined path and resolves it to an identifier pointing to the resourcepack's path of the specified extension(returns null if no path can be resolved).<br>
     * If definedPath is null, will try to resolve a relative file with the same name as the rootIdentifier with the extension, otherwise: <br>
     * definedPath will be formatted to replace "\\" with "/" the extension will be appended if not there already. <br>
     * It will first try using definedPath as an absolute path, if it cant resolve(or definedPath starts with ./), definedPath will be considered relative. <br>
     * Relative paths support going to parent directories using "..".
     */
    public static Identifier resolveAsset(Identifier rootIdentifier, String path, String defaultedTypeDirectory, String extension, ResourceManager resourceManager) {
        if (path == null) {
            path = rootIdentifier.getPath().substring(0, rootIdentifier.getPath().length() - 11);
            if (!path.endsWith(extension))
                path = path + extension;
            Identifier pathIdentifier = Identifier.of(rootIdentifier.getNamespace(), path);
            return resourceManager.getResource(pathIdentifier).isPresent() ? pathIdentifier: null;
        }

        Identifier pathIdentifier = Identifier.tryParse(path);

        path = pathIdentifier.getPath().replace('\\', '/');
        if (!path.endsWith(extension))
            path = path + extension;

        if (path.startsWith("./"))
            path = path.substring(2);
        else if (!path.contains("..")) {
            pathIdentifier = Identifier.of(pathIdentifier.getNamespace(), path);
            if (resourceManager.getResource(pathIdentifier).isPresent())
                return pathIdentifier;
            else if (path.startsWith("assets/")) {
                path = path.substring(7);
                int sep = path.indexOf('/');
                pathIdentifier = Identifier.of(path.substring(0, sep), path.substring(sep + 1));
                if (resourceManager.getResource(pathIdentifier).isPresent())
                    return pathIdentifier;
            }
            pathIdentifier = Identifier.of(pathIdentifier.getNamespace(), defaultedTypeDirectory + "/" + path);
            if (resourceManager.getResource(pathIdentifier).isPresent())
                return pathIdentifier;
        }

        LinkedList<String> pathParts = new LinkedList<>(Arrays.asList(rootIdentifier.getPath().split("/")));
        pathParts.removeLast();

        if (path.contains("/")) {
            for (String part : path.split("/")) {
                if (part.equals("..")) {
                    if (pathParts.size() == 0)
                        return null;
                    pathParts.removeLast();
                } else
                    pathParts.addLast(part);
            }
        } else
            pathParts.addLast(path);
        path = String.join("/", pathParts);

        pathIdentifier = Identifier.of(rootIdentifier.getNamespace(), path);

        return resourceManager.getResource(pathIdentifier).isPresent() ? pathIdentifier : null;
    }

    public static Identifier resolveAsset(Identifier rootIdentifier, PropertyValue path, String defaultedTypeDirectory, String extension, ResourceManager resourceManager) {
        return resolveAsset(rootIdentifier, path == null ? null : path.value(), defaultedTypeDirectory, extension, resourceManager);
    }
}
