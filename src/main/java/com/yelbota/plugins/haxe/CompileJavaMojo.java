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
package com.yelbota.plugins.haxe;

import com.yelbota.plugins.haxe.tasks.compile.CompileJavaTask;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * @goal compileJava
 * @phase compile
 */
public class CompileJavaMojo extends AbstractCompileMojo {

    /**
     * hxjava dependency version. also you can add org.haxe.lib:hxjava:version:haxelib dependency manually.
     * @parameter default-value="2.10.2"
     */
    private String hxJavaVersion;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        super.execute();

        CompileJavaTask task = new CompileJavaTask(pluginHome, haxeUnpackDirectory, nekoUnpackDirectory, outputDirectory, getLog(), project, main, debug, hxJavaVersion);
        task.execute();
    }
}
