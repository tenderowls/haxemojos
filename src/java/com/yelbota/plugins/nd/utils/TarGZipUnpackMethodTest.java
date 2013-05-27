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

import com.yelbota.plugins.nd.stubs.LoggerStub;
import org.codehaus.plexus.util.FileUtils;
import org.testng.annotations.Test;

import java.io.File;

public class TarGZipUnpackMethodTest extends AbstractUnpackMethodTest {

    @Test
    public void testUnpack() throws Exception {

        File file = FileUtils.resolveFile(new File("."), "src/test/resources/unit/archive.tgz");
        File directory = createDirectory("target/unit/archive/tgz");

        UnpackMethod unpackMethod = new TarGZipUnpackMethod(new LoggerStub());
        unpackMethod.unpack(file, directory);
    }
}
