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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author Clayn <clayn_osmato@gmx.de>
 */
public abstract class ConfigurationManager
{

    public abstract Configuration load(InputStream in) throws IOException;

    public abstract void store(Configuration config, OutputStream out) throws IOException;

    public final ReadOnlyConfigurationManager readOnly()
    {
        ConfigurationManager self = this;
        return new ReadOnlyConfigurationManager()
        {
            @Override
            public Configuration load(InputStream in) throws IOException
            {
                return self.load(in);
            }
        };
    }

    public static Configuration load(Class<? extends ConfigurationManager> clazz,
            InputStream in) throws InstantiationException, IllegalAccessException, IOException
    {
        ConfigurationManager manager = clazz.newInstance();
        return manager.load(in);
    }
}
