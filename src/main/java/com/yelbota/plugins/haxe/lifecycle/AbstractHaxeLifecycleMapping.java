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
package com.yelbota.plugins.haxe.lifecycle;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.lifecycle.mapping.Lifecycle;

public abstract class AbstractHaxeLifecycleMapping
{
    private Map<String, Lifecycle> lifecycleMap;

    public AbstractHaxeLifecycleMapping()
    {
        super();
    }

    public Map<String, Lifecycle> getLifecycles()
    {
        if ( lifecycleMap != null )
        {
            return lifecycleMap;
        }

        lifecycleMap = new LinkedHashMap<String, Lifecycle>();
        Lifecycle lifecycle = new Lifecycle();

        lifecycle.setId( "default" );
        Map<String, String> phases = new LinkedHashMap<String, String>();
        phases.put( "process-resources", "org.apache.maven.plugins:maven-resources-plugin:resources" );
        phases.put( "compile", getCompiler() );
        phases.put( "process-test-resources", "org.apache.maven.plugins:maven-resources-plugin:testResources" );
        phases.put( "test-compile", "com.yelbota.plugins:haxe-maven-plugin:testCompile");
        phases.put( "test", "com.yelbota.plugins:haxe-maven-plugin:testRun" );

        if ( getPackage() != null )
        {
            phases.put( "package", getPackage() );
        }
        phases.put( "install", "org.apache.maven.plugins:maven-install-plugin:install" );
        phases.put( "deploy", "org.apache.maven.plugins:maven-deploy-plugin:deploy" );
        lifecycle.setPhases( phases );

        lifecycleMap.put( "default", lifecycle );
        return lifecycleMap;
    }

    protected String getPackage()
    {
        return null;
    }

    public abstract String getCompiler();

    @Deprecated
    public List<String> getOptionalMojos( String lifecycle )
    {
        return null;
    }

    @Deprecated
    public Map<String, String> getPhases( String lifecycle )
    {
        Lifecycle lifecycleMapping = getLifecycles().get( lifecycle );

        if ( lifecycleMapping != null )
        {
            return lifecycleMapping.getPhases();
        }

        return null;
    }
}
