package shcm.shsupercm.fabric.citresewn.cit;

import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import shcm.shsupercm.fabric.citresewn.CITResewn;
import shcm.shsupercm.fabric.citresewn.api.CITTypeContainer;
import shcm.shsupercm.fabric.citresewn.cit.model.CITModelsAccess;
import shcm.shsupercm.fabric.citresewn.cit.resource.format.PropertyGroup;
import shcm.shsupercm.fabric.citresewn.cit.resource.format.PropertyKey;
import shcm.shsupercm.fabric.citresewn.cit.resource.format.PropertyValue;

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
     *
     * @param conditions conditions that were parsed out of the property group
     * @param properties group of properties to be read into this type
     * @param context shared context of the entire CIT loading process with some helpful methods
     * @throws CITParsingException if errored while parsing the type
     */
    public abstract void load(List<CITCondition> conditions, PropertyGroup properties, LoadContext context) throws CITParsingException;

    protected void warn(String message, PropertyValue value, PropertyGroup properties) {
        CITResewn.logWarnLoading("Warning: " + properties.messageWithDescriptorOf(message, value == null ? -1 : value.position()));
    }

    /**
     * Shared context of the entire CIT loading process with some helpful methods for type loading.
     */
    public record LoadContext(
            ResourceManager resourceManager,
            CITModelsAccess modelsAccess) {

        /**
         * Resolves a resourcepack asset using a relative or absolute path.
         * @param originResource nullable, the original file path for relative resolution
         * @param path path to resolve
         * @param expectedRoot expected root folder in the resourcepack
         * @param expectedSuffix expected file extension to check if it was not supplied originally
         * @return the full resource manager identifier of the resolved asset or {@code Optional.empty())} if one was not found
         */
        public Optional<Identifier> resolve(Identifier originResource, String path, String expectedRoot, String expectedSuffix) {
            if (path == null)
                return Optional.empty();

            Identifier pathIdentifier = Identifier.tryParse(path);
            if (pathIdentifier == null)
                return Optional.empty();

            path = pathIdentifier.getPath().replace('\\', '/');
            if (!path.endsWith(expectedSuffix))
                path = path + expectedSuffix;

            if (path.startsWith("./"))
                path = path.substring(2);
            else if (!path.contains("..")) {
                pathIdentifier = Identifier.of(pathIdentifier.getNamespace(), path);
                if (isResource(pathIdentifier))
                    return Optional.of(pathIdentifier);
                else if (path.startsWith("assets/")) {
                    path = path.substring("assets/".length());
                    int sep = path.indexOf('/');
                    pathIdentifier = Identifier.of(path.substring(0, sep), path.substring(sep + 1));
                    if (isResource(pathIdentifier))
                        return Optional.of(pathIdentifier);
                }
                pathIdentifier = Identifier.of(pathIdentifier.getNamespace(), expectedRoot + "/" + path);
                if (isResource(pathIdentifier))
                    return Optional.of(pathIdentifier);
            }

            if (originResource == null)
                return Optional.empty();

            LinkedList<String> pathParts = new LinkedList<>(Arrays.asList(originResource.getPath().split("/")));
            pathParts.removeLast();

            if (path.contains("/")) {
                for (String part : path.split("/")) {
                    if (part.equals("..")) {
                        if (pathParts.isEmpty())
                            return Optional.empty();
                        pathParts.removeLast();
                    } else
                        pathParts.addLast(part);
                }
            } else
                pathParts.addLast(path);
            path = String.join("/", pathParts);

            pathIdentifier = Identifier.of(originResource.getNamespace(), path);

            return isResource(pathIdentifier) ? Optional.of(pathIdentifier) : Optional.empty();
        }

        /**
         * Resolves a texture using a relative or absolute path.
         * @param originResource nullable, the original file path for relative resolution
         * @param path path to resolve
         * @return the full resource manager identifier of the resolved texture or {@code Optional.empty())} if one was not found
         */
        public Optional<Identifier> resolveTexture(Identifier originResource, String path) {
            if (path == null && originResource != null && originResource.getPath().endsWith(",properties")) {
                path = originResource.getPath().substring(0, originResource.getPath().length() - ".properties".length());
                if (!path.endsWith(".png"))
                    path = path + ".png";
                Identifier pathIdentifier = Identifier.of(originResource.getNamespace(), path);
                return isResource(pathIdentifier) ? Optional.of(pathIdentifier) : Optional.empty();
            }

            return resolve(originResource, path, "textures", ".png");
        }

        /**
         * Resolves a model using a relative or absolute path.
         * @param originResource nullable, the original file path for relative resolution
         * @param path path to resolve
         * @return normalized model identifier relative to the models root or {@code Optional.empty())} if one was not found
         */
        public Optional<Identifier> resolveModel(Identifier originResource, String path) {
            Optional<Identifier> resolvedModel;
            if (path == null && originResource != null && originResource.getPath().endsWith(",properties")) {
                path = originResource.getPath().substring(0, originResource.getPath().length() - ".properties".length());
                if (!path.endsWith(".json"))
                    path = path + ".json";
                Identifier pathIdentifier = Identifier.of(originResource.getNamespace(), path);
                resolvedModel = isResource(pathIdentifier) ? Optional.of(pathIdentifier) : Optional.empty();
            } else
                resolvedModel = resolve(originResource, path, "models", ".json");

            return resolvedModel.map(modelId ->
                    modelId.withPath(modelPath -> {
                        modelPath = modelPath.substring(0, modelPath.length() - ".json".length());
                        return modelPath.startsWith("models/") ? modelPath.substring("models/".length()) : ("../" + modelPath);
                    }));
        }

        /**
         * Retrieves a resource from the shared resource manager
         * @param id full path of the resource
         * @return the {@link Resource} or {@code Optional.empty())} if the resource was not found in any loaded resourcepack
         */
        public Optional<Resource> getResource(Identifier id) {
            return resourceManager().getResource(id);
        }

        /**
         * Checks whether a resource exists in one of the loaded resourcepacks
         * @param id full path of the resource
         * @return true if the resource was found in any of the loaded resourcepack
         */
        public boolean isResource(Identifier id) {
            return getResource(id).isPresent();
        }
    }
}
