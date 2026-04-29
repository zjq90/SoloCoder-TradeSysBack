package com.tradesys.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataScope {

    String tableAlias() default "";

    String deptAlias() default "";

    String userAlias() default "";

    String agentAlias() default "a";
}
