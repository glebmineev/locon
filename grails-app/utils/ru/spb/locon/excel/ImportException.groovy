package ru.spb.locon.excel

/**
 * User: Gleb
 * Date: 02.02.13
 * Time: 22:02
 */
class ImportException extends Exception {
  ImportException() {
  }

  ImportException(String s) {
    super(s)
  }

  ImportException(String s, Throwable throwable) {
    super(s, throwable)
  }

  ImportException(Throwable throwable) {
    super(throwable)
  }
}
