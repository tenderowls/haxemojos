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

import com.yelbota.plugins.haxe.tasks.CommandTask;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;

/**
 * @goal testRun
 * @phase test-run
 */
public class TestRunMojo extends UnpackHaxeMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        super.execute();

        try
        {
            CommandTask task = new CommandTask(pluginHome, haxeUnpackDirectory, nekoUnpackDirectory, outputDirectory, getLog(),  project);
            task.runExecutable(task.setupRuntime(), new String[]{ new File(nekoUnpackDirectory, "neko").getAbsolutePath(), project.getBuild().getFinalName() + "-test.n" });
        }
        catch (CommandTask.ProcessExecutionException e)
        {
            throw new MojoFailureException("Test failed", e);
        }
    }
}
