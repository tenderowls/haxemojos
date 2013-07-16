/**
 * Copyright (C) 2012 https://github.com/tenderowls/haxemojos
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tenderowls.opensource.haxemojos.components.nativeProgram;

import com.tenderowls.opensource.haxemojos.utils.NativeProgramVersion;
import org.codehaus.plexus.component.annotations.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Component(role = NativeProgram.class, hint = "haxe", isolatedRealm = true)
public final class HaxeNativeProgram extends AbstractNativeProgram {

    @Override
    protected List<String> updateArguments(List<String> arguments)
    {
        List<String> list = new ArrayList<String>();
        File executable = new File(directory, isWindows() ? "haxe.exe" : "haxe");
        updateExecutableMod(executable);
        list.add(executable.getAbsolutePath());
        list.addAll(arguments);

        return list;
    }

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

