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
package com.yelbota.plugins.haxe.tasks.compile;

import com.yelbota.plugins.haxe.utils.HaxeFileExtensions;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.List;

public class CompileJavaTask extends AbstractCompileTask {

    protected File workDir;

    private String hxjavaVersion;

    public CompileJavaTask(File pluginHome, File haxeUnpackDirectory, File nekoUnpackDirectory, File outputDirectory, Log log, MavenProject project, String main, boolean debug, String hxjavaVersion)
    {
        super(pluginHome, haxeUnpackDirectory, nekoUnpackDirectory, outputDirectory, log, project, main, debug);
        this.hxjavaVersion = hxjavaVersion;
    }

    @Override
    protected List<String> prepareArgumentsList()
    {
        List<String> argumentsList = super.prepareArgumentsList();
        workDir = new File(outputDirectory, getWorkDirectory());

        if (workDir.exists())
        {
            if (!workDir.isDirectory())
            {
                workDir.delete();
                workDir.mkdirs();
            }
        } else workDir.mkdirs();

        argumentsList.add("-java");
        argumentsList.add(workDir.getAbsolutePath());

        return argumentsList;
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        boolean hasHxJavaDependency = false;

        // Lookup hxjava dependency in project dependencies.
        for (Dependency dependency : project.getDependencies())
        {
            if (dependency.getArtifactId().equals("hxjava"))
            {
                hasHxJavaDependency = true;
                break;
            }
        }

        if (!hasHxJavaDependency)
        {
            if (hxjavaVersion == null)
            {
                throw new MojoFailureException("hxjava dependency not found in the scope. Define hxjavaVersion or add `org.haxe.lib:hxjava:version:haxelib` manually");
            }
            else {
                project.getDependencyArtifacts().add(new DefaultArtifact(
                        "org.haxe.lib", "hxjava", hxjavaVersion,
                        "compile", HaxeFileExtensions.HAXELIB, "",
                        new DefaultArtifactHandler(HaxeFileExtensions.HAXELIB)));
            }
        }

        // TODO prevent versions less than 2.10
        super.execute();

        File jar = new File(workDir, getWorkDirectory() + ".jar");

        // Include artifact in reactor.
        if (jar.exists())
        {
            String artifactFinalName = project.getBuild().getFinalName() + "." + project.getPackaging();
            File artifactFile = new File(outputDirectory, artifactFinalName);

            if (artifactFile.exists())
                artifactFile.delete();

            jar.renameTo(artifactFile);
            project.getArtifact().setFile(artifactFile);
        }
    }

    private String getWorkDirectory()
    {
        return "haxe-java";
    }
}
