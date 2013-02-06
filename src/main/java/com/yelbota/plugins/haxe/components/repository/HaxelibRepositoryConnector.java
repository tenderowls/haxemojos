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
package com.yelbota.plugins.haxe.components.repository;

import com.yelbota.plugins.haxe.components.nativeProgram.NativeProgram;
import com.yelbota.plugins.haxe.components.nativeProgram.NativeProgramException;
import com.yelbota.plugins.haxe.utils.HaxeFileExtensions;
import org.codehaus.plexus.logging.Logger;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.spi.connector.*;
import org.sonatype.aether.transfer.ArtifactTransferException;

import java.util.Collection;

public class HaxelibRepositoryConnector implements RepositoryConnector {

    //-------------------------------------------------------------------------
    //
    //  Fields
    //
    //-------------------------------------------------------------------------

    private final RemoteRepository repository;

    private NativeProgram haxelib;

    private Logger logger;

    //-------------------------------------------------------------------------
    //
    //  Public methods
    //
    //-------------------------------------------------------------------------

    public HaxelibRepositoryConnector(RemoteRepository repository, NativeProgram haxelib, Logger logger)
    {
        this.repository = repository;
        this.haxelib = haxelib;
        this.logger = logger;
    }

    @Override
    public void get(Collection<? extends ArtifactDownload> artifactDownloads, Collection<? extends MetadataDownload> metadataDownloads)
    {
        for (ArtifactDownload artifactDownload : artifactDownloads)
        {
            Artifact artifact = artifactDownload.getArtifact();

            if (artifact.getExtension().equals(HaxeFileExtensions.HAXELIB))
            {
                try
                {
                    int code = haxelib.execute(
                            "install",
                            artifact.getArtifactId(),
                            artifact.getVersion()
                    );

                    if (code > 0)
                    {
                        artifactDownload.setException(new ArtifactTransferException(
                                artifact, repository, "Can't resolve artifact " + artifact.toString()));
                    }
                }
                catch (NativeProgramException e)
                {
                    artifactDownload.setException(new ArtifactTransferException(
                            artifact, repository, e));
                }
            }
        }
    }

    @Override
    public void put(Collection<? extends ArtifactUpload> artifactUploads, Collection<? extends MetadataUpload> metadataUploads)
    {
        // TODO Deploying to http://lib.haxe.org. Need to define haxelib packaging?
    }

    @Override
    public void close()
    {
    }
}
