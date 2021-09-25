# Mod Authors

CIT Resewn is not just for resourcepacks! Mods can add both explicit 
and implicit support to change their items' visuals when CIT Resewn is
present.

## Mod Assets

This part assumes you have a basic understanding of making CIT 
resource packs and have read through [Resource Pack Authors](pack_authors.md).

If you didn't know, your mod jar is basically a resourcepack!  
This feature requires [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api) 
to be installed to work, specifically `fabric-resource-loader-v0`.

Example:

Let's say the mod "Gold Sticks" wants to add replacement textures for multiple gold sticks. 
The gold stick is identified by `goldsticks:golden_stick`.  
Now, the mod resources have the following file structure:
```html
src/main/resources/assets/goldsticks/models/item/golden_stick.json
src/main/resources/assets/goldsticks/textures/item/golden_stick.png

src/main/resources/assets/goldsticks/citresewn/multiple_golden_sticks_2.properties
src/main/resources/assets/goldsticks/citresewn/multiple_golden_sticks_2.png
```

Now, `multiple_golden_sticks_2.properties` contains the following CIT:
```properties
type=item
items=goldsticks:golden_stick
stackSize=2-
texture=multiple_golden_sticks_2.png
```

If CIT Resewn is present(and Fabric API), it will load that cit from the mod jar.
And if you have 2 or more golden sticks, the `multiple_golden_sticks_2.png` texture will 
be used instead of `golden_stick.png`!

## API

CIT Resewn also provides an API that allows adding custom CIT types and conditions.

### CIT Types:

To register custom cit types, call `CITParser.REGISTRY#put` during your mod's 
client initialisation.

For example:
```java
@Override
public void onInitializeClient() {
    // Either add CIT Resewn as a dependency or check with fabric if
    // CIT Resewn is loaded(for optional support) before doing this.
    
    // Register type=backpack to allow cits to change your mod's backpack's texture
    CITParser.REGISTRY.put("backpack", CITBackpack::new);
}
```

Then, implement `CITBackpack extends CIT` and handle parsing in its constructor.  
From there, the cit would be read during asset reloads and you'll need to handle 
storage during construction and clean up during `#dispose()`.

For working examples that utilize caching, look at the default types in CIT Resewn.

### Conditions

This is not implemented yet! Follow issue [#13](https://github.com/SHsuperCM/CITResewn/issues/13) for updates!