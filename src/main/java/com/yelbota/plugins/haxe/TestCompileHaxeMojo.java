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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.util.List;

/**
 * @goal test-compile
 * @phase test-compile
 */
public final class TestCompileHaxeMojo extends AbstractCompileJavaMojo {

    /**
     * @parameter
     */
    private String testRunner;

    @Override
    protected List<String> prepareArgumentsList()
    {
        List<String> argumentsList = super.prepareArgumentsList();

        // Add project test-compile source roots (such as src/test/haxe)
        for (String path : project.getTestCompileSourceRoots())
            addSourcePath(argumentsList, path);

        return argumentsList;
    }

    @Override
    protected void addMain(List<String> argumentsList)
    {
        argumentsList.add("-main");
        argumentsList.add(testRunner);
    }

    @Override
    protected String getWorkDirectory()
    {
        return super.getWorkDirectory() + "-test";
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        if (testRunner != null) {
            super.execute();
        }
        else {
            getLog().info("No test runner defined. Define `testRunner` in configuration section of haxe-maven-plugin to enable unit tests");
        }
    }
}
