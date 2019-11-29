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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.bplaced.clayn.jshed.fx.app.action.Action.Parameter;
import static net.bplaced.clayn.jshed.fx.app.action.ActionFactory.ActionType.BASIC;

/**
 *
 * @author Clayn <clayn_osmato@gmx.de>
 * @param <R> the return type of the current action in construction
 */
public final class ActionFactory<R>
{

    public static enum ActionType
    {
        BASIC, PARAMETERLESS, EXECUTION;
    }

    public static Map<String, Action<?>> createActions(Object obj) throws ActionConstructionException
    {
        Map<String, Action<?>> actions = new HashMap<>();
        if (obj != null)
        {
            Class<?> clazz = obj.getClass();
            List<Method> methods = Stream.concat(Arrays.stream(
                    clazz.getMethods()), Arrays.stream(
                            clazz.getDeclaredMethods()))
                    .filter((m) -> m.isAnnotationPresent(Execution.class))
                    .collect(Collectors.toList());
            for (Method m : methods)
            {
                Action<?> ac;
                Execution exec = m.getAnnotation(Execution.class);
                String val = exec.value();
                String name = val == null || val.trim().isEmpty() ? clazz.getSimpleName() + "." + m.getName() : val;
                if (actions.containsKey(name))
                {
                    throw new ActionConstructionException(
                            "Multiple actions with name " + name + " found");
                }
                m.setAccessible(true);
                if (m.getParameters().length != 0 && !Void.TYPE.isAssignableFrom(
                        m.getReturnType()))
                {
                    ac = createBasicAction(m, obj);
                } else if (m.getParameters().length == 0 && !Void.TYPE.isAssignableFrom(
                        m.getReturnType()))
                {
                    ac = createParameterLessAction(m, obj);
                } else
                {
                    ac = createExecutionAction(m, obj);
                }
                if (ac != null)
                {
                    actions.put(name, ac);
                }
            }

        }
        return actions;
    }

    private static void checkParameters(Method m) throws ActionConstructionException
    {
        if (m == null || m.getParameters().length == 0)
        {
            return;
        }
        for (int i = 0; i < m.getParameterCount(); ++i)
        {
            Annotation a[] = m.getParameterAnnotations()[i];
            boolean found = a != null && Arrays.stream(a)
                    .anyMatch((an) -> an.annotationType().equals(
                            ActionParameter.class));
            if (!found)
            {
                throw new ActionConstructionException(
                        "Failed to construct action for method '" + m.getName() + "'. Missing parameter annotation at parameterindex: " + i);
            }
        }
    }

    private static List<Parameter> extractParameters(Method m) throws ActionConstructionException
    {
        List<Parameter> parameters = new ArrayList<>();
        for (java.lang.reflect.Parameter p : m.getParameters())
        {
            String name;
            boolean mandatory = true;
            if (p.isAnnotationPresent(ActionParameter.class))
            {
                name = p.getAnnotation(ActionParameter.class).value();

                mandatory = p.getAnnotation(ActionParameter.class).mandatory();
            }else {
                throw new ActionConstructionException("No annotation present for parameter: "+p.getName()+" in method "+m.getName());
            }
            Parameter par = new Parameter(name, p.getType(), mandatory);
            parameters.add(par);
        }
        return parameters;
    }

    private static Action<?> createBasicAction(Method m, Object obj) throws ActionConstructionException
    {
        checkParameters(m);
        List<Parameter> parameters = extractParameters(m);
        Action<?> ac = new Action<Object>()
        {
            @Override
            public Object execute(ParameterList localPar) throws Exception
            {
                if (!verify(localPar))
                {
                    throw new ActionExecutionException(
                            "Parameters are not matching the requirements");
                }
                Object[] arguments = new Object[parameters.size()];

                for (int i = 0; i < parameters.size(); ++i)
                {
                    arguments[i] = localPar.getParameter(
                            parameters.get(i).getName());
                }
                return m.invoke(obj, arguments);
            }

            @Override
            public List<Parameter> getParameters()
            {
                return parameters;
            }

            @Override
            public Class<? extends Object> getReturnType()
            {
                return m.getReturnType();
            }
        };
        return ac;
    }

    private static ParameterLessAction<?> createParameterLessAction(Method m,
            Object obj) throws ActionConstructionException
    {
        checkParameters(m);
        ParameterLessAction<?> ac = new ParameterLessAction<Object>()
        {

            @Override
            public Class<? extends Object> getReturnType()
            {
                return m.getReturnType();
            }

            @Override
            public Object execute() throws Exception
            {
                return m.invoke(obj);
            }
        };
        return ac;
    }

