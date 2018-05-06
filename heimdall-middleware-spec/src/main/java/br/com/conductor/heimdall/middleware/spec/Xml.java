
package br.com.conductor.heimdall.middleware.spec;

public interface Xml {

     public <T> String parse(T object);

     public <T> T parse(String xml, Class<?> classType);
}
