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
package net.bplaced.clayn.jshed.conf;

import java.util.Set;
import net.bplaced.clayn.jshed.impl.conf.IntegerKey;
import net.bplaced.clayn.jshed.impl.conf.StringKey;
import net.bplaced.clayn.jshed.util.StringConverter;

/**
 *
 * @author Clayn <clayn_osmato@gmx.de>
 */
public interface Configuration
{

    public abstract static class Key<T> implements StringConverter<T>
    {

        private final String key;

        public Key(String key)
        {
            this.key = key;
        }

        @Override
        public String toString(T val)
        {
            return val == null ? null : val.toString();
        }

        public String getKey()
        {
            return key;
        }
    }

    /**
     * Sets the value for the given key to the new value. If there was already a
     * value set for the key it will be returned.
     *
     * @param <T> the type of the value
     * @param key the key for the value
     * @param val the new value
     * @return the old value if one was set or {@code null} otherwise
     */
    <T> T set(Key<T> key, T val);

    <T> T get(Key<T> key, T def);

    default String getString(String key, String def)
    {
        return get(new StringKey(key), def);
    }

    default String getString(String key)
    {
        return getString(key, null);
    }

    default int getInt(String key, int def)
    {
        return get(new IntegerKey(key), def);
    }

    default int getInt(String key)
    {
        return getInt(key, -1);
    }

    /**
     * Returns a set with all set configuration names in this configuration
     *
     * @return the names of all set configurations.
     */
    Set<String> getConfigurationNames();

    default <T> T get(Key<T> key)
    {
        return get(key, null);
    }

    /**
     * Adds the given listener to this configuration. Upon setting a value all
     * added listeners will be invoked on the same thread.
     *
     * @param listener the listener to add.
     */
    void addChangeListener(ConfigurationChangeListener listener);

    void removeChangeListener(ConfigurationChangeListener listener);
}
