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
package com.tenderowls.opensource.haxemojos;

import com.tenderowls.opensource.haxemojos.components.nativeProgram.NativeProgram;
import com.tenderowls.opensource.haxemojos.components.nativeProgram.NativeProgramException;
import com.tenderowls.opensource.haxemojos.utils.OutputNamesHelper;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.xml.Xpp3Dom;

import java.io.File;
import java.util.List;

/**
 * Run 'haxelib run [lib_name]' with parameters.
 * The goal should be specified in the plugin settings in the section <pre>&#60;executions>&#60;/executions></pre>
 * Example:
 * <pre>
 * &#60;executions&#62;
 * &#60;execution&#62;
 *     &#60;id&#62;munit-test&#60;/id&#62;
 *     &#60;phase>test&#60;/phase&#62;
 *     &#60;configuration&#62;
 *         &#60;haxelib&#62;munit&#60;/haxelib&#62;
 *         &#60;arguments&#62;
 *             &#60;argument&#62;test&#60;/argument&#62;
 *         &#60;/arguments&#62;
 *     &#60;/configuration&#62;
 *     &#60;goals&#62;
 *         &#60;goal&#62;haxelibRun&#60;/goal&#62;
 *     &#60;/goals&#62;
 * &#60;/execution&#62;
 * &#60;/executions&#62;
 * </pre>
 */
@Mojo(name = "haxelibRun")
public final class HaxelibRunMojo extends AbstractHaxeMojo {

    @Component(hint = "haxelib")
    private NativeProgram haxelibRunner;

    /**
     * Name of the lib which would be executed
     */
    @Parameter(required=true)
    private String haxelib;

    /**
     * Directory for running goal
     */
    @Parameter(required=false)
    private String baseDir;

    /**
     * List of parameters for execution
     */
    @Parameter(required=true)
    private List<String> arguments;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        super.execute();

        try {
            arguments.add(0, haxelib);
            arguments.add(0, "run");
            if(StringUtils.isNotEmpty(baseDir)) {
                File directory = new File(baseDir);
                haxelibRunner.execute(arguments, directory);
            } else {
                haxelibRunner.execute(arguments);
            }

        } catch (NativeProgramException e) {
            throw new MojoFailureException("Run Haxelib failed", e);
        }
    }
}
