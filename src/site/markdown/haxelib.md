Haxelib repository
---------------------------------------
Haxemojos allows you to use projects from http://lib.haxe.org.
Just add dependency into `dependencies` section of your project.

    <dependencies>
        <!-- ... -->
        <dependency>
            <groupId>org.haxe.lib</groupId>
            <artifactId>hsl</artifactId>
            <version>2.0</version>
            <type>haxelib</type>
        </dependency>
    </dependencies>

Haxelib dependencies are not maven dependencies in
ordinary sense. Haxemojos uses `haxelib` tool to
download and install it.