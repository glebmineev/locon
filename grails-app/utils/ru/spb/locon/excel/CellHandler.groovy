package ru.spb.locon.excel

import org.apache.poi.hssf.util.CellReference
import org.apache.poi.ss.usermodel.*

/**
 * User: Gleb
 * Date: 02.02.13
 * Time: 19:20
 */
class CellHandler {

  Integer rowNumber
  Map<String, Object> data = new HashMap<String, Object>()

  public CellHandler(Row row){
    rowNumber = row.getRowNum()
    Iterator<Cell> cellIterator = row.cellIterator()
    while (cellIterator.hasNext()){
      Cell cell = cellIterator.next()
      String columnLetter = CellReference.convertNumToColString(cell.getColumnIndex())

      switch (cell.getCellType()){
        case Cell.CELL_TYPE_STRING:
          data.put(columnLetter, cell.getStringCellValue())
          break
        case Cell.CELL_TYPE_NUMERIC:
          data.put(columnLetter, cell.getNumericCellValue())
          break
      }

    }
  }

  public boolean validate(){
    if (data.get("B") == null)
      return false
    return true
  }



}
