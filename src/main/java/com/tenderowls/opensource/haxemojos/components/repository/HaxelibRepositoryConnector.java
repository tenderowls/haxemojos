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

import com.tenderowls.opensource.haxemojos.utils.HaxeFileExtensions;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.WriterFactory;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.spi.connector.*;
import org.sonatype.aether.transfer.ArtifactNotFoundException;
import org.sonatype.aether.transfer.ArtifactTransferException;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *  Connector to familiar haxelib repository (http://lib.haxe.org/files).
 *  Resolves haxelib via http and put it into local repository, however
 *  transitive dependencies will be resolved bypassing Maven with haxelib
 *  utility.
 */
public class HaxelibRepositoryConnector implements RepositoryConnector {

    //-------------------------------------------------------------------------
    //
    //  Fields
    //
    //-------------------------------------------------------------------------

    private final HttpClient httpClient;

    private final HaxelibRepositoryLayout layout;

    private final RemoteRepository repository;

    private final RepositoryConnector defaultRepositoryConnector;

    private final Logger logger;

    //-------------------------------------------------------------------------
    //
    //  Public methods
    //
    //-------------------------------------------------------------------------

    public HaxelibRepositoryConnector(RemoteRepository repository, RepositoryConnector defaultRepositoryConnector, Logger logger)
    {
        this.layout = new HaxelibRepositoryLayout();
        this.repository = repository;
        this.defaultRepositoryConnector = defaultRepositoryConnector;
        this.logger = logger;
        this.httpClient = new DefaultHttpClient();
    }

    @Override
    public void get(Collection<? extends ArtifactDownload> artifactDownloads, Collection<? extends MetadataDownload> metadataDownloads)
    {
        if (artifactDownloads != null)
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

            try
            {
                String uri = "http://lib.haxe.org/" + layout.getPath(artifact.getArtifactId(), artifact.getVersion());
                logger.info("Resolving " + artifact + " from " + uri);

                HttpResponse response = httpClient.execute(new HttpGet(uri));
                HttpEntity entity = response.getEntity();

                switch (response.getStatusLine().getStatusCode())
                {
                    case 200:

                        logger.info("Installing into local repository");

                        if (entity != null)
                        {
                            InputStream remoteFile = entity.getContent();

                            try
                            {
                                OutputStream localFile = new FileOutputStream(artifactDownload.getFile());
                                IOUtil.copy(remoteFile, localFile);
                                localFile.close();

                                Model model = generateModel(
                                        artifact.getGroupId(),
                                        artifact.getArtifactId(),
                                        artifact.getVersion());

                                File metadataFile = generatePomFile(model);
                                String pomPath = artifactDownload.getFile().getAbsolutePath().replace(artifact.getExtension(), "pom");
                                metadataFile.renameTo(new File(pomPath));
                            }
                            finally {
                                remoteFile.close();
                            }
                        }
                        break;
                    case 404:
                        if ("pom".equals(artifact.getExtension()))
                            break;

                        artifactDownload.setException(new ArtifactNotFoundException(artifact, repository));
                        EntityUtils.consume(entity);
                        break;
                    default:
                        artifactDownload.setException(new ArtifactTransferException(artifact, repository,
                                String.format("Server returned: %s %s",
                                        response.getStatusLine().getStatusCode(),
                                        response.getStatusLine().getReasonPhrase())));
                        EntityUtils.consume(entity);
                        break;
                }
            }
            catch (IOException e)
            {
                artifactDownload.setException(new ArtifactTransferException(artifact, repository, e));
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
        if (httpClient != null) {
            httpClient.getConnectionManager().shutdown();
        }
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
