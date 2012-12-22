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

import com.yelbota.plugins.haxe.utils.HaxeFileExtensions;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.util.List;

public abstract class AbstractCompileJavaMojo extends AbstractCompileHaxeMojo {

    protected File haxeJavaWorkDirectory;

    /**
     * @parameter default-value="2.10.2"
     * @required
     */
    protected String hxjavaVersion;

    @Override
    protected List<String> prepareArgumentsList()
    {
        List<String> argumentsList = super.prepareArgumentsList();
        haxeJavaWorkDirectory = new File(outputDirectory, getWorkDirectory());

        if (haxeJavaWorkDirectory.exists())
        {
            if (!haxeJavaWorkDirectory.isDirectory())
            {
                haxeJavaWorkDirectory.delete();
                haxeJavaWorkDirectory.mkdirs();
            }
        } else haxeJavaWorkDirectory.mkdirs();

        argumentsList.add("-java");
        argumentsList.add(haxeJavaWorkDirectory.getAbsolutePath());

        return argumentsList;
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        boolean hasHxJavaDependency = false;
        for (Dependency dependency: project.getDependencies()) {
            if (dependency.getArtifactId() == "hxjava") {
                hasHxJavaDependency = true;
                break;
            }
        }

        if (!hasHxJavaDependency) {

            project.getDependencyArtifacts().add(new DefaultArtifact(
                    "org.haxe.lib", "hxjava", hxjavaVersion,
                    "compile", HaxeFileExtensions.HAXELIB, "",
                    new DefaultArtifactHandler(HaxeFileExtensions.HAXELIB)));
        }

        // TODO prevent versions less than 2.10
        super.execute();
    }

    protected String getWorkDirectory()
    {
        return "haxe-java";
    }
}
