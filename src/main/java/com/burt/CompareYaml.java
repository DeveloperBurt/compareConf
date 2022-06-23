package com.burt;

public class CompareYaml {

    public static void main(String[] args) {
        String fileType = Constants.FILE_TYPE_YAML;
        Compare compare = new Compare();
        compare.exportFileEnvAttrValue(fileType);
    }

}

