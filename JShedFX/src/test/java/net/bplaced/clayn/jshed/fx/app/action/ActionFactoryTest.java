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

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Clayn <clayn_osmato@gmx.de>
 */
public class ActionFactoryTest
{

    private final AtomicInteger tester = new AtomicInteger(0);

    @Execution("basic")
    private String basicMethod(@ActionParameter("name") String name)
    {
        tester.incrementAndGet();
        return String.format("Hello %s", name);
    }

    @Execution("exec")
    private void executionMethod()
    {
        tester.incrementAndGet();
    }

    @Execution
    private String parameterLessAction()
    {
        return "Hello World";
    }

    private static class MethodWithoutAnnotationTest
    {

        @Execution("exec")
        private void executionMethod(String parameter)
        {

        }
    }
    
    private static class MultipleNamesActionTest {
        
        @Execution("exec")
        private void executionMethod()
        {

        }
         @Execution("exec")
        private void executionMethod2()
        {

        }
    }

    @Test(expected = ActionConstructionException.class)
    public void testMissingMethodParameter() throws ActionConstructionException {
        ActionFactory.createActions(new MethodWithoutAnnotationTest());
    }
    
    @Test(expected = ActionConstructionException.class)
    public void testNameMultipleTest() throws ActionConstructionException {
        ActionFactory.createActions(new MultipleNamesActionTest());
    }
    
   @Test(expected = ActionBuildingException.class)
    public void testNameMultipleBuildTest() {
        ActionFactory.begin("exec")
                .type(ActionFactory.ActionType.EXECUTION)
                .call((list)
                        -> 
                        {
                            tester.set(1);
                })
                .action("exec");
    }
    @Test(expected = ActionBuildingException.class)
    public void testNameMultipleBuildTest2() {
        ActionFactory.begin("exec")
                .type(ActionFactory.ActionType.EXECUTION)
                .call((list)
                        -> 
                        {
                            tester.set(1);
                })
                .action("exec2")
                .name("exec");
    }
    
    @Test
    public void testCreateActions() throws ActionConstructionException
    {
        Map<String, Action<?>> list = ActionFactory.createActions(this);
        Assert.assertEquals(3, list.size());
        Assert.assertTrue(list.containsKey("exec"));
        Assert.assertTrue(list.containsKey("basic"));
        Assert.assertTrue(list.containsKey(
                "ActionFactoryTest.parameterLessAction"));
    }

    @Test
    public void testBasicAction() throws Exception
    {
        Map<String, Action<?>> list = ActionFactory.createActions(this);
        Action<String> ac = (Action<String>) list.get("basic");
        Assert.assertNotNull(ac);
        tester.set(0);
        Assert.assertEquals(0, tester.get());
        Assert.assertEquals("Hello World", ac.execute(
                ParameterList.create().setParameter("name", "World")));
        Assert.assertEquals("Hello Sun", ac.execute(
                ParameterList.create().setParameter("name", "Sun")));
        Assert.assertEquals(2, tester.get());
    }

    @Test
    public void testExecutionAction() throws ActionConstructionException, Exception
    {
        Map<String, Action<?>> list = ActionFactory.createActions(this);
        Action<Void> ac = (Action<Void>) list.get("exec");
        tester.set(0);
        Assert.assertEquals(0, tester.get());
        ac.execute();
        Assert.assertEquals(1, tester.get());
        if (ac instanceof ExecutionAction)
        {
            ExecutionAction exec = (ExecutionAction) ac;
            tester.set(0);
            Assert.assertEquals(0, tester.get());
            exec.call();
            Assert.assertEquals(1, tester.get());
        }
    }

    @Test
    public void testExecutionActionBuild() throws Exception
    {
        Action<Void> ac = (Action<Void>) ActionFactory.begin("exec")
                .type(ActionFactory.ActionType.EXECUTION)
                .call((list)
                        -> 
                        {
                            tester.set(1);
                })
                .build(null);
        tester.set(0);
        Assert.assertEquals(0, tester.get());
        ac.execute();
        Assert.assertEquals(1, tester.get());
    }

    @Test
    public void testParameterLessActionBuild() throws Exception
    {
        Action<String> ac = (Action<String>) ActionFactory.begin("parameterless")
                .type(ActionFactory.ActionType.PARAMETERLESS)
                .call(() -> "Hello World")
                .build(null);
        Assert.assertEquals("Hello World", ac.execute());
    }

