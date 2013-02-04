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

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.repository.RepositorySystem;

import java.io.File;
import java.util.List;

abstract public class AbstractHaxeMojo extends AbstractMojo {

    @Component
    protected MavenProject project;

    @Component
    protected RepositorySystem repositorySystem;

    @Component
    public List<Artifact> pluginArtifacts;

    @Parameter (property="localRepository", required = true, readonly = true)
    protected ArtifactRepository localRepository;

    @Parameter (property="project.remoteArtifactRepositories", required = true, readonly = true)
    protected List<ArtifactRepository> remoteRepositories;

    // TODO WTF
    @Parameter(property = "project.build.directory", required = true)
    protected File outputDirectory;
}
