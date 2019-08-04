package org.sysfoundry.kiln.base.evt;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface OnEvent {

    String[] value() default {};
}
