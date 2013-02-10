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
package org.haxe.maven;

import org.haxe.maven.components.HaxeCompiler;
import org.haxe.maven.utils.ArtifactFilterHelper;
import org.haxe.maven.utils.CompileTarget;
import org.haxe.maven.utils.OutputNamesHelper;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.*;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.texen.util.FileUtil;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Properties;

/**
 * Compile tests with `neko` compile target.
 */
@Mojo(name = "testCompile", defaultPhase = LifecyclePhase.TEST_COMPILE, requiresDependencyResolution = ResolutionScope.TEST)
public class TestCompileMojo extends AbstractHaxeMojo {

    /**
     * Test runner class. If is not defined then generated automatically.
     */
    @Parameter
    private String testRunner;

    @Component
    private HaxeCompiler compiler;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        super.execute();

        if (project.getTestCompileSourceRoots().size() == 0)
        {
            getLog().info("No test sources to compile");
            return;
        }

        if (testRunner == null)
        {
            testRunner = generateTestRunner();
        }

        String output = OutputNamesHelper.getTestOutput(project);
        EnumMap<CompileTarget, String> targets = new EnumMap<CompileTarget, String>(CompileTarget.class);
        targets.put(CompileTarget.neko, output);

        try
        {
            compiler.compile(project, targets, testRunner, true, true,
                    ArtifactFilterHelper.TEST, getCommonAdditionalArgs());
        }
        catch (Exception e)
        {
            throw new MojoFailureException("Tests compilation failed", e);
        }
    }

    private String generateTestRunner() throws MojoExecutionException
    {
        File testRunnerDir = new File(project.getBuild().getDirectory(), "testRunner");
        testRunnerDir.mkdirs();
        project.addTestCompileSourceRoot(testRunnerDir.getAbsolutePath());
        File testRunnerFile = new File(testRunnerDir, "GeneratedTestRunner.hx");

        Properties p = new Properties();

        p.setProperty(VelocityEngine.VM_CONTEXT_LOCALSCOPE, Boolean.toString(true));
        p.setProperty(VelocityEngine.RESOURCE_LOADER, "classpath");
        p.setProperty("classpath."
                      + VelocityEngine.RESOURCE_LOADER
                      + ".class", ClasspathResourceLoader.class.getName());

        Velocity.init(p);

        try
        {
            FileWriter sw = new FileWriter(testRunnerFile);
            File surefireDir = new File(project.getBuild().getDirectory(), "surefire-reports");
            surefireDir.mkdir();

            VelocityContext context = new VelocityContext();
            context.put("surefireDir", surefireDir.getAbsolutePath());
            context.put("cases", getTestClasses());

            Velocity.mergeTemplate("/testReport.vm", "UTF8", context, sw);
            sw.flush();
            sw.close();
        }
        catch (Exception e)
        {
            throw new MojoExecutionException("Can't generate TestRunner", e);
        }

        return "GeneratedTestRunner";
    }

    private List<String> getTestClasses() throws IOException
    {
        ArrayList<String> result = new ArrayList<String>();

        for (String testSourcesRootPath : project.getTestCompileSourceRoots())
        {
            File testSourcesRoot = new File(testSourcesRootPath);
            URI rootURI = testSourcesRoot.toURI();
            List<File> fileList = getFileList(testSourcesRoot);

            for (File file : fileList)
            {
                URI fileURI = file.toURI();
                fileURI = rootURI.relativize(fileURI);
                String className = fileURI.toString()
                        .replace(".hx", "")
                        .replaceAll("/", ".");

                result.add(className);
            }
        }

        return result;
    }

    private List<File> getFileList(File root) throws IOException
    {
        ArrayList<File> result = new ArrayList<File>();

        for (String fileName : root.list())
        {
            File file = new File(root, fileName);
            if (file.isDirectory())
            {
                result.addAll(getFileList(file));
            } else
            {
                // If file contains haxe.unit.TestCase in imports
                // or inherits it then believe that it is test case.
                if (FileUtils.fileRead(file).indexOf("haxe.unit.TestCase") > -1)
                    result.add(file);
            }
        }

        return result;
    }


}