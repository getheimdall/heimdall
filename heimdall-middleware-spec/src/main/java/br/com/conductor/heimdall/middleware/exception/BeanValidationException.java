package br.com.conductor.heimdall.middleware.exception;

import lombok.Getter;

@Getter
public class BeanValidationException extends RuntimeException {

     private static final long serialVersionUID = -3415601794675335171L;
     
     private String violations;
     
     public BeanValidationException(String mensagem, String violations) {
          super(mensagem);
          this.violations = violations;
     }
}