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

import org.apache.maven.artifact.Artifact;
import org.codehaus.plexus.component.annotations.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component(role = NativeProgram.class, hint = "neko")
public final class NekoNativeProgram extends AbstractNativeProgram {

    private static final String LD_LIBRARY_PATH = "LD_LIBRARY_PATH";
    private static final String DYLD_LIBRARY_PATH = "DYLD_LIBRARY_PATH";

    @Override
    public void initialize(Artifact artifact, File outputDirectory, File pluginHome, Set<String> path, Map<String, String> env)
    {
        super.initialize(artifact, outputDirectory, pluginHome, path, env);

        if (!isWindows())
        {
            // Using LD_LIBRARY_PATH in Linux and DYLD_LIBRARY_PATH in OSX
            String ldName = isUnix() ? LD_LIBRARY_PATH : DYLD_LIBRARY_PATH;
            String ld = env.get(ldName);
            ld = (ld != null) ? ld + ":" + directory.getAbsolutePath() : directory.getAbsolutePath();
            env.put(ldName, ld);
        }

        env.put("NEKOPATH", directory.getAbsolutePath());
    }

    @Override
    protected List<String> updateArguments(List<String> arguments)
    {
        List<String> list = new ArrayList<String>();
        File executable = new File(directory, isWindows() ? "neko.exe" : "neko");
        list.add(executable.getAbsolutePath());
        list.addAll(arguments);

        return list;
    }
}

