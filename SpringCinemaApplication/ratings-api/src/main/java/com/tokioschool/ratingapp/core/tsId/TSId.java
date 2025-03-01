package com.tokioschool.ratingapp.core.tsId;

import org.hibernate.annotations.IdGeneratorType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@IdGeneratorType(TSIdCreatorGenerate.class)
@Retention(RetentionPolicy.RUNTIME) @Target({ElementType.METHOD,ElementType.FIELD})
public @interface TSId {
}
