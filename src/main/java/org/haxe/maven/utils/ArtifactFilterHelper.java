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
package org.haxe.maven.utils;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.artifact.resolver.filter.CumulativeScopeArtifactFilter;

import java.util.HashSet;
import java.util.Set;

public class ArtifactFilterHelper {

    static Set<String> compileScopes = new HashSet<String>();
    static Set<String> testScopes = new HashSet<String>();

    static
    {
        compileScopes.add(Artifact.SCOPE_COMPILE);
        compileScopes.add(Artifact.SCOPE_PROVIDED);
        compileScopes.add(Artifact.SCOPE_RUNTIME);
        compileScopes.add(Artifact.SCOPE_SYSTEM);
        compileScopes.add(Artifact.SCOPE_IMPORT);

        testScopes.addAll(compileScopes);
        testScopes.add(Artifact.SCOPE_TEST);
    }

    public static final ArtifactFilter COMPILE = new CumulativeScopeArtifactFilter(compileScopes);
    public static final ArtifactFilter TEST = new CumulativeScopeArtifactFilter(testScopes);
}
