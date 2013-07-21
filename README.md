Haxemojos
--------------------------------------------
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

See [http://opensource.tenderowls.com/haxemojos](http://opensource.tenderowls.com/haxemojos)

[haxe]: http://haxe.org "Haxe"
[neko]: http://nekovm.org "Neko"
[maven]: http://maven.apache.org "Apache Maven"
