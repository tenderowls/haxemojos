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
 * Run 'haxelib' with parameters.
 */
@Mojo(name = "haxelibRun")
public final class HaxelibRunMojo extends AbstractHaxeMojo {

    @Component(hint = "haxelib")
    private NativeProgram haxelibRunner;

    @Parameter(required=true)
    private String haxelib;

    @Parameter(required=true)
    private List<String> arguments;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        super.execute();

        try
        {
            arguments.add(0, haxelib);
            arguments.add(0, "run");
            haxelibRunner.execute(arguments);
        }
        catch (NativeProgramException e)
        {
            throw new MojoFailureException("Run Haxelib failed", e);
        }
    }
}
