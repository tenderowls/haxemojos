/**
 * Copyright (C) 2012 https://github.com/yelbota/haxe-maven-plugin
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
package com.yelbota.plugins.haxe.components.nativeProgram;

import org.apache.maven.artifact.Artifact;

import java.io.File;
import java.util.List;
import java.util.Set;

public interface NativeProgram {

    void initialize(Artifact artifact, File outputDirectory, File pluginHome, Set<String> path);

    int execute(List<String> arguments) throws NativeProgramException;

    int execute(String[] arguments) throws NativeProgramException;

    int execute(String arg1) throws NativeProgramException;

    int execute(String arg1, String arg2) throws NativeProgramException;

    int execute(String arg1, String arg2, String arg3) throws NativeProgramException;

    int execute(String arg1, String arg2, String arg3, String arg4) throws NativeProgramException;
}
