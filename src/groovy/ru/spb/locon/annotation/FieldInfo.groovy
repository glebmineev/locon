package ru.spb.locon.annotation

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * User: Gleb
 * Date: 25.11.12
 * Time: 14:09
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FieldInfo {
  
  boolean isFilter() default false
  boolean isEditable() default false
}