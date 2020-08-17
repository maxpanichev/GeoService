package ru.natlex.geo;

import org.apache.poi.hssf.usermodel.*;
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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;


@Service
@Scope("prototype")
public class XLSBuilder {
    static final int COLUMN_WIDTH = 4000;
    SectionDAO dao;
    TaskDAO taskDao;

    @Autowired
    public void setDao(SectionDAO dao) {
        this.dao = dao;
    }

    @Autowired
    public void setTaskDao(TaskDAO taskDao) {
        this.taskDao = taskDao;
    }

    @Async
    public void build(TaskRequest taskRequest) {
        HSSFWorkbook workbook = null;
        ByteArrayOutputStream stream = null;
        try {
            List<Section> sections = dao.getAll();
            if (sections == null)
                return;

            workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet("Sections");
            buildHeader(sections, sheet);
            buildBody(sections, sheet);
            stream = new ByteArrayOutputStream();
            workbook.write(stream);
            workbook.close();
            taskRequest.setAttachment(stream.toByteArray());
            stream.close();
            taskRequest.setStatus(TaskStatus.DONE);
            taskDao.update(taskRequest);
        } catch (Exception e) {
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

    private void buildHeader(List<Section> sections, HSSFSheet sheet) {
        // find max geoClasses size to build header
        int maxGeoCnt = sections.stream().map(Section::getGeologicalClasses).mapToInt(List::size).max().getAsInt();
        HSSFRow header = sheet.createRow(0);
        HSSFCell headerCell = header.createCell(0);
        sheet.setColumnWidth(0, COLUMN_WIDTH);
        headerCell.setCellValue("Section name");

        int geoCnt = 1;
        for (int i=1; i<=maxGeoCnt*2; i++) {
            headerCell = header.createCell(i); // Class name header
            sheet.setColumnWidth(i, COLUMN_WIDTH);
            headerCell.setCellValue("Class " + geoCnt + " name");

            headerCell = header.createCell(++i); // Class code header
            sheet.setColumnWidth(i, COLUMN_WIDTH);
            headerCell.setCellValue("Class " + geoCnt + " code");

            geoCnt++;
        }
    }

    private void buildBody(List<Section> sections, HSSFSheet sheet) {
        int rowNum = 1;
        for (Section section: sections) {
            HSSFRow row = sheet.createRow(rowNum++);
            HSSFCell cell = row.createCell(0);
            cell.setCellValue(section.getName());
            int geoNum = 0;
            for (GeologicalClass geo: section.getGeologicalClasses()) {
                cell = row.createCell(++geoNum);
                cell.setCellValue(geo.getName());
                cell = row.createCell(++geoNum);
                cell.setCellValue(geo.getCode());
            }
        }
    }


}
