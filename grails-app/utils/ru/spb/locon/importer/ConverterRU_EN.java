package ru.spb.locon.importer;

import java.util.HashMap;
import java.util.Map;

public class ConverterRU_EN {

  private static Map<String, String> ru_enABC = new HashMap<String, String>();

  static {
    ru_enABC.put("А", "A");
    ru_enABC.put("Б", "B");
    ru_enABC.put("В", "V");
    ru_enABC.put("Г", "G");
    ru_enABC.put("Д", "D");
    ru_enABC.put("Е", "E");
    ru_enABC.put("Ё", "E");
    ru_enABC.put("Ж", "ZH");
    ru_enABC.put("З", "Z");
    ru_enABC.put("И", "I");
    ru_enABC.put("Й", "IY");
    ru_enABC.put("К", "K");
    ru_enABC.put("Л", "L");
    ru_enABC.put("М", "M");
    ru_enABC.put("Н", "N");
    ru_enABC.put("О", "O");
    ru_enABC.put("П", "P");
    ru_enABC.put("Р", "R");
    ru_enABC.put("С", "S");
    ru_enABC.put("Т", "T");
    ru_enABC.put("У", "U");
    ru_enABC.put("Ф", "F");
    ru_enABC.put("Х", "KH");
    ru_enABC.put("Ц", "TS");
    ru_enABC.put("Ч", "CH");
    ru_enABC.put("Ш", "SH");
    ru_enABC.put("Щ", "SHCH");
    ru_enABC.put("Ы", "Y");
    ru_enABC.put("Ъ", "");
    ru_enABC.put("Ь", "");
    ru_enABC.put("Э", "E");
    ru_enABC.put("Ю", "U");
    ru_enABC.put("Я", "YA");

    ru_enABC.put("а", "a");
    ru_enABC.put("б", "b");
    ru_enABC.put("в", "v");
    ru_enABC.put("г", "g");
    ru_enABC.put("д", "d");
    ru_enABC.put("е", "e");
    ru_enABC.put("ё", "e");
    ru_enABC.put("ж", "zh");
    ru_enABC.put("з", "z");
    ru_enABC.put("и", "i");
    ru_enABC.put("й", "iy");
    ru_enABC.put("к", "k");
    ru_enABC.put("л", "l");
    ru_enABC.put("м", "m");
    ru_enABC.put("н", "n");
    ru_enABC.put("о", "o");
    ru_enABC.put("п", "p");
    ru_enABC.put("р", "r");
    ru_enABC.put("с", "s");
    ru_enABC.put("т", "t");
    ru_enABC.put("у", "u");
    ru_enABC.put("ф", "f");
    ru_enABC.put("х", "kh");
    ru_enABC.put("ц", "ts");
    ru_enABC.put("ч", "ch");
    ru_enABC.put("ш", "sh");
    ru_enABC.put("щ", "shch");
    ru_enABC.put("ы", "y");
    ru_enABC.put("ъ", "");
    ru_enABC.put("ь", "");
    ru_enABC.put("э", "e");
    ru_enABC.put("ю", "u");
    ru_enABC.put("я", "ya");

    ru_enABC.put(".", "");
    //ru_enABC.put("/", " ");
    ru_enABC.put("'", " ");
  }

  public static String translit(String source) {
    String result = "";
    if (source != null && !source.isEmpty()) {
      char[] chars = source.toCharArray();
      for (int i = 0; i < chars.length; i++) {
        String symbol = Character.toString(chars[i]);
        if (ru_enABC.containsKey(symbol)) {
          String value = ru_enABC.get(symbol);
          result = result + value;
        } else {
          result = result + symbol;
        }
      }
    }
    return result;
  }
}
