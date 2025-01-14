package uk.ac.ebi.pride.data.controller;

/**
 * DataAccessException is thrown when there is an error during i/o via data access controller
 * 
 * User: rwang
 * Date: 03-Feb-2010
 * Time: 08:45:27
 */
public class DataAccessException extends Exception {

    public DataAccessException(String message) {
        super(message);
    }

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
