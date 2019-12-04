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
package net.bplaced.clayn.jshed.fx.app.action;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

/**
 *
 * @author Clayn <clayn_osmato@gmx.de>
 */
public abstract class ActionPipeline
{

    private final Map<String,Action<?>> actions=new HashMap<>();
    private <T> Callable<T> getAction(String name, ParameterList parameter)
    {
        if(!actions.containsKey(name)) {
            throw new NullPointerException("No action found for name: "+name);
        }
        Action<?> ac=actions.get(name);
        return new Callable<T>()
        {
            @Override
            public T call() throws Exception
            {
                return (T) ac.execute(parameter);
            }
        };
    }

    protected abstract <T> void execute(Callable<T> action) throws Exception;

    /**
     * Calls the action with the given name using the given parameter. This method may 
     * block until execution is finished. The consumers can be used to handle the 
     * result and in what thread the result will be set.
     *
     * @param <T> the type of the actions result
     * @param name the name of the action
     * @param parameter the parameter for the execution
     * @param onResult the reciever for the result
     * @param resultExecutor the consumer that will execute the setting of the result
     * @throws ActionExecutionException if an error occurs during the execution
     */
    public <T> void callAction(String name, ParameterList parameter,
            Consumer<T> onResult,Consumer<Runnable> resultExecutor) throws ActionExecutionException
    {
        Callable<T> action = getAction(name, parameter);
        try
        {
            Callable<T> exec=new Callable<T>()
        {
            @Override
            public T call() throws Exception
            {
                T result=action.call();
                if(onResult!=null) {
                    Runnable resSetting=new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            onResult.accept(result);
                        }
                    };
                    if(resultExecutor==null) {
                        resSetting.run();
                    }else {
                        resultExecutor.accept(resSetting);
                    }
                }
                return result;
            }
        };
            execute(exec);
        } catch (Exception ex)
        {
            throw new ActionExecutionException("Failed to execute " + name, ex);
        }
    }
    
    public <T> void callAction(String name, ParameterList paramters,Consumer<T> onResult) throws ActionExecutionException {
        callAction(name, paramters, onResult, null);
    }
    
    public void callAction(String name, ParameterList parameters) throws ActionExecutionException {
        callAction(name, parameters, null);
    }
    
    public void callAction(String name) throws ActionExecutionException {
        callAction(name, ParameterList.emptyList());
    }
}
