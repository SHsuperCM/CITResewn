# Configuration

## Changing the Config

The config is located at `%appdata%/.minecraft/config/citresewn.json`.

If you have [Cloth Config](https://www.curseforge.com/minecraft/mc-mods/cloth-config) installed, 
run `/citresewn config` and if you have [Mod Menu](https://www.curseforge.com/minecraft/mc-mods/modmenu) 
installed, in the main menu/pause screen go to `Mods > CIT Resewn > Config`.

## Config Values

| Config Name       | Description | Default Config Value |
| --- | --- | --- |
| `Enabled` <br>or `enabled` | Whether CIT Resewn should load CITs. | `true` |
| `Mute Errors` <br>or `mute_errors` | Should cit errors be muted in the logs. | `false` |
| `Mute Warns` <br>or `mute_warns` | Should cit warnings be muted in the logs. | `false` |
| `Cache` <br>or `cache_ms` | How often should the cache be refreshed. <br>If set to 0, caching will be disabled. | `50`ms(every tick) |
| `Broken Paths` <br>or `broken_paths` | Should CIT Resewn be able to read broken folder/file names from CIT packs. This option is unstable and not recommended, any bugs it causes will be ignored. <br><br> Broken Paths are any files in a resourcepack that do not follow standard minecraft pack formatting. The pack format permits names for files and folders that may only contain the following: lower case english alphabet(`a-z`), any number(`0-9`), underscores(`_`), dots(`.`) and dashes(`-`). | `false` |