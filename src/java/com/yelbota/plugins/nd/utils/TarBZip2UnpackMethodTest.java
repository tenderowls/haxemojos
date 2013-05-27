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
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;

public class TarBZip2UnpackMethodTest extends AbstractUnpackMethodTest {

    @Test
    public void testUnpack() throws Exception {

        File file = FileUtils.resolveFile(pwd, "src/test/resources/unit/archive.tbz2");
        File directory = createDirectory("target/unit/archive/tbz2");

        UnpackMethod unpackMethod = new TarBZip2UnpackMethod();

        String os = System.getProperty("os.name").toLowerCase();

        if (!(os.indexOf("mac") > -1 || os.indexOf("lin") > -1)) {

            try {
                unpackMethod.unpack(file, directory);
                Assert.fail("TBzip2UnpackMethod runned not on mac or linux should throws UnpackException");
            }
            catch (UnpackMethod.UnpackMethodException e) {
                // Ok.
            }
        }
        else {
            unpackMethod.unpack(file, directory);
        }
    }
}
