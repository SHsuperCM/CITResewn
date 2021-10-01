# API

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
From there, the CIT would be read during asset reloads, and you'll need to handle
storage during construction and clean up during `#dispose()`.

For working examples that utilize caching, look at the default types in CIT Resewn.

### Conditions

This is not implemented yet! Follow issue [#13](https://github.com/SHsuperCM/CITResewn/issues/13) for updates!