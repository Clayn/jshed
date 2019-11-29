/*
 * The MIT License
 *
 * Copyright 2019 Your Organisation.
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
package net.bplaced.clayn.jshed.fx.util;

import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import net.bplaced.clayn.jshed.util.ProgressingTask;

/**
 * An implementation of the {@link ProgressingTask} that provides bindings to
 * the progress and done values of the underling task. The changes for those
 * bindings are done on the FXApplication Thread.
 *
 * @author Clayn <clayn_osmato@gmx.de>
 */
public class FXProgressingTask extends ProgressingTask
{

    private volatile Consumer<Double> volatileConsumer = null;
    private final Consumer<Double> defaultConsumer = new Consumer<Double>()
    {
        @Override
        public void accept(Double t)
        {
            Platform.runLater(() -> progress.set(t));
            if (volatileConsumer != null)
            {
                volatileConsumer.accept(t);
            }
        }
    };
    private final ReadOnlyDoubleWrapper progress = new ReadOnlyDoubleWrapper(-1);
    private final ReadOnlyBooleanWrapper done = new ReadOnlyBooleanWrapper(false);

    public FXProgressingTask()
    {
        done.bind(progress.greaterThanOrEqualTo(1.0));
        super.setOnProgressChanged(defaultConsumer);
    }

    public final ReadOnlyDoubleProperty progressProperty()
    {
        return progress.getReadOnlyProperty();
    }

    public final ReadOnlyBooleanProperty doneProperty()
    {
        return done.getReadOnlyProperty();
    }

    @Override
    public final void setOnProgressChanged(Consumer<Double> onProgressChanged)
    {
        volatileConsumer = onProgressChanged;
    }

    @Override
    public Consumer<Double> getOnProgressChanged()
    {
        return volatileConsumer;
    }

    @Override
    public double getProgress()
    {
        return progress.get();
    }

    @Override
    public boolean isDone()
    {
        return done.get();
    }
}
