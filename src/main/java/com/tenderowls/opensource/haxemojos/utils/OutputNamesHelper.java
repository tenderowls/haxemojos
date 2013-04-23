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
package com.tenderowls.opensource.haxemojos.utils;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;

public class OutputNamesHelper {

    public static String getTestOutput(MavenProject project)
    {
        return project.getBuild().getFinalName() + "-test.n";
    }

    public static String getHarValidationOutput(Artifact artifact)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(artifact.getArtifactId());
        sb.append("-");
        sb.append(artifact.getVersion());
        sb.append("-harValidate");
        return sb.toString();
    }
}
