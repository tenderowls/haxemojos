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

import com.yelbota.plugins.haxe.components.HaxeCompiler;
import com.yelbota.plugins.haxe.utils.CompileTarget;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.io.File;
import java.util.EnumMap;

/**
 * Compile `jar`. Note, that this `jar` is different with `jar` which compiles with
 * `maven-compiler-plugin`. Haxe jar includes all dependencies and Haxe runtime classes.
 * If you want share your haxe code in the module, use `har` packaging.
 */
@Mojo(name="compileJava", defaultPhase = LifecyclePhase.COMPILE, requiresDependencyResolution = ResolutionScope.COMPILE)
public class CompileJavaMojo extends AbstractCompileMojo {

    @Component
    private HaxeCompiler haxeCompiler;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        super.execute();

        File output = new File(outputDirectory, project.getBuild().getFinalName());

        if (output.exists() && output.isFile())
            output.delete();

        EnumMap<CompileTarget, String> targets = new EnumMap<CompileTarget, String>(CompileTarget.class);
        targets.put(CompileTarget.java, output.getName());

        try
        {
            haxeCompiler.compile(project, targets, main, debug, false);
        }
        catch (Exception e)
        {
            throw new MojoFailureException("Java compilation failed", e);
        }

        File jar = new File(output, output.getName() + ".jar");

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
}
