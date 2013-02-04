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
package com.yelbota.plugins.haxe.tasks;

import com.yelbota.plugins.haxe.utils.CleanStream;
import com.yelbota.plugins.haxe.utils.HaxeFileExtensions;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.io.IOException;

public class CommandTask implements HaxeTask {

    private File haxelibExecutable;
    private File haxelibHome;
    private String[] envp;

    protected String arguments;

    protected File pluginHome;
    protected File haxeUnpackDirectory;
    protected File nekoUnpackDirectory;
    protected File outputDirectory;

    protected Log log;
    protected MavenProject project;

    public CommandTask(File pluginHome, File haxeUnpackDirectory, File nekoUnpackDirectory, File outputDirectory, Log log, MavenProject project)
    {
        this.pluginHome = pluginHome;
        this.haxeUnpackDirectory = haxeUnpackDirectory;
        this.nekoUnpackDirectory = nekoUnpackDirectory;
        this.outputDirectory = outputDirectory;
        this.log = log;
        this.project = project;
    }

    public void execute() throws MojoExecutionException, MojoFailureException
    {
        executeArguments();
    }

    protected void executeArguments() throws MojoExecutionException, MojoFailureException
    {
        this.prepareArguments();

        // Setup runtime with environment vars
        Runtime runtime = setupRuntime();

        // Setup haxelib
        resolveHaxelibDependencies(runtime);

        try
        {
            // Configure final arguments for haxe compiler
            File haxeExecutable = new File(haxeUnpackDirectory, "haxe");
            String[] args = StringUtils.split(arguments, " ");
            String[] finalArgs = new String[args.length + 1];
            System.arraycopy(new String[]{haxeExecutable.getAbsolutePath()}, 0, finalArgs, 0, 1);
            System.arraycopy(args, 0, finalArgs, 1, args.length);

            log.info(StringUtils.join(finalArgs, " "));
            runExecutable(runtime, finalArgs);
        }
        catch (ProcessExecutionException e)
        {
            throw new MojoFailureException("haXe execution failed", e);
        }
    }

    public Runtime setupRuntime()
    {
        haxelibExecutable = new File(haxeUnpackDirectory, "haxelib");
        haxelibHome = FileUtils.resolveFile(pluginHome, "_haxelib");

        File javaBin = new File(System.getProperty("java.home"), "bin");
        String path = haxeUnpackDirectory.getAbsolutePath() + ":" +
                      nekoUnpackDirectory.getAbsolutePath() + ":" +
                      javaBin.getAbsolutePath();

        log.info(path);

        envp = new String[]{
                "PATH=" + path,
                "HOME=" + pluginHome.getAbsolutePath()
        };

        return Runtime.getRuntime();
    }

    private void resolveHaxelibDependencies(Runtime runtime) throws MojoFailureException
    {
        try
        {
            runExecutable(runtime, new String[]{
                    haxelibExecutable.getAbsolutePath(), "setup",
                    haxelibHome.getAbsolutePath()
            });
        }
        catch (ProcessExecutionException e)
        {
            throw new MojoFailureException("Can't setup haxelib");
        }

        for (Artifact artifact : project.getDependencyArtifacts())
        {
            if (artifact.getType().equals(HaxeFileExtensions.HAXELIB) && !artifact.isResolved())
            {
                log.info("Resolving " + artifact + " haxelib dependency");
                try
                {
                    runExecutable(runtime, new String[]{
                            haxelibExecutable.getAbsolutePath(), "install",
                            artifact.getArtifactId(),
                            artifact.getVersion()
                    });

                    artifact.setResolved(true);
                }
                catch (ProcessExecutionException e)
                {
                    throw new MojoFailureException("Could not find artifact " + artifact, e);
                }
            }
        }
    }

    protected void prepareArguments() throws MojoFailureException
    {
        // Override this method for arguments modification.
    }

    public void runExecutable(Runtime runtime, String[] args) throws ProcessExecutionException
    {
        try
        {
            Process process = runtime.exec(args, envp, outputDirectory);
            processExecution(process);
        }
        catch (IOException e)
        {
            throw new ProcessExecutionException(e);
        }
    }

    private void processExecution(Process process) throws ProcessExecutionException
    {
        try
        {
            CleanStream cleanError = new CleanStream(
                    process.getErrorStream(),
                    log,
                    CleanStream.CleanStreamType.ERROR
            );

            CleanStream cleanOutput = new CleanStream(
                    process.getInputStream(),
                    log,
                    CleanStream.CleanStreamType.INFO
            );

            cleanError.start();
            cleanOutput.start();

            int code = process.waitFor();

            if (code > 0)
                throw new ProcessExecutionException(code);
        }
        catch (InterruptedException e)
        {
            throw new ProcessExecutionException(e);
        }
    }

    public void setArguments(String arguments)
    {
        this.arguments = arguments;
    }

    public static class ProcessExecutionException extends Exception {

        public ProcessExecutionException(Throwable cause)
        {
            super(cause);
        }

        public ProcessExecutionException(int code)
        {
            super("Exit code " + code);
        }
    }
}
