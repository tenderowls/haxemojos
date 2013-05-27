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
package com.yelbota.plugins.nd;

import com.yelbota.plugins.nd.utils.UnpackMethod;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * @author Aleksey Fomkin
 */
public class UnpackHelper {

    /**
     * @author Aleksey Fomkin
     */
    class UnpackHelperException extends MojoFailureException {

        public UnpackHelperException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    //-------------------------------------------------------------------------
    //
    //  Public methods
    //
    //-------------------------------------------------------------------------

    public void unpack(File directory, Artifact artifact,
                       Map<String, UnpackMethod> unpackMethods) throws UnpackHelperException {

        unpack(directory, artifact, unpackMethods, null);
    }

    /**
     * Unpack `artifact` to `directory`.
     * @throws UnpackHelperException
     */
    public void unpack(File directory, Artifact artifact,
                       Map<String, UnpackMethod> unpackMethods,
                       Log log) throws UnpackHelperException {

        if (directory.exists()) {

            if (directory.isDirectory()) {
                logAlreadyUnpacked();
            } else {
                new MojoFailureException(directory.getAbsolutePath() + ", which must be directory for unpacking, now is file");
            }
        } else {
            try {
                logUnpacking();
                directory.mkdirs();
                UnpackMethod unpackMethod = unpackMethods.get(artifact.getType());
                unpackMethod.unpack(artifact.getFile(), directory, log);
            } catch (IOException e) {
                throw new UnpackHelperException("Can't unpack " + artifact, e);
            } catch (UnpackMethod.UnpackMethodException e) {
                throw new UnpackHelperException("Can't unpack " + artifact, e);
            }
        }
    }

    //-------------------------------------------------------------------------
    //
    //  Abstract methods
    //
    //-------------------------------------------------------------------------

    protected void logAlreadyUnpacked() {
        // Empty default implementation.
    }

    protected void logUnpacking() {
        // Empty default implementation.
    }
}