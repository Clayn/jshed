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

import java.util.List;

/**
 *
 * @author Clayn <clayn_osmato@gmx.de>
 * @param <R>
 */
public interface Action<R>
{

    public static class Parameter
    {

        private final String name;
        private final Class<?> type;
        private final boolean mandatory;

        public String getName()
        {
            return name;
        }

        public Class<?> getType()
        {
            return type;
        }

        public boolean isMandatory()
        {
            return mandatory;
        }

        public Parameter(String name,
                Class<?> type)
        {
            this(name,type,true);
        }

        public Parameter(String name,
                Class<?> type, boolean mandatory)
        {
            this.name = name;
            this.type = type;
            this.mandatory = mandatory;
        }
        

    }

    /**
     * Executes the action with the parameters.
     *
     * @param parameters the parameters for the actions
     * @throws Exception if an excetion occurs during the execution
     * @return the result of the action
     */
    R execute(ParameterList parameters) throws Exception;

    /**
     * Executes the action without any parameter.
     *
     * @throws Exception if an excetion occurs during the execution
     * @return the result of the action
     */
    default R execute() throws Exception
    {
        return execute(ParameterList.emptyList());
    }

    /**
     * Checks wether or not the given parameters are valid/useable or not. This
     * is an convenient method for an easy check if the action don't trust the
     * executiron environment.
     *
     * @param parameters the parameters to check
     * @return {
     * @true} if and only if all required parameters are available and are
     * matching the types.
     */
    default boolean verify(ParameterList parameters)
    {
        for(Parameter p:getParameters()) {
            
            Object val=parameters.getParameter(p.getName());
            System.out.print("Found parameter value for name "+p.getName()+": "+val);
            System.out.println(" of type: "+val.getClass());
            System.out.println("Requires: "+p.getType());
            if(p.isMandatory()&&val==null) {
                return false;
            }
            if(val!=null&&!p.getType().isAssignableFrom(val.getClass())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns a list of the parameters required for this action. While some
     * parameters may not be mandatory e.g. they can be {@code null} they have
     * to be listed here.
     *
     * @return the list of required parameters.
     */
    List<Parameter> getParameters();

    /**
     * Returns the return type of this action.
     *
     * @return the return type of this action
     */
    Class<? extends R> getReturnType();
}
