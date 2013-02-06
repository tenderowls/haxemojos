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
package com.yelbota.plugins.haxe.components;

import com.sun.istack.internal.NotNull;
import com.yelbota.plugins.haxe.components.nativeProgram.NativeProgram;
import com.yelbota.plugins.haxe.components.nativeProgram.NativeProgramException;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy;
import org.apache.maven.artifact.repository.MavenArtifactRepository;
import org.apache.maven.artifact.repository.layout.DefaultRepositoryLayout;
import org.apache.maven.artifact.resolver.ArtifactResolutionRequest;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Plugin;
import org.apache.maven.project.MavenProject;
import org.apache.maven.repository.RepositorySystem;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;

import java.io.File;
import java.util.*;

@Component(role = NativeBootstrap.class)
public class NativeBootstrap {

    //-------------------------------------------------------------------------
    //
    //  Injection
    //
    //-------------------------------------------------------------------------

    @Requirement
    private RepositorySystem repositorySystem;

    @Requirement(hint = "haxe")
    private NativeProgram haxe;

    @Requirement(hint = "neko")
    private NativeProgram neko;

    @Requirement(hint = "haxelib")
    private NativeProgram haxelib;

    //-------------------------------------------------------------------------
    //
    //  Fields
    //
    //-------------------------------------------------------------------------

    private MavenProject project;

    private ArtifactRepository localRepository;

    //-------------------------------------------------------------------------
    //
    //  Public
    //
    //-------------------------------------------------------------------------

    public void initialize(MavenProject project, ArtifactRepository localRepository) throws Exception
    {
        this.project = project;
        this.localRepository = localRepository;

        Map<String, Plugin> pluginMap = project.getBuild().getPluginsAsMap();
        Plugin plugin = pluginMap.get("com.yelbota.plugins:haxe-maven-plugin");
        Artifact pluginArtifact = resolveArtifact(repositorySystem.createPluginArtifact(plugin));
        String pluginHomeName = plugin.getArtifactId() + "-" + plugin.getVersion();
        File pluginHome = new File(pluginArtifact.getFile().getParentFile(), pluginHomeName);

        if (!pluginHome.exists())
            pluginHome.mkdirs();

        initializePrograms(pluginHome, plugin.getDependencies());
        initializeHaxelib(pluginHome);
    }

    //-------------------------------------------------------------------------
    //
    //  Private methods
    //
    //-------------------------------------------------------------------------

    private void initializeHaxelib(File pluginHome) throws Exception
    {
        try
        {
            File haxelibHome = new File(pluginHome, "_haxelib");

            if (!haxelibHome.exists())
            {
                // Setup haxelib
                haxelib.execute("setup", haxelibHome.getAbsolutePath());
            }
        }
        catch (NativeProgramException e)
        {
            throw new Exception("Cant setup haxelib", e);
        }

        // Add haxelib virtual repository.
        project.getRemoteArtifactRepositories().add(new MavenArtifactRepository("lib.haxe.org", "http://lib.haxe.org",
                new HaxelibRepositoryLayout(),
                new ArtifactRepositoryPolicy(false, ArtifactRepositoryPolicy.UPDATE_POLICY_ALWAYS, ArtifactRepositoryPolicy.CHECKSUM_POLICY_IGNORE),
                new ArtifactRepositoryPolicy(true, ArtifactRepositoryPolicy.UPDATE_POLICY_NEVER, ArtifactRepositoryPolicy.CHECKSUM_POLICY_IGNORE)
        ));
    }

