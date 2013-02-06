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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class HaxelibRepositoryConnector implements RepositoryConnector {

    //-------------------------------------------------------------------------
    //
    //  Fields
    //
    //-------------------------------------------------------------------------

    private final RemoteRepository repository;

    private final RepositoryConnector defaultRepositoryConnector;

    private final NativeProgram haxelib;

    private final Logger logger;

    //-------------------------------------------------------------------------
    //
    //  Public methods
    //
    //-------------------------------------------------------------------------

    public HaxelibRepositoryConnector(RemoteRepository repository, RepositoryConnector defaultRepositoryConnector, NativeProgram haxelib, Logger logger)
    {
        this.repository = repository;
        this.defaultRepositoryConnector = defaultRepositoryConnector;
        this.haxelib = haxelib;
        this.logger = logger;
    }

    @Override
    public void get(Collection<? extends ArtifactDownload> artifactDownloads, Collection<? extends MetadataDownload> metadataDownloads)
    {
        if (artifactDownloads == null)
        {
            defaultRepositoryConnector.get(artifactDownloads, metadataDownloads);
        }
        else
        {
            ArrayList<ArtifactDownload> normalArtifacts = new ArrayList<ArtifactDownload>();
            ArrayList<ArtifactDownload> haxelibArtifacts = new ArrayList<ArtifactDownload>();

            // Separate artifacts collection. Get haxelib artifacts and all others.
            for (ArtifactDownload artifactDownload : artifactDownloads)
            {
                Artifact artifact = artifactDownload.getArtifact();
                if (artifact.getExtension().equals(HaxeFileExtensions.HAXELIB))
                    haxelibArtifacts.add(artifactDownload);
                else normalArtifacts.add(artifactDownload);
            }

            // Get normal artifacts
            defaultRepositoryConnector.get(normalArtifacts, metadataDownloads);

            getHaxelibs(haxelibArtifacts);
        }
    }

    private void getHaxelibs(List<ArtifactDownload> haxelibArtifacts)
    {
        for (ArtifactDownload artifactDownload : haxelibArtifacts)
        {
            Artifact artifact = artifactDownload.getArtifact();
            logger.info("Resolving " + artifact);
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
                    else
                    {
                        try
                        {
                            // TODO In this case we can had a problems with new version of plugin.
                            // TODO Need custom dependency resolver.
                            artifactDownload.getFile().createNewFile();
                        }
                        catch (IOException e)
                        {
                            logger.error("Can't create haxelib dummy artifact", e);
                        }
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
