package ru.spb.locon

import ru.spb.locon.importer.ImportEvent

/**
 * User: Gleb
 * Date: 02.02.13
 * Time: 20:12
 */
public interface IImporterComposer {

  void addRow(ImportEvent event)
  void doProgress(int value, String message)
  void complete(String message)

}