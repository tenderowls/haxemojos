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
package org.haxe.maven;

import org.haxe.maven.utils.ArtifactFilterHelper;
import org.haxe.maven.utils.CompileTarget;
import org.haxe.maven.utils.HaxeFileExtensions;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.io.File;
import java.util.EnumMap;

/**
 * Compile in nekovm bytecode.
 */
@Mojo(name = "compileNeko", defaultPhase = LifecyclePhase.COMPILE, requiresDependencyResolution = ResolutionScope.COMPILE)
public class CompileNekoMojo extends AbstractCompileMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        super.execute();

        File output = new File(outputDirectory, project.getBuild().getFinalName() + "." + HaxeFileExtensions.NEKO);

        if (output.exists())
            output.delete();

        EnumMap<CompileTarget, String> targets = new EnumMap<CompileTarget, String>(CompileTarget.class);
        targets.put(CompileTarget.neko, output.getAbsolutePath());

        try
        {
            compiler.compile(project, targets, main, debug, false,
                    ArtifactFilterHelper.COMPILE, getCommonAdditionalArgs());
        }
        catch (Exception e)
        {
            throw new MojoFailureException("Neko compilation failed", e);
        }

        if (output.exists())
            project.getArtifact().setFile(output);
    }
}