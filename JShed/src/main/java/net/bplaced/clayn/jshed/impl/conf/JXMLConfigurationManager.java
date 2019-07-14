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
import java.util.Properties;
import net.bplaced.clayn.jshed.conf.Configuration;
import net.bplaced.clayn.jshed.conf.ConfigurationManager;

/**
 *
 * @author Clayn <clayn_osmato@gmx.de>
 */
public class JXMLConfigurationManager extends ConfigurationManager
{

    @Override
    public Configuration load(InputStream in) throws IOException
    {
        Properties prop = new Properties();
        prop.loadFromXML(in);
        return new SimpleConfiguration(prop);
    }

    @Override
    public void store(Configuration config, OutputStream out) throws IOException
    {
        Properties props = new Properties();
        for (String key : config.getConfigurationNames())
        {
            String val = config.getString(key);
            if (val != null)
            {
                props.setProperty(key, val);
            }
        }
        props.storeToXML(out, "");
    }

}
