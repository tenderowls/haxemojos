HAR packaging
---------------------------------------
As you know Haxe doesn't have its own bytecode
so HAR (Haxe Archive) is a way to share modules
in maven style. Technically HAR is just `zip`
archive containing source code of your module and
metainfo of supported build targets and
compiler version.

If you want to build HAR package, just set
packaging of your module to `har`

    <packaging>har</packaging>

In other cases it is normal maven artifact: it
supports transitivity and can be uploaded
into Nexus or Artifactory.

Build targets
---------------------------------------
You can configure Haxemojos to check several
compiler targets for your module `foo`.
For example: your module support `java`
and `flash` compilation.

    <targets>
        <target>java</target>
        <target>swf</target>
    </targets>

Haxemojos will try to compile all classes in
a module with all compilation targets. When you
use your module foo in a project which is not
flash or java you will receive a warning.

    [WARNING] Dependency `foo` is not compatible with your compile targets.