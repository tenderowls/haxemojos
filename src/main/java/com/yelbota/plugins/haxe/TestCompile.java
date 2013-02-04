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

import com.yelbota.plugins.haxe.tasks.compile.CompileNekoTask;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.List;

@Mojo(name = "testCompile", defaultPhase = LifecyclePhase.TEST_COMPILE)
public class TestCompile extends AbstractCompileMojo {

    @Parameter
    private String testRunner;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        super.execute();

        TestCompileNekoTask task = new TestCompileNekoTask(pluginHome, haxeUnpackDirectory, nekoUnpackDirectory, outputDirectory, getLog(), project, testRunner, true);
        task.execute();
    }

    private static class TestCompileNekoTask extends CompileNekoTask {

        public TestCompileNekoTask(File pluginHome, File haxeUnpackDirectory, File nekoUnpackDirectory, File outputDirectory, Log log, MavenProject project, String main, boolean debug)
        {
            super(pluginHome, haxeUnpackDirectory, nekoUnpackDirectory, outputDirectory, log, project, main, debug);
        }

        @Override
        protected List<String> prepareArgumentsList()
        {
            List<String> argumentsList = super.prepareArgumentsList();

            for (String root: project.getTestCompileSourceRoots())
                addSourcePath(argumentsList, root);

            return argumentsList;
        }

        @Override
        protected String getFinalArtifactName()
        {
            return project.getBuild().getFinalName() + "-test.n";
        }
    }
}