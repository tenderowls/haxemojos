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

import com.yelbota.plugins.nd.DependencyHelper;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * @goal dependency
 * @threadSafe
 */
public class DependencyHaxeMojo extends AbstractHaxeMojo {

    public static final String ZIP = "zip";
    public static final String TGZ = "tgz";

    /**
     * Haxe version
     * @parameter property="haxeVersion"
     */
    private String haxeVersion;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        getHaxeArtifact();
    }

    public Artifact getHaxeArtifact() throws MojoFailureException
    {

        DependencyHelper dependencyHelper = new DependencyHelper() {

            @Override
            protected String getDefaultArtifactId() throws MojoFailureException
            {
                return "haxe-compiler";
            }

            @Override
            protected String getDefaultGroupId() throws MojoFailureException
            {
                return "org.haxe.compiler";
            }

            @Override
            protected String getDefaultVersion() throws MojoFailureException
            {
                getLog().debug("haxeVersion = " + haxeVersion);
                return haxeVersion;
            }

            @Override
            protected String getDefaultPackaging() throws MojoFailureException
            {
                getLog().debug(getSDKArtifactPackaging(getDefaultClassifier()));
                return getSDKArtifactPackaging(getDefaultClassifier());
            }

            @Override
            protected String getDefaultClassifier() throws MojoFailureException
            {
                String result = super.getDefaultClassifier();

                if (result.equals(OS_CLASSIFIER_LINUX))
                {
                    String arch = System.getProperty("os.arch");
                    return result + (arch.indexOf("64") > -1 ? "64" : "32");
                }

                return result;
            }
        };

        Artifact artifact = dependencyHelper.resolve(
                pluginArtifacts,
                repositorySystem,
                localRepository,
                remoteRepositories
        );

        getLog().debug("No plugin dependency defined.");

        if (artifact == null && haxeVersion == null)
        {
            throw new MojoFailureException("haxeVersion or plugin dependency must be defined");
        }

        return artifact;
    }


    public String getSDKArtifactPackaging(String classifier)
    {
        if (classifier.equals(DependencyHelper.OS_CLASSIFIER_WINDOWS))
        {
            return ZIP;
        } else
        {

            return TGZ;
        }
    }
}
