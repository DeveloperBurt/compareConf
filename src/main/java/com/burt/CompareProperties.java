package com.burt;

public class CompareProperties {

    public static void main(String[] args) {
        String fileType = Constants.FILE_TYPE_PROPERTIES;
        Compare compare = new Compare();
        compare.exportFileEnvAttrValue(fileType);
    }

}

