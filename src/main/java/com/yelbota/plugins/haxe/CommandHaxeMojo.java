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

import com.google.common.base.Joiner;
import com.yelbota.plugins.haxe.utils.CleanStream;
import com.yelbota.plugins.haxe.utils.HaxeFileExtensions;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.io.IOException;

/**
 * @goal command
 * @phase package
 */
public class CommandHaxeMojo extends UnpackHaxeMojo {

    /**
     * Custom adt arguments
     *
     * @parameter property="arguments"
     */
    protected String arguments;

    private File haxelibExecutable;

    private File haxelibHome;

    private String[] envp;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        super.execute();
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

            getLog().info(Joiner.on(" ").join(finalArgs));
            runExecutable(runtime, finalArgs);
        }
        catch (ProcessExecutionException e)
        {
            throw new MojoFailureException("haXe execution failed", e);
        }
    }

    protected Runtime setupRuntime()
    {
        haxelibExecutable = new File(haxeUnpackDirectory, "haxelib");
        haxelibHome = FileUtils.resolveFile(pluginHome, "_haxelib");

        File javaBin = new File(System.getProperty("java.home"), "bin");
        String path = haxeUnpackDirectory.getAbsolutePath() + ":" +
                      nekoUnpackDirectory.getAbsolutePath() + ":" +
                      javaBin.getAbsolutePath();

        getLog().info(path);

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
                getLog().info("Resolving " + artifact + " haxelib dependency");
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

    protected void runExecutable(Runtime runtime, String[] args) throws ProcessExecutionException
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
                    getLog(),
                    CleanStream.CleanStreamType.ERROR
            );

            CleanStream cleanOutput = new CleanStream(
                    process.getInputStream(),
                    getLog(),
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
