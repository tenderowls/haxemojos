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

import org.apache.maven.model.Resource;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

public class ProjectHelper {

    public static Iterable<File> getResourceDirectories(MavenProject project) {

        HashSet<String> resourcePaths = new HashSet<String>();
        ArrayList<File> result = new ArrayList<File>();

        for (Resource resource : project.getResources()) {
            String targetPath = resource.getTargetPath();
            resourcePaths.add(targetPath == null ? resource.getDirectory() : targetPath);
        }

        for (String sourceRoot: resourcePaths) {
            File file = new File(sourceRoot);
            if (file.exists() && file.isDirectory())
                result.add(file);
        }

        return result;
    }
}
