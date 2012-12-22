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

import com.yelbota.plugins.nd.UnpackHelper;
import com.yelbota.plugins.nd.utils.DefaultUnpackMethods;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.console.ConsoleLoggerManager;

import java.io.File;

/**
 * @goal unpack
 */
public class UnpackHaxeMojo extends DependencyHaxeMojo {

    /**
     * Plugin home-folder
     * @parameter expression="${user.home}/.haxe-maven-plugin"
     */
    public File pluginHome;

    /**
     * Resolved artifact with haxe package.
     */
    protected Artifact haxeArtifact;

    /**
     * Resolved artifact with haxe package.
     */
    private Artifact nekoArtifact;

    /**
     * Haxe unpack directory
     */
    protected File haxeUnpackDirectory;

    /**
     * Neko unpack directory
     */
    protected File nekoUnpackDirectory;


    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {

        if (!pluginHome.exists())
        {
            if (!pluginHome.mkdirs())
            {
                new MojoFailureException("Can't create unpack directory: " + pluginHome.getAbsolutePath());
            }
        }

        haxeArtifact = getHaxeArtifact();
        nekoArtifact = getNekoArtifact();

        haxeUnpackDirectory = unpackArtifact(haxeArtifact);
        nekoUnpackDirectory = unpackArtifact(nekoArtifact);
    }

    private File unpackArtifact(Artifact artifact) throws MojoFailureException
    {
        File unpackDirectory = new File(pluginHome, artifact.getArtifactId() + "-" + artifact.getVersion());
        getLog().debug(artifact + " unpack directory = " + unpackDirectory.getAbsolutePath());

        if (!unpackDirectory.exists())
        {
            File tmpDir = new File(outputDirectory, "tmpUnpack");

            UnpackHelper unpackHelper = new UnpackHelper() {

                @Override
                protected void logAlreadyUnpacked()
                {
                }

                @Override
                protected void logUnpacking()
                {
                    getLog().info("Unpacking...");
                }
            };

            ConsoleLoggerManager plexusLoggerManager = new ConsoleLoggerManager();
            Logger plexusLogger = plexusLoggerManager.getLoggerForComponent(ROLE);
            DefaultUnpackMethods unpackMethods = new DefaultUnpackMethods(plexusLogger);
            unpackHelper.unpack(tmpDir, artifact, unpackMethods, getLog());

            for (String firstFileName : tmpDir.list())
            {
                File firstFile = new File(tmpDir, firstFileName);
                firstFile.renameTo(unpackDirectory);
                break;
            }
        }
        else getLog().debug(artifact + " already unpacked");

        return unpackDirectory;
    }
}
