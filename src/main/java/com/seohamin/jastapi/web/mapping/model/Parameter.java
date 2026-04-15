package com.seohamin.jastapi.web.mapping.model;

public class Parameter {

    private String name;
    private Class<?> type;
    private ParameterSource parameterSource;
    private String annotationValue;

    public Parameter(
            final String name,
            final Class<?> type,
            final ParameterSource parameterSource,
            final String annotationValue
    ) {
        this.name = name;
        this.type = type;
        this.parameterSource = parameterSource;
        this.annotationValue = annotationValue;
    }

    public String getName() {
        return name;
    }

    public Class<?> getType() {
        return type;
    }

    public ParameterSource getParameterSource() {
        return parameterSource;
    }

    public String getAnnotationValue() {
        return annotationValue;
    }
}
