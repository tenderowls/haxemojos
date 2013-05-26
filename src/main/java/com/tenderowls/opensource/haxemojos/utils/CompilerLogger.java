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
package com.tenderowls.opensource.haxemojos.utils;

import org.codehaus.plexus.logging.Logger;
import java.util.LinkedList;
import java.util.List;

public class CompilerLogger implements Logger {

    private final Logger baseLogger;
    private final List<String> warnings;
    private final List<String> errors;

    public CompilerLogger(Logger baseLogger)
    {
        this.warnings = new LinkedList<String>();
        this.errors = new LinkedList<String>();
        this.baseLogger = baseLogger;
    }

    public void debug(String s)
    {
        parse(s);
    }

    public void debug(String s, Throwable throwable)
    {
        baseLogger.debug(s, throwable);
    }

    public boolean isDebugEnabled()
    {
        return baseLogger.isDebugEnabled();
    }

    public void info(String s)
    {
        parse(s);
    }

    public void info(String s, Throwable throwable)
    {
        parse(s);
    }

    public boolean isInfoEnabled()
    {
        return baseLogger.isInfoEnabled();
    }

    public void warn(String s)
    {
        baseLogger.warn(s);
    }

    public void warn(String s, Throwable throwable)
    {
        baseLogger.warn(s, throwable);
    }

    public boolean isWarnEnabled()
    {
        return baseLogger.isWarnEnabled();
    }

    public void error(String s)
    {
        parse(s);
    }

    public void error(String s, Throwable throwable)
    {
        parse(s);
    }

    public boolean isErrorEnabled()
    {
        return baseLogger.isErrorEnabled();
    }

    public void fatalError(String s)
    {
        baseLogger.fatalError(s);
    }

    public void fatalError(String s, Throwable throwable)
    {
        baseLogger.fatalError(s, throwable);
    }

    public boolean isFatalErrorEnabled()
    {
        return baseLogger.isFatalErrorEnabled();
    }

    public int getThreshold()
    {
        return baseLogger.getThreshold();
    }

    public void setThreshold(int i)
    {
        baseLogger.setThreshold(i);
    }

    public Logger getChildLogger(String s)
    {
        return baseLogger.getChildLogger(s);
    }

    public String getName()
    {
        return baseLogger.getName();
    }

    public List<String> getWarnings()
    {
        return warnings;
    }

    public List<String> getErrors()
    {
        return errors;
    }

    private void parse(String s)
    {
        String sLower = s.toLowerCase();

        if (sLower.contains("warning :") || sLower.contains("warning:"))
        {
            warnings.add(s);
            baseLogger.warn(s);
        }
        else if (sLower.matches("^.*:[0-9]*:.* [0-9]*-[0-9]*.*"))
        {
            // TODO convert to Java compiler style
            errors.add(s);
        }
        else
        {
            baseLogger.info(s);
        }
    }
}