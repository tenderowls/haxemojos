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
package com.yelbota.plugins.nd.utils;

import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.io.IOException;

/**
 * *.tar.bz2 and tbz2 unpack method. Note: supported only in unix-like
 * operation systems
 *
 * @author Aleksey Fomkin
 */
public class TarBZip2UnpackMethod implements UnpackMethod {

    @Override

    /**
     * @inheritDoc
     */
    public void unpack(File file, File directory, Log log) throws IOException, UnpackMethodException {

        String os = System.getProperty("os.name").toLowerCase();

        if (!(os.indexOf("mac") > -1 || os.indexOf("lin") > -1)) {
            throw new UnpackMethodException("Tar-bz2 archives supported only under Linux and Mac OS X.");
        }

        try {

            // Java 6 doesn't support symlinks.
            ProcessBuilder builder = new ProcessBuilder(
                    "tar", "-jxvf", file.getAbsolutePath(),
                    "-C", directory.getAbsolutePath()
            );

            Process process = builder.start();

            CleanStream cleanError = new CleanStream(process.getErrorStream(), log);
            CleanStream cleanOutput = new CleanStream(process.getInputStream(), log);

            cleanError.start();
            cleanOutput.start();
            process.waitFor();

        } catch (InterruptedException e) {
            throw new UnpackMethodException("Something happened during running `tar`", e);
        }
    }

    @Override

    /**
     * @inheritDoc
     */
    public void unpack(File file, File directory) throws IOException, UnpackMethodException {
        unpack(file, directory, null);
    }
}