    private void initializePrograms(File pluginHome, List<Dependency> pluginDependencies) throws Exception
    {
        Map<String, Artifact> artifactsMap = new HashMap<String, Artifact>();
        Set<String> path = new HashSet<String>();
        File outputDirectory = getOutputDirectory();

        // Add java to PATH
        path.add(new File(System.getProperty("java.home"), "bin").getAbsolutePath());

        for (Dependency dependency : pluginDependencies)
        {
            String artifactKey = dependency.getGroupId() + ":" + dependency.getArtifactId();

            if (artifactKey.equals(HAXE_COMPILER_KEY) || artifactKey.equals(NEKO_KEY))
            {
                String classifier = getDefaultClassifier();
                Artifact artifact = resolveArtifact(repositorySystem.createArtifactWithClassifier(
                        dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion(),
                        getSDKArtifactPackaging(classifier), classifier
                ));
                artifactsMap.put(artifactKey, artifact);
            }
        }

        if (artifactsMap.get(HAXE_COMPILER_KEY) == null)
        {
            throw new Exception(String.format(
                    "Haxe Compile dependency (%s) not fount in haxe-maven-plugin dependencies",
                    HAXE_COMPILER_KEY));
        }

        if (artifactsMap.get(NEKO_KEY) == null)
        {
            throw new Exception(String.format(
                    "Neko Runtime dependency (%s) not fount in haxe-maven-plugin dependencies",
                    NEKO_KEY));
        }

        haxe.initialize(artifactsMap.get(HAXE_COMPILER_KEY), outputDirectory, pluginHome, path);
        haxelib.initialize(artifactsMap.get(HAXE_COMPILER_KEY), outputDirectory, pluginHome, path);
        neko.initialize(artifactsMap.get(NEKO_KEY), outputDirectory, pluginHome, path);
    }

    @NotNull
    private File getOutputDirectory()
    {
        File outputDirectory = new File(project.getBuild().getDirectory());

        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs();
        }
        else if (!outputDirectory.isDirectory()) {
            outputDirectory.delete();
            outputDirectory.mkdirs();
        }

        return outputDirectory;
    }

    @NotNull
    private String getSDKArtifactPackaging(String classifier)
    {
        if (classifier.equals(OS_CLASSIFIER_WINDOWS))
        {
            return ZIP;
        } else
        {
            return TGZ;
        }
    }

    @NotNull
    private String getDefaultClassifier() throws Exception
    {
        String systemName = System.getProperty("os.name");
        String preparedName = systemName.toLowerCase();

        if (preparedName.indexOf("win") > -1)
        {
            return OS_CLASSIFIER_WINDOWS;
        } else if (preparedName.indexOf("lin") > -1)
        {
            String arch = System.getProperty("os.arch");
            if (arch.indexOf("64") > -1)
            {
                return OS_CLASSIFIER_LINUX + "64";
            }
            return OS_CLASSIFIER_LINUX;
        } else if (preparedName.indexOf("mac") > -1)
        {
            return OS_CLASSIFIER_MAC;
        } else
        {
            throw new Exception(systemName + " is not supported");
        }
    }

    @NotNull
    private Artifact resolveArtifact(Artifact artifact) throws Exception
    {
        ArtifactResolutionRequest request = new ArtifactResolutionRequest();

        request.setArtifact(artifact);
        request.setLocalRepository(localRepository);
        request.setRemoteRepositories(project.getRemoteArtifactRepositories());
        ArtifactResolutionResult resolutionResult = repositorySystem.resolve(request);

        if (!resolutionResult.isSuccess())
        {
            if (artifact.getType().equals(TGZ)) {
                artifact = repositorySystem.createArtifactWithClassifier(
                        artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion(),
                        TARGZ, artifact.getClassifier());
                request = new ArtifactResolutionRequest();
                request.setArtifact(artifact);
                request.setLocalRepository(localRepository);
                request.setRemoteRepositories(project.getRemoteArtifactRepositories());
                resolutionResult = repositorySystem.resolve(request);
                if (resolutionResult.isSuccess()) {
                    return artifact;
                }
            }
            String message = "Failed to resolve artifact " + artifact;
            throw new Exception(message);
        }

        return artifact;
    }

    private static final String ZIP = "zip";
    private static final String TGZ = "tgz";
    private static final String TARGZ = "tar.gz";
    private static final String OS_CLASSIFIER_MAC = "mac";
    private static final String OS_CLASSIFIER_WINDOWS = "windows";
    private static final String OS_CLASSIFIER_LINUX = "linux";
    private static final String HAXE_COMPILER_KEY = "org.haxe.compiler:haxe-compiler";
    private static final String NEKO_KEY = "org.nekovm:nekovm";

    private class HaxelibRepositoryLayout extends DefaultRepositoryLayout {

        @Override
        public String getId()
        {
            return "haxelib";
        }
    }
    
}
