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
package com.yelbota.plugins.haxe;

import com.yelbota.plugins.haxe.utils.CompileTarget;
import com.yelbota.plugins.haxe.utils.HarMetadata;
import com.yelbota.plugins.haxe.utils.HaxeFileExtensions;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.codehaus.plexus.archiver.zip.ZipArchiver;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Set;


/**
 * Builds a `har` package. This is a zip archive which
 * contains metainfo about supported compilation targets.
 */
@Mojo(name = "compileHar", defaultPhase = LifecyclePhase.COMPILE, requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class CompileHarMojo extends AbstractCompileMojo {

    /**
     * Validation targets for `har`. HMP will try to build project with
     * all of declared targets.
     */
    @Parameter(required = true)
    private Set<CompileTarget> targets;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        super.execute();

        try
        {
            File outputBase = new File(outputDirectory, project.getBuild().getFinalName() + "-harValidate");
            validateTargets(outputBase);
            File metadata = createHarMetadata(outputBase);

            ZipArchiver archiver = new ZipArchiver();
            archiver.addFile(metadata, HarMetadata.METADATA_FILE_NAME);

            for (String compileRoot : project.getCompileSourceRoots())
                archiver.addDirectory(new File(compileRoot));

            File destFile = new File(outputDirectory, project.getBuild().getFinalName() + "." + HaxeFileExtensions.HAR);
            archiver.setDestFile(destFile);
            archiver.createArchive();
            project.getArtifact().setFile(destFile);
        }
        catch (IOException e)
        {
            throw new MojoFailureException("Error occurred during `har` package creation", e);
        }
        catch (Exception e)
        {
            throw new MojoFailureException("Har validation failed", e);
        }
    }

    private File createHarMetadata(File outputBase) throws Exception
    {
        HarMetadata harMetaData = new HarMetadata();
        harMetaData.target = targets;

        JAXBContext jaxbContext = JAXBContext.newInstance(HarMetadata.class, CompileTarget.class);
        File metadataFile = new File(outputBase, HarMetadata.METADATA_FILE_NAME);
        FileOutputStream stream = new FileOutputStream(metadataFile);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(harMetaData, stream);

        return metadataFile;
    }

    private void validateTargets(File outputBase) throws Exception
    {
        EnumMap<CompileTarget, String> compileTargets = new EnumMap<CompileTarget, String>(CompileTarget.class);

        if (!outputBase.exists())
            outputBase.mkdirs();

        for (CompileTarget target : targets)
        {
            File outputFile = outputBase;

            switch (target)
            {
                case java:
                {
                    outputFile = new File(outputBase, "java");
                    break;
                }
                case neko:
                {
                    outputFile = new File(outputBase, "neko.n");
                    break;
                }
            }

            compileTargets.put(target, outputFile.getAbsolutePath());
        }

        compiler.compile(project, compileTargets, main, debug, false);
    }
}
