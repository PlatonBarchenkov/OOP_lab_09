package org.example;

/**
 * Исключение, выбрасываемое при ошибках сохранения данных в файл.
 */
public class DataSaveException extends Exception {
    public DataSaveException(String message) {
        super(message);
    }
}
