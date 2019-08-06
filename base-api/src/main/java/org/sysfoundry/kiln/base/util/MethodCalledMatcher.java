/*
 * Copyright 2019 Sysfoundry (www.sysfoundry.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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