    @Test
    public void testExecutionActionBuildWithoutType() throws Exception
    {
        Action<Void> ac = (Action<Void>) ActionFactory.begin("exec")
                .call((list)
                        -> 
                        {
                            tester.set(1);
                })
                .build(null);
        tester.set(0);
        Assert.assertEquals(0, tester.get());
        ac.execute();
        Assert.assertEquals(1, tester.get());
    }

    @Test
    public void testMultiplenBuildWithoutType() throws Exception
    {
        Map<String, Action<?>> actions = ActionFactory.begin("exec")
                .call((list)
                        -> 
                        {
                            tester.set(1);
                })
                .action("parameterless")
                .call(() -> "Hello World")
                .build();
        Assert.assertEquals(2, actions.size());
        Action<Void> exec = (Action<Void>) actions.get("exec");
        tester.set(0);
        Assert.assertEquals(0, tester.get());
        exec.execute();
        Assert.assertEquals(1, tester.get());
        Action<String> para = (Action<String>) actions.get("parameterless");
        Assert.assertEquals("Hello World", para.execute());
    }

    @Test
    public void testBasicActionBuild() throws Exception
    {
        Action<String> ac = ActionFactory.begin("action")
                .type(ActionFactory.ActionType.BASIC)
                .result(String.class)
                .call((list)
                        -> 
                        {
                            tester.set(1);
                            return "Hello World";
                })
                .build(null);
        tester.set(0);
        Assert.assertEquals(0, tester.get());
        ac.execute();
        Assert.assertEquals("Hello World", ac.execute());
        Assert.assertEquals(1, tester.get());
    }

    @Test
    public void testBasicActionWithParameterBuild() throws Exception
    {
        Action<String> ac = ActionFactory.begin("action")
                .type(ActionFactory.ActionType.BASIC)
                .result(String.class)
                .parameter("name", String.class)
                .call(new Function<ParameterList, String>()
                {
                    @Override
                    public String apply(ParameterList t)
                    {
                        tester.set(1);
                        return String.format("Hello %s", t.getParameter("name",
                                String.class));
                    }

                })
                .build(null);
        tester.set(0);
        Assert.assertEquals(0, tester.get());
        String result = ac.execute(ParameterList.create().setParameter("name",
                "World"));
        Assert.assertEquals("Hello World", result);
        result = ac.execute(ParameterList.create().setParameter("name",
                "Other World"));
        Assert.assertEquals("Hello Other World", result);
        Assert.assertEquals(1, tester.get());
    }

    @Test
    public void testMultipleBuild() throws Exception
    {
        Map<String, Action<?>> actions = ActionFactory.begin("exec")
                .type(ActionFactory.ActionType.EXECUTION)
                .call((list)
                        -> 
                        {
                            tester.set(1);
                })
                .action("parameterless")
                .type(ActionFactory.ActionType.PARAMETERLESS)
                .call(() -> "Hello World")
                .build();
        Assert.assertEquals(2, actions.size());
        Action<Void> exec = (Action<Void>) actions.get("exec");
        tester.set(0);
        Assert.assertEquals(0, tester.get());
        exec.execute();
        Assert.assertEquals(1, tester.get());
        Action<String> para = (Action<String>) actions.get("parameterless");
        Assert.assertEquals("Hello World", para.execute());
    }

    @Test
    public void testParameterLessActionBuildWithoutType() throws Exception
    {
        Action<String> ac = (Action<String>) ActionFactory.begin("parameterless")
                .result(String.class)
                .call(() -> "Hello World")
                .build(null);
        Assert.assertEquals("Hello World", ac.execute());
    }

    @Test
    public void testParameterLessAction() throws Exception
    {
        Map<String, Action<?>> list = ActionFactory.createActions(this);
        Action<String> ac = (Action<String>) list.get(
                "ActionFactoryTest.parameterLessAction");
        Assert.assertEquals("Hello World", ac.execute());
        if (ac instanceof ParameterLessAction)
        {
            ParameterLessAction<String> ac2 = (ParameterLessAction<String>) ac;
            Assert.assertEquals("Hello World", ac2.execute());
        }
    }
}
