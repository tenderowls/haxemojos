package com.tenderowls.opensource.haxemojos.components.nativeProgram;

import com.tenderowls.opensource.haxemojos.utils.NativeProgramVersion;

import java.io.File;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Dukobpa3
 * Date: 02.09.13
 * Time: 18:53
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractHaxeNativeProgram extends AbstractNativeProgram {

    @Override
    protected List<String> getEnvironment()
    {
        List<String> env = super.getEnvironment();
        File std = new File(directory, "std");

        boolean haxeStdPathEnvVarIsOldStyle;

        try {
            NativeProgramVersion haxeVersion = new NativeProgramVersion(artifact.getVersion());
            haxeStdPathEnvVarIsOldStyle = haxeVersion.compare("3.0.0-rc2") < 0;
        } catch (NativeProgramVersion.NativeProgramVersionException e) {
            haxeStdPathEnvVarIsOldStyle = false;
        }

        String haxeStdPathEnvVar = haxeStdPathEnvVarIsOldStyle
                ? "HAXE_LIBRARY_PATH="
                : "HAXE_STD_PATH=";

        env.add(haxeStdPathEnvVar + std.getAbsolutePath());
        env.add("JAVA_HOME=" + System.getenv("JAVA_HOME"));

        return env;
    }
}
