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
import org.apache.maven.artifact.metadata.ArtifactMetadata;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy;
import org.apache.maven.artifact.repository.Authentication;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.repository.Proxy;

import java.util.List;

@SuppressWarnings("deprecation")
public class ArtifactRepositoryStub implements ArtifactRepository {

    @Override
    public String pathOf(Artifact artifact) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String pathOfRemoteRepositoryMetadata(ArtifactMetadata artifactMetadata) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String pathOfLocalRepositoryMetadata(ArtifactMetadata metadata, ArtifactRepository repository) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getUrl() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setUrl(String url) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getBasedir() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getProtocol() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getId() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setId(String id) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ArtifactRepositoryPolicy getSnapshots() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setSnapshotUpdatePolicy(ArtifactRepositoryPolicy policy) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ArtifactRepositoryPolicy getReleases() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setReleaseUpdatePolicy(ArtifactRepositoryPolicy policy) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ArtifactRepositoryLayout getLayout() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setLayout(ArtifactRepositoryLayout layout) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getKey() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isUniqueVersion() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isBlacklisted() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setBlacklisted(boolean blackListed) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Artifact find(Artifact artifact) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<String> findVersions(Artifact artifact) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isProjectAware() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setAuthentication(Authentication authentication) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Authentication getAuthentication() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setProxy(Proxy proxy) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Proxy getProxy() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<ArtifactRepository> getMirroredRepositories() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setMirroredRepositories(List<ArtifactRepository> mirroredRepositories) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
