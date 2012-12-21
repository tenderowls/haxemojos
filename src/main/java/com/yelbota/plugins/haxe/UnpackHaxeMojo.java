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
 * @threadSafe
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
     * Final unpack directory
     */
    protected File unpackDirectory;


    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        if (!pluginHome.exists()) {
            if (!pluginHome.mkdirs()) {
                new MojoFailureException("Can't create unpack directory: " + pluginHome.getAbsolutePath());
            }
        }

        haxeArtifact = getHaxeArtifact();
        unpackDirectory = new File(pluginHome, haxeArtifact.getArtifactId() + "-" + haxeArtifact.getVersion());

        if (unpackDirectory.exists()) {
            getLog().debug("haXe already unpacked");
        }
        else {

            File unpackDir = new File(outputDirectory, "tmpUnpack");

            getLog().debug("Unpack directory = " + unpackDirectory.getAbsolutePath());

            UnpackHelper unpackHelper = new UnpackHelper() {

                @Override
                protected void logAlreadyUnpacked() {
                }

                @Override
                protected void logUnpacking() {
                    getLog().info("Unpacking...");
                }
            };

            ConsoleLoggerManager plexusLoggerManager = new ConsoleLoggerManager();
            Logger plexusLogger = plexusLoggerManager.getLoggerForComponent(ROLE);
            DefaultUnpackMethods unpackMethods = new DefaultUnpackMethods(plexusLogger);
            unpackHelper.unpack(unpackDir, haxeArtifact, unpackMethods, getLog());

            for (String firstFileName: unpackDir.list()) {
                File firstFile = new File(unpackDir, firstFileName);
                firstFile.renameTo(unpackDirectory);
                break;
            }
        }
    }
}
