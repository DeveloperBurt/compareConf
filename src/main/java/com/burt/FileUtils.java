package com.burt;

import java.io.File;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;


public class FileUtils {

    public static Set<String> getAllFile(String fileType) {
        Set<String> allEnvFile = new HashSet<>();
        for (int i = 0; i < Constants.env.length; i++) {
            URL url = CompareProperties.class.getClassLoader().getResource(Constants.env[i] + "/");
            allEnvFileSetWrapper(allEnvFile, url, fileType);
        }
        return allEnvFile;
    }

    private static void allEnvFileSetWrapper(Set<String> allEnvFile, URL url, String fileType) {
        if (url == null) {
            return;
        }
        File[] fileList = new File(url.getPath()).listFiles();
        for (int j = 0; j < fileList.length; j++) {
            if (fileList[j].getName().endsWith(fileType) || (Constants.FILE_TYPE_YAML.equals(fileType) && fileList[j].getName().endsWith(Constants.FILE_TYPE_YML)) || (Constants.FILE_TYPE_YML.equals(fileType) && fileList[j].getName().endsWith(Constants.FILE_TYPE_YAML))) {
                allEnvFile.add(fileList[j].getName());
            }
        }
    }

}
