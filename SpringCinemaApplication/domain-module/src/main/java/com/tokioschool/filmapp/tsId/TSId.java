package com.tokioschool.filmapp.tsId;

import org.hibernate.annotations.IdGeneratorType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotación personalizada para generar identificadores únicos.
 *
 * La anotación `@TSId` se utiliza para marcar campos o métodos que requieren
 * un identificador único generado automáticamente. Utiliza la clase
 * `TSIdCreatorGenerate` como generador de identificadores.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@IdGeneratorType(TSIdCreatorGenerate.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface TSId {
}