package ru.spb.locon.excel

/**
 * User: Gleb
 * Date: 02.02.13
 * Time: 20:03
 */
class InvalidRowException extends Exception{

  InvalidRowException() {
  }

  InvalidRowException(String s) {
    super(s)
  }

  InvalidRowException(String s, Throwable throwable) {
    super(s, throwable)
  }

  InvalidRowException(Throwable throwable) {
    super(throwable)
  }
}
