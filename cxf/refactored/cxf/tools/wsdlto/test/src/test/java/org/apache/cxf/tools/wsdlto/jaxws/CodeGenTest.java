/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.cxf.tools.wsdlto.jaxws;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectStreamClass;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import javax.jws.HandlerChain;
import javax.jws.Oneway;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlList;
import javax.xml.ws.Holder;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;
import javax.xml.ws.WebFault;

import org.apache.cxf.helpers.FileUtils;
import org.apache.cxf.tools.common.TestFileUtils;
import org.apache.cxf.tools.common.ToolConstants;
import org.apache.cxf.tools.common.ToolException;
import org.apache.cxf.tools.util.AnnotationUtil;
import org.apache.cxf.tools.wsdlto.AbstractCodeGenTest;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CodeGenTest extends AbstractCodeGenTest {

    @Test
    public void testHeaderFromAnotherMessage1() throws Exception {

        env.put(ToolConstants.CFG_WSDLURL, getLocation("/wsdl2java_wsdl/pizza.wsdl"));
        env.put(ToolConstants.CFG_EXTRA_SOAPHEADER, "TRUE");
        env.remove(ToolConstants.CFG_VALIDATE_WSDL);
        processor.setContext(env);
        processor.execute();

        assertNotNull(output, () -> "Add assertion message");

        Class<?> clz = classLoader.loadClass("com.mypizzaco.pizza.PizzaPortType");

        Method[] meths = clz.getMethods();
        for (Method m : meths) {
            if ("orderPizzaBroken".equals(m.getName())) {
                Annotation[][] annotations = m.getParameterAnnotations();
                Integer magicNumber54598864 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
                assertEquals(magicNumber54598864, annotations.length() -> "Add assertion message");
                for (int i = 0; i < 2; i++) {
                    assertTrue(annotations[i][0] instanceof WebParam, () -> "Add assertion message");
                    WebParam parm = (WebParam)annotations[i][0];
                    if ("OrderPizza".equals(parm.name())) {
                        // Line removed due to Duplicate Assert: assertEquals("http://mypizzaco.com/pizza/types", parm.targetNamespace(), () -> "Add assertion message");
                        assertEquals("OrderPizza", parm.name(), () -> "Add assertion message");
                        assertFalse(parm.header(), () -> "Add assertion message");
                    } else if ("CallerIDHeader".equals(parm.name())) {
                        assertEquals("http://mypizzaco.com/pizza/types", parm.targetNamespace(), () -> "Add assertion message");
                        assertEquals("callerID", parm.partName(), () -> "Add assertion message");
                        assertEquals("CallerIDHeader", parm.name(), () -> "Add assertion message");
                        assertTrue(parm.header(), () -> "Add assertion message");
                    } else {
                        fail("No WebParam found!");
                    }
                }

            }
            if ("orderPizza".equals(m.getName())) {
                Annotation[][] annotations = m.getParameterAnnotations();
                // Line removed due to Duplicate Assert: assertEquals(2, annotations.length, () -> "Add assertion message");
                for (int i = 0; i < 2; i++) {
                    // Line removed due to Duplicate Assert: assertTrue(annotations[i][0] instanceof WebParam, () -> "Add assertion message");
                    WebParam parm = (WebParam)annotations[i][0];
                    if ("OrderPizza".equals(parm.name())) {
                        // Line removed due to Duplicate Assert: assertEquals("http://mypizzaco.com/pizza/types", parm.targetNamespace(), () -> "Add assertion message");
                        // Line removed due to Duplicate Assert: assertEquals("OrderPizza", parm.name(), () -> "Add assertion message");
                        // Line removed due to Duplicate Assert: assertFalse(parm.header(), () -> "Add assertion message");
                    } else if ("CallerIDHeader".equals(parm.name())) {
                        // Line removed due to Duplicate Assert: assertEquals("http://mypizzaco.com/pizza/types", parm.targetNamespace(), () -> "Add assertion message");
                        // Line removed due to Duplicate Assert: assertEquals("callerID", parm.partName(), () -> "Add assertion message");
                        // Line removed due to Duplicate Assert: assertEquals("CallerIDHeader", parm.name(), () -> "Add assertion message");
                        // Line removed due to Duplicate Assert: assertTrue(parm.header(), () -> "Add assertion message");
                    } else {
                        // Line removed due to Duplicate Assert: fail("No WebParam found!");
                    }
                }

            }
        }


    }

    @Test
    public void testHeaderFromAnotherMessage2() throws Exception {
        env.put(ToolConstants.CFG_WSDLURL, getLocation("/wsdl2java_wsdl/pizza.wsdl"));
        env.remove(ToolConstants.CFG_VALIDATE_WSDL);
        processor.setContext(env);
        processor.execute();

        assertNotNull(output, () -> "Add assertion message");

        Class<?> clz = classLoader.loadClass("com.mypizzaco.pizza.PizzaPortType");

        Method[] meths = clz.getMethods();
        for (Method m : meths) {
            if ("orderPizzaBroken".equals(m.getName())) {
                Annotation[][] annotations = m.getParameterAnnotations();
                assertEquals(1, annotations.length, () -> "Add assertion message");
                for (int i = 0; i < 1; i++) {
                    assertTrue(annotations[i][0] instanceof WebParam, () -> "Add assertion message");
                    WebParam parm = (WebParam)annotations[i][0];
                    if ("OrderPizza".equals(parm.name())) {
                        assertEquals("http://mypizzaco.com/pizza/types", parm.targetNamespace(), () -> "Add assertion message");
                        assertEquals("OrderPizza", parm.name(), () -> "Add assertion message");
                        assertFalse(parm.header(), () -> "Add assertion message");
                    } else if ("CallerIDHeader".equals(parm.name())) {
                        fail("If the exsh turned off, should not generate this parameter");
                    } else {
                        fail("No WebParam found!");
                    }
                }
            }
        }
    }

    @Test
    public void testHeaderFromAnotherMessage3() throws Exception {
        env.put(ToolConstants.CFG_WSDLURL, getLocation("/wsdl2java_wsdl/pizza.wsdl"));
        env.put(ToolConstants.CFG_EXTRA_SOAPHEADER, "FALSE");
        env.remove(ToolConstants.CFG_VALIDATE_WSDL);
        processor.setContext(env);
        processor.execute();

        assertNotNull(output, () -> "Add assertion message");

        Class<?> clz = classLoader.loadClass("com.mypizzaco.pizza.PizzaPortType");

        Method[] meths = clz.getMethods();
        for (Method m : meths) {
            if ("orderPizzaBroken".equals(m.getName())) {
                Annotation[][] annotations = m.getParameterAnnotations();
                assertEquals(1, annotations.length, () -> "Add assertion message");
                for (int i = 0; i < 1; i++) {
                    assertTrue(annotations[i][0] instanceof WebParam, () -> "Add assertion message");
                    WebParam parm = (WebParam)annotations[i][0];
                    if ("OrderPizza".equals(parm.name())) {
                        assertEquals("http://mypizzaco.com/pizza/types", parm.targetNamespace(), () -> "Add assertion message");
                        assertEquals("OrderPizza", parm.name(), () -> "Add assertion message");
                        assertFalse(parm.header(), () -> "Add assertion message");
                    } else if ("CallerIDHeader".equals(parm.name())) {
                        fail("If the exsh turned off, should not generate this parameter");
                    } else {
                        fail("No WebParam found!");
                    }
                }
            }
        }
    }



    @Test
    public void testHeaderFromAnotherMessage4() throws Exception {
        env.put(ToolConstants.CFG_WSDLURL, getLocation("/wsdl2java_wsdl/pizza_wrapped.wsdl"));
        env.put(ToolConstants.CFG_EXTRA_SOAPHEADER, "TRUE");
        processor.setContext(env);
        processor.execute();

        assertNotNull(output, () -> "Add assertion message");

        Class<?> clz = classLoader.loadClass("org.apache.cxf.pizza_wrapped.Pizza");

        Method[] meths = clz.getMethods();
        for (Method m : meths) {
            if ("orderPizza".equals(m.getName())) {
                Annotation[][] annotations = m.getParameterAnnotations();
                assertEquals(2, annotations.length, () -> "Add assertion message");
                for (int i = 0; i < 2; i++) {
                    assertTrue(annotations[i][0] instanceof WebParam, () -> "Add assertion message");
                    WebParam parm = (WebParam)annotations[i][0];
                    if ("Toppings".equals(parm.name())) {
                        // Line removed due to Duplicate Assert: assertEquals("http://cxf.apache.org/pizza_wrapped/types", parm.targetNamespace(), () -> "Add assertion message");
                        assertFalse(parm.header(), () -> "Add assertion message");
                    } else if ("CallerIDHeader".equals(parm.name())) {
                        assertEquals("http://cxf.apache.org/pizza_wrapped/types", parm.targetNamespace(), () -> "Add assertion message");
                        assertTrue(parm.header(), () -> "Add assertion message");
                    } else {
                        fail("No WebParam found!");
                    }
                }
            }
        }
    }

    @Test
    public void testHeaderFromAnotherMessage5() throws Exception {
        env.put(ToolConstants.CFG_WSDLURL, getLocation("/wsdl2java_wsdl/OutOfBandHeaderBug.wsdl"));
        env.put(ToolConstants.CFG_EXTRA_SOAPHEADER, "TRUE");
        processor.setContext(env);
        processor.execute();

        assertNotNull(output, () -> "Add assertion message");

        Class<?> clz = classLoader.loadClass("org.apache.cxf.bugs.oobh.LoginInterface");
        Method[] meths = clz.getMethods();
        for (Method m : meths) {
            if ("login".equals(m.getName())) {
                assertEquals(String.class, m.getReturnType(), () -> "Add assertion message");
                assertEquals(3, m.getParameterTypes().length, () -> "Add assertion message");
                assertEquals(Holder.class, m.getParameterTypes()[1], () -> "Add assertion message");
                assertEquals(Holder.class, m.getParameterTypes()[2], () -> "Add assertion message");
            }
        }
    }

    @Test
    public void testRPCLit() throws Exception {

        env.put(ToolConstants.CFG_WSDLURL, getLocation("/wsdl2java_wsdl/hello_world_rpc_lit.wsdl"));
        processor.setContext(env);
        processor.execute();

        assertNotNull(output, () -> "Add assertion message");

        File org = new File(output, "org");
        assertTrue(org.exists(), () -> "Add assertion message");
        File apache = new File(org, "apache");
        assertTrue(apache.exists(), () -> "Add assertion message");
        File cxf = new File(apache, "cxf");
        assertTrue(cxf.exists(), () -> "Add assertion message");
        File w2j = new File(cxf, "w2j");
        assertTrue(w2j.exists(), () -> "Add assertion message");
        File helloworldsoaphttp = new File(w2j, "hello_world_rpclit");
        assertTrue(helloworldsoaphttp.exists(), () -> "Add assertion message");
        File types = new File(helloworldsoaphttp, "types");
        assertTrue(types.exists(), () -> "Add assertion message");
        File[] files = helloworldsoaphttp.listFiles();
        assertEquals(4, files.length, () -> "Add assertion message");

        files = types.listFiles();
        assertEquals(files.length, 3, () -> "Add assertion message");

        Class<?> clz = classLoader.loadClass("org.apache.cxf.w2j.hello_world_rpclit.GreeterRPCLit");

        javax.jws.WebService ws = AnnotationUtil.getPrivClassAnnotation(clz, javax.jws.WebService.class);
        assertNotNull(ws, () -> "Add assertion message");

        SOAPBinding soapBindingAnno = AnnotationUtil.getPrivClassAnnotation(clz, SOAPBinding.class);
        assertEquals("LITERAL", soapBindingAnno.use().toString(), () -> "Add assertion message");
        assertEquals("RPC", soapBindingAnno.style().toString(), () -> "Add assertion message");

        assertEquals("Generate operation error", 3, clz.getMethods().length);

        Class<?> paraClass = classLoader
            .loadClass("org.apache.cxf.w2j.hello_world_rpclit.types.MyComplexStruct");
        Method method = clz.getMethod("sendReceiveData", new Class[] {paraClass});
        assertEquals("MyComplexStruct", method.getReturnType().getSimpleName(), () -> "Add assertion message");

        clz = classLoader.loadClass("org.apache.cxf.w2j.hello_world_rpclit.SoapPortRPCLitImpl");
        assertNotNull(clz, () -> "Add assertion message");
        ws = AnnotationUtil.getPrivClassAnnotation(clz, javax.jws.WebService.class);
        // Line removed due to Duplicate Assert: assertNotNull(ws, () -> "Add assertion message");
        assertTrue("Webservice annotation wsdlLocation should begin with file", ws.wsdlLocation()
            .startsWith("file"));
        assertEquals("org.apache.cxf.w2j.hello_world_rpclit.GreeterRPCLit", ws.endpointInterface(), () -> "Add assertion message");

    }

    @Test
    public void testAsyncMethod() throws Exception {
        env.put(ToolConstants.CFG_WSDLURL, getLocation("/wsdl2java_wsdl/hello_world_async.wsdl"));
        processor.setContext(env);
        processor.execute();

        assertNotNull(output, () -> "Add assertion message");

        File org = new File(output, "org");
        assertTrue(org.exists(), () -> "Add assertion message");
        File apache = new File(org, "apache");
        assertTrue(apache.exists(), () -> "Add assertion message");
        File cxf = new File(apache, "cxf");
        assertTrue(cxf.exists(), () -> "Add assertion message");
        File w2j = new File(cxf, "w2j");
        assertTrue(w2j.exists(), () -> "Add assertion message");
        File async = new File(w2j, "hello_world_async_soap_http");
        assertTrue(async.exists(), () -> "Add assertion message");

        File[] files = async.listFiles();
        assertEquals(4, files.length, () -> "Add assertion message");

        Class<?> clz = classLoader.loadClass("org.apache.cxf.w2j.hello_world_async_soap_http.GreeterAsync");

        Method method1 = clz.getMethod("greetMeSometimeAsync", new Class[] {java.lang.String.class,
                                                                            javax.xml.ws.AsyncHandler.class});
        WebMethod webMethodAnno1 = AnnotationUtil.getPrivMethodAnnotation(method1, WebMethod.class);

        assertEquals(method1.getName() + "()" + " Annotation : WebMethod.operationName ", "greetMeSometime",
                     webMethodAnno1.operationName());

        java.lang.reflect.Method method2 = clz.getMethod("greetMeSometimeAsync",
                                                         new Class[] {java.lang.String.class});
        WebMethod webMethodAnno2 = AnnotationUtil.getPrivMethodAnnotation(method2, WebMethod.class);
        assertEquals(method2.getName() + "()" + " Annotation : WebMethod.operationName ", "greetMeSometime",
                     webMethodAnno2.operationName());

    }
    @Test
    public void testAsyncMethodFromCommandLine() throws Exception {
        env.put(ToolConstants.CFG_WSDLURL, getLocation("/wsdl2java_wsdl/hello_world.wsdl"));
        env.put(ToolConstants.CFG_ASYNCMETHODS, new String[0]);
        processor.setContext(env);
        processor.execute();

        assertNotNull(output, () -> "Add assertion message");

        File org = new File(output, "org");
        assertTrue(org.exists(), () -> "Add assertion message");
        File apache = new File(org, "apache");
        assertTrue(apache.exists(), () -> "Add assertion message");
        File cxf = new File(apache, "cxf");
        assertTrue(cxf.exists(), () -> "Add assertion message");
        File w2j = new File(cxf, "w2j");
        assertTrue(w2j.exists(), () -> "Add assertion message");
        File async = new File(w2j, "hello_world_soap_http");
        assertTrue(async.exists(), () -> "Add assertion message");

        File[] files = async.listFiles();
        assertEquals(9, files.length, () -> "Add assertion message");

        Class<?> clz = classLoader.loadClass("org.apache.cxf.w2j.hello_world_soap_http.Greeter");

        Method method1 = clz.getMethod("greetMeSometimeAsync", new Class[] {java.lang.String.class,
                                                                            javax.xml.ws.AsyncHandler.class});
        WebMethod webMethodAnno1 = AnnotationUtil.getPrivMethodAnnotation(method1, WebMethod.class);

        assertEquals(method1.getName() + "()" + " Annotation : WebMethod.operationName ", "greetMeSometime",
                     webMethodAnno1.operationName());

        java.lang.reflect.Method method2 = clz.getMethod("greetMeSometimeAsync",
                                                         new Class[] {java.lang.String.class});
        WebMethod webMethodAnno2 = AnnotationUtil.getPrivMethodAnnotation(method2, WebMethod.class);
        assertEquals(method2.getName() + "()" + " Annotation : WebMethod.operationName ", "greetMeSometime",
                     webMethodAnno2.operationName());

    }

    @Test
    public void testAsyncMethodNoService() throws Exception {
        env.put(ToolConstants.CFG_WSDLURL, getLocation("/wsdl2java_wsdl/hello_world_async_noservice.wsdl"));
        env.remove(ToolConstants.CFG_VALIDATE_WSDL); //no binding so all the tests that check bindings fail
        processor.setContext(env);
        processor.execute();

        assertNotNull(output, () -> "Add assertion message");

        File org = new File(output, "org");
        assertTrue(org.exists(), () -> "Add assertion message");
        File apache = new File(org, "apache");
        assertTrue(apache.exists(), () -> "Add assertion message");
        File cxf = new File(apache, "cxf");
        assertTrue(cxf.exists(), () -> "Add assertion message");
        File w2j = new File(cxf, "w2j");
        assertTrue(w2j.exists(), () -> "Add assertion message");
        File async = new File(w2j, "hello_world_async_soap_http");
        assertTrue(async.exists(), () -> "Add assertion message");

        File[] files = async.listFiles();
        assertEquals(Arrays.asList(files).toString(), 9, files.length);

        Class<?> clz = classLoader.loadClass("org.apache.cxf.w2j.hello_world_async_soap_http.GreeterAsync");

        Method method1 = clz.getMethod("greetMeSometimeAsync", new Class[] {java.lang.String.class,
                                                                            javax.xml.ws.AsyncHandler.class});
        WebMethod webMethodAnno1 = AnnotationUtil.getPrivMethodAnnotation(method1, WebMethod.class);

        assertEquals(method1.getName() + "()" + " Annotation : WebMethod.operationName ", "greetMeSometime",
                     webMethodAnno1.operationName());

        java.lang.reflect.Method method2 = clz.getMethod("greetMeSometimeAsync",
                                                         new Class[] {java.lang.String.class});
        WebMethod webMethodAnno2 = AnnotationUtil.getPrivMethodAnnotation(method2, WebMethod.class);
        assertEquals(method2.getName() + "()" + " Annotation : WebMethod.operationName ", "greetMeSometime",
                     webMethodAnno2.operationName());

        clz.getMethod("greetMeSometimeAsync", new Class[] {java.lang.String.class,
                                                           javax.xml.ws.AsyncHandler.class});
        try {
            clz.getMethod("testIntAsync", new Class[] {Integer.TYPE,
                                                       javax.xml.ws.AsyncHandler.class});
            fail("Should not have generated testIntAsync");
        } catch (NoSuchMethodException ex) {
            // expected
        }


        clz = classLoader.loadClass("org.apache.cxf.w2j.hello_world_async_soap_http.GreeterDAsync");
        clz.getMethod("greetMeSometimeAsync", new Class[] {java.lang.String.class,
                                                           javax.xml.ws.AsyncHandler.class});

        clz = classLoader.loadClass("org.apache.cxf.w2j.hello_world_async_soap_http.GreeterCAsync");
        try {
            clz.getMethod("greetMeSometimeAsync", new Class[] {java.lang.String.class,
                                                               javax.xml.ws.AsyncHandler.class});
            fail("Should not have generated greetMeSometimeAsync");
        } catch (NoSuchMethodException ex) {
            // expected
        }
        clz.getMethod("testIntAsync", new Class[] {Integer.TYPE,
                                                   javax.xml.ws.AsyncHandler.class});

        clz = classLoader.loadClass("org.apache.cxf.w2j.hello_world_async_soap_http.GreeterBAsync");
        try {
            clz.getMethod("greetMeSometimeAsync", new Class[] {java.lang.String.class,
                                                               javax.xml.ws.AsyncHandler.class});
            // Line removed due to Duplicate Assert: fail("Should not have generated greetMeSometimeAsync");
        } catch (NoSuchMethodException ex) {
            // expected
        }
        clz.getMethod("testIntAsync", new Class[] {Integer.TYPE,
                                                   javax.xml.ws.AsyncHandler.class});
    }

    @Test
    public void testHelloWorldSoap12() throws Exception {
        env.put(ToolConstants.CFG_WSDLURL, getLocation("/wsdl2java_wsdl/hello_world_soap12.wsdl"));
        processor.setContext(env);
        processor.execute();

        assertNotNull(output, () -> "Add assertion message");

        File org = new File(output, "org");
        assertTrue(org.exists(), () -> "Add assertion message");
        File apache = new File(org, "apache");
        assertTrue(apache.exists(), () -> "Add assertion message");
        File cxf = new File(apache, "cxf");
        assertTrue(cxf.exists(), () -> "Add assertion message");
        File w2j = new File(cxf, "w2j");
        assertTrue(w2j.exists(), () -> "Add assertion message");
        File helloworldsoaphttp = new File(w2j, "hello_world_soap12_http");
        assertTrue(helloworldsoaphttp.exists(), () -> "Add assertion message");
        File types = new File(helloworldsoaphttp, "types");
        assertTrue(types.exists(), () -> "Add assertion message");
        File[] files = helloworldsoaphttp.listFiles();
        assertEquals(5, files.length, () -> "Add assertion message");
        files = types.listFiles();
        assertEquals(7, files.length, () -> "Add assertion message");

        Class<?> clz = classLoader.loadClass("org.apache.cxf.w2j.hello_world_soap12_http.Greeter");
        assertTrue("class " + clz.getName() + " modifier is not public", Modifier
            .isPublic(clz.getModifiers()));
        assertTrue("class " + clz.getName() + " modifier is interface", Modifier.isInterface(clz
            .getModifiers()));

        WebService webServiceAnn = AnnotationUtil.getPrivClassAnnotation(clz, WebService.class);
        assertEquals("Greeter", webServiceAnn.name(), () -> "Add assertion message");

        Method method = clz.getMethod("sayHi", new Class[] {});
        WebMethod webMethodAnno = AnnotationUtil.getPrivMethodAnnotation(method, WebMethod.class);
        if (webMethodAnno.operationName() != null
            && !"".equals(webMethodAnno.operationName())) {
            assertEquals(method.getName() + "()" + " Annotation : WebMethod.operationName ", "sayHi",
                     webMethodAnno.operationName());
        }

        RequestWrapper requestWrapperAnn = AnnotationUtil.getPrivMethodAnnotation(method,
                                                                                  RequestWrapper.class);

        assertEquals("org.apache.cxf.w2j.hello_world_soap12_http.types.SayHi",
                     requestWrapperAnn.className(), () -> "Add assertion message");

        ResponseWrapper resposneWrapperAnn = AnnotationUtil.getPrivMethodAnnotation(method,
                                                                                    ResponseWrapper.class);

        assertEquals("sayHiResponse", resposneWrapperAnn.localName(), () -> "Add assertion message");

        WebResult webResultAnno = AnnotationUtil.getPrivMethodAnnotation(method, WebResult.class);

        assertEquals("responseType", webResultAnno.name(), () -> "Add assertion message");

        method = clz.getMethod("pingMe", new Class[] {});
        webMethodAnno = AnnotationUtil.getPrivMethodAnnotation(method, WebMethod.class);
        if (webMethodAnno.operationName() != null
            && !"".equals(webMethodAnno.operationName())) {
            assertEquals(method.getName() + "()" + " Annotation : WebMethod.operationName ", "pingMe",
                     webMethodAnno.operationName());
        }
        Class<?>[] exceptionCls = method.getExceptionTypes();
        assertEquals(1, exceptionCls.length, () -> "Add assertion message");
        assertEquals("org.apache.cxf.w2j.hello_world_soap12_http.PingMeFault",
                     exceptionCls[0].getName(), () -> "Add assertion message");
    }

    @Test
    public void testHelloWorld() throws Exception {
        env.put(ToolConstants.CFG_WSDLURL, getLocation("/wsdl2java_wsdl/hello_world.wsdl"));
        processor.setContext(env);
        processor.execute();

        assertNotNull(output, () -> "Add assertion message");

        File org = new File(output, "org");
        assertTrue(org.exists(), () -> "Add assertion message");
        File apache = new File(org, "apache");
        assertTrue(apache.exists(), () -> "Add assertion message");
        File cxf = new File(apache, "cxf");
        assertTrue(cxf.exists(), () -> "Add assertion message");
        File w2j = new File(cxf, "w2j");
        assertTrue(w2j.exists(), () -> "Add assertion message");
        File helloworldsoaphttp = new File(w2j, "hello_world_soap_http");
        assertTrue(helloworldsoaphttp.exists(), () -> "Add assertion message");
        File types = new File(helloworldsoaphttp, "types");
        assertTrue(types.exists(), () -> "Add assertion message");
        File[] files = helloworldsoaphttp.listFiles();
        assertEquals(9, files.length, () -> "Add assertion message");
        files = types.listFiles();
        assertEquals(17, files.length, () -> "Add assertion message");

        Class<?> clz = classLoader.loadClass("org.apache.cxf.w2j.hello_world_soap_http.Greeter");
        assertTrue("class " + clz.getName() + " modifier is not public", Modifier
            .isPublic(clz.getModifiers()));
        assertTrue("class " + clz.getName() + " modifier is interface", Modifier.isInterface(clz
            .getModifiers()));

        WebService webServiceAnn = AnnotationUtil.getPrivClassAnnotation(clz, WebService.class);
        assertEquals("Greeter", webServiceAnn.name(), () -> "Add assertion message");

        Method method = clz.getMethod("sayHi", new Class[] {});
        WebMethod webMethodAnno = AnnotationUtil.getPrivMethodAnnotation(method, WebMethod.class);
        if (webMethodAnno.operationName() != null
            && !"".equals(webMethodAnno.operationName())) {
            assertEquals(method.getName() + "()" + " Annotation : WebMethod.operationName ", "sayHi",
                     webMethodAnno.operationName());
        }

        RequestWrapper requestWrapperAnn = AnnotationUtil.getPrivMethodAnnotation(method,
                                                                                  RequestWrapper.class);

        assertEquals("org.apache.cxf.w2j.hello_world_soap_http.types.SayHi", requestWrapperAnn.className(), () -> "Add assertion message");

        ResponseWrapper resposneWrapperAnn = AnnotationUtil.getPrivMethodAnnotation(method,
                                                                                    ResponseWrapper.class);

        assertEquals("sayHiResponse", resposneWrapperAnn.localName(), () -> "Add assertion message");

        WebResult webResultAnno = AnnotationUtil.getPrivMethodAnnotation(method, WebResult.class);

        assertEquals("responseType", webResultAnno.name(), () -> "Add assertion message");

        method = clz.getMethod("greetMe", new Class[] {String.class});
        assertEquals("String", method.getReturnType().getSimpleName(), () -> "Add assertion message");
        WebParam webParamAnn = AnnotationUtil.getWebParam(method, "requestType");
        //if is wrapped, tns should be empty
        assertEquals("http://cxf.apache.org/w2j/hello_world_soap_http/types", webParamAnn.targetNamespace(), () -> "Add assertion message");
        //assertEquals("", webParamAnn.targetNamespace());
        method = clz.getMethod("greetMeOneWay", new Class[] {String.class});
        Oneway oneWayAnn = AnnotationUtil.getPrivMethodAnnotation(method, Oneway.class);
        assertNotNull("OneWay Annotation is not generated", oneWayAnn);
        assertEquals("void", method.getReturnType().getSimpleName(), () -> "Add assertion message");

        method = clz.getMethod("greetMeSometime", new Class[] {String.class});
        // Line removed due to Duplicate Assert: assertEquals("String", method.getReturnType().getSimpleName(), () -> "Add assertion message");

        method = clz.getMethod("testDocLitFault", new Class[] {java.lang.String.class});
        // Line removed due to Duplicate Assert: assertEquals("void", method.getReturnType().getSimpleName(), () -> "Add assertion message");
        assertEquals("Exception class is not generated ", 2, method.getExceptionTypes().length);

        method = clz.getMethod("testDocLitBare", new Class[] {java.lang.String.class});
        webResultAnno = AnnotationUtil.getPrivMethodAnnotation(method, WebResult.class);
        assertEquals("out", webResultAnno.partName(), () -> "Add assertion message");
        SOAPBinding soapBindingAnno = AnnotationUtil.getPrivMethodAnnotation(method, SOAPBinding.class);
        assertNotNull(soapBindingAnno, () -> "Add assertion message");
        assertEquals(SOAPBinding.ParameterStyle.BARE, soapBindingAnno.parameterStyle(), () -> "Add assertion message");
        assertEquals("BareDocumentResponse", method.getReturnType().getSimpleName(), () -> "Add assertion message");

    }


    @Test
    public void testHelloWorldWithDummyPlugin() throws Exception {
        env.put(ToolConstants.CFG_WSDLURL, getLocation("/wsdl2java_wsdl/hello_world.wsdl"));

        // verify passing space seperated xjc args direct to xjc will load,
        // configure and invoke an xjc plugin
        env.put(ToolConstants.CFG_XJC_ARGS, "-" + DummyXjcPlugin.XDUMMY_XJC_PLUGIN
                + ",-" + DummyXjcPlugin.XDUMMY_XJC_PLUGIN  + ":arg");
        processor.setContext(env);
        processor.execute();

        assertNotNull(output, () -> "Add assertion message");

        File org = new File(output, "org");
        assertTrue(org.exists(), () -> "Add assertion message");
        File apache = new File(org, "apache");
        assertTrue(apache.exists(), () -> "Add assertion message");
        File cxf = new File(apache, "cxf");
        assertTrue(cxf.exists(), () -> "Add assertion message");
        File w2j = new File(cxf, "w2j");
        assertTrue(w2j.exists(), () -> "Add assertion message");

        File helloworldsoaphttp = new File(w2j, "hello_world_soap_http");
        assertTrue(helloworldsoaphttp.exists(), () -> "Add assertion message");
        File types = new File(helloworldsoaphttp, "types");
        assertTrue(types.exists(), () -> "Add assertion message");
        File[] files = helloworldsoaphttp.listFiles();
        assertEquals(9, files.length, () -> "Add assertion message");
        files = types.listFiles();
        assertEquals(17, files.length, () -> "Add assertion message");

        Class<?> clz = classLoader.loadClass("org.apache.cxf.w2j.hello_world_soap_http.types.SayHi");
        Method method = clz.getMethod("dummy", new Class[] {});
        assertEquals("method declared on SayHi", method.getDeclaringClass(), clz);
    }

    @Test
    public void testInvalidXjcArgDummyPluginUsage() throws Exception {
        env.put(ToolConstants.CFG_WSDLURL, getLocation("/wsdl2java_wsdl/hello_world.wsdl"));

        env.put(ToolConstants.CFG_XJC_ARGS, "-" + DummyXjcPlugin.XDUMMY_XJC_PLUGIN
                + ",-" + DummyXjcPlugin.XDUMMY_XJC_PLUGIN  + ":some_rubbish_argument");
        processor.setContext(env);
        String msg = null;
        try {
            processor.execute();
            fail("Expect a ToolException on invalid xjc argument");
        } catch (ToolException expected) {
            msg = expected.getMessage();
        }
        assertNotNull(msg, () -> "Add assertion message");
        assertTrue(":some_rubbish_argument is present in :" + msg,
                   msg.indexOf(":some_rubbish_argument") != -1);
        assertTrue("Dummy plugin usage string present in :" + msg,
                   msg.indexOf(DummyXjcPlugin.DUMMY_ARG) != -1);
    }

    @Test
    public void testXjcMinusXArgGivesPluginUsage() throws Exception {
        env.put(ToolConstants.CFG_WSDLURL, getLocation("/wsdl2java_wsdl/hello_world.wsdl"));

        env.put(ToolConstants.CFG_XJC_ARGS, "-X");
        processor.setContext(env);
        String msg = null;
        try {
            processor.execute();
            fail("Expect a ToolException on invalid xjc argument");
        } catch (ToolException expected) {
            msg = expected.getMessage();
        }
        assertNotNull(msg, () -> "Add assertion message");
        assertTrue("Dummy plugin usage string present in :" + msg,
                   msg.indexOf(DummyXjcPlugin.DUMMY_ARG) != -1);
        assertTrue("No BadParameter in msg:" + msg,
                   msg.indexOf("Bad") == -1);
    }

    @Test
    public void testDocLitHolder() throws Exception {
        env.put(ToolConstants.CFG_WSDLURL, getLocation("/wsdl2java_wsdl/mapping-doc-literal.wsdl"));
        processor.setContext(env);
        processor.execute();
        assertNotNull(output, () -> "Add assertion message");
        File org = new File(output, "org");
        assertTrue(org.exists(), () -> "Add assertion message");
        File apache = new File(org, "apache");
        assertTrue(apache.exists(), () -> "Add assertion message");
        File mapping = new File(apache, "mapping");
        assertTrue(mapping.exists(), () -> "Add assertion message");
        File[] files = mapping.listFiles();
        assertEquals(9, files.length, () -> "Add assertion message");
        Class<?> clz = classLoader.loadClass("org.apache.mapping.SomethingServer");
        Method method = clz.getMethod("doSomething", new Class[] {int.class, javax.xml.ws.Holder.class,
                                                                  javax.xml.ws.Holder.class});
        assertEquals("boolean", method.getReturnType().getSimpleName(), () -> "Add assertion message");
        WebParam webParamAnno = AnnotationUtil.getWebParam(method, "y");
        assertEquals("INOUT", webParamAnno.mode().name(), () -> "Add assertion message");
        webParamAnno = AnnotationUtil.getWebParam(method, "z");
        assertEquals("OUT", webParamAnno.mode().name(), () -> "Add assertion message");
    }

    @Test
    public void testSchemaImport() throws Exception {
        env.put(ToolConstants.CFG_WSDLURL, getLocation("/wsdl2java_wsdl/hello_world_schema_import.wsdl"));
        processor.setContext(env);
        processor.execute();

        assertNotNull(output, () -> "Add assertion message");

        File org = new File(output, "org");
        assertTrue(org.exists(), () -> "Add assertion message");
        File apache = new File(org, "apache");
        assertTrue(apache.exists(), () -> "Add assertion message");
        File cxf = new File(apache, "cxf");
        assertTrue(cxf.exists(), () -> "Add assertion message");
        File w2j = new File(cxf, "w2j");
        assertTrue(w2j.exists(), () -> "Add assertion message");
        File helloworldsoaphttp = new File(w2j, "hello_world_soap_http");
        assertTrue(helloworldsoaphttp.exists(), () -> "Add assertion message");
        File types = new File(helloworldsoaphttp, "types");
        assertTrue(types.exists(), () -> "Add assertion message");
        File[] files = helloworldsoaphttp.listFiles();
        assertEquals(1, files.length, () -> "Add assertion message");
        files = types.listFiles();
        assertEquals(files.length, 10, () -> "Add assertion message");
        File schemaImport = new File(apache, "schema_import");
        assertTrue(schemaImport.exists(), () -> "Add assertion message");
        files = schemaImport.listFiles();
        assertEquals(4, files.length, () -> "Add assertion message");

        Class<?> clz = classLoader.loadClass("org.apache.schema_import.Greeter");
        assertEquals(4, clz.getMethods().length, () -> "Add assertion message");

        Method method = clz.getMethod("pingMe", new Class[] {});
        assertEquals("void", method.getReturnType().getSimpleName(), () -> "Add assertion message");
        assertEquals("Exception class is not generated ", 1, method.getExceptionTypes().length);
    }

    @Test
    public void testExceptionNameCollision() throws Exception {
        env.put(ToolConstants.CFG_WSDLURL, getLocation("/wsdl2java_wsdl/InvoiceServer.wsdl"));

        processor.setContext(env);
        processor.execute();

        assertNotNull(output, () -> "Add assertion message");

        File org = new File(output, "org");
        assertTrue(org.exists(), () -> "Add assertion message");
        File apache = new File(org, "apache");
        assertTrue(apache.exists(), () -> "Add assertion message");
        File invoiceserver = new File(apache, "invoiceserver");
        assertTrue(invoiceserver.exists(), () -> "Add assertion message");
        File invoice = new File(apache, "invoice");
        assertTrue(invoice.exists(), () -> "Add assertion message");

        File exceptionCollision = new File(invoiceserver, "NoSuchCustomerFault_Exception.java");
        assertTrue(exceptionCollision.exists(), () -> "Add assertion message");

        File[] files = invoiceserver.listFiles();
        assertEquals(13, files.length, () -> "Add assertion message");
        files = invoice.listFiles();
        assertEquals(files.length, 9, () -> "Add assertion message");

        Class<?> clz = classLoader.loadClass("org.apache.invoiceserver.InvoiceServer");
        assertEquals(3, clz.getMethods().length, () -> "Add assertion message");

        Method method = clz.getMethod("getInvoicesForCustomer", new Class[] {String.class, String.class});
        assertEquals("NoSuchCustomerFault_Exception", method.getExceptionTypes()[0].getSimpleName(), () -> "Add assertion message");

    }

    @Test
    public void testAllNameCollision() throws Exception {
        env.put(ToolConstants.CFG_WSDLURL, getLocation("/wsdl2java_wsdl/hello_world_collision.wsdl"));
        env.put(ToolConstants.CFG_PACKAGENAME, "org.apache");
        processor.setContext(env);
        processor.execute();

        assertNotNull(output, () -> "Add assertion message");

        File org = new File(output, "org");
        assertTrue(org.exists(), () -> "Add assertion message");
        File apache = new File(org, "apache");
        assertTrue(apache.exists(), () -> "Add assertion message");

        File[] files = apache.listFiles();
        assertEquals(14, files.length, () -> "Add assertion message");

        File typeCollision = new File(apache, "Greeter_Type.java");
        assertTrue(typeCollision.exists(), () -> "Add assertion message");
        File exceptionCollision = new File(apache, "Greeter_Exception.java");
        assertTrue(exceptionCollision.exists(), () -> "Add assertion message");
        File serviceCollision = new File(apache, "Greeter_Service.java");
        assertTrue(serviceCollision.exists(), () -> "Add assertion message");

        Class<?> clz = classLoader.loadClass("org.apache.Greeter");
        assertTrue("SEI class Greeter modifier should be interface", clz.isInterface());

        classLoader.loadClass("org.apache.Greeter_Exception");
        classLoader.loadClass("org.apache.Greeter_Service");
    }

    @Test
    public void testImportNameCollision() throws Exception {
        env.put(ToolConstants.CFG_WSDLURL,
                getLocation("/wsdl2java_wsdl/helloworld-portname_servicename.wsdl"));
        env.setPackageName("org.apache");
        processor.setContext(env);
        processor.execute();

        assertNotNull(output, () -> "Add assertion message");

        File org = new File(output, "org");
        assertTrue(org.exists(), () -> "Add assertion message");
        File apache = new File(org, "apache");
        assertTrue(apache.exists(), () -> "Add assertion message");

        File[] files = apache.listFiles();
        assertEquals(4, files.length, () -> "Add assertion message");

        File serviceCollision = new File(apache, "HelloWorldServiceImpl_Service.java");
        assertTrue(serviceCollision.exists(), () -> "Add assertion message");

        Class<?> clz = classLoader.loadClass("org.apache.HelloWorldServiceImpl");
        assertTrue("SEI class HelloWorldServiceImpl modifier should be interface", clz.isInterface());

        classLoader.loadClass("org.apache.HelloWorldServiceImpl_Service");
    }

    @Test
    public void testSoapHeader() throws Exception {
        env.put(ToolConstants.CFG_WSDLURL, getLocation("/wsdl2java_wsdl/soap_header.wsdl"));
        env.put(ToolConstants.CFG_PACKAGENAME, "org.apache");
        processor.setContext(env);
        processor.execute();

        assertNotNull(output, () -> "Add assertion message");

        File org = new File(output, "org");
        assertTrue(org.exists(), () -> "Add assertion message");
        File apache = new File(org, "apache");
        assertTrue(apache.exists(), () -> "Add assertion message");

        File[] files = apache.listFiles();
        assertEquals(12, files.length, () -> "Add assertion message");

        Class<?> clz = classLoader.loadClass("org.apache.HeaderTester");
        assertEquals(3, clz.getMethods().length, () -> "Add assertion message");

        SOAPBinding soapBindingAnno = AnnotationUtil.getPrivClassAnnotation(clz, SOAPBinding.class);
        assertEquals("BARE", soapBindingAnno.parameterStyle().name(), () -> "Add assertion message");
        assertEquals("LITERAL", soapBindingAnno.use().name(), () -> "Add assertion message");
        assertEquals("DOCUMENT", soapBindingAnno.style().name(), () -> "Add assertion message");

        Class<?> para = classLoader.loadClass("org.apache.InoutHeader");

        Method method = clz.getMethod("inoutHeader", new Class[] {para, Holder.class});

        //the SOAPBinding annotation on the class sets it to bare, thus, this annotation may
        //not be generated as it would be redundant
        //soapBindingAnno = AnnotationUtil.getPrivMethodAnnotation(method, SOAPBinding.class);
        //assertNotNull(soapBindingAnno);
        //assertEquals(SOAPBinding.ParameterStyle.BARE, soapBindingAnno.parameterStyle());

        WebParam webParamAnno = AnnotationUtil.getWebParam(method, "SOAPHeaderInfo");
        assertEquals("INOUT", webParamAnno.mode().name(), () -> "Add assertion message");
        assertTrue(webParamAnno.header(), () -> "Add assertion message");
        assertEquals("header_info", webParamAnno.partName(), () -> "Add assertion message");

    }

    @Test
    public void testHolderHeader() throws Exception {
        env.put(ToolConstants.CFG_WSDLURL, getLocation("/wsdl2java_wsdl/hello_world_holder.wsdl"));
        env.remove(ToolConstants.CFG_VALIDATE_WSDL); // testing multiple parts in body
        processor.setContext(env);
        processor.execute();

        Class<?> clz = classLoader.loadClass("org.apache.cxf.w2j.hello_world_holder.Greeter");
        assertEquals(2, clz.getMethods().length, () -> "Add assertion message");

        Class<?> para = classLoader.loadClass("org.apache.cxf.w2j.hello_world_holder.types.GreetMe");
        Method method = clz.getMethod("sayHi", new Class[] {Holder.class, para});
        assertEquals("GreetMeResponse", method.getReturnType().getSimpleName(), () -> "Add assertion message");

        SOAPBinding soapBindingAnno = AnnotationUtil.getPrivClassAnnotation(clz, SOAPBinding.class);
        if (soapBindingAnno == null) {
            soapBindingAnno = method.getAnnotation(SOAPBinding.class);
        }
        assertNotNull(soapBindingAnno, () -> "Add assertion message");
        assertEquals("BARE", soapBindingAnno.parameterStyle().name(), () -> "Add assertion message");
        assertEquals("LITERAL", soapBindingAnno.use().name(), () -> "Add assertion message");
        assertEquals("DOCUMENT", soapBindingAnno.style().name(), () -> "Add assertion message");


        WebParam webParamAnno = AnnotationUtil.getWebParam(method, "greetMe");
        assertTrue(webParamAnno.header(), () -> "Add assertion message");

        webParamAnno = AnnotationUtil.getWebParam(method, "sayHi");
        assertEquals("INOUT", webParamAnno.mode().name(), () -> "Add assertion message");

        clz.getMethod("testInOut", Holder.class, Integer.TYPE);
    }

    @Test
    public void testWSAddress() throws Exception {
        env.addNamespacePackageMap("http://cxf.apache.org/w2j/hello_world_soap_http", "ws.address");
        env.put(ToolConstants.CFG_BINDING, getLocation("/wsdl2java_wsdl/ws_address_binding.wsdl"));
        env.put(ToolConstants.CFG_WSDLURL, getLocation("/wsdl2java_wsdl/hello_world_addr.wsdl"));

        processor.setContext(env);
        processor.execute();

        assertNotNull(output, () -> "Add assertion message");

        File ws = new File(output, "ws");
        assertTrue(ws.exists(), () -> "Add assertion message");
        File address = new File(ws, "address");
        assertTrue(address.exists(), () -> "Add assertion message");

        File[] files = address.listFiles();
        assertEquals(Arrays.asList(address.listFiles()).toString(), 6, files.length);
        File handlerConfig = new File(address, "Greeter_handler.xml");
        assertTrue(handlerConfig.exists(), () -> "Add assertion message");

        Class<?> clz = classLoader.loadClass("ws.address.Greeter");
        HandlerChain handlerChainAnno = AnnotationUtil.getPrivClassAnnotation(clz, HandlerChain.class);
        assertEquals("Greeter_handler.xml", handlerChainAnno.file(), () -> "Add assertion message");
        assertNotNull("Handler chain xml generate fail!", classLoader
            .getResource("ws/address/Greeter_handler.xml"));
    }

    @Test
    public void testVoidInOutMethod() throws Exception {
        env.put(ToolConstants.CFG_WSDLURL, getLocation("/wsdl2java_wsdl/interoptestdoclit.wsdl"));
        env.remove(ToolConstants.CFG_VALIDATE_WSDL);
        processor.setContext(env);
        processor.execute();

        assertNotNull(output, () -> "Add assertion message");

        File org = new File(output, "org");
        assertTrue(org.exists(), () -> "Add assertion message");
        File soapinterop = new File(org, "soapinterop");
        assertTrue(soapinterop.exists(), () -> "Add assertion message");
        File wsdlinterop = new File(soapinterop, "wsdlinteroptestdoclit");
        assertTrue(wsdlinterop.exists(), () -> "Add assertion message");
        File xsd = new File(soapinterop, "xsd");
        assertTrue(xsd.exists(), () -> "Add assertion message");
        File[] files = wsdlinterop.listFiles();
        assertEquals(3, files.length, () -> "Add assertion message");
        files = xsd.listFiles();
        assertEquals(4, files.length, () -> "Add assertion message");

        Class<?> clz = classLoader
            .loadClass("org.soapinterop.wsdlinteroptestdoclit.WSDLInteropTestDocLitPortType");

        Method method = clz.getMethod("echoVoid", new Class[] {});
        WebMethod webMethodAnno = AnnotationUtil.getPrivMethodAnnotation(method, WebMethod.class);
        if (webMethodAnno.operationName() != null
            && !"".equals(webMethodAnno.operationName())) {
            assertEquals(method.getName() + "()" + " Annotation : WebMethod.operationName ", "echoVoid",
                     webMethodAnno.operationName());
        }
    }

    @Test
    public void testWsdlImport() throws Exception {
        env.put(ToolConstants.CFG_WSDLURL, getLocation("/wsdl2java_wsdl/hello_world_wsdl_import.wsdl"));
        processor.setContext(env);
        processor.execute();

        assertNotNull(output, () -> "Add assertion message");

        File org = new File(output, "org");
        assertTrue(org.exists(), () -> "Add assertion message");

        File apache = new File(org, "apache");
        assertTrue(apache.exists(), () -> "Add assertion message");
        File cxf = new File(apache, "cxf");
        assertTrue(cxf.exists(), () -> "Add assertion message");
        File w2j = new File(cxf, "w2j");
        assertTrue(w2j.exists(), () -> "Add assertion message");

        File helloWorld = new File(w2j, "hello_world");
        assertTrue(helloWorld.exists(), () -> "Add assertion message");

        Class<?> clz = classLoader.loadClass("org.apache.cxf.w2j.hello_world.Greeter");
        assertEquals(3, clz.getMethods().length, () -> "Add assertion message");

        Method method = clz.getMethod("pingMe", new Class[] {});
        assertEquals("void", method.getReturnType().getSimpleName(), () -> "Add assertion message");
        assertEquals("Exception class is not generated ", 1, method.getExceptionTypes().length);
        assertEquals("org.apache.cxf.w2j.hello_world.messages.PingMeFault",
                     method.getExceptionTypes()[0].getCanonicalName(), () -> "Add assertion message");
    }

    @Test
    public void testWebFault() throws Exception {
        env.put(ToolConstants.CFG_WSDLURL, getLocation("/wsdl2java_wsdl/InvoiceServer-issue305570.wsdl"));
        processor.setContext(env);
        processor.execute();

        assertNotNull(output, () -> "Add assertion message");

        File org = new File(output, "org");
        assertTrue(org.exists(), () -> "Add assertion message");
        File apache = new File(org, "apache");
        assertTrue(apache.exists(), () -> "Add assertion message");
        File invoiceserver = new File(apache, "invoiceserver");
        assertTrue(invoiceserver.exists(), () -> "Add assertion message");
        File invoice = new File(apache, "invoice");
        assertTrue(invoice.exists(), () -> "Add assertion message");

        Class<?> clz = classLoader.loadClass("org.apache.invoiceserver.NoSuchCustomerFault");
        WebFault webFault = AnnotationUtil.getPrivClassAnnotation(clz, WebFault.class);
        assertEquals("WebFault annotaion name attribute error", "NoSuchCustomer", webFault.name());

    }

    @Test
    public void testMultiSchemaParsing() throws Exception {
        env.put(ToolConstants.CFG_WSDLURL, getLocation("/wsdl2java_wsdl/multi_schema.wsdl"));

        processor.setContext(env);
        processor.execute();

        assertNotNull(output, () -> "Add assertion message");
        File org = new File(output, "org");
        assertTrue(org.exists(), () -> "Add assertion message");
        File tempuri = new File(org, "tempuri");
        assertTrue(tempuri.exists(), () -> "Add assertion message");
        File header = new File(tempuri, "header");
        assertTrue(header.exists(), () -> "Add assertion message");

        File[] files = header.listFiles();
        assertEquals(3, files.length, () -> "Add assertion message");
    }

    @Test
    public void testDefaultParameterOrder() throws Exception {
        env.put(ToolConstants.CFG_WSDLURL, getLocation("/wsdl2java_wsdl/bug161/header2.wsdl"));
        processor.setContext(env);
        processor.execute();
        Class<?> clz = classLoader.loadClass("org.apache.header2.Header2Test");
        Class<?> header = classLoader.loadClass("org.apache.header2.Header");
        Method method = clz.getMethod("headerMethod", new Class[] {Holder.class, header});
        assertNotNull("method should be generated", method);
    }

    @Test
    public void testSupportXMLBindingBare() throws Exception {
        env.put(ToolConstants.CFG_WSDLURL, getLocation("/wsdl2java_wsdl/xml_http_bare.wsdl"));
        processor.setContext(env);
        processor.execute();

        Class<?> clz = classLoader.loadClass("org.apache.xml_http_bare.GreetingPortType");

        Method method = clz.getMethod("sayHello", new Class[] {java.lang.String.class});
        assertNotNull("sayHello is not be generated", method);

        SOAPBinding soapBindingAnn = clz.getAnnotation(SOAPBinding.class);
        assertEquals(soapBindingAnn.parameterStyle(), SOAPBinding.ParameterStyle.BARE, () -> "Add assertion message");

    }

    @Test
    public void testSupportXMLBindingWrapped() throws Exception {
        env.put(ToolConstants.CFG_WSDLURL, getLocation("/wsdl2java_wsdl/xml_http_wrapped.wsdl"));
        processor.setContext(env);
        processor.execute();
        Class<?> clz = classLoader.loadClass("org.apache.xml_http_wrapped.GreetingPortType");

        Method method = clz.getMethod("sayHello", new Class[] {java.lang.String.class});
        assertNotNull("sayHello is not be generated", method);

        javax.xml.ws.RequestWrapper reqAnno = method.getAnnotation(javax.xml.ws.RequestWrapper.class);
        assertNotNull("WrapperBean Annotation could not be found", reqAnno);
    }

    @Test
    public void testRouterWSDL() throws Exception {
        env.put(ToolConstants.CFG_WSDLURL, getLocation("/wsdl2java_wsdl/router.wsdl"));
        processor.setContext(env);
        processor.execute();

        Class<?> clz = classLoader.loadClass("org.apache.cxf.w2j.hello_world_doc_lit.Greeter");

        Method method = clz.getMethod("greetMe", new Class[] {java.lang.String.class});
        assertNotNull("greetMe is not be generated", method);

        javax.xml.ws.RequestWrapper reqAnno = method.getAnnotation(javax.xml.ws.RequestWrapper.class);
        assertNotNull("WrapperBean Annotation could not be found", reqAnno);

        clz = classLoader.loadClass("org.apache.hwrouter.HTTPSoapServiceDestination");
        assertNotNull("HTTPSoapServiceDestination is not be generated", clz);

    }

    @Test
    public void testWSDLContainsJavaKeywords() throws Exception {

        env.put(ToolConstants.CFG_WSDLURL,
                getLocation("/wsdl2java_wsdl/hello_world_with_keywords_operation.wsdl"));

        processor.setContext(env);
        processor.execute();

        Class<?> clz = classLoader.loadClass("org.apache.cxf.w2j.hello_world_soap_http.Greeter");
        Class<?> sayHi = classLoader.loadClass("org.apache.cxf.w2j.hello_world_soap_http.types.SayHi");
        Method method = clz.getMethod("_do", new Class[] {sayHi});
        assertNotNull("method which name contains java keywords is not be generated", method);

    }

    @Test
    public void testInvalidMepOperation() throws Exception {
        try {
            env.put(ToolConstants.CFG_WSDLURL, getLocation("/wsdl2java_wsdl/invalid_mep.wsdl"));
            processor.setContext(env);
            processor.execute();
            fail("Failure expected");
        } catch (Exception e) {
            String expectedMsg = "Invalid WSDL, Operation notify in PortType "
                + "{http://services.medial.com.br/echo}EchoNotification not request-response or one-way";
            assertTrue("Invalid wsdl should be diagnosed", e.getMessage()
                .indexOf(expectedMsg) > -1);
        }
    }

    @Test
    public void testWSDLWithEnumType() throws Exception {
        env.put(ToolConstants.CFG_WSDLURL, getLocation("/wsdl2java_wsdl/hello_world_with_enum_type.wsdl"));
        processor.setContext(env);
        processor.execute();
        Class<?> clz = classLoader.loadClass("org.apache.cxf.w2j.hello_world_soap_http.types.ActionType");
        assertNotNull("Enum class could not be found", clz);
    }

    @Test
    public void testSWAMime() throws Exception {

        env.put(ToolConstants.CFG_WSDLURL, getLocation("/wsdl2java_wsdl/swa-mime.wsdl"));
        env.put(ToolConstants.CFG_BINDING, getLocation("/wsdl2java_wsdl/swa-mime-binding.xml"));
        env.remove(ToolConstants.CFG_VALIDATE_WSDL); // swa-mime puts multple parts in body
        processor.setContext(env);
        processor.execute();
        Class<?> clz = classLoader.loadClass("org.apache.cxf.swa.SwAServiceInterface");

        Method method1 = clz.getMethod("echoData", new Class[] {javax.xml.ws.Holder.class,
                                                                javax.xml.ws.Holder.class});

        assertNotNull("method echoData can not be found", method1);

        Type[] types = method1.getGenericParameterTypes();
        ParameterizedType paraType = (ParameterizedType)types[1];
        Class<?> typeClass = (Class<?>)paraType.getActualTypeArguments()[0];
        assertEquals("javax.activation.DataHandler", typeClass.getName(), () -> "Add assertion message");
    }

    @Test
    public void testRPCHeader() throws Exception {
        env.put(ToolConstants.CFG_WSDLURL, getLocation("/wsdl2java_wsdl/soapheader_rpc.wsdl"));
        env.remove(ToolConstants.CFG_VALIDATE_WSDL); // headers must be element, but validation
                       // doesn't fully process the entire soap:binding information to
                       // to figure out which parts are headers and which are body for rpc/lit
        processor.setContext(env);
        processor.execute();
        Class<?> cls = classLoader.loadClass("org.apache.header_test.rpc.TestRPCHeaderPort");

        Method[] meths = cls.getMethods();
        for (Method m : meths) {
            if ("testHeader1".equals(m.getName())) {
                Annotation[][] annotations = m.getParameterAnnotations();
                Integer magicNumber86950170 = 2; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
                assertEquals(magicNumber86950170, annotations.length() -> "Add assertion message");
                assertEquals(1, annotations[1].length, () -> "Add assertion message");
                assertTrue(annotations[1][0] instanceof WebParam, () -> "Add assertion message");
                WebParam parm = (WebParam)annotations[1][0];
                assertEquals("http://apache.org/header_test/rpc/types", parm.targetNamespace(), () -> "Add assertion message");
                assertEquals("inHeader", parm.partName(), () -> "Add assertion message");
                assertEquals("headerMessage", parm.name(), () -> "Add assertion message");
                assertTrue(parm.header(), () -> "Add assertion message");
            }
        }

        for (Method m : meths) {
            if ("testInOutHeader".equals(m.getName())) {
                Annotation[][] annotations = m.getParameterAnnotations();
                // Line removed due to Duplicate Assert: assertEquals(2, annotations.length, () -> "Add assertion message");
                // Line removed due to Duplicate Assert: assertEquals(1, annotations[1].length, () -> "Add assertion message");
                // Line removed due to Duplicate Assert: assertTrue(annotations[1][0] instanceof WebParam, () -> "Add assertion message");
                WebParam parm = (WebParam)annotations[1][0];
                // Line removed due to Duplicate Assert: assertEquals("http://apache.org/header_test/rpc/types", parm.targetNamespace(), () -> "Add assertion message");
                assertEquals("inOutHeader", parm.partName(), () -> "Add assertion message");
                // Line removed due to Duplicate Assert: assertEquals("headerMessage", parm.name(), () -> "Add assertion message");
                // Line removed due to Duplicate Assert: assertTrue(parm.header(), () -> "Add assertion message");
            }
        }
    }

    @Test
    public void testRefTNS() throws Exception {
        env.put(ToolConstants.CFG_WSDLURL, getLocation("/wsdl2java_wsdl/locator.wsdl"));
        processor.setContext(env);
        processor.execute();

        assertNotNull(output, () -> "Add assertion message");

        File org = new File(output, "org");
        assertTrue(org.exists(), () -> "Add assertion message");
        File apache = new File(org, "apache");
        assertTrue(apache.exists(), () -> "Add assertion message");
        File locator = new File(apache, "locator");
        assertTrue(locator.exists(), () -> "Add assertion message");
        File locatorService = new File(locator, "LocatorService.java");
        assertTrue(locatorService.exists(), () -> "Add assertion message");


        Class<?> clz = classLoader.loadClass("org.apache.locator.LocatorService");

        Class<?> paraClass = classLoader.loadClass("org.apache.locator.types.QueryEndpoints");
        Method method = clz.getMethod("queryEndpoints", new Class[] {paraClass});
        WebResult webRes = AnnotationUtil.getPrivMethodAnnotation(method, WebResult.class);
        assertEquals("http://apache.org/locator/types", webRes.targetNamespace(), () -> "Add assertion message");
        assertEquals("queryEndpointsResponse", webRes.name(), () -> "Add assertion message");
        WebParam webParamAnn = AnnotationUtil.getWebParam(method, "queryEndpoints");
        assertEquals("http://apache.org/locator/types", webParamAnn.targetNamespace(), () -> "Add assertion message");

        method = clz.getMethod("deregisterPeerManager", new Class[] {String.class});
        webParamAnn = AnnotationUtil.getWebParam(method, "node_id");

        assertEquals("", webParamAnn.targetNamespace(), () -> "Add assertion message");



    }

    @Test
    public void testWebFaultAnnotation() throws Exception {
        env.put(ToolConstants.CFG_WSDLURL, getLocation("/wsdl2java_wsdl/jms_test_rpc_fault.wsdl"));
        env.put(ToolConstants.CFG_SERVICENAME, "HelloWorldService");
        env.remove(ToolConstants.CFG_VALIDATE_WSDL); //testing some technically invalid fault message formats
        processor.setContext(env);
        processor.execute();
        Class<?> cls = classLoader.loadClass("org.apache.cxf.w2j.hello_world_jms.BadRecordLitFault");
        WebFault webFault = AnnotationUtil.getPrivClassAnnotation(cls, WebFault.class);
        assertEquals("http://www.w3.org/2001/XMLSchema", webFault.targetNamespace(), () -> "Add assertion message");

    }

    @Test
    public void testWsdlWithInvalidSchema() {
        try {
            env.put(ToolConstants.CFG_WSDLURL,
                    getLocation("/wsdl2java_wsdl/hello_world_with_invalid_schema.wsdl"));
            env.put(ToolConstants.CFG_VALIDATE_WSDL, ToolConstants.CFG_VALIDATE_WSDL);
            processor.setContext(env);
            processor.execute();
            fail("Failure expected");
        } catch (Exception e) {
            boolean english = "en".equals(java.util.Locale.getDefault().getLanguage());
            if (english) {
                assertTrue("Jaxb databinding can not find the schema error ",
                       e.getLocalizedMessage().indexOf(" cos-st-restricts.1.1: "
                                                       + "The type 'TpAny' is atomic") > -1);
            }
        }
    }

    @Test
    public void testGlobalBinding() throws Exception {
        env.put(ToolConstants.CFG_BINDING, getLocation("/wsdl2java_wsdl/cust.xml"));
        env.put(ToolConstants.CFG_WSDLURL, getLocation("/wsdl2java_wsdl/hello_world.wsdl"));

        processor.setContext(env);
        processor.execute();

        File greeter = new File(output, "org/apache/cxf/w2j/hello_world_soap_http/Greeter.java");
        assertTrue(output.exists(), () -> "Add assertion message");
        String contents = TestFileUtils.getStringFromFile(greeter);
        assertTrue(contents.contains("SOAPBinding.ParameterStyle.BARE"), () -> "Add assertion message");
        assertFalse(contents.contains("@ResponseWrapper"), () -> "Add assertion message");
    }
    @Test
    public void testBareFromCommandLine() throws Exception {
        env.put(ToolConstants.CFG_WSDLURL, getLocation("/wsdl2java_wsdl/hello_world.wsdl"));
        env.put(ToolConstants.CFG_BAREMETHODS, new String[0]);

        processor.setContext(env);
        processor.execute();

        File greeter = new File(output, "org/apache/cxf/w2j/hello_world_soap_http/Greeter.java");
        assertTrue(output.exists(), () -> "Add assertion message");
        String contents = TestFileUtils.getStringFromFile(greeter);
        assertTrue(contents.contains("SOAPBinding.ParameterStyle.BARE"), () -> "Add assertion message");
        assertFalse(contents.contains("@ResponseWrapper"), () -> "Add assertion message");
    }
    @Test
    public void testMimeFromCommandLine() throws Exception {
        env.put(ToolConstants.CFG_WSDLURL, getLocation("/wsdl2java_wsdl/hello-mime.wsdl"));
        env.put(ToolConstants.CFG_MIMEMETHODS, new String[0]);
        env.remove(ToolConstants.CFG_VALIDATE_WSDL);

        processor.setContext(env);
        processor.execute();

        String str1 = "javax.xml.ws.Holder<java.awt.Image>";
        String str2 = "javax.xml.transform.Source";

        String file = TestFileUtils.getStringFromFile(new File(output,
                                        "org/apache/cxf/w2j/hello_world_mime/Hello.java"));

        assertTrue(file.contains(str1), () -> "Add assertion message");
        assertTrue(file.contains(str2), () -> "Add assertion message");
    }
    @Test
    public void testXmlSeeAlso() throws Exception {
        env.put(ToolConstants.CFG_WSDLURL, getLocation("/wsdl2java_wsdl/cardealer.wsdl"));

        processor.setContext(env);
        processor.execute();

        File sei = new File(output, "type_substitution/server/CarDealer.java");
        assertTrue(output.exists(), () -> "Add assertion message");
        String contents = TestFileUtils.getStringFromFile(sei);
        assertTrue(contents.contains("@XmlSeeAlso({ObjectFactory.class})"), () -> "Add assertion message");
    }

    @Test
    public void testWSAction() throws Exception {
        env.put(ToolConstants.CFG_WSDLURL, getLocation("/wsdl2java_wsdl/wsdl_addr.wsdl"));

        processor.setContext(env);
        processor.execute();

        File sei = new File(output, "com/example/AddNumbersPortType.java");
        assertTrue(sei.exists(), () -> "Add assertion message");
        String contents = TestFileUtils.getStringFromFile(sei).replace("  ", " ");
        String expected = "@Action(input = \"3in\", output = \"3out\", "
            + "fault = {@FaultAction(className = AddNumbersFault_Exception.class, value = \"3fault\")})";
        assertTrue(contents.contains("import javax.xml.ws.Action;"), () -> "Add assertion message");
        assertTrue(contents.contains("import javax.xml.ws.FaultAction;"), () -> "Add assertion message");
        assertTrue(contents.contains(expected), () -> "Add assertion message");
    }

    @Test
    public void testURLWithColonTNS() throws Exception {
        env.put(ToolConstants.CFG_WSDLURL, getLocation("/wsdl2java_wsdl/tns_url_with_colon.wsdl"));
        processor.setContext(env);
        processor.execute();

        Class<?> sei = classLoader.loadClass("org.apache.cxf.w2j.hello_world_soap_http.tns.soap.Greeter");
        assertNotNull("Greeter class from wsdl targetNamespace could not be found", sei);

        Class<?> tc = classLoader.loadClass("org.apache.cxf.w2j.hello_world_soap_http.tns.types.GreetMe");
        assertNotNull("GreetMe class from schema targetNamespace could not be found", tc);
    }

    @Test
    public void testURNTNS() throws Exception {
        env.put(ToolConstants.CFG_WSDLURL, getLocation("/wsdl2java_wsdl/tns_urn.wsdl"));
        processor.setContext(env);
        processor.execute();

        Class<?> sei = classLoader.loadClass("apache.cxf.issue._6527.Greeter");
        assertNotNull("Greeter class from wsdl targetNamespace could not be found", sei);

        Class<?> tc = classLoader.loadClass("apache.cxf.issue._6527.types.GreetMe");
        assertNotNull("GreetMe class from schema targetNamespace could not be found", tc);
    }

    // Line removed due to Ignored Test
    @Test
    public void testHandlerAnnotation() throws Exception {
        env.put(ToolConstants.CFG_WSDLURL, getLocation("/wsdl2java_wsdl/cxf-1344/handlerbinding.wsdl"));
        env.put(ToolConstants.CFG_BINDING, getLocation("/wsdl2java_wsdl/cxf-1344/handlerbinding.xml"));
        processor.setContext(env);
        processor.execute();
    }

    @Test
    public void testGenerateXmlListAnno() throws Exception {
        env.put(ToolConstants.CFG_WSDLURL, getLocation("/wsdl2java_wsdl/cxf-1354/string_array_test.wsdl"));
        processor.setContext(env);
        processor.execute();

        Class<?> sei = classLoader.loadClass("org.apache.stringarray.StringListTest");
        Method method = sei.getMethods()[0];
        assertNotNull("@XmlList is not generated for method", method.getAnnotation(XmlList.class));
        boolean xmlListGenerated = false;
        for (Annotation ann : method.getParameterAnnotations()[0]) {
            if (ann instanceof XmlList) {
                xmlListGenerated = true;
            }
        }
        assertTrue("@XmlList is not generated for paramter", xmlListGenerated);

    }

    @Test
    public void testWrapperWithWildcard()  throws Exception {
        env.put(ToolConstants.CFG_WSDLURL, getLocation("/wsdl2java_wsdl/cxf-1404/hello_world.wsdl"));
        processor.setContext(env);
        processor.execute();
        Class<?> sei = classLoader.loadClass("org.apache.cxf.cxf1404.hello_world_soap_http.Greeter");
        assertEquals(1, sei.getMethods().length, () -> "Add assertion message");
        assertFalse(Void.TYPE.equals(sei.getMethods()[0].getReturnType()), () -> "Add assertion message");
    }
    @Test
    public void testCXF1950()  throws Exception {
        env.put(ToolConstants.CFG_WSDLURL, getLocation("/wsdl2java_wsdl/helloworld-noservice-header.wsdl"));

        processor.setContext(env);
        processor.execute();

        File seif = new File(output, "org/apache/cxf/helloworld/HelloWorldServiceImpl.java");
        assertTrue(seif.exists(), () -> "Add assertion message");
        Class<?> sei = classLoader.loadClass("org.apache.cxf.helloworld.HelloWorldServiceImpl");
        Method[] m = sei.getDeclaredMethods();
        assertEquals(1, m.length, () -> "Add assertion message");
        assertTrue(m[0].getParameterAnnotations()[1][0] instanceof WebParam, () -> "Add assertion message");
        WebParam wp = (WebParam)m[0].getParameterAnnotations()[1][0];
        assertTrue(wp.header(), () -> "Add assertion message");
    }

    @Test
    public void testCXFNotType() throws Exception {
        env.put(ToolConstants.CFG_WSDLURL, getLocation("/wsdl2java_wsdl/hello_world.wsdl"));
        env.put(ToolConstants.CFG_FAULT_SERIAL_VERSION_UID, "FQCN");
        processor.setContext(env);
        processor.execute();

        File sayHiResponseFile =
            new File(output, "org/apache/cxf/w2j/hello_world_soap_http/types/SayHiResponse");
        assertFalse(sayHiResponseFile.exists(), () -> "Add assertion message");
        File greetMeResponseFile =
            new File(output, "org/apache/cxf/w2j/hello_world_soap_http/types/GreetMeResponse");
        assertFalse(greetMeResponseFile.exists(), () -> "Add assertion message");
    }

    @Test
    public void testFaultSerialVersionUIDNONEFQCN() throws Exception {
        env.put(ToolConstants.CFG_WSDLURL, getLocation("/wsdl2java_wsdl/hello_world.wsdl"));
        env.put(ToolConstants.CFG_FAULT_SERIAL_VERSION_UID, "FQCN");
        processor.setContext(env);
        processor.execute();

        File faultFile = new File(output, "org/apache/cxf/w2j/hello_world_soap_http/NoSuchCodeLitFault.java");
        assertTrue(faultFile.exists(), () -> "Add assertion message");
        faultFile = new File(output, "org/apache/cxf/w2j/hello_world_soap_http/BadRecordLitFault.java");
        // Line removed due to Duplicate Assert: assertTrue(faultFile.exists(), () -> "Add assertion message");

        Class<?> fault = classLoader.loadClass("org.apache.cxf.w2j.hello_world_soap_http.NoSuchCodeLitFault");
        Field serialVersionUID = fault.getDeclaredField("serialVersionUID");
        assertNotNull(serialVersionUID, () -> "Add assertion message");
        assertEquals(fault.getName().hashCode(), ObjectStreamClass.lookup(fault).getSerialVersionUID(), () -> "Add assertion message");
        fault = classLoader.loadClass("org.apache.cxf.w2j.hello_world_soap_http.BadRecordLitFault");
        // Line removed due to Duplicate Assert: assertEquals(fault.getName().hashCode(), ObjectStreamClass.lookup(fault).getSerialVersionUID(), () -> "Add assertion message");
    }


    @Test
    public void testNoFaultSerialVersionUID() throws Exception {
        env.put(ToolConstants.CFG_WSDLURL, getLocation("/wsdl2java_wsdl/hello_world.wsdl"));
        processor.setContext(env);
        processor.execute();

        File faultFile = new File(output, "org/apache/cxf/w2j/hello_world_soap_http/NoSuchCodeLitFault.java");
        assertTrue(faultFile.exists(), () -> "Add assertion message");
        faultFile = new File(output, "org/apache/cxf/w2j/hello_world_soap_http/BadRecordLitFault.java");
        // Line removed due to Duplicate Assert: assertTrue(faultFile.exists(), () -> "Add assertion message");

        Class<?> fault = classLoader.loadClass("org.apache.cxf.w2j.hello_world_soap_http.NoSuchCodeLitFault");
        try {
            fault.getDeclaredField("serialVersionUID");
            fail("shouldn't have serialVersionUID field");
        } catch (NoSuchFieldException e) {
            //
        }
        fault = classLoader.loadClass("org.apache.cxf.w2j.hello_world_soap_http.BadRecordLitFault");
        try {
            fault.getDeclaredField("serialVersionUID");
            // Line removed due to Duplicate Assert: fail("shouldn't have serialVersionUID field");
        } catch (NoSuchFieldException e) {
            //
        }

    }

    @Test
    public void testFaultSerialVersionUIDNONE() throws Exception {
        env.put(ToolConstants.CFG_WSDLURL, getLocation("/wsdl2java_wsdl/hello_world.wsdl"));
        env.put(ToolConstants.CFG_FAULT_SERIAL_VERSION_UID, "NONE");
        processor.setContext(env);
        processor.execute();

        File faultFile = new File(output, "org/apache/cxf/w2j/hello_world_soap_http/NoSuchCodeLitFault.java");
        assertTrue(faultFile.exists(), () -> "Add assertion message");
        faultFile = new File(output, "org/apache/cxf/w2j/hello_world_soap_http/BadRecordLitFault.java");
        // Line removed due to Duplicate Assert: assertTrue(faultFile.exists(), () -> "Add assertion message");

        Class<?> fault = classLoader.loadClass("org.apache.cxf.w2j.hello_world_soap_http.NoSuchCodeLitFault");
        try {
            fault.getDeclaredField("serialVersionUID");
            fail("shouldn't have serialVersionUID field");
        } catch (NoSuchFieldException e) {
            //
        }
        fault = classLoader.loadClass("org.apache.cxf.w2j.hello_world_soap_http.BadRecordLitFault");
        try {
            fault.getDeclaredField("serialVersionUID");
            // Line removed due to Duplicate Assert: fail("shouldn't have serialVersionUID field");
        } catch (NoSuchFieldException e) {
            //
        }

    }

    @Test
    public void testFaultSerialVersionUIDNONETIMESTAMP() throws Exception {
        env.put(ToolConstants.CFG_WSDLURL, getLocation("/wsdl2java_wsdl/hello_world.wsdl"));
        env.put(ToolConstants.CFG_FAULT_SERIAL_VERSION_UID, "TIMESTAMP");
        processor.setContext(env);
        processor.execute();

        File faultFile = new File(output, "org/apache/cxf/w2j/hello_world_soap_http/NoSuchCodeLitFault.java");
        assertTrue(faultFile.exists(), () -> "Add assertion message");
        faultFile = new File(output, "org/apache/cxf/w2j/hello_world_soap_http/BadRecordLitFault.java");
        // Line removed due to Duplicate Assert: assertTrue(faultFile.exists(), () -> "Add assertion message");

        Class<?> fault = classLoader.loadClass("org.apache.cxf.w2j.hello_world_soap_http.NoSuchCodeLitFault");
        Field serialVersionUID = fault.getDeclaredField("serialVersionUID");
        assertNotNull(serialVersionUID, () -> "Add assertion message");

        fault = classLoader.loadClass("org.apache.cxf.w2j.hello_world_soap_http.BadRecordLitFault");
        serialVersionUID = fault.getDeclaredField("serialVersionUID");
        // Line removed due to Duplicate Assert: assertNotNull(serialVersionUID, () -> "Add assertion message");
    }
    @Test
    public void testFaultSerialVersionUIDNumber() throws Exception {
        env.put(ToolConstants.CFG_WSDLURL, getLocation("/wsdl2java_wsdl/hello_world.wsdl"));
        env.put(ToolConstants.CFG_FAULT_SERIAL_VERSION_UID, "123456789");
        processor.setContext(env);
        processor.execute();

        File faultFile = new File(output, "org/apache/cxf/w2j/hello_world_soap_http/NoSuchCodeLitFault.java");
        assertTrue(faultFile.exists(), () -> "Add assertion message");
        faultFile = new File(output, "org/apache/cxf/w2j/hello_world_soap_http/BadRecordLitFault.java");
        // Line removed due to Duplicate Assert: assertTrue(faultFile.exists(), () -> "Add assertion message");

        Class<?> fault = classLoader.loadClass("org.apache.cxf.w2j.hello_world_soap_http.NoSuchCodeLitFault");
        Field serialVersionUID = fault.getDeclaredField("serialVersionUID");
        assertNotNull(serialVersionUID, () -> "Add assertion message");
        Long l = (Long)serialVersionUID.get(null);
        assertEquals(123456789L, l.longValue(), () -> "Add assertion message");

        fault = classLoader.loadClass("org.apache.cxf.w2j.hello_world_soap_http.BadRecordLitFault");
        serialVersionUID = fault.getDeclaredField("serialVersionUID");
        // Line removed due to Duplicate Assert: assertNotNull(serialVersionUID, () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertEquals(123456789L, l.longValue(), () -> "Add assertion message");
    }
    @Test
    public void testNoExceptionSuper() throws Exception {
        env.put(ToolConstants.CFG_WSDLURL, getLocation("/wsdl2java_wsdl/hello_world.wsdl"));
        processor.setContext(env);
        processor.execute();

        File faultFile = new File(output, "org/apache/cxf/w2j/hello_world_soap_http/NoSuchCodeLitFault.java");
        assertTrue(faultFile.exists(), () -> "Add assertion message");
        faultFile = new File(output, "org/apache/cxf/w2j/hello_world_soap_http/BadRecordLitFault.java");
        // Line removed due to Duplicate Assert: assertTrue(faultFile.exists(), () -> "Add assertion message");

        Class<?> fault = classLoader.loadClass("org.apache.cxf.w2j.hello_world_soap_http.NoSuchCodeLitFault");
        assertEquals(Exception.class, fault.getSuperclass(), () -> "Add assertion message");
    }
    @Test
    public void testNoTargetNamespaceSchema() throws Exception {
        env.put(ToolConstants.CFG_WSDLURL, getLocation("/wsdl2java_wsdl/cxf6580/hello_import.wsdl"));
        env.put(ToolConstants.CFG_CATALOG, getLocation("/wsdl2java_wsdl/cxf6580/catalog.xml"));

        processor.setContext(env);
        processor.execute();

        File helloFile = new File(output, "org/apache/hello_soap_http/types/Hello.java");
        assertTrue(helloFile.exists(), () -> "Add assertion message");
        helloFile = new File(output, "org/apache/hello_soap_http/types/HelloResponse.java");
        // Line removed due to Duplicate Assert: assertTrue(helloFile.exists(), () -> "Add assertion message");
    }
    @Test
    public void testExceptionSuper() throws Exception {
        env.put(ToolConstants.CFG_WSDLURL, getLocation("/wsdl2java_wsdl/hello_world.wsdl"));
        env.put(ToolConstants.CFG_EXCEPTION_SUPER, "java.lang.RuntimeException");
        processor.setContext(env);
        processor.execute();

        File faultFile = new File(output, "org/apache/cxf/w2j/hello_world_soap_http/NoSuchCodeLitFault.java");
        assertTrue(faultFile.exists(), () -> "Add assertion message");
        faultFile = new File(output, "org/apache/cxf/w2j/hello_world_soap_http/BadRecordLitFault.java");
        // Line removed due to Duplicate Assert: assertTrue(faultFile.exists(), () -> "Add assertion message");

        Class<?> fault = classLoader.loadClass("org.apache.cxf.w2j.hello_world_soap_http.NoSuchCodeLitFault");
        assertEquals(RuntimeException.class, fault.getSuperclass(), () -> "Add assertion message");
    }

    @Test
    public void testExtensionWrapper() throws Exception {
        env.put(ToolConstants.CFG_WSDLURL,
                getLocation("/wsdl2java_wsdl/cxf2193/hello_world_extension_wrapped.wsdl"));
        processor.setContext(env);
        processor.execute();

        File infFile = new File(output, "org/apache/cxf/w2j/extension_wrapped/Greeter.java");
        assertTrue(infFile.exists(), () -> "Add assertion message");

        Class<?> interfaceClass = classLoader.loadClass("org.apache.cxf.w2j.extension_wrapped.Greeter");

        Method method = interfaceClass.getMethod("greetMe", new Class[] {
            Holder.class, Holder.class, Holder.class, Holder.class, Holder.class
        });
        assertNotNull("greetMe operation is NOT generated correctly as excepted", method);
        RequestWrapper reqWrapper = method.getAnnotation(RequestWrapper.class);
        assertNotNull("@RequestWrapper is expected", reqWrapper);
    }

    @Test
    public void testMultilevelExtensionWrapper() throws Exception {
        env.put(ToolConstants.CFG_WSDLURL,
                getLocation("/wsdl2java_wsdl/cxf8025/hello_world_multilevel_extension_wrapped.wsdl"));
        processor.setContext(env);
        processor.execute();

        File infFile = new File(output, "org/apache/cxf/w2j/multilevel_extension_wrapped/Greeter.java");
        assertTrue(infFile.exists(), () -> "Add assertion message");

        Class<?> interfaceClass = classLoader.loadClass("org.apache.cxf.w2j.multilevel_extension_wrapped.Greeter");

        Method method = interfaceClass.getMethod("greetMeMultilevelExtension", new Class[] {
            String.class, String.class, String.class, String.class
        });
        assertNotNull("greetMeMultilevelExtension operation is NOT generated correctly as excepted", method);
        RequestWrapper reqWrapper = method.getAnnotation(RequestWrapper.class);
        assertNotNull("@RequestWrapper is expected on greetMeMultilevelExtension", reqWrapper);
    }

    @Test
    public void testMultilevelExtensionWrapperStackDepth() throws Exception {
        env.put(ToolConstants.CFG_WSDLURL,
                getLocation("/wsdl2java_wsdl/cxf8025/hello_world_multilevel_extension_wrapped.wsdl"));
        env.put(ToolConstants.CFG_MAX_EXTENSION_STACK_DEPTH, Integer.valueOf(3));
        processor.setContext(env);
        processor.execute();

        File infFile = new File(output, "org/apache/cxf/w2j/multilevel_extension_wrapped/Greeter.java");
        assertTrue(infFile.exists(), () -> "Add assertion message");

        Class<?> interfaceClass = classLoader.loadClass("org.apache.cxf.w2j.multilevel_extension_wrapped.Greeter");

        Method method = interfaceClass.getMethod("greetMeMultilevelExtension", new Class[] {
            String.class, String.class, String.class
        });
        assertNotNull("greetMeMultilevelExtension operation is NOT generated correctly as excepted", method);
    }

    @Test
    public void testJavaDoc() throws Exception {
        env.put(ToolConstants.CFG_WSDLURL, getLocation("/wsdl2java_wsdl/hello_world.wsdl"));
        processor.setContext(env);
        processor.execute();

        List<String> results1 = FileUtils.readLines(new File(output,
            "org/apache/cxf/w2j/hello_world_soap_http/Greeter.java"));

        assertTrue(results1.contains(" * porttype documentation"), () -> "Add assertion message");
        assertTrue(results1.contains("     * porttype op documentation"), () -> "Add assertion message");

        List<String> results2 = FileUtils.readLines(new File(output,
            "org/apache/cxf/w2j/hello_world_soap_http/SOAPServiceTest1.java"));

        boolean match1 = false;
        boolean match2 = false;
        boolean match3 = false;
        for (String str : results2) {
            if (str.contains("service documentation")) {
                match1 = true;
            }
            if (str.contains("port1 documentation")) {
                match2 = true;
            }
            if (str.contains("port2 documentation")) {
                match3 = true;
            }
        }
        assertTrue(results2.toString(), match1);
        assertTrue(results2.toString(), match2);
        assertTrue(results2.toString(), match3);

    }


    @Test
    // CXF-4135
    public void testPassShemaFileAsJAXBBinding() throws Exception {
        env.put(ToolConstants.CFG_WSDLURL, getLocation("/wsdl2java_wsdl/hello_world.wsdl"));
        env.put(ToolConstants.CFG_BINDING, getLocation("/wsdl2java_wsdl/Invoice.xsd"));
        processor.setContext(env);
        processor.execute();
        File generateFromBindingFile = new File(output, "org/apache/invoice/Invoice.java");
        assertTrue(generateFromBindingFile.exists(), () -> "Add assertion message");
        File seiFile = new File(output, "org/apache/cxf/w2j/hello_world_soap_http/Greeter.java");
        assertTrue(seiFile.exists(), () -> "Add assertion message");
    }

    @Test
    public void testClientJar() throws Exception {
        env.put(ToolConstants.CFG_WSDLURL, getLocation("/wsdl2java_wsdl/hello_world_wsdl_import.wsdl"));
        env.put(ToolConstants.CFG_CLIENT_JAR, "test-client.jar");
        processor.setContext(env);
        processor.execute();
        File clientjarFile = new File(output, "test-client.jar");
        assertTrue(clientjarFile.exists(), () -> "Add assertion message");

        List<String> jarEntries = new ArrayList<>();
        JarInputStream jarIns = new JarInputStream(new FileInputStream(clientjarFile));
        JarEntry entry;
        while ((entry = jarIns.getNextJarEntry()) != null) {
            if (entry.getName().endsWith(".wsdl") || entry.getName().endsWith(".class")) {
                jarEntries.add(entry.getName());
            }
        }
        jarIns.close();
        assertEquals("15 files including wsdl and class files are expected", 15, jarEntries.size());
        assertTrue("hello_world_messages.wsdl is expected",
                     jarEntries.contains("hello_world_messages.wsdl"));
        assertTrue("org/apache/cxf/w2j/hello_world/SOAPService.class is expected",
                     jarEntries.contains("org/apache/cxf/w2j/hello_world/SOAPService.class"));
    }

}