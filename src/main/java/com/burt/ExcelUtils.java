package com.burt;

import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.export.ExcelExportService;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.Collection;

public class ExcelUtils {

    /**
     * @param entity    Excel Title
     * @param pojoClass Excel Object Class
     * @param dataSet   Excel Data List
     */
    public static Workbook exportExcel(ExportParams entity, Class<?> pojoClass, Collection<?> dataSet) {
        Workbook workbook = new XSSFWorkbook();
        new ExcelExportService().createSheet(workbook, entity, pojoClass, dataSet);
        return workbook;
    }

}
