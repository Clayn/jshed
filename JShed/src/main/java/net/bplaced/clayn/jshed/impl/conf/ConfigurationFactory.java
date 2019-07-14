/*
 * The MIT License
 *
 * Copyright 2019 Clayn <clayn_osmato@gmx.de>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.bplaced.clayn.jshed.impl.conf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import net.bplaced.clayn.jshed.conf.Configuration;
import net.bplaced.clayn.jshed.conf.ConfigurationManager;

/**
 *
 * @author Clayn <clayn_osmato@gmx.de>
 */
public final class ConfigurationFactory
{

    private static final ConfigurationFactory INSTANCE = new ConfigurationFactory();
    private final Map<Class<? extends ConfigurationManager>, ConfigurationManager> loadedManagers = new HashMap<>();

    private ConfigurationManager loadManager(
            Class<? extends ConfigurationManager> clazz)
    {
        if (loadedManagers.containsKey(clazz))
        {
            return loadedManagers.get(clazz);
        }
        try
        {
            ConfigurationManager manager = clazz.newInstance();
            loadedManagers.put(clazz, manager);
        } catch (InstantiationException | IllegalAccessException ex)
        {
            throw new RuntimeException(ex);
        }
        return loadManager(clazz);
    }

    /**
     * Loads a configuration using an instance of the given
     * {@link ConfigurationManager} class and inputstream. The instance will be
     * cached so additional usage of the same class should be faster.
     *
     * @param manager the manager implementation to use
     * @param in the inputstream to load the configuration from
     * @return a new configuration loaded by the given manager implementation
     * @throws IOException if an I/O exception occurs while loading
     */
    public static Configuration loadConfiguration(
            Class<? extends ConfigurationManager> manager, InputStream in) throws IOException
    {
        return INSTANCE.loadManager(manager).load(in);
    }

    public static void storeConfiguration(
            Class<? extends ConfigurationManager> manager, Configuration config,
            OutputStream out) throws IOException
    {
        INSTANCE.loadManager(manager).store(config, out);
    }
}
