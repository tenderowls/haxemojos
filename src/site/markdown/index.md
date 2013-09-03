Introduction
------------------------------------------
Haxemojos is a [Maven][maven] plugin created
to build [Haxe][haxe] projects. It supports
transitive dependency management, haxelib
dependencies and haxeunit.

Plugin designed in a
true-maven-style (as authors understand it).
It means you don't need to download and install
into your system nothing then Java and
[Maven][maven]. Haxe compiler and [Neko runtime][neko]
are downloading by Maven dependency resolution
mechanism as plugin dependencies and installing
it into version-separated directories in mavens
local repository. It allows to use different
haxe versions in your projects. Haxemojos is
definitely great when you have projects which written
long time ago but need support.

[haxe]: http://haxe.org "Haxe"
[neko]: http://nekovm.org "Neko"
[maven]: http://maven.apache.org "Apache Maven"

Available packaging
---------------------------------------

`swc`, `swf`, `neko`, `java`, `har`

Goals Overview
---------------------------------------
The Haxemojos Maven Plugin has six goals:

  * [haxemojos:compileHar](./compileHar-mojo.html)
     Builds a `har` package. This is a zip archive which
     contains metainfo about supported compilation targets.
  * [haxemojos:compileJava](./compileJava-mojo.html) Compile `jar`. Note, that this `jar` is different with `jar` which compiles with `maven-compiler-plugin`. Haxe jar includes all dependencies and Haxe runtime classes. If you want share your haxe code in the module, use `har` packaging.
  * [haxemojos:compileNeko](./compileNeko-mojo.html) Compile to nekovm bytecode.
  * [haxemojos:compileSwf](./compileSwf-mojo.html) Compile SWF for Adobe(R) Flash Player(TM) or Adobe(R) AIR(TM).
  * [haxemojos:compileSwc](./compileSwc-mojo.html) Compile SWC library.
  * [haxemojos:testCompile](./testCompile-mojo.html) Compile tests with `neko` compile target.
  * [haxemojos:testRun](./testRun-mojo.html) Run tests with `neko`.
  * [haxemojos:haxelibRun](./haxelibRun-mojo.html) Run some runnable lib such "haxelib run munit test" or "haxelib run openfl build flash"
