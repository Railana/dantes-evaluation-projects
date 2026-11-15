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
package org.apache.cxf.common.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.function.Function;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.aop.framework.ProxyFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ClassHelperTest {

    private Object proxiedObject;

    private Object springAopObject;
    
    private Object cglibProxyObject;

    private InvocationHandler realObjectInternalProxy;

    private Object realObjectInternalSpring;

    private Bus bus;

    private Bus currentThreadBus;
    
    private Function<String, Integer> fn;

    @Before
    public void setUp() {
        // Removed line due to General Fixture smell: realObjectInternalProxy = new InvocationHandler() {
            @Override
            public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        realObjectInternalProxy = new InvocationHandler() {
                return null;
            }
        };
// Removed line due to General Fixture smell: 
        // Removed line due to General Fixture smell: proxiedObject = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                // Removed line due to General Fixture smell: new Class[]{AnyInterface.class}, realObjectInternalProxy);
// Removed line due to General Fixture smell: 
        // Removed line due to General Fixture smell: // Removed line due to General Fixture smell: realObjectInternalSpring = new Object();
// Removed line due to General Fixture smell: 
        // Removed line due to General Fixture smell: ProxyFactory proxyFactory = new ProxyFactory(realObjectInternalSpring);
        // Removed line due to General Fixture smell: proxyFactory.addAdvice(new AfterReturningAdvice() {
// Removed line due to General Fixture smell: 
            // Removed line due to General Fixture smell: @Override
            public void afterReturning(Object o, Method method, Object[] objects, Object o1) throws Throwable {

        proxiedObject = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{AnyInterface.class}, realObjectInternalProxy);

        // Removed line due to General Fixture smell: realObjectInternalSpring = new Object();

        ProxyFactory proxyFactory = new ProxyFactory(realObjectInternalSpring);
        proxyFactory.addAdvice(new AfterReturningAdvice() {

            @Override

            }
        });

        // Removed line due to General Fixture smell: springAopObject = proxyFactory.getProxy();
        
        final Callback callback = new net.sf.cglib.proxy.InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        proxiedObject = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{AnyInterface.class}, realObjectInternalProxy);

        // Removed line due to General Fixture smell: realObjectInternalSpring = new Object();

        ProxyFactory proxyFactory = new ProxyFactory(realObjectInternalSpring);
        proxyFactory.addAdvice(new AfterReturningAdvice() {

            @Override
                return null;
            }
        };
        // Removed line due to General Fixture smell: cglibProxyObject = Enhancer.create(Object.class, new Class[] {Function.class}, callback);
        
        // Removed line due to General Fixture smell: fn = Integer::parseInt;

        // Removed line due to General Fixture smell: currentThreadBus = BusFactory.getThreadDefaultBus();

        bus = mock(Bus.class);

        BusFactory.setThreadDefaultBus(bus);
    }

    @After
    public void tearDown() {

        proxiedObject = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{AnyInterface.class}, realObjectInternalProxy);

        // Removed line due to General Fixture smell: realObjectInternalSpring = new Object();

        ProxyFactory proxyFactory = new ProxyFactory(realObjectInternalSpring);
        proxyFactory.addAdvice(new AfterReturningAdvice() {

            @Override
        currentThreadBus = BusFactory.getThreadDefaultBus();
        BusFactory.setThreadDefaultBus(currentThreadBus);
    }

    @Test
    public void getRealClassPropertyWasSetInBus() {

        proxiedObject = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{AnyInterface.class}, realObjectInternalProxy);

        // Removed line due to General Fixture smell: realObjectInternalSpring = new Object();

        ProxyFactory proxyFactory = new ProxyFactory(realObjectInternalSpring);
        proxyFactory.addAdvice(new AfterReturningAdvice() {

            @Override
        realObjectInternalProxy = new InvocationHandler() {

        when(bus.getProperty(ClassHelper.USE_DEFAULT_CLASS_HELPER)).thenReturn(true);
        when(bus.getProperty(ClassUnwrapper.class.getName())).thenReturn(null);

        assertSame(realObjectInternalProxy.getClass(), ClassHelper.getRealClass(proxiedObject), () -> "Add assertion message");

        verify(bus, times(1)).getProperty(ClassHelper.USE_DEFAULT_CLASS_HELPER);
        verify(bus, times(1)).getProperty(ClassUnwrapper.class.getName());

    }

    @Test
    public void getRealClassPropertyWasNotSetInBus() {

        proxiedObject = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{AnyInterface.class}, realObjectInternalProxy);

        // Removed line due to General Fixture smell: realObjectInternalSpring = new Object();

        ProxyFactory proxyFactory = new ProxyFactory(realObjectInternalSpring);
        proxyFactory.addAdvice(new AfterReturningAdvice() {

            @Override
        springAopObject = proxyFactory.getProxy();
        realObjectInternalSpring = new Object();

        when(bus.getProperty(ClassHelper.USE_DEFAULT_CLASS_HELPER)).thenReturn(false);
        when(bus.getProperty(ClassUnwrapper.class.getName())).thenReturn(null);

        assertSame(realObjectInternalSpring.getClass(), ClassHelper.getRealClass(springAopObject), () -> "Add assertion message");

        verify(bus, times(1)).getProperty(ClassHelper.USE_DEFAULT_CLASS_HELPER);
        verify(bus, times(1)).getProperty(ClassUnwrapper.class.getName());

    }

    @Test
    public void getRealClassFromClassPropertyWasSetInBus() {

        proxiedObject = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{AnyInterface.class}, realObjectInternalProxy);

        // Removed line due to General Fixture smell: realObjectInternalSpring = new Object();

        ProxyFactory proxyFactory = new ProxyFactory(realObjectInternalSpring);
        proxyFactory.addAdvice(new AfterReturningAdvice() {

            @Override

        when(bus.getProperty(ClassHelper.USE_DEFAULT_CLASS_HELPER)).thenReturn(true);
        when(bus.getProperty(ClassUnwrapper.class.getName())).thenReturn(null);

        assertSame(proxiedObject.getClass(), ClassHelper.getRealClassFromClass(proxiedObject.getClass()), () -> "Add assertion message");

        verify(bus, times(1)).getProperty(ClassHelper.USE_DEFAULT_CLASS_HELPER);
        verify(bus, times(1)).getProperty(ClassUnwrapper.class.getName());
    }

    @Test
    public void getRealClassFromClassPropertyWasNotSetInBus() {

        proxiedObject = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{AnyInterface.class}, realObjectInternalProxy);

        // Removed line due to General Fixture smell: realObjectInternalSpring = new Object();

        ProxyFactory proxyFactory = new ProxyFactory(realObjectInternalSpring);
        proxyFactory.addAdvice(new AfterReturningAdvice() {

            @Override
        springAopObject = proxyFactory.getProxy();
        realObjectInternalSpring = new Object();

        when(bus.getProperty(ClassHelper.USE_DEFAULT_CLASS_HELPER)).thenReturn(false);
        when(bus.getProperty(ClassUnwrapper.class.getName())).thenReturn(null);

        assertSame(realObjectInternalSpring.getClass(), ClassHelper.getRealClassFromClass(springAopObject.getClass()), () -> "Add assertion message");

        verify(bus, times(1)).getProperty(ClassHelper.USE_DEFAULT_CLASS_HELPER);
        verify(bus, times(1)).getProperty(ClassUnwrapper.class.getName());
    }


    @Test
    public void getRealObjectPropertyWasSetInBus() {

        proxiedObject = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{AnyInterface.class}, realObjectInternalProxy);

        // Removed line due to General Fixture smell: realObjectInternalSpring = new Object();

        ProxyFactory proxyFactory = new ProxyFactory(realObjectInternalSpring);
        proxyFactory.addAdvice(new AfterReturningAdvice() {

            @Override
        realObjectInternalProxy = new InvocationHandler() {

        when(bus.getProperty(ClassHelper.USE_DEFAULT_CLASS_HELPER)).thenReturn(true);
        when(bus.getProperty(ClassUnwrapper.class.getName())).thenReturn(null);

        assertSame(realObjectInternalProxy, ClassHelper.getRealObject(proxiedObject), () -> "Add assertion message");

        verify(bus, times(1)).getProperty(ClassHelper.USE_DEFAULT_CLASS_HELPER);
        verify(bus, times(1)).getProperty(ClassUnwrapper.class.getName());

    }

    @Test
    public void getRealObjectPropertyWasNotSetInBus() {

        proxiedObject = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{AnyInterface.class}, realObjectInternalProxy);

        // Removed line due to General Fixture smell: realObjectInternalSpring = new Object();

        ProxyFactory proxyFactory = new ProxyFactory(realObjectInternalSpring);
        proxyFactory.addAdvice(new AfterReturningAdvice() {

            @Override
        springAopObject = proxyFactory.getProxy();
        realObjectInternalSpring = new Object();

        when(bus.getProperty(ClassHelper.USE_DEFAULT_CLASS_HELPER)).thenReturn(false);
        when(bus.getProperty(ClassUnwrapper.class.getName())).thenReturn(null);

        assertSame(realObjectInternalSpring, ClassHelper.getRealObject(springAopObject), () -> "Add assertion message");

        verify(bus, times(1)).getProperty(ClassHelper.USE_DEFAULT_CLASS_HELPER);
        verify(bus, times(1)).getProperty(ClassUnwrapper.class.getName());

    }

    @Test
    public void getRealLambdaClassPropertyWasNotSetInBus() {

        proxiedObject = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{AnyInterface.class}, realObjectInternalProxy);

        // Removed line due to General Fixture smell: realObjectInternalSpring = new Object();

        ProxyFactory proxyFactory = new ProxyFactory(realObjectInternalSpring);
        proxyFactory.addAdvice(new AfterReturningAdvice() {

            @Override
        fn = Integer::parseInt;
        when(bus.getProperty(ClassHelper.USE_DEFAULT_CLASS_HELPER)).thenReturn(false);
        when(bus.getProperty(ClassUnwrapper.class.getName())).thenReturn(null);

        assertSame(fn.getClass(), ClassHelper.getRealClass(fn), () -> "Add assertion message");

        verify(bus, times(1)).getProperty(ClassHelper.USE_DEFAULT_CLASS_HELPER);
        verify(bus, times(1)).getProperty(ClassUnwrapper.class.getName());
    }

    @Test
    public void getRealLambdaClassPropertyWasSetInBus() {

        proxiedObject = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{AnyInterface.class}, realObjectInternalProxy);

        // Removed line due to General Fixture smell: realObjectInternalSpring = new Object();

        ProxyFactory proxyFactory = new ProxyFactory(realObjectInternalSpring);
        proxyFactory.addAdvice(new AfterReturningAdvice() {

            @Override
        fn = Integer::parseInt;
        when(bus.getProperty(ClassHelper.USE_DEFAULT_CLASS_HELPER)).thenReturn(true);
        when(bus.getProperty(ClassUnwrapper.class.getName())).thenReturn(null);

        assertSame(fn.getClass(), ClassHelper.getRealClass(fn), () -> "Add assertion message");

        verify(bus, times(1)).getProperty(ClassHelper.USE_DEFAULT_CLASS_HELPER);
        verify(bus, times(1)).getProperty(ClassUnwrapper.class.getName());
    }

    @Test
    public void getRealLambdaClassFromClassPropertyWasSetInBus() {

        proxiedObject = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{AnyInterface.class}, realObjectInternalProxy);

        // Removed line due to General Fixture smell: realObjectInternalSpring = new Object();

        ProxyFactory proxyFactory = new ProxyFactory(realObjectInternalSpring);
        proxyFactory.addAdvice(new AfterReturningAdvice() {

            @Override
        fn = Integer::parseInt;
        when(bus.getProperty(ClassHelper.USE_DEFAULT_CLASS_HELPER)).thenReturn(true);
        when(bus.getProperty(ClassUnwrapper.class.getName())).thenReturn(null);

        assertSame(fn.getClass(), ClassHelper.getRealClassFromClass(fn.getClass()), () -> "Add assertion message");

        verify(bus, times(1)).getProperty(ClassHelper.USE_DEFAULT_CLASS_HELPER);
        verify(bus, times(1)).getProperty(ClassUnwrapper.class.getName());
    }

    @Test
    public void getRealLambdaClassFromClassPropertyWasNotSetInBus() {

        proxiedObject = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{AnyInterface.class}, realObjectInternalProxy);

        // Removed line due to General Fixture smell: realObjectInternalSpring = new Object();

        ProxyFactory proxyFactory = new ProxyFactory(realObjectInternalSpring);
        proxyFactory.addAdvice(new AfterReturningAdvice() {

            @Override
        fn = Integer::parseInt;
        when(bus.getProperty(ClassHelper.USE_DEFAULT_CLASS_HELPER)).thenReturn(false);
        when(bus.getProperty(ClassUnwrapper.class.getName())).thenReturn(null);

        assertSame(fn.getClass(), ClassHelper.getRealClassFromClass(fn.getClass()), () -> "Add assertion message");

        verify(bus, times(1)).getProperty(ClassHelper.USE_DEFAULT_CLASS_HELPER);
        verify(bus, times(1)).getProperty(ClassUnwrapper.class.getName());
    }

    @Test
    public void getRealCglibClassFromClassPropertyWasNotSetInBus() {

        proxiedObject = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{AnyInterface.class}, realObjectInternalProxy);

        // Removed line due to General Fixture smell: realObjectInternalSpring = new Object();

        ProxyFactory proxyFactory = new ProxyFactory(realObjectInternalSpring);
        proxyFactory.addAdvice(new AfterReturningAdvice() {

            @Override
        cglibProxyObject = Enhancer.create(Object.class, new Class[] {Function.class}, callback);
        when(bus.getProperty(ClassHelper.USE_DEFAULT_CLASS_HELPER)).thenReturn(false);
        when(bus.getProperty(ClassUnwrapper.class.getName())).thenReturn(null);

        assertSame(Object.class, ClassHelper.getRealClassFromClass(cglibProxyObject.getClass()), () -> "Add assertion message");

        verify(bus, times(1)).getProperty(ClassHelper.USE_DEFAULT_CLASS_HELPER);
        verify(bus, times(1)).getProperty(ClassUnwrapper.class.getName());
    }

    @Test
    public void getRealCglibClassFromClassPropertyWasSetInBus() {

        proxiedObject = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{AnyInterface.class}, realObjectInternalProxy);

        // Removed line due to General Fixture smell: realObjectInternalSpring = new Object();

        ProxyFactory proxyFactory = new ProxyFactory(realObjectInternalSpring);
        proxyFactory.addAdvice(new AfterReturningAdvice() {

            @Override
        cglibProxyObject = Enhancer.create(Object.class, new Class[] {Function.class}, callback);
        when(bus.getProperty(ClassHelper.USE_DEFAULT_CLASS_HELPER)).thenReturn(true);
        when(bus.getProperty(ClassUnwrapper.class.getName())).thenReturn(null);

        assertSame(cglibProxyObject.getClass(), ClassHelper.getRealClassFromClass(cglibProxyObject.getClass()), () -> "Add assertion message");

        verify(bus, times(1)).getProperty(ClassHelper.USE_DEFAULT_CLASS_HELPER);
        verify(bus, times(1)).getProperty(ClassUnwrapper.class.getName());
    }

    public interface AnyInterface {
        void anyMethod();
    }


}