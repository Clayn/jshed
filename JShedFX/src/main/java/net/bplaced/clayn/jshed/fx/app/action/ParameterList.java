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

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Clayn <clayn_osmato@gmx.de>
 */
public class ParameterList
{

    private final Map<String, Object> parameters = new HashMap<>();

    public Object getParameter(String name) 
    {
        return parameters.get(name);
    }
    
    public <T> T get(String name) throws ClassCastException{
        return (T)getParameter(name);
    }
    
    public <T> T getParameter(String name, Class<T> clazz) throws ClassCastException
    {
        return get(name);
    }

    public ParameterList setParameter(String name, Object val)
    {
        parameters.put(name, val);
        return this;
    }

    public static ParameterList create(Object ...values) {
        ParameterList list=new ParameterList();
        if(values!=null) {
            if(values.length==1) {
                Object key=values[0];
                if(key!=null) {
                    list.setParameter(key.toString(), null);
                }
            }
            for(int i=0;i<values.length-1;i+=2) {
                Object key=values[i];
                Object val=values[i+1];
                if(key!=null) {
                    list.setParameter(key.toString(), val);
                }
            }
            if(values.length%2!=0) {
                Object key=values[values.length-1];
                if(key!=null) {
                    list.setParameter(key.toString(), null);
                }
            }
        }
        return list;
    }
    
    public static ParameterList emptyList() {
        return create();
    }
    
   
}
