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
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.io.IOException;

/**
 * @goal test-run
 * @phase test-run
 */
public class TestRunHaxeMojo extends CommandHaxeMojo {

    @Override
    protected void executeArguments() throws MojoExecutionException, MojoFailureException
    {
        File testJarFile = new File(outputDirectory, "haxe-java-test/haxe-java-test.jar");

        if (!testJarFile.exists())
            return;

        try
        {
            Runtime runtime = setupRuntime();
            runExecutable(runtime, new String[]{"java", "-jar", testJarFile.getAbsolutePath()});
        }
        catch (CommandHaxeMojo.ProcessExecutionException e)
        {
            new MojoFailureException("Test failed", e);
        }
    }
}
