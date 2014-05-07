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
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;

public abstract class AbstractCompileFlashMojo extends AbstractCompileMojo {

    /**
     * More type strict flash API
     */
    @Parameter
    private boolean flashStrict;

    /**
     * Change the SWF version (6 to 11.x)
     */
    @Parameter(defaultValue = "11.2", required = true)
    private String swfVersion;

    /**
     * Place objects found on the stage of the SWF lib
     */
    @Parameter
    private boolean flashUseStage;

    /**
     * Define SWF header (width:height:fps:color)
     */
    @Parameter
    private String swfHeader;
    
    @Override
    public abstract void execute() throws MojoExecutionException, MojoFailureException;

    protected void execute(String extension, CompileTarget compileTarget) throws MojoExecutionException, MojoFailureException
    {
        super.execute();

        File output = new File(outputDirectory, project.getBuild().getFinalName() + "." + extension);

        if (output.exists())
            output.delete();

        EnumMap<CompileTarget, String> targets = new EnumMap<CompileTarget, String>(CompileTarget.class);
        targets.put(compileTarget, output.getAbsolutePath());

        try
        {
            List<String> additionalArgs = getFlashAdditionalArguments();
            additionalArgs.addAll(getCommonAdditionalArgs());
            compiler.compile(project, targets, main, debug, false, ArtifactFilterHelper.COMPILE, additionalArgs);
        }
        catch (Exception e)
        {
            throw new MojoFailureException("Flash compilation failed", e);
        }

        if (output.exists())
            project.getArtifact().setFile(output);
    }

    protected List<String> getFlashAdditionalArguments()
    {
        List<String> additionalArgs = new LinkedList<String>();

        if (flashStrict)
            additionalArgs.add("--flash-strict");

        if (flashUseStage)
            additionalArgs.add("-flash-use-stage");

        if (swfHeader != null) {
            additionalArgs.add("-swf-header");
            additionalArgs.add(swfHeader);
        }

        additionalArgs.add("-swf-version");
        additionalArgs.add(swfVersion);
        return additionalArgs;
    }
}
