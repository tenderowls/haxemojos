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
package com.tenderowls.opensource.haxemojos.components.nativeProgram;

import com.yelbota.plugins.nd.UnpackHelper;
import com.yelbota.plugins.nd.utils.DefaultUnpackMethods;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.repository.RepositorySystem;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;
import com.tenderowls.opensource.haxemojos.utils.CleanStream;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Represents package on native application (or group on applications) as
 * simple executor arguments
 */
public abstract class AbstractNativeProgram implements NativeProgram {

    final String OS = System.getProperty("os.name").toLowerCase();

    //-------------------------------------------------------------------------
    //
    //  Injections
    //
    //-------------------------------------------------------------------------

    @Requirement
    protected RepositorySystem repositorySystem;

    @Requirement
    protected Logger logger;

    //-------------------------------------------------------------------------
    //
    //  Fields
    //
    //-------------------------------------------------------------------------

    protected Artifact artifact;

    protected File outputDirectory;

    protected File pluginHome;

    protected Set<String> path;

    protected Map<String, String> env;

    protected File directory;

    private boolean initialized = false;

    //-------------------------------------------------------------------------
    //
    //  Public methods
    //
    //-------------------------------------------------------------------------

    public void initialize(Artifact artifact, File outputDirectory, File pluginHome, Set<String> path, Map<String, String> env)
    {
        if (initialized)
            return;

        this.artifact = artifact;
        this.outputDirectory = outputDirectory;
        this.pluginHome = pluginHome;
        this.path = path;
        this.env = env;

        try
        {
            this.directory = getDirectory(artifact);
        }
        catch (Exception e)
        {
            logger.error(String.format("Can't unpack %s", artifact.getArtifactId()), e);
        }

        initialized = true;
    }

    public int execute(List<String> arguments) throws NativeProgramException
    {
        return execute(arguments, logger);
    }

    public int execute(List<String> arguments, Logger outputLogger) throws NativeProgramException
    {
        try
        {
            List<String> environmentList = getEnvironment();
            String[] environment = environmentList.toArray(new String[environmentList.size()]);
            arguments = updateArguments(arguments);

            if (isWindows())
            {
                arguments.add(0, "/C");
                arguments.add(0, "cmd");
            }

            logger.debug("Evironment: " + StringUtils.join(environmentList.iterator(), "\n"));
            logger.debug("Executing: " + StringUtils.join(arguments.iterator(), " "));

            Process process = Runtime.getRuntime().exec(
                    arguments.toArray(new String[arguments.size()]),
                    environment,
                    outputDirectory
            );

            return processExecution(process, outputLogger);
        }
        catch (IOException e)
        {
            throw new NativeProgramException("Executable not found", e);
        }
        catch (Exception e)
        {
            throw new NativeProgramException("", e);
        }
    }

    @Override
    public int execute(String ...arguments) throws NativeProgramException
    {
        List<String> list = new ArrayList<String>();

        Collections.addAll(list, arguments);

        return execute(list);
    }

    @Override
    public File getHome()
    {
        return pluginHome;
    }

    //-------------------------------------------------------------------------
    //
    //  Protected methods
    //
    //-------------------------------------------------------------------------

    protected abstract List<String> updateArguments(List<String> arguments);

    protected List<String> getEnvironment()
    {
        ArrayList<String> result = new ArrayList<String>();
        result.add("PATH=" + StringUtils.join(path.iterator(), File.pathSeparator));
        String homeString = pluginHome.getAbsolutePath();

        if (isWindows())
        {
            result.add("HOMEDRIVE=" + homeString.substring(0,2));
            result.add("HOMEPATH=" + homeString.substring(2));
        }

        result.add("HOME=" + homeString);

        for (String evnKey : env.keySet())
        {
            result.add(evnKey + "=" + env.get(evnKey));
        }
        return result;
    }

    protected int processExecution(Process process, Logger outputLogger) throws NativeProgramException
    {
        try
        {
            CleanStream cleanError = new CleanStream(
                    process.getErrorStream(),
                    outputLogger, CleanStream.CleanStreamType.ERROR
            );

            CleanStream cleanOutput = new CleanStream(
                    process.getInputStream(),
                    outputLogger, CleanStream.CleanStreamType.INFO
            );

            cleanError.start();
            cleanOutput.start();

            return process.waitFor();
        }
        catch (InterruptedException e)
        {
            throw new NativeProgramException("Program was interrupted", e);
        }
    }

    private File getDirectory(Artifact artifact) throws Exception
    {
        File unpackDirectory = new File(pluginHome, artifact.getArtifactId() + "-" + artifact.getVersion());

        if (!unpackDirectory.exists())
        {
            File tmpDir = new File(outputDirectory, unpackDirectory.getName() + "-unpack");

            if (tmpDir.exists())
                tmpDir.delete();

            UnpackHelper unpackHelper = new UnpackHelper();
            DefaultUnpackMethods unpackMethods = new DefaultUnpackMethods(logger);
            unpackHelper.unpack(tmpDir, artifact, unpackMethods, null);
            String[] tmpDirListing = tmpDir.list();

            // Sometimes we have archive which contains directory
            // with content, sometimes we have content in the
            // root of the archive.
            File sourceDirectory = tmpDirListing.length == 1
                ? new File(tmpDir, tmpDirListing[0])
                : tmpDir;

            FileUtils.copyDirectoryStructure(sourceDirectory, unpackDirectory);
            updateExecutableMod(sourceDirectory, unpackDirectory);
        }

        String directoryPath = unpackDirectory.getAbsolutePath();
        // Add current directory to path
        path.add(directoryPath);
        return unpackDirectory;
    }

    private void updateExecutableMod(File sourceDirectory, File unpackDirectory) throws IOException
    {
        Iterator<File> sourceFiles = FileUtils.getFiles(sourceDirectory, "**", null).iterator();
        Iterator<File> unpackedFiles = FileUtils.getFiles(unpackDirectory, "**", null).iterator();

        while (sourceFiles.hasNext() && unpackedFiles.hasNext())
        {
            File sourceFile = sourceFiles.next();
            File unpackedFile = unpackedFiles.next();

            if (sourceFile.canExecute())
                unpackedFile.setExecutable(true);
        }
    }

    protected boolean isWindows()
    {
        return OS.contains("win");
    }

    protected boolean isUnix()
    {
        return (OS.contains("nix") ||
                OS.contains("nux") ||
                OS.contains("aix"));
    }


}
