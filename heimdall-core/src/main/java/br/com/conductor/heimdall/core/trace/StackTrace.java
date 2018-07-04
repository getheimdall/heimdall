package br.com.conductor.heimdall.core.trace;

import lombok.Data;

@Data
public class StackTrace {

    public String clazz;

    public String message;

    public String stack;

}
