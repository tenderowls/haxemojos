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

import com.yelbota.plugins.haxe.utils.CleanStream;
import com.yelbota.plugins.nd.DependencyHelper;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @goal command
 * @phase package
 * @threadSafe
 */
public class CommandHaxeMojo extends UnpackHaxeMojo {

    /**
     * Custom adt arguments
     * @parameter property="arguments"
     */
    protected String arguments;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        super.execute();
        prepareArguments();

        ArrayList<String> finalArgs = new ArrayList<String>();
        String executableName = "haxe";

        if (haxeArtifact.getClassifier() == DependencyHelper.OS_CLASSIFIER_WINDOWS)
                executableName += ".exe";

        File adtFile = FileUtils.resolveFile(unpackDirectory, executableName);
        finalArgs.add(adtFile.getAbsolutePath());

        for (String s: StringUtils.split(arguments, " "))
            finalArgs.add(s);

        execute(finalArgs.toArray(new String[]{}));
    }

    protected void prepareArguments() throws MojoFailureException
    {
        // Override this method for arguments modification.
    }

    public void execute(String[] args) throws MojoFailureException
    {
        try {

            getLog().debug(StringUtils.join(args, " "));
            Process process = Runtime.getRuntime().exec(args);

            CleanStream cleanError = new CleanStream(process.getErrorStream(),
                    getLog(), CleanStream.CleanStreamType.ERROR);

            CleanStream cleanOutput = new CleanStream(process.getInputStream(),
                    getLog(), CleanStream.CleanStreamType.INFO);

            cleanError.start();
            cleanOutput.start();

            int code = process.waitFor();

            if (code > 0) {
                throw new MojoFailureException("haxe fails with return code #" + code );
            }

        } catch (IOException e) {

            throw new MojoFailureException("Cant execute haxe", e);

        } catch (InterruptedException e) {

            throw new MojoFailureException("Process was interrupted", e);
        }
    }
}
