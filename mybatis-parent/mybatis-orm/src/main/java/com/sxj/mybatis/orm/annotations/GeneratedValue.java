/*
 * @(#)GeneratedValue.java 2013年12月23日 下午23:33:33
 *
 * Copyright (c) 2011-2013 Makersoft.org all rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *
 */
package com.sxj.mybatis.orm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Class description goes here.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface GeneratedValue
{
    
    GenerationType strategy() default GenerationType.AUTO;
    
    int length() default 32;
    
    String table() default "IDENTITIES";
    
    String idColumn() default "IDENTITIES_ID";
    
    String delimiterColumn() default "IDENTITIES_DELIMITER";
}
