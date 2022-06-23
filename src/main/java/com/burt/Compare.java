package com.burt;

import cn.afterturn.easypoi.excel.entity.ExportParams;
import org.apache.poi.ss.usermodel.Workbook;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Compare {

    public void exportFileEnvAttrValue(String fileType) {
        Set<String> confFileSet = FileUtils.getAllFile(fileType);
        Map<String, Set<String>> confFileItemSetMap = new HashMap<>();
        Map<String, Map<String, Map<String, Object>>> confFileEnvItemValueMap = new HashMap<>();
        for (String confFile : confFileSet) {
            Set<String> itemSet = getConfFileAllItem(confFile, fileType);
            Map<String, Map<String, Object>> envItemValue = getAllEnvItemValue(confFile, fileType);
            confFileEnvItemValueMap.put(confFile, envItemValue);
            confFileItemSetMap.put(confFile, itemSet);
        }
        String exportExcelName = fileType + "_" + System.currentTimeMillis();
        exportAllAttrValue(exportExcelName, confFileItemSetMap, confFileEnvItemValueMap, fileType);
    }

    public Set<String> getConfFileAllItem(String confFile, String fileType) {
        Set<String> itemSet = new HashSet<>();
        for (int i = 0; i < Constants.env.length; i++) {
            URL fileUrl = CompareProperties.class.getClassLoader().getResource(Constants.env[i] + "/" + confFile);
            if (fileUrl != null) {
                Set<String> envItemSet = getItem(fileUrl.getPath(), fileType);
                itemSet.addAll(envItemSet);
            }
        }
        return itemSet;
    }

    private Set<String> getItem(String filePath, String fileType) {
        return Constants.FILE_TYPE_PROPERTIES.equals(fileType) ? (Set<String>) getPropertiesConfFileItemValue(filePath, Constants.CONF_ITEM_ONLY) : (Set<String>) getYamlConfFileItemValue(filePath, Constants.CONF_ITEM_ONLY);
    }

    private Object getYamlConfFileItemValue(String filePath, String confItemType) {
        Object itemValueMap;
        Yaml confYaml = new Yaml();
        Map<String, Object> confMap = new HashMap<>();
        try {
            InputStream stream = new FileInputStream(filePath);
            confMap = confYaml.loadAs(stream, Map.class);
            if (confMap == null || confMap.isEmpty() == true) {
                throw new RuntimeException("Failed to read config file");
            }
            itemValueMap = yamlItemValueMapWrapper(initItemValueCollection(confItemType), confMap, "");
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception file path is " + filePath);
            throw new RuntimeException("Failed to read config file");
        }
        return itemValueMap;
    }

    private Object getPropertiesConfFileItemValue(String filePath, String confItemType) {
        Object itemValueCollection = initItemValueCollection(confItemType);
        try {
            Properties prop = new Properties();
            InputStream stream = new BufferedInputStream(new FileInputStream(filePath));
            prop.load(stream);
            Enumeration propertyNames = prop.propertyNames();
            while (propertyNames.hasMoreElements()) {
                String item = propertyNames.nextElement().toString();
                String itemValue = prop.getProperty(item);
                itemValueCollectionWrapper(itemValueCollection, item, itemValue);
            }
            return itemValueCollection;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to read config file");
        }
    }

    private Object initItemValueCollection(String confItemType) {
        return confItemType.equals(Constants.CONF_ITEM_ONLY) ? new HashSet<String>() : new HashMap<String, Object>();
    }

    private Object itemValueCollectionWrapper(Object itemValueCollection, String item, Object value) {
        if (itemValueCollection instanceof Set) {
            ((Set<String>) itemValueCollection).add(item);
        } else {
            if (item.toLowerCase().endsWith("username")
                    || item.toLowerCase().endsWith("password")
                    || item.toLowerCase().endsWith("key")
                    || item.toLowerCase().endsWith("salt")
                    || item.toLowerCase().endsWith("secret")
                    || item.toLowerCase().endsWith("token")) {
                value = "******";
            }
            ((Map<String, Object>) itemValueCollection).put(item, value);
        }
        return itemValueCollection;
    }

    public Map<String, Map<String, Object>> getAllEnvItemValue(String confFile, String fileType) {
        Map<String, Map<String, Object>> envItemValueMap = new HashMap<>();
        for (int i = 0; i < Constants.env.length; i++) {
            URL fileUrl = CompareProperties.class.getClassLoader().getResource(Constants.env[i] + "/" + confFile);
            if (fileUrl != null) {
                envItemValueMap.put(Constants.env[i], getAttrValue(fileUrl.getPath(), fileType));
            }
        }
        return envItemValueMap;
    }

    private Map<String, Object> getAttrValue(String filePath, String fileType) {
        return Constants.FILE_TYPE_PROPERTIES.equals(fileType) ? (Map<String, Object>) getPropertiesConfFileItemValue(filePath, Constants.CONF_ITEM_AND_VALUE) : (Map<String, Object>) getYamlConfFileItemValue(filePath, Constants.CONF_ITEM_AND_VALUE);
    }

    private Object yamlItemValueMapWrapper(Object itemValueCollection, Map<String, Object> map, String parentItem) {
        for (Map.Entry entry : map.entrySet()) {
            String item = !"".equals(parentItem) ? parentItem + "." + entry.getKey() : entry.getKey() + "";
            Object itemValue = entry.getValue();
            if (itemValue instanceof Map) {
                Map<String, Object> subItem = (Map) itemValue;
                if (subItem != null) {
                    yamlItemValueMapWrapper(itemValueCollection, subItem, item);
                }
            } else {
                itemValueCollectionWrapper(itemValueCollection, item, itemValue);
            }
        }
        return itemValueCollection;
    }

    public void exportAllAttrValue(String exportExcelName, Map<String, Set<String>> confFileItemSetMap, Map<String, Map<String, Map<String, Object>>> confFileEnvItemValueMap, String fileType) {
        Map<String, Object> excelMap = new HashMap<>();
        List<ResultVO> dataList = new ArrayList<>();
        int serialNumber = 1;
        for (Map.Entry entry : confFileItemSetMap.entrySet()) {
            String confFile = (String) entry.getKey();
            Set<String> itemSet = (Set<String>) entry.getValue();
            for (String item : itemSet) {
                ResultVO vo = wrapperResultVO(serialNumber, confFile, item, confFileEnvItemValueMap, fileType);
                dataList.add(vo);
                serialNumber++;
            }
        }
        excelMap.put("dataList", dataList);
        Workbook workbook = ExcelUtils.exportExcel(new ExportParams(), ResultVO.class, dataList);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream("/Users/burt/Desktop/" + exportExcelName + ".xlsx");
            workbook.write(fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ResultVO wrapperResultVO(int serialNumber, String confFile, String item, Map<String, Map<String, Map<String, Object>>> confFileEnvItemValueMap, String fileType) {
        ResultVO vo = new ResultVO();
        vo.setSerialNumber(serialNumber + "");
        vo.setConfFile(confFile);
        vo.setItem(item);
        vo.setDevValue(confFileEnvItemValueMap.get(confFile) != null ? (confFileEnvItemValueMap.get(confFile).get("dev") != null ? (confFileEnvItemValueMap.get(confFile).get("dev").get(item) != null ? confFileEnvItemValueMap.get(confFile).get("dev").get(item).toString() : "N/A") : "N/A") : "N/A");
        vo.setStageValue(confFileEnvItemValueMap.get(confFile) != null ? (confFileEnvItemValueMap.get(confFile).get("stage") != null ? (confFileEnvItemValueMap.get(confFile).get("stage").get(item) != null ? confFileEnvItemValueMap.get(confFile).get("stage").get(item).toString() : "N/A") : "N/A") : "N/A");
        vo.setProdValue(confFileEnvItemValueMap.get(confFile) != null ? (confFileEnvItemValueMap.get(confFile).get("prod") != null ? (confFileEnvItemValueMap.get(confFile).get("prod").get(item) != null ? confFileEnvItemValueMap.get(confFile).get("prod").get(item).toString() : "N/A") : "N/A") : "N/A");
        vo.setIsSame(vo.getDevValue().equals(vo.getStageValue()) && vo.getDevValue().equals(vo.getProdValue()) ? Constants.YES : Constants.NO);
        if (Constants.FILE_TYPE_PROPERTIES.equals(fileType)) {
            vo.setDevValue(new String(vo.getDevValue().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
            vo.setStageValue(new String(vo.getStageValue().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
            vo.setProdValue(new String(vo.getProdValue().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
        }
        return vo;
    }

}
