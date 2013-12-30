package org.slf4j.impl;

import org.slf4j.ILoggerFactory;
import org.slf4j.helpers.SubstituteLoggerFactory;
import org.slf4j.spi.LoggerFactoryBinder;

/**
 *
 * @author igel
 */
public class StaticLoggerBinder implements LoggerFactoryBinder {

    private static final StaticLoggerBinder SINGLETON = new StaticLoggerBinder();
    private final ILoggerFactory factory = new SubstituteLoggerFactory();

    public static StaticLoggerBinder getSingleton() {
        return SINGLETON;
    }

    @Override
    public ILoggerFactory getLoggerFactory() {
        return factory;
    }

    @Override
    public String getLoggerFactoryClassStr() {
        return null;
    }
}
