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
import org.codehaus.plexus.logging.Logger;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface NativeProgram {

    void initialize(Artifact artifact, File outputDirectory, File pluginHome, Set<String> path, Map<String, String> env);

    int execute(List<String> arguments) throws NativeProgramException;

    int execute(List<String> arguments, Logger outputLogger) throws NativeProgramException;

    int execute(List<String> arguments, File targetDirectory) throws NativeProgramException;

    int execute(File targetDirectory, String ...arguments) throws NativeProgramException;

    int execute(String ...arguments) throws NativeProgramException;

    /**
     * Main executable
     * @param arguments commandline args
     * @param targetDirectory directory in which we will run command
     * @param outputLogger logger
     * @return
     * @throws NativeProgramException
     */
    int execute(List<String> arguments, File targetDirectory, Logger outputLogger) throws NativeProgramException;

    public File getHome();
}
