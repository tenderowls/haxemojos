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
import com.tenderowls.opensource.haxemojos.components.nativeProgram.NativeProgram;
import com.tenderowls.opensource.haxemojos.utils.HaxeFileExtensions;
import com.tenderowls.opensource.haxemojos.utils.HaxeResource;
import org.apache.maven.artifact.Artifact;
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
import java.util.Properties;

public abstract class AbstractHaxeMojo extends AbstractMojo {

    /**
     * Define a conditional compilation flag
     */
    @Parameter
    protected List<String> defines;

    /**
<<<<<<< Updated upstream
     * Define compiler configuration with Macros
     */
    @Parameter
    protected String macro;
=======
     * Define a compile time constant
     */
    @Parameter
    protected Properties defineValues;
>>>>>>> Stashed changes

    /**
     * Turn on verbose mode
     */
    @Parameter
    protected boolean verbose;

    /**
     * Add a named resource files
     *
     * <pre>
     * &#60;resources&#62;
     *     &#60;resource&#62;
     *         &#60;name&#62;license-test&#60;/name&#62;
     *         &#60;file&#62;LICENSE.txt&#60;/file&#62;
     *     &#60;/resource&#62;
     * &#60;/resources&#62;</pre>
     */
    @Parameter
    protected List<HaxeResource> resources;

    @Component
    private NativeBootstrap bootstrap;

    @Component(hint = "haxelib")
    private NativeProgram haxelibRunner;

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

            for (Artifact artifact : project.getArtifacts())
            {
                if (artifact.getType().equals(HaxeFileExtensions.HAXELIB))
                {
                    File haxelibDir = new File(haxelibRunner.getHome(), "_haxelib");
                    File artifactDir = new File(haxelibDir, artifact.getArtifactId());
                    File installedDir = new File(artifactDir, artifact.getVersion().replace(".", ","));

                    if (!installedDir.exists())
                    {
                        String packagePath = artifact.getFile().getAbsolutePath();

                        if (haxelibRunner.execute("local", packagePath) > 0)
                            throw new Exception("Can't install haxelib locally from " + packagePath);
                    }
                }
            }
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
        
        if(macro != null) {
            args.add("--macro");
            args.add(macro);
        }

        if (defineValues != null)
        {
            for (String name : defineValues.stringPropertyNames())
            {
                args.add("-D");
                args.add(name+"="+defineValues.getProperty(name));
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
