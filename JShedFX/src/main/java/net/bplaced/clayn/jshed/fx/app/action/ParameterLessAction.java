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
package net.bplaced.clayn.jshed.fx.app.action;

import java.util.Collections;
import java.util.List;

/**
 *
 * @author Clayn <clayn_osmato@gmx.de>
 * @param <R>
 */
public interface ParameterLessAction<R> extends Action<R>
{

    @Override
    public default R execute(ParameterList parameters) throws Exception
    {
        return execute();
    }
    
    @Override
    R execute() throws Exception;

    @Override
    public default List<Parameter> getParameters()
    {
        return Collections.emptyList();
    }

    @Override
    public default boolean verify(ParameterList parameters) {
        return true;
    }
    
    
    
    
}
