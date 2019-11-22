package com.horcrux.util.csvutil.exception;

import java.net.URI;

/**
 * Created by midhun on 21/11/19.
 */
public class CSVValidationException extends Exception {

    private final String entityName;

    private final String errorKey;

    private Object paramMap;

    public CSVValidationException(String defaultMessage, String entityName, String errorKey) {
        super(defaultMessage);
        this.entityName = entityName;
        this.errorKey = errorKey;
    }

    public CSVValidationException(String defaultMessage, Object paramMap){
        this(defaultMessage,"","");
        this.paramMap = paramMap;
    }

    public Object getParamMap(){
        return this.paramMap;
    }
}
