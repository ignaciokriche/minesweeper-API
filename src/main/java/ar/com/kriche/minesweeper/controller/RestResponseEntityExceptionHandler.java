package ar.com.kriche.minesweeper.controller;

import ar.com.kriche.minesweeper.service.game.GameNotFoundException;
import ar.com.kriche.minesweeper.service.game.IllegalGameActionException;
import ar.com.kriche.minesweeper.service.game.IllegalGameConfigurationException;
import ar.com.kriche.minesweeper.service.player.InvalidUserNameException;
import ar.com.kriche.minesweeper.service.player.PlayerNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * @Author Kriche 2020
 */
@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {
            PlayerNotFoundException.class,
            GameNotFoundException.class})
    protected ResponseEntity<Object> handleNotFound(RuntimeException ex, WebRequest request) {
        String bodyOfResponse = ex.getMessage();
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(value = {
            InvalidUserNameException.class,
            IllegalGameActionException.class,
            IllegalGameConfigurationException.class})
    protected ResponseEntity<Object> handleInvalidActions(RuntimeException ex, WebRequest request) {
        String bodyOfResponse = ex.getMessage();
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

}
