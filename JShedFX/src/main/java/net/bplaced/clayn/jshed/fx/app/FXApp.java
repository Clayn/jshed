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
package net.bplaced.clayn.jshed.fx.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Clayn <clayn_osmato@gmx.de>
 */
public class FXApp
{

    private static final Logger LOG = LoggerFactory.getLogger(FXApp.class);
    private final ErrorService DEFAULT_SERVICE = new ErrorService()
    {
        @Override
        public void handleException(Exception ex)
        {
            if (ex == null)
            {
                return;
            }
            if (loggingExceptions)
            {
                LOG.error("", ex);
            }
            if (errorService != null)
            {
                errorService.handleException(ex);
            }
        }
    };
    private boolean loggingExceptions = true;
    private ErrorService errorService = null;
    private static final FXApp INSTANCE = new FXApp();

    private FXApp()
    {

    }

    public ErrorService getErrorService()
    {
        return errorService;
    }

    public void setErrorService(ErrorService errorService)
    {
        this.errorService = errorService;
    }

    /**
     * Returns the JShed error service. That service will log the exception if
     * enabled and will delegate that exception to the error service set with {@link #setErrorService(net.bplaced.clayn.jshed.fx.app.ErrorService)
     * }.
     *
     * @return the JShed error service.
     */
    public ErrorService getJShedErrorService()
    {
        return DEFAULT_SERVICE;
    }

    public boolean isLoggingExceptions()
    {
        return loggingExceptions;
    }

    public void setLoggingExceptions(boolean loggingExceptions)
    {
        this.loggingExceptions = loggingExceptions;
    }

    public static FXApp getApplication()
    {
        return INSTANCE;
    }
}
