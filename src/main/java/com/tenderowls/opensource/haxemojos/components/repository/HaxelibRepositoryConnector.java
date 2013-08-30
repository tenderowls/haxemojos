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
package com.tenderowls.opensource.haxemojos.components.repository;

import com.tenderowls.opensource.haxemojos.components.nativeProgram.NativeProgram;
import com.tenderowls.opensource.haxemojos.components.nativeProgram.NativeProgramException;
import com.tenderowls.opensource.haxemojos.utils.HaxeFileExtensions;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.WriterFactory;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.spi.connector.*;
import org.sonatype.aether.transfer.ArtifactTransferException;
import org.sonatype.aether.util.layout.MavenDefaultLayout;
import org.sonatype.aether.util.layout.RepositoryLayout;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class HaxelibRepositoryConnector implements RepositoryConnector {

    //-------------------------------------------------------------------------
    //
    //  Fields
    //
    //-------------------------------------------------------------------------

    private final RepositoryLayout layout;

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
        this.layout = new MavenDefaultLayout();
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
            logger.debug("Resolving " + artifact);

            if (artifact.getExtension().equals(HaxeFileExtensions.HAXELIB))
            {
                try
                {
                    File haxelibDir = new File(haxelib.getHome(), "_haxelib");
                    File artifactDir = new File(haxelibDir, artifact.getArtifactId());
                    File installedDir = new File(artifactDir, artifact.getVersion().replaceAll("\\.", ","));

                    if (!installedDir.exists())
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
                        else {
                            Model model = generateModel(
                                    artifact.getGroupId(),
                                    artifact.getArtifactId(),
                                    artifact.getVersion());
                            try {
                                File metadataFile = generatePomFile(model);
                                String pomPath = artifactDownload.getFile().getAbsolutePath().replace(artifact.getExtension(), "pom");
                                metadataFile.renameTo(new File(pomPath));
                                File dummyFile = artifactDownload.getFile();
                                dummyFile.createNewFile();
                            } catch (IOException e) {
                                artifactDownload.setException(new ArtifactTransferException(
                                        artifact, repository, e));
                            }
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
        defaultRepositoryConnector.put(artifactUploads, metadataUploads);
    }

    @Override
    public void close()
    {
    }

    /**
     * Generates a (temporary) POM file from the plugin configuration. It's the responsibility of the caller to delete
     * the generated file when no longer needed.
     */
    private File generatePomFile(Model model) {

        Writer writer = null;
        try {
            File pomFile = File.createTempFile("mvninstall", ".pom");
            writer = WriterFactory.newXmlWriter(pomFile);
            new MavenXpp3Writer().write(writer, model);
            return pomFile;
        } catch (IOException e) {
            logger.error("Error writing temporary POM file: " + e.getMessage(), e);
            return null;
        } finally {
            IOUtil.close(writer);
        }
    }

    /**
     * Generates a minimal model from the user-supplied artifact information.
     * @return The generated model, never <code>null</code>.
     */
    private Model generateModel(String groupId, String artifactId, String version) {

        Model model = new Model();

        model.setModelVersion("4.0.0");
        model.setGroupId(groupId);
        model.setArtifactId(artifactId);
        model.setVersion(version);
        model.setPackaging(HaxeFileExtensions.HAXELIB);
        model.setDescription("POM was created from Haxemojos");

        return model;
    }
}
