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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import net.bplaced.clayn.jshed.JShed;
import net.bplaced.clayn.jshed.util.ProgressingTask;

/**
 *
 * @author Clayn <clayn_osmato@gmx.de>
 */
public final class IOTools
{

    private IOTools()
    {
    }

    public static ProgressingTask copyAsync(InputStream src, OutputStream dest,
            long amount)
    {
        long max = amount;
        AtomicLong current = new AtomicLong(0);
        AtomicBoolean done = new AtomicBoolean(false);
        ProgressingTask progress = new ProgressingTask()
        {

            @Override
            public double getProgress()
            {
                return max > 0 ? (current.get() * 1.0) / (max * 1.0) : -1;
            }

            public boolean isDone()
            {
                return done.get();
            }

        };
        Runnable task = new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    byte[] buffer = new byte[256];
                    int read;
                    while ((read = src.read(buffer)) != -1)
                    {
                        dest.write(buffer, 0, read);
                        current.addAndGet(read);
                        if (progress.getOnProgressChanged() != null)
                        {
                            progress.getOnProgressChanged().accept(
                                    progress.getProgress());
                        }
                    }
                    dest.flush();
                    done.set(true);
                } catch (IOException ex)
                {
                    throw new RuntimeException(ex);
                }
            }
        };
        JShed.getExecutorService().execute(task);
        return progress;
    }

    public static void copy(InputStream src, OutputStream dest) throws IOException
    {
        Objects.requireNonNull(src);
        Objects.requireNonNull(dest);
        byte[] buffer = new byte[256];
        int read;
        while ((read = src.read(buffer)) != -1)
        {
            dest.write(buffer, 0, read);
        }
        dest.flush();
    }

    public static void copy(DataSource src, DataSink dest) throws IOException
    {
        try (InputStream in = src.getSource(); OutputStream out = dest.getSink())
        {
            copy(in, out);
        }
    }

    public static IOObject toIOObject(File f)
    {
        Objects.requireNonNull(f);
        return toIOObject(f.toPath());
    }

    public static IOObject toIOObject(Path p)
    {
        Objects.requireNonNull(p);
        return new IOObject()
        {
            @Override
            public InputStream getSource() throws IOException
            {
                return Files.newInputStream(p);
            }

            @Override
            public OutputStream getSink() throws IOException
            {
                return Files.newOutputStream(p);
            }
        };
    }
}
