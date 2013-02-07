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
package com.yelbota.plugins.haxe.components;

import com.yelbota.plugins.haxe.components.nativeProgram.NativeProgram;
import com.yelbota.plugins.haxe.utils.CompileTarget;
import com.yelbota.plugins.haxe.utils.CompilerLogger;
import com.yelbota.plugins.haxe.utils.HarMetadata;
import com.yelbota.plugins.haxe.utils.HaxeFileExtensions;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.archiver.zip.ZipUnArchiver;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component(role = HaxeCompiler.class)
public final class HaxeCompiler {

    @Requirement(hint = "haxe")
    private NativeProgram haxe;

    @Requirement
    private Logger logger;

    private File outputDirectory;

    public void compile(MavenProject project, Map<CompileTarget, String> targets, String main, boolean debug, boolean includeTestSources) throws Exception
    {
        compile(project, targets, main, debug, includeTestSources, null);
    }

    public void compile(MavenProject project, Map<CompileTarget, String> targets, String main, boolean debug, boolean includeTestSources, ArtifactFilter artifactFilter) throws Exception
    {
        compile(project, targets, main, debug, includeTestSources, artifactFilter, null);
    }

    public void compile(MavenProject project, Map<CompileTarget, String> targets, String main, boolean debug, boolean includeTestSources, ArtifactFilter artifactFilter, List<String> additionalArguments) throws Exception
    {
        List<String> args = new ArrayList<String>();

        for (String sourceRoot : project.getCompileSourceRoots()) {
            addSourcePath(args, sourceRoot);
        }

        if (includeTestSources) {
            for (String sourceRoot : project.getTestCompileSourceRoots()) {
                addSourcePath(args, sourceRoot);
            }
        }

        addLibs(args, project, artifactFilter);
        addHars(args, project, targets.keySet(), artifactFilter);
        addDebug(args, debug);

        if (main != null)
            addMain(args, main);

        if (additionalArguments != null)
            args.addAll(additionalArguments);

        for (CompileTarget target : targets.keySet())
        {
            String output = targets.get(target);
            List<String> argsClone = new ArrayList<String>();
            argsClone.addAll(args);
            addTarget(argsClone, target);
            argsClone.add(output);

            CompilerLogger compilerLogger = new CompilerLogger(logger);
            haxe.execute(argsClone, compilerLogger);

            if (compilerLogger.getErrors().size() > 0)
            {
                logger.info("-------------------------------------------------------------");
                logger.error("COMPILATION ERROR :");
                logger.info("-------------------------------------------------------------");

                for (String error: compilerLogger.getErrors()) {
                    logger.error(error);
                }

                throw new Exception("Compilation failure");
            }
        }
    }

    private void addLibs(List<String> argumentsList, MavenProject project, ArtifactFilter artifactFilter)
    {
        for (Artifact artifact : project.getArtifacts())
        {
            boolean filtered = artifactFilter != null && !artifactFilter.include(artifact);

            if (!filtered && artifact.getType().equals(HaxeFileExtensions.HAXELIB))
            {
                String haxelibId = artifact.getArtifactId() + ":" + artifact.getVersion();
                argumentsList.add("-lib");
                argumentsList.add(haxelibId);
            }
        }
    }

    private void addTarget(List<String> args, CompileTarget target)
    {
        switch (target)
        {
            case java: {
                args.add("-java");
                break;
            }
            case neko: {
                args.add("-neko");
                break;
            }
            case swf: {
                args.add("-swf");
                break;
            }
        }
    }

    private void addDebug(List<String> argumentsList, boolean debug)
    {
        if (debug)
            argumentsList.add("-debug");
    }

    private void addHars(List<String> argumentsList, MavenProject project, Set<CompileTarget> targets, ArtifactFilter artifactFilter)
    {
        File dependenciesDirectory = new File(outputDirectory, "dependencies");

        if (!dependenciesDirectory.exists())
            dependenciesDirectory.mkdir();

        for (Artifact artifact: project.getArtifacts())
        {
            boolean filtered = artifactFilter != null && !artifactFilter.include(artifact);

            if (!filtered && artifact.getType().equals(HaxeFileExtensions.HAR))
            {
                File harUnpackDirectory = new File(dependenciesDirectory, artifact.getArtifactId());

                if (!harUnpackDirectory.exists())
                {
                    harUnpackDirectory.mkdir();
                    ZipUnArchiver unArchiver = new ZipUnArchiver();
                    unArchiver.enableLogging(logger);
                    unArchiver.setSourceFile(artifact.getFile());
                    unArchiver.setDestDirectory(harUnpackDirectory);
                    unArchiver.extract();
                }

                try
                {
                    File metadataFile = new File(harUnpackDirectory, HarMetadata.METADATA_FILE_NAME);
                    JAXBContext jaxbContext = JAXBContext.newInstance(HarMetadata.class, CompileTarget.class);
                    HarMetadata metadata = (HarMetadata) jaxbContext.createUnmarshaller().unmarshal(metadataFile);

                    if (!metadata.target.containsAll(targets))
                        logger.warn("Dependency " + artifact + " is not compatible with your compile targets.");
                }
                catch (JAXBException e)
                {
                    logger.warn("Can't read " + artifact + "metadata", e);
                }

                addSourcePath(argumentsList, harUnpackDirectory.getAbsolutePath());
            }
        }
    }

    private void addMain(List<String> argumentsList, String main)
    {
        argumentsList.add("-main");
        argumentsList.add(main);
    }

    private void addSourcePath(List<String> argumentsList, String sourcePath)
    {
        argumentsList.add("-cp");
        argumentsList.add(sourcePath);
    }

    public void setOutputDirectory(File outputDirectory)
    {
        this.outputDirectory = outputDirectory;
    }
}
