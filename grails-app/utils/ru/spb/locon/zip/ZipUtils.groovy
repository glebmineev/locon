package ru.spb.locon.zip

import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * Утилита для архивирования файлов.
 */
class ZipUtils {

  /**
   * Метод упаковки в архив файлов.
   * @param directory - директория с файлами.
   * @param to - директория куда сохранять архив.
   * @throws IOException
   */
  public static void pack(File directory, String to) throws IOException {
    URI base = directory.toURI();
    Deque<File> queue = new LinkedList<File>();
    queue.push(directory);
    OutputStream out = new FileOutputStream(new File(to));
    Closeable res = out;

    try {
      ZipOutputStream zout = new ZipOutputStream(out);
      res = zout;
      while (!queue.isEmpty()) {
        directory = queue.pop();
        for (File child : directory.listFiles()) {
          String name = base.relativize(child.toURI()).getPath();
          if (child.isDirectory()) {
            queue.push(child);
            name = name.endsWith("/") ? name : name + "/";
            zout.putNextEntry(new ZipEntry(name));
          } else {
            zout.putNextEntry(new ZipEntry(name));
            InputStream is = new FileInputStream(child);
            try {
              byte[] buffer = new byte[1024];
              while (true) {
                int readCount = is.read(buffer);
                if (readCount < 0) {
                  break;
                }
                zout.write(buffer, 0, readCount);
              }
            } finally {
              is.close();
            }
            zout.closeEntry();
          }
        }
      }
    } finally {
      res.close();
    }
  }

}
