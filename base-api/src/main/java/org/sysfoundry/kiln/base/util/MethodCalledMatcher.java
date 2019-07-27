package org.sysfoundry.kiln.base.util;

import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matcher;

import java.lang.reflect.Method;
import java.util.Objects;

public class MethodCalledMatcher
        extends AbstractMatcher<Method>
{
    public static Matcher<Method> methodCalled(String methodName)
    {
        return new MethodCalledMatcher(methodName);
    }

    private String methodName;

    public MethodCalledMatcher(String methodName)
    {
        Objects.nonNull(methodName);
        this.methodName = methodName;
    }

    @Override
    public boolean matches(Method method) {
        return method.getName().equals(methodName);
    }

    @Override
    public String toString() {
        return "methodCalled(" + methodName + ")";
    }
}