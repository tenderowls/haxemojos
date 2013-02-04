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

import com.yelbota.plugins.haxe.tasks.HaxeTask;
import com.yelbota.plugins.haxe.tasks.compile.CompileJavaTask;
import com.yelbota.plugins.haxe.tasks.compile.CompileNekoTask;
import com.yelbota.plugins.haxe.utils.CompileTarget;
import com.yelbota.plugins.haxe.utils.HaxeFileExtensions;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.archiver.zip.ZipArchiver;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Mojo(name="compileHar", defaultPhase = LifecyclePhase.COMPILE)
public class CompileHarMojo extends AbstractCompileMojo {

    /**
     * Compile targets for `har`. Ignored in another packaging types.
     */
    @Parameter(required = true)
    private List<CompileTarget> targets;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        super.execute();

        for (CompileTarget target : targets)
        {
            HaxeTask task = getCompileTask(target);
            task.execute();
        }

        try
        {
            ZipArchiver archiver = new ZipArchiver();
            for (String compileRoot: project.getCompileSourceRoots()) {
                archiver.addDirectory(new File(compileRoot));
            }

            File destFile = new File(outputDirectory, project.getBuild().getFinalName() + "." + HaxeFileExtensions.HAR);
            archiver.setDestFile(destFile);
            archiver.createArchive();
        }
        catch (IOException e)
        {
            throw new MojoFailureException("Error occurred during `har` package creation", e);
        }
    }

    public HaxeTask getCompileTask(CompileTarget target)
    {
        switch (target)
        {
            case java:
                return new CompileJavaTask(pluginHome, haxeUnpackDirectory, nekoUnpackDirectory, outputDirectory, getLog(), project, main, debug, null);
            case neko:
                return new CompileNekoTask(pluginHome, haxeUnpackDirectory, nekoUnpackDirectory, outputDirectory, getLog(), project, main, debug);
        }

        return null;
    }

}
