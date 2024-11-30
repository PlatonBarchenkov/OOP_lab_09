// Файл: DataLoadException.java
package org.example;

/**
 * Исключение, выбрасываемое при ошибках загрузки данных из файла.
 */
public class DataLoadException extends Exception {
    public DataLoadException(String message) {
        super(message);
    }
}
