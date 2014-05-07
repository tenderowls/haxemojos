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
package com.tenderowls.opensource.haxemojos;

import com.tenderowls.opensource.haxemojos.components.HaxeCompiler;
import com.tenderowls.opensource.haxemojos.utils.*;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.*;
import org.codehaus.plexus.archiver.zip.ZipArchiver;
import org.codehaus.plexus.util.StringUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Builds a `har` package. This is a zip archive which
 * contains metainfo about supported compilation targets.
 */
@Mojo(name = "compileHar", defaultPhase = LifecyclePhase.COMPILE, requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class CompileHarMojo extends AbstractHaxeMojo {

    /**
     * Validation targets for `har`. HMP will try to build project with
     * all of declared targets.
     */
    @Parameter(required = true)
    private Set<CompileTarget> targets;

    /**
     * Exclude classes from compilation
     */
    @Parameter(required = false)
    private Set<String> ignoreClasses;

    /**
     * Package without targets validation
     */
    @Parameter
    private boolean skipValidation;

    /**
     * More type strict flash API
     */
    @Parameter
    private boolean flashStrict;

    /**
     * Change the SWF version (6 to 11.x)
     */
    @Parameter(defaultValue = "11.2", required = true)
    private String swfVersion;

    @Component
    protected HaxeCompiler compiler;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        super.execute();
        compiler.setOutputDirectory(outputDirectory);

        try
        {
            String outputDirectoryName = OutputNamesHelper.getHarValidationOutput(project.getArtifact());
            File outputBase = new File(outputDirectory, outputDirectoryName);

            if (!outputBase.exists())
                outputBase.mkdirs();

            if (!skipValidation) {
                validateTargets(outputBase);
            }
            else {
                getLog().warn("Validation skipped");
            }

            File metadata = createHarMetadata(outputBase);

            ZipArchiver archiver = new ZipArchiver();
            archiver.addFile(metadata, HarMetadata.METADATA_FILE_NAME);

            for (String compileRoot : project.getCompileSourceRoots()) {
                archiver.addDirectory(new File(compileRoot));
            }

            for (File resourceDir: ProjectHelper.getResourceDirectories(project)) {
                archiver.addDirectory(resourceDir);
            }

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
                case swf:
                {
                    outputFile = new File(outputBase, "flash.swf");
                    break;
                }
                case swc:
                {
                    outputFile = new File(outputBase, "flash.swc");
                    break;
                }
                case js:
                {
                    outputFile = new File(outputBase, "js");
                    break;
                }
            }

            compileTargets.put(target, outputFile.getAbsolutePath());
        }

        List<String> additionalArgs = new LinkedList<String>();

        // Add flash args
        if (targets.contains(CompileTarget.swf) || targets.contains(CompileTarget.swc))
        {
            if (flashStrict)
                additionalArgs.add("--flash-strict");

            additionalArgs.add("-swf-version");
            additionalArgs.add(swfVersion);
        }

        // Create macro command which add all classes
        // from compile classpath to haxe compiler.
        String sourcePaths = StringUtils
            .join(project.getCompileSourceRoots().iterator(), "','")
            .replace("\\", "\\\\");

        String ignoreClassesJoined = "";
        if (ignoreClasses != null) {
            ignoreClassesJoined = StringUtils.join(ignoreClasses.iterator(), "','");
            if (ignoreClasses.size() > 0) {
                ignoreClassesJoined = "'" + ignoreClassesJoined + "'";
            }
        }

        additionalArgs.add("--macro");
        additionalArgs.add("haxe.macro.Compiler.include('', true, ["+ignoreClassesJoined+"], [ '" + sourcePaths + "' ])");
        additionalArgs.addAll(getCommonAdditionalArgs());

        getLog().info(String.format(
                "Validating targets: %s",
                StringUtils.join(compileTargets.keySet().iterator(), ", ")
        ));

        compiler.compile(project, compileTargets, null, false, false, ArtifactFilterHelper.COMPILE, additionalArgs);
    }
}
