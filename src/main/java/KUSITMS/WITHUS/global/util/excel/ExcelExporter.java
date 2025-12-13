package KUSITMS.WITHUS.global.util.excel;

import KUSITMS.WITHUS.domain.application.application.dto.ApplicationResponseDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.List;

public class ExcelExporter {

    public static Workbook createExcel(List<ApplicationResponseDTO.Detail> list) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("지원서 상세");

        Row header = sheet.createRow(0);
        String[] columns = {
                "ID", "이름", "이메일", "전화번호", "성별", "전공",
                "지원 분야", "합불 상태",
                "서류 평균", "면접 평균",
                "주소", "생년월일", "학교",
                "지인 수"
        };

        for (int i = 0; i < columns.length; i++) {
            header.createCell(i).setCellValue(columns[i]);
        }

        int rowIdx = 1;
        for (var dto : list) {
            Row row = sheet.createRow(rowIdx++);

            row.createCell(0).setCellValue(dto.id());
            row.createCell(1).setCellValue(dto.name());
            row.createCell(2).setCellValue(dto.email());
            row.createCell(3).setCellValue(dto.phoneNumber());
            row.createCell(4).setCellValue(dto.gender() != null ? dto.gender().name() : "");
            row.createCell(5).setCellValue(dto.major());
            row.createCell(6).setCellValue(dto.appliedPosition());
            row.createCell(7).setCellValue(dto.status().name());
            row.createCell(8).setCellValue(dto.documentAverageScore());
            row.createCell(9).setCellValue(dto.interviewAverageScore());
            row.createCell(10).setCellValue(dto.address());
            row.createCell(11).setCellValue(dto.birthDate() != null ? dto.birthDate().toString() : "");
            row.createCell(12).setCellValue(dto.university());
            row.createCell(13).setCellValue(dto.acquaintanceCount());
        }

        return workbook;
    }

}
