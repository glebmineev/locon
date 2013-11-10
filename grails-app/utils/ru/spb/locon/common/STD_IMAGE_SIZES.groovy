package ru.spb.locon.common

/**
 * Стандартные размеры файлов картинок.
 */
public enum STD_IMAGE_SIZES {

  SMALLEST(100), SMALL(150), MIDDLE(300), LARGE(500)

  private int size

  STD_IMAGE_SIZES(int size) {
    this.size = size
  }

  int getSize() {
    return size
  }

  void setSize(int size) {
    this.size = size
  }

}