package ru.spb.locon.excel

import org.apache.poi.hssf.usermodel.*
import org.apache.poi.poifs.filesystem.POIFSFileSystem
import org.apache.poi.ss.usermodel.Row

/**
 * User: Gleb
 * Date: 02.02.13
 * Time: 19:20
 */
class ExcelObject extends HashMap<String, List<CellHandler>>{

  ExcelObject(InputStream is) {

    POIFSFileSystem file = new POIFSFileSystem(is)
    HSSFWorkbook workbook = new HSSFWorkbook(file)

    (0..workbook.getNumberOfSheets() - 1).each { sheetIndex ->
      HSSFSheet sheet = workbook.getSheetAt(sheetIndex)
      if (!"Списки".equals(sheet.getSheetName())) {
        Iterator<Row> rowIterator = sheet.rowIterator()
        List<CellHandler> cellHandlers = new ArrayList<CellHandler>()
        while (rowIterator.hasNext()) {

          Row row = rowIterator.next()
          CellHandler cellHandler = null
          try {
            cellHandler = new CellHandler(row)
          } catch(InvalidRowException ex) {

          }
          if (cellHandler != null && cellHandler.data.size() > 0)
            cellHandlers.add(cellHandler)

        }

        put(sheet.sheetName, cellHandlers)

      }

    }

  }

}