    private static ExecutionAction createExecutionAction(Method m, Object obj) throws ActionConstructionException
    {
        checkParameters(m);
        List<Parameter> parameters = extractParameters(m);
        ExecutionAction ac = new ExecutionAction()
        {

            @Override
            public List<Parameter> getParameters()
            {
                return parameters;
            }

            @Override
            public void call(ParameterList localPar) throws Exception
            {
                if (!verify(localPar))
                {
                    throw new ActionExecutionException(
                            "Parameters are not matching the requirements");
                }
                Object[] arguments = new Object[parameters.size()];

                for (int i = 0; i < parameters.size(); ++i)
                {
                    arguments[i] = localPar.getParameter(
                            parameters.get(i).getName());
                }
                m.invoke(obj, arguments);
            }
        };
        return ac;
    }
    private String firstName = null;
    private ActionType currentType = null;
    private String currentName = null;
    private Class<?> currentReturnType = Void.TYPE;
    private Function<ParameterList, ?> function = null;
    private Consumer<ParameterList> consumer = null;
    private Supplier<?> supplier = null;
    private final List<Parameter> currentParameters = new ArrayList<>();

    private final Map<String, Action<?>> actions = new HashMap<>();

    private void throwNameUsedException(String name) throws ActionBuildingException
    {
        if (actions.containsKey(name))
        {
            throw new ActionBuildingException(
                    "Action name '" + name + "' is already used");
        }
    }

    /**
     * Adds a new parameter with the given name and type.
     *
     * @param name the name for the parameter
     * @param type the type of the parameter
     * @return this action factory
     * @see #parameter(net.bplaced.clayn.jshed.fx.app.action.Action.Parameter,
     * net.bplaced.clayn.jshed.fx.app.action.Action.Parameter...)
     */
    public ActionFactory parameter(String name, Class<?> type)
    {
        return parameter(new Parameter(name, type));
    }

    /**
     * Adds the given parameter and all additional {@link Parameter parameter}
     * to the action in construction. Any {@code null} values are filtered.
     *
     * @param p the first parameter to add
     * @param add additional parameters to add
     * @return this action factory
     * @see #parameter(java.lang.String, java.lang.Class)
     */
    public ActionFactory parameter(Parameter p, Parameter... add)
    {
        if (p != null)
        {
            currentParameters.add(p);
        }
        if (add != null)
        {
            currentParameters
                    .addAll(Arrays.asList(add).stream().filter(Objects::nonNull).collect(
                            Collectors.toList()));
        }
        return this;
    }

    /**
     * Sets the return type for the action in construction. If the type is
     * {@code null}, {@link Void} gets assumed.
     *
     * @param <T> the new return type
     * @param result the return type for the action
     * @return this action factory
     */
    public <T> ActionFactory<T> result(Class<T> result)
    {
        currentReturnType = result == null ? Void.TYPE : result;
        return (ActionFactory<T>) this;
    }

    /**
     * Sets the type for the action. An action can be one of the following:<br>
     * {@link ActionType#BASIC BASIC}: actions that can use parameters and may
     * return something <br> {@link ActionType#PARAMETERLESS PARAMETERLESS}:
     * actions that won't use parameters and may return someting <br>
     * {@link ActionType#EXECUTION EXECUTION}: actions that can use parameters
     * but return only {@link Void} <br>
     * <br>
     * If no type is set the type will be determinated from the calling function
     * set with {@code call}. When the type is set it will be checked if a
     * matching calling function was set.
     *
     * @param type
     * @return
     */
    public ActionFactory type(ActionType type)
    {
        currentType = Objects.requireNonNull(type);
        return this;
    }

    /**
     * Sets the calling action for {@link ActionType#EXECUTION execution}
     * actions.
     *
     * @param action the action to use for execution
     * @return this action factory
     * @see #call(java.util.function.Function)
     * @see #call(java.util.function.Supplier)
     */
    public ActionFactory call(Consumer<ParameterList> action)
    {
        consumer = action;
        return this;
    }

    /**
     * Sets the calling action for {@link ActionType#BASIC basic} actions.
     *
     * @param action the action to use for execution
     * @return this action factory
     * @see #call(java.util.function.Consumer)
     * @see #call(java.util.function.Supplier)
     */
    public ActionFactory<R> call(Function<ParameterList, R> action)
    {
        function = action;
        return this;
    }

    /**
     * Sets the calling action for
     * {@link ActionType#PARAMETERLESS parameterless} actions.
     *
     * @param action the action to use for execution
     * @return this action factory
     * @see #call(java.util.function.Function)
     * @see #call(java.util.function.Consumer)
     */
    public ActionFactory<R> call(Supplier<R> action)
    {
        supplier = action;
        return this;
    }

    /**
     * Sets the name for the current action in construction.
     *
     * @param name the new name for the action
     * @return this action factory
     * @throws ActionBuildingException if the used name is already used by a
     * build action in this factory
     * @throws NullPointerException if the name was {@code null}
     */
    public ActionFactory<R> name(String name) throws ActionBuildingException, NullPointerException
    {
        throwNameUsedException(Objects.requireNonNull(name));
        currentName = name;
        return this;
    }

