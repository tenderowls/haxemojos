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
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.logging.Logger;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.spi.connector.RepositoryConnector;
import org.sonatype.aether.spi.connector.RepositoryConnectorFactory;
import org.sonatype.aether.spi.locator.Service;
import org.sonatype.aether.spi.locator.ServiceLocator;
import org.sonatype.aether.transfer.NoRepositoryConnectorException;

@Component(role = RepositoryConnectorFactory.class, hint = "haxelib")
public class HaxelibRepositoryConnectorFactory implements RepositoryConnectorFactory, Service {

    @Requirement(hint = "wagon")
    private RepositoryConnectorFactory defaultRepositoryConnectorFactory;

    @Requirement(hint = "haxelib")
    private NativeProgram haxelib;

    @Requirement
    private Logger logger;

    @Override
    public RepositoryConnector newInstance(RepositorySystemSession session, RemoteRepository repository) throws
            NoRepositoryConnectorException
    {
        RepositoryConnector defaultRepositoryConnector = defaultRepositoryConnectorFactory.newInstance(session, repository);
        return new HaxelibRepositoryConnector(repository, defaultRepositoryConnector, haxelib, logger);
    }

    @Override
    public int getPriority()
    {
        return 1;
    }

    @Override
    public void initService(ServiceLocator locator)
    {

    }
}
