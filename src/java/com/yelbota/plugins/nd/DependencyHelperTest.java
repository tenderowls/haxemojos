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
package com.yelbota.plugins.nd;

import com.yelbota.plugins.nd.stubs.ArtifactRepositoryStub;
import com.yelbota.plugins.nd.stubs.RepositorySystemStub;
import junit.framework.Assert;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.testing.stubs.ArtifactStub;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class DependencyHelperTest {

    @Test
    public void testResolveWhenArtifactPresentInPluginDependencies() throws Exception {

        DependencyHelper dependencyHelper = new DependencyHelper() {

            @Override
            protected String getDefaultArtifactId() {
                return "test-artifact";
            }

            @Override
            protected String getDefaultGroupId() {
                return "test-group";
            }

            @Override
            protected String getDefaultVersion() {
                return "1.0";
            }

            @Override
            protected String getDefaultPackaging() {
                return "zip";
            }
        };

        List<Artifact> pluginArtifacts = new ArrayList<Artifact>();
        Artifact artifactStub = new ArtifactStub();
        artifactStub.setArtifactId(dependencyHelper.getDefaultArtifactId());
        artifactStub.setGroupId(dependencyHelper.getDefaultGroupId());

        pluginArtifacts.add(artifactStub);

        Artifact resolvedArtifact = dependencyHelper.resolve(
                pluginArtifacts,
                new RepositorySystemStub(),
                new ArtifactRepositoryStub(),
                new ArrayList<ArtifactRepository>()
        );

        Assert.assertEquals(artifactStub, resolvedArtifact);
    }
}
