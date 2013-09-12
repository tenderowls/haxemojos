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
package com.tenderowls.opensource.haxemojos;

import com.tenderowls.opensource.haxemojos.utils.ArtifactFilterHelper;
import com.tenderowls.opensource.haxemojos.utils.CompileTarget;
import com.tenderowls.opensource.haxemojos.utils.HaxeFileExtensions;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.io.File;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Compile JS.
 */
@Mojo(name="compileJs", defaultPhase = LifecyclePhase.COMPILE, requiresDependencyResolution = ResolutionScope.COMPILE)
public class CompileJsMojo extends AbstractCompileMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        super.execute();

        File output = new File(outputDirectory, project.getBuild().getFinalName() + "." + HaxeFileExtensions.JS);

        if (output.exists())
            output.delete();

        EnumMap<CompileTarget, String> targets = new EnumMap<CompileTarget, String>(CompileTarget.class);
        targets.put(CompileTarget.js, output.getAbsolutePath());

        try
        {
            List<String> additionalArgs = new LinkedList<String>();

            additionalArgs.addAll(getCommonAdditionalArgs());

            compiler.compile(project, targets, main, debug, false, ArtifactFilterHelper.COMPILE, additionalArgs);
        }
        catch (Exception e)
        {
            throw new MojoFailureException("JS compilation failed", e);
        }

        if (output.exists())
            project.getArtifact().setFile(output);
    }

}
