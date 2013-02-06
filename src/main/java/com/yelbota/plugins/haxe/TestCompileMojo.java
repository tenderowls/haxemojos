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

import com.yelbota.plugins.haxe.components.HaxeCompiler;
import com.yelbota.plugins.haxe.utils.CompileTarget;
import com.yelbota.plugins.haxe.utils.OutputNamesHelper;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.*;

import java.util.EnumMap;

/**
 * Compile tests with `neko` compile target.
 */
@Mojo(name = "testCompile", defaultPhase = LifecyclePhase.TEST_COMPILE, requiresDependencyResolution = ResolutionScope.TEST)
public class TestCompileMojo extends AbstractHaxeMojo {

    /**
     * Test runner class.
     */
    @Parameter
    private String testRunner;

    @Component
    private HaxeCompiler haxeCompiler;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        super.execute();

        String output = OutputNamesHelper.getTestOutput(project);
        EnumMap<CompileTarget, String> targets = new EnumMap<CompileTarget, String>(CompileTarget.class);
        targets.put(CompileTarget.neko, output);

        try
        {
            haxeCompiler.compile(project, targets, testRunner, true, true);
        }
        catch (Exception e)
        {
            throw new MojoFailureException("Tests compilation failed", e);
        }
    }
}