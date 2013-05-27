/**
 * Copyright (C) 2012 https://github.com/yelbota/native-dependency-maven-plugin-base
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
package com.yelbota.plugins.nd.stubs;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.InvalidRepositoryException;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.artifact.resolver.ArtifactResolutionRequest;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.Repository;
import org.apache.maven.repository.ArtifactDoesNotExistException;
import org.apache.maven.repository.ArtifactTransferFailedException;
import org.apache.maven.repository.ArtifactTransferListener;
import org.apache.maven.repository.RepositorySystem;
import org.apache.maven.settings.Mirror;
import org.apache.maven.settings.Proxy;
import org.apache.maven.settings.Server;
import org.sonatype.aether.RepositorySystemSession;

import java.io.File;
import java.util.List;

public class RepositorySystemStub implements RepositorySystem {

    @Override
    public Artifact createArtifact(String groupId, String artifactId, String version, String packaging) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Artifact createArtifact(String groupId, String artifactId, String version, String scope, String type) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Artifact createProjectArtifact(String groupId, String artifactId, String version) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Artifact createArtifactWithClassifier(String groupId, String artifactId, String version, String type, String classifier) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Artifact createPluginArtifact(Plugin plugin) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Artifact createDependencyArtifact(Dependency dependency) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ArtifactRepository buildArtifactRepository(Repository repository) throws InvalidRepositoryException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ArtifactRepository createDefaultRemoteRepository() throws InvalidRepositoryException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ArtifactRepository createDefaultLocalRepository() throws InvalidRepositoryException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ArtifactRepository createLocalRepository(File localRepository) throws InvalidRepositoryException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ArtifactRepository createArtifactRepository(String id, String url, ArtifactRepositoryLayout repositoryLayout, ArtifactRepositoryPolicy snapshots, ArtifactRepositoryPolicy releases) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<ArtifactRepository> getEffectiveRepositories(List<ArtifactRepository> repositories) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Mirror getMirror(ArtifactRepository repository, List<Mirror> mirrors) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void injectMirror(List<ArtifactRepository> repositories, List<Mirror> mirrors) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void injectProxy(List<ArtifactRepository> repositories, List<Proxy> proxies) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void injectAuthentication(List<ArtifactRepository> repositories, List<Server> servers) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void injectMirror(RepositorySystemSession session, List<ArtifactRepository> repositories) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void injectProxy(RepositorySystemSession session, List<ArtifactRepository> repositories) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void injectAuthentication(RepositorySystemSession session, List<ArtifactRepository> repositories) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ArtifactResolutionResult resolve(ArtifactResolutionRequest request) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void publish(ArtifactRepository repository, File source, String remotePath, ArtifactTransferListener transferListener) throws ArtifactTransferFailedException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void retrieve(ArtifactRepository repository, File destination, String remotePath, ArtifactTransferListener transferListener) throws ArtifactTransferFailedException, ArtifactDoesNotExistException {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
