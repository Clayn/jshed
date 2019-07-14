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
package net.bplaced.clayn.jshed.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

/**
 *
 * @author Clayn <clayn_osmato@gmx.de>
 */
public class IOToolsTest
{

    public IOToolsTest()
    {
    }

    @BeforeClass
    public static void setUpClass()
    {
    }

    @AfterClass
    public static void tearDownClass()
    {
    }

    @Before
    public void setUp()
    {
    }

    @After
    public void tearDown()
    {
    }

    @Test
    public void testCopyStreams() throws UnsupportedEncodingException, IOException
    {
        byte[] data = "Hello World".getBytes("UTF-8");
        ByteArrayInputStream src = new ByteArrayInputStream(data);
        ByteArrayOutputStream dest = new ByteArrayOutputStream();
        try (InputStream in = src; OutputStream out = dest)
        {
            IOTools.copy(in, out);
        }
        byte[] result = dest.toByteArray();
        Assert.assertArrayEquals(data, result);
    }

    @Test(expected = NullPointerException.class)
    public void testCopyStreamsSrcNull() throws UnsupportedEncodingException, IOException
    {
        byte[] data = "Hello World".getBytes("UTF-8");
        ByteArrayInputStream src = new ByteArrayInputStream(data);
        ByteArrayOutputStream dest = new ByteArrayOutputStream();
        try (InputStream in = src; OutputStream out = dest)
        {
            IOTools.copy(null, out);
        }
    }

    @Test(expected = NullPointerException.class)
    public void testCopyStreamsDestNull() throws UnsupportedEncodingException, IOException
    {
        InputStream src = Mockito.mock(InputStream.class);
        Mockito.when(src.read()).thenThrow(IllegalArgumentException.class);
        Mockito.when(src.read(Mockito.any())).thenThrow(
                IllegalArgumentException.class);
        Mockito.when(src.read(Mockito.any(), Mockito.anyInt(), Mockito.anyInt())).thenThrow(
                IllegalArgumentException.class);
        ByteArrayOutputStream dest = new ByteArrayOutputStream();
        try (InputStream in = src; OutputStream out = dest)
        {
            IOTools.copy(in, null);
        }
    }

    @Test(expected = NullPointerException.class)
    public void testCopyStreamsBothNull() throws UnsupportedEncodingException, IOException
    {
        byte[] data = "Hello World".getBytes("UTF-8");
        ByteArrayInputStream src = new ByteArrayInputStream(data);
        ByteArrayOutputStream dest = new ByteArrayOutputStream();
        try (InputStream in = null; OutputStream out = dest)
        {
            IOTools.copy(in, null);
        }
    }

    @Test
    public void testCopySourceSink() throws UnsupportedEncodingException, IOException
    {
        byte[] data = "Hello World".getBytes("UTF-8");
        ByteArrayInputStream src = new ByteArrayInputStream(data);
        ByteArrayOutputStream dest = new ByteArrayOutputStream();
        IOTools.copy(() -> src, () -> dest);
        byte[] result = dest.toByteArray();
        Assert.assertArrayEquals(data, result);
    }
}
