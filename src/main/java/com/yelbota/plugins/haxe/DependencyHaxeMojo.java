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
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Downloads haxe and neko dependencies
 */
@Mojo(name = "dependency")
public class DependencyHaxeMojo extends AbstractHaxeMojo {

    /**
     * NekoVM version
     */
    @Parameter(property = "nekoVersion")
    private String nekoVersion;

    /**
     * Haxe version
     */
    @Parameter(property = "haxeVersion")
    private String haxeVersion;

    /**
     * NME version
     */
    @Parameter(property = "nmeVersion")
    private String nmeVersion;

    //@Parameter(property = "dependencies")
    //private List<DependencyNode> haxeDependencies;

    public static final String ZIP = "zip";
    public static final String TGZ = "tar.gz";

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        getHaxeArtifact();
        getNekoArtifact();
        getNMEArtifact();
    }

    public Artifact getHaxeArtifact() throws MojoFailureException
    {
        DependencyHelper dependencyHelper = new HaxeDependencyHelper() {

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
                return haxeVersion;
            }
        };

        Artifact artifact = dependencyHelper.resolve(
                pluginArtifacts,
                repositorySystem,
                localRepository,
                remoteRepositories
        );

        return artifact;
    }

    public Artifact getNekoArtifact() throws MojoFailureException
    {

        DependencyHelper dependencyHelper = new HaxeDependencyHelper() {

            @Override
            protected String getDefaultArtifactId() throws MojoFailureException
            {
                return "nekovm";
            }

            @Override
            protected String getDefaultGroupId() throws MojoFailureException
            {
                return "org.nekovm";
            }

            @Override
            protected String getDefaultVersion() throws MojoFailureException
            {
                return nekoVersion;
            }
        };

        Artifact artifact = dependencyHelper.resolve(
                pluginArtifacts,
                repositorySystem,
                localRepository,
                remoteRepositories
        );

        return artifact;
    }

    public Artifact getNMEArtifact() throws MojoFailureException
    {
        DependencyHelper dependencyHelper = null;
        Artifact artifact = null;
        if (nmeVersion != null) {
            dependencyHelper = new HaxeDependencyHelper() {
                @Override
                protected String getDefaultArtifactId() throws MojoFailureException
                {
                    return "nme";
                }

                @Override
                protected String getDefaultGroupId() throws MojoFailureException
                {
                    return "org.haxenme";
                }

                @Override
                protected String getDefaultVersion() throws MojoFailureException
                {
                    return nmeVersion;
                }
            };
            artifact = dependencyHelper.resolve(
                pluginArtifacts,
                repositorySystem,
                localRepository,
                remoteRepositories
            );
        }

        return artifact;
    }

    private static abstract class HaxeDependencyHelper extends DependencyHelper {

        @Override
        protected String getDefaultPackaging() throws MojoFailureException
        {
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
}
