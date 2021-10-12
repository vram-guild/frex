# FREX Rendering Extensions
FREX currently support the Fabric API and Mod Loader.  

Packaged as a separate mod so that rendering implementations and mods that consume these extensions can
depend on it without directly depending on specific implementation.

More information on using FREX is available on the [Renderosity Wiki](https://github.com/grondag/renderosity/wiki).

# Using FREX

Add the maven repo where my libraries live to your build.gradle

```gradle
repositories {
    maven {
    	name = "vram"
    	url = "https://maven.vram.io/"
    }
}
```

And add FREX to your dependencies

```gradle
dependencies {
	modCompile "io.vram:frex-fabric-mc117:6.0.+"
	include "io.vram:frex-fabric-mc117:6.0.+"
}
```

The ```include``` is not necessary if you are depending on another mod that also includes FREX.  Currently, [Canvas](https://github.com/vram-guild/canvas) and [JMX](https://github.com/grondag/json-model-extensions) both include FREX.

Note that version is subject to change - look at the repo to find latest.
