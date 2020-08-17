package ru.natlex.geo;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.natlex.geo.DAO.TaskDAO;
import ru.natlex.geo.DAO.SectionDAO;
import ru.natlex.geo.entity.GeologicalClass;
import ru.natlex.geo.entity.Section;
import ru.natlex.geo.entity.TaskRequest;
import java.io.BufferedInputStream;
import java.io.IOException;



@Service
@Scope("prototype")
public class XLSReader {
    TaskDAO taskDao;
    SectionDAO sectionDAO;

    @Autowired
    public void setTaskDao(TaskDAO taskDao) {
        this.taskDao = taskDao;
    }
    @Autowired
    public void setGeoDao(SectionDAO sectionDao) {
        this.sectionDAO = sectionDao;
    }

    @Async
    @Transactional
    public void read(BufferedInputStream stream, TaskRequest taskRequest) {
        HSSFWorkbook workbook = null;
        try {
            workbook = new HSSFWorkbook(stream);
            HSSFSheet sheet = workbook.getSheetAt(workbook.getActiveSheetIndex());
            if (sheet.getLastRowNum() == -1)
                throw new Exception("Sheet is empty");

            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    if (!checkHeader(row))
                        throw new Exception("Invalid table structure");
                } else {
                    readRow(row);
                }
            }
            taskRequest.setStatus(TaskStatus.DONE);
            taskDao.update(taskRequest);
        } catch (Exception e) {
            e.printStackTrace();
            taskRequest.setStatus(TaskStatus.ERROR);
            taskRequest.setError(e.getMessage());
            taskDao.update(taskRequest);
        } finally {
            try {
                if (workbook != null) workbook.close();
                if (stream != null) stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /** Считать строку и записать в БД */
    private void readRow(Row row) throws Exception {
        if (row.getLastCellNum() == -1) // empty row - skip
            return;

        Section s = new Section();
        s.setName(row.getCell(0).getStringCellValue());
        for (short i = 1; i < row.getLastCellNum(); i++) {
            GeologicalClass geo = new GeologicalClass();
            Cell cell = row.getCell(i);
            String name = getValue(cell);
            geo.setName(name);
            cell = row.getCell(++i);
            String code = getValue(cell);
            geo.setCode(code);
            s.addGeo(geo);
            geo.setSection(s);
        }
        sectionDAO.add(s);
    }

    /** Считаем, что файл валиден, если совпадает заголовок первого столбца */
    private boolean checkHeader(Row row) throws Exception {
        Cell cell = row.getCell(0);
        String val = getValue(cell);
        System.out.println("header " + val);
        return  val.equalsIgnoreCase("Section name");
    }

    /** Get string value or throw exception */
    private String getValue(Cell cell) throws Exception {
        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue();
        } else
            throw new Exception("Invalid table structure");
    }

}
