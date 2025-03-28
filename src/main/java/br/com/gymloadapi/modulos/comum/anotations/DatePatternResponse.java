package br.com.gymloadapi.modulos.comum.anotations;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@JacksonAnnotationsInside
@Target(ElementType.FIELD)
@JsonFormat(pattern = "dd/MM/yyyy")
@Retention(RetentionPolicy.RUNTIME)
public @interface DatePatternResponse {
}
