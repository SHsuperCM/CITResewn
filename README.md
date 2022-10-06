<p align="center">
  <img src="https://citresewn.shcm.io/img/project_description/logo_shadow.png" width="200px">
</p>

CIT Resewn is MCPatcher's CIT features re-written outside of optifine as a standalone mod for fabric.

The main CIT Resewn mod serves as an API to add types and conditions while CIT Resewn: Defaults uses that API to provide the default types and conditions that naturally come with the CIT format.

## Downloads
You can get CIT Resewn(bundled with Defaults) from Modrinth, Curse Forge or by compiling it from source

              <a href="https://modrinth.com/mod/cit-resewn"><img src="https://citresewn.shcm.io/img/modrinth.png" width="50px"></a>       
<a href="https://www.curseforge.com/minecraft/mc-mods/cit-resewn"><img src="https://citresewn.shcm.io/img/curseforge.png" width="50px"></a>

## CIT Docs
Docs for CIT Resewn's usage are available over at https://citresewn.shcm.io

## API

CIT Resewn is distributed for development through Modrinth's Maven repository under `cit-resewn`.<br>
Defaults can be added separately through the same Maven under `cit-resewn-defaults`.

Gradle example:
```groovy
// Add the modrinth maven repository:
repositories {
    ..
    maven {
        name = "Modrinth"
        url = "https://api.modrinth.com/maven"
        content {
            includeGroup "maven.modrinth"
        }
    }
}

dependencies {
    ..
    // Add the base CIT Resewn API to the project
    modCompileOnly "maven.modrinth:cit-resewn:1.1.2+1.19.2"
    // Add Defaults to the project
    modCompileOnly "maven.modrinth:cit-resewn-defaults:1.1.2+1.19.2"
}
```

API usage documentation will be available soon over at [the docs](https://citresewn.shcm.io/mods/mod_api/).

For example usage of the CIT Resewn API, take a look at how Defaults does it.

## Contributing

Bug fixes and feature implementations are always welcome and will usually be accepted once verified to be ok/fit in the mod.

Translations aren't really necessary but if you PR them there's no reason they won't be accepted.

Lastly, the docs site is also open source, PRs can be made to the [docs branch](https://github.com/SHsuperCM/CITResewn/tree/docs).
