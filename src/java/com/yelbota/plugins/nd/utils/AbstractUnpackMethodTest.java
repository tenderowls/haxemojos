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
package com.yelbota.plugins.nd.utils;

import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.IOException;

public abstract class AbstractUnpackMethodTest {

    protected final File pwd = new File(".");

    protected File createDirectory(String path) throws IOException {

        File file = FileUtils.resolveFile(pwd, path);

        if (file.exists()) {

            FileUtils.cleanDirectory(file);
            file.delete();
        }

        file.mkdirs();
        return file;
    }
}