    private void checkType()
    {

        if (currentType == null)
        {
            if (function != null)
            {
                currentType = ActionType.BASIC;
            } else if (consumer != null)
            {
                currentType = ActionType.EXECUTION;
            } else if (supplier != null)
            {
                currentType = ActionType.PARAMETERLESS;
            } else
            {
                throw new ActionBuildingException(
                        "No type and no useable function was set");
            }
        } else
        {
            if (ActionType.BASIC == currentType && function == null)
            {
                throw new ActionBuildingException(
                        "Actions with type " + BASIC + " need a function");
            }
            if (ActionType.EXECUTION == currentType && consumer == null)
            {
                throw new ActionBuildingException(
                        "Actions with type " + ActionType.EXECUTION + " need a consumer");
            }
            if (ActionType.PARAMETERLESS == currentType && supplier == null)
            {
                throw new ActionBuildingException(
                        "Actions with type " + ActionType.PARAMETERLESS + " need a supplier");
            }
        }
    }

    private Action<?> buildBaseAction()
    {
        return new Action<Object>()
        {
            private final List<Parameter> actionParameters = new ArrayList<>(
                    currentParameters);
            private final Class<?> returnType = currentReturnType;
            private final Function<ParameterList, ?> action = function;

            @Override
            public Object execute(ParameterList parameters) throws Exception
            {
                return action.apply(parameters);
            }

            @Override
            public List<Parameter> getParameters()
            {
                return actionParameters;
            }

            @Override
            public Class<? extends Object> getReturnType()
            {
                return returnType;
            }
        };
    }

    private Action<?> buildExecutionAction()
    {
        return new ExecutionAction()
        {
            private final List<Parameter> actionParameters = new ArrayList<>(
                    currentParameters);
            private final Consumer<ParameterList> action = consumer;

            @Override
            public void call(ParameterList parameters) throws Exception
            {
                action.accept(parameters);
            }

            @Override
            public List<Parameter> getParameters()
            {
                return actionParameters;
            }
        };
    }

    private Action<?> buildParameterLess()
    {
        return new ParameterLessAction<Object>()
        {
            private final Class<?> returnType = currentReturnType;
            private final Supplier<?> action = supplier;

            @Override
            public Object execute() throws Exception
            {
                return action.get();
            }

            @Override
            public Class<? extends Object> getReturnType()
            {
                return returnType;
            }
        };
    }

    private void intermediateBuild()
    {
        if (currentName == null || currentName.trim().isEmpty())
        {

        }
        checkType();
        Action<?> ac = null;
        switch (currentType)
        {
            case BASIC:
                ac = buildBaseAction();
                break;
            case PARAMETERLESS:
                ac = buildParameterLess();
                break;
            case EXECUTION:
                ac = buildExecutionAction();
                break;
        }
        if (ac != null)
        {
            actions.put(currentName, ac);
        }
    }

    /**
     * Creates a new action factory and begins the creation of an action with
     * the given name.
     *
     * @param name the name for the new action
     * @return a new action factory that started the building of an action
     */
    public static ActionFactory<Void> begin(String name)
    {
        return new ActionFactory<Void>().action(name);
    }

    /**
     * Starts the construction of a new action and finishes the current action
     * in construction.
     *
     * @param name the name for the new action
     * @return the action factory
     * @throws ActionBuildingException when the current action in construction
     * can't be build.
     */
    public ActionFactory<Void> action(String name) throws ActionBuildingException
    {
        throwNameUsedException(Objects.requireNonNull(name));
        if (currentName != null)
        {
            intermediateBuild();
        }
        if (firstName == null)
        {
            firstName = currentName;
        }
        currentParameters.clear();
        currentType = null;
        currentReturnType = null;
        function = null;
        consumer = null;
        supplier = null;
        return result(Void.TYPE)
                .name(name);
    }

    /**
     * Builds the current action and returns all actions that where build within
     * this factory.
     *
     * @return the build actions from this factory
     * @throws ActionBuildingException when the current action in construction
     * can't be build.
     */
    public Map<String, Action<?>> build() throws ActionBuildingException
    {
        intermediateBuild();
        return actions;
    }

    /**
     * Builds the actions and returns the one with the given name. If the name
     * is {@code null} or an unkown name was used while only one action was
     * constructed, the first build action gets returned.
     *
     * @param name the name for the action to build
     * @return the action with the given name or the first constructed if no
     * such name was found and only one action exists or the name was
     * {@code null}
     */
    public Action<R> build(String name)
    {
        String get = name == null ? firstName : name;
        Map<String, Action<?>> acs = build();
        if (!acs.containsKey(get) && acs.size() == 1)
        {
            return (Action<R>) acs.get(acs.keySet().iterator().next());
        } else
        {
            return (Action<R>) acs.get(get);
        }
    }
}
