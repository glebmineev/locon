package ru.spb.locon.zulModels.importer.exceptions;

/**
 * Created with IntelliJ IDEA.
 * User: gleb
 * Date: 3/16/13
 * Time: 2:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class ImportException extends Exception {

    public ImportException() {
    }

    public ImportException(String message) {
        super(message);
    }

    public ImportException(String message, Throwable cause) {
        super(message, cause);
    }

    public ImportException(Throwable cause) {
        super(cause);
    }

    public ImportException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
