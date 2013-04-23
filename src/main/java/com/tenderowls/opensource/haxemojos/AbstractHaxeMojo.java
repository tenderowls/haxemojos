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

import com.tenderowls.opensource.haxemojos.components.NativeBootstrap;
import com.tenderowls.opensource.haxemojos.utils.HaxeResource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public abstract class AbstractHaxeMojo extends AbstractMojo {

    /**
     * Define a conditional compilation flag
     */
    @Parameter
    protected List<String> defines;

    /**
     * Turn on verbose mode
     */
    @Parameter
    protected boolean verbose;

    /**
     * Add a named resource files
     *
     *   <resources>
     *       <resource>
     *           <name>license-test</name>
     *           <file>LICENSE.txt</file>
     *       </resource>
     *   </resources>
     */
    @Parameter
    protected List<HaxeResource> resources;

    @Component
    private NativeBootstrap bootstrap;

    @Parameter(property = "localRepository", required = true, readonly = true)
    private ArtifactRepository localRepository;

    @Component
    protected MavenProject project;

    protected File outputDirectory;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        try
        {
            outputDirectory = new File(project.getBuild().getDirectory());
            bootstrap.initialize(project, localRepository);
        }
        catch (Exception e)
        {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    public List<String> getCommonAdditionalArgs()
    {
        LinkedList<String> args = new LinkedList<String>();
        
        if (defines != null)
        {
            for (String define : defines)
            {
                args.add("-D");
                args.add(define);
            }
        }

        if (resources != null)
        {
            for (HaxeResource resource : resources)
            {
                args.add("-resource");
                args.add(resource.file.getAbsolutePath() + "@" + resource.name);
            }
        }

        if (verbose)
            args.add("-verbose");

        return args;
    }
}
