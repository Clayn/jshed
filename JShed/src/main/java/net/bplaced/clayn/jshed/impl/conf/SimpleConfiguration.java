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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import net.bplaced.clayn.jshed.conf.Configuration;
import net.bplaced.clayn.jshed.conf.ConfigurationChangeListener;

/**
 *
 * @author Clayn <clayn_osmato@gmx.de>
 */
public class SimpleConfiguration implements Configuration {

    private final Properties properties;
    private final List<ConfigurationChangeListener> listeners = new ArrayList<>();

    public SimpleConfiguration() {
        this(new Properties());
    }

    SimpleConfiguration(Properties properties) {
        this.properties = Objects.requireNonNull(properties);
    }
    
    private <T> void invokeListeners(Key<T> key, T old, T newV) {
        for(ConfigurationChangeListener listener:listeners) {
            listener.changed(this, key, old, newV);
        }
    }

    @Override
    public <T> T set(Key<T> key, T val) {
        Objects.requireNonNull(key);
        T old = get(key);
        properties.setProperty(key.getKey(), key.toString(val));
        invokeListeners(key, old, val);
        return old;
    }

    @Override
    public <T> T get(Key<T> key, T def) {
        Objects.requireNonNull(key);
        T val = key.fromString(properties.getProperty(key.getKey()));
        return val == null ? def : val;
    }

    @Override
    public int getInt(String key, int def) {
        IntegerKey intKey = new IntegerKey(key);
        String val = properties.getProperty(key);
        return intKey.fromStringPrimitive(val);
    }

    @Override
    public Set<String> getConfigurationNames() {
        return properties.stringPropertyNames();
    }

    @Override
    public void removeChangeListener(ConfigurationChangeListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void addChangeListener(ConfigurationChangeListener listener) {
        listeners.add(listener);
    }
}
