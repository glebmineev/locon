package ru.spb.locon.excel

import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.codehaus.groovy.grails.commons.ApplicationHolder
import ru.spb.locon.OrderEntity
import ru.spb.locon.OrderProductEntity
import ru.spb.locon.ProductEntity
import ru.spb.locon.common.PathBuilder
import ru.spb.locon.common.StringUtils

import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

public class UploadExcelHandler {

  UUID uuid = UUID.randomUUID()
  String root = ApplicationHolder.application.mainContext.servletContext.getRealPath(System.getProperty("file.separator"))

  public void createExcelFromOrderEntity(OrderEntity entity) {
    StringUtils stringUtils = new StringUtils()
    String twoLevelUp = stringUtils.buildPath(2, root)

    try {
      PathBuilder pathBuilder = new PathBuilder()

      pathBuilder
          .appendPath(twoLevelUp)
          .appendPath("admin")
          .appendPath("orders")
          .appendPath(uuid.toString())

      pathBuilder.checkDir()

      HSSFWorkbook workbook = fillExcel(entity)

      pathBuilder
          .appendString(entity.getNumber())
          .appendExt("xls")

      FileOutputStream out =
        new FileOutputStream(
            new File(pathBuilder.build()));
      workbook.write(out);
      out.close();

      String from = new PathBuilder()
          .appendPath(twoLevelUp)
          .appendPath("admin")
          .appendPath("orders")
          .appendPath(uuid.toString()).build()

      String to = new PathBuilder()
          .appendPath(twoLevelUp)
          .appendPath("admin")
          .appendPath("orders")
          .appendString(uuid.toString())
          .appendExt("zip")
          .build()

      pack(new File(from), to)

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  HSSFWorkbook fillExcel(OrderEntity entity){
    HSSFWorkbook workbook = new HSSFWorkbook();
    HSSFSheet sheet = workbook.createSheet("Заказы");
    int rownum = 0
    createCellWithLabel(sheet.createRow(rownum++), "Заказ номер:", entity.getNumber() as String)

    createCellWithLabel(sheet.createRow(rownum++), "ФИО:", entity.getFio() as String)

    createCellWithLabel(sheet.createRow(rownum++), "Телефон:", entity.getFio() as String)

    createCellWithLabel(sheet.createRow(rownum++), "E-mail:", entity.getEmail() as String)

    createCellWithLabel(sheet.createRow(rownum++), "Адрес доставки:" , entity.getAddress() as String)

    createCellWithLabel(sheet.createRow(rownum++), "Примечания:" , entity.getNotes() as String)

    createCellWithLabel(sheet.createRow(rownum++), "Способ оплаты:" , entity.getEmoney() ? entity.getEmoney() : entity.getCourier())

    rownum++

    createTableProductsHead(sheet.createRow(rownum++))

    OrderEntity.get(entity.id).orderProductList.each {it ->
      createTableProductsRow(sheet.createRow(rownum++), it)
    }

    return workbook
  }

  private void createCellWithLabel(Row row, String label, Object value){
    createCell(row, label, 0)
    createCell(row, value, 1)
  }

  private void createCell(Row row, Object value, int cellNumber){
    Cell orderValue = row.createCell(cellNumber)
    if (value instanceof String)
      orderValue.setCellValue(value as String)
    if (value instanceof Boolean)
      orderValue.setCellValue(value as Boolean)
    if (value instanceof Long)
      orderValue.setCellValue(value as Double)
  }

  private void createTableProductsHead(Row row){
    createCell(row, "Товар", 0)
    createCell(row, "Цена", 1)
    createCell(row, "Количество", 2)
    createCell(row, "Стоимость", 3)
  }

  private void createTableProductsRow(Row row, OrderProductEntity entity){
    ProductEntity product = ProductEntity.get(entity.product.id)
    long price = product.price
    long count = entity.countProduct
    createCell(row, product.name, 0)
    createCell(row, price, 1)
    createCell(row, count, 2)
    createCell(row, (price * count), 3)
  }

  /**
   * Метод упаковки в архив excel файлов.
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
