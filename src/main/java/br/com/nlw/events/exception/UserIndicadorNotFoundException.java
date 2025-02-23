package br.com.nlw.events.exception;

import org.springframework.web.bind.annotation.PathVariable;

public class UserIndicadorNotFoundException extends RuntimeException{
    public UserIndicadorNotFoundException(String msg){
        super(msg);
    }
}
