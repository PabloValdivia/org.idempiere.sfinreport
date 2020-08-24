package com.smj.webui.component;

import com.smj.entity.*;

import org.compiere.report.*;
import org.compiere.model.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.hssf.usermodel.*;
import org.compiere.util.*;
import java.util.*;
import java.io.*;
import java.math.*;
import java.text.*;

public class SmjXlsReport
{
    private int cols;
    private short endRegion;
    
    public SmjXlsReport() {
        this.cols = 0;
        this.endRegion = 2;
    }
    
     //disabled class for debug because have problem xls lib in v6.2
   
    @SuppressWarnings("deprecation")
	public HSSFWorkbook generate(final LinkedList<ReportTO> data, final String[] generalTitle, final String clientName, final String clientNIT, final String periodName, final String currencyName, final MReportColumn[] m_columns, final String city, final Integer logoId) {
        int fila = 0;
        this.cols = m_columns.length + 2;
        this.endRegion = (short)(this.cols - 1);
        try {
            HSSFWorkbook book = new HSSFWorkbook();
            HSSFFont font = book.createFont();
            font.setBoldweight((short)700);
            HSSFSheet sheet = book.createSheet(generalTitle[0]);
            //Titulos Pricipales
            font.setFontHeightInPoints((short)10);
            font.setFontName("Arial");
            font.setBoldweight((short)700);
            HSSFCellStyle cellStyle = book.createCellStyle();
            cellStyle.setWrapText(true);
            cellStyle.setAlignment((short)2);
            cellStyle.setVerticalAlignment((short)0);
            cellStyle.setFont(font);
            if (logoId > 0) {
                final MImage mimage = MImage.get(Env.getCtx(), (int)logoId);
                final byte[] imageData = mimage.getData();
                if (imageData !=null){
                	final HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
                    final HSSFClientAnchor anchor = new HSSFClientAnchor(100, 50, 200, 255, (short)0, 0, (short)1, 1);
                    anchor.setAnchorType(2);
                    final int pictureIndex = book.addPicture(imageData, 6);
                    patriarch.createPicture(anchor, pictureIndex);
                    for (int i = 0; i < 5; ++i) {
                        final HSSFRow row = sheet.createRow(fila++);
                    }	
                }
                	
            }
            HSSFRow row = sheet.createRow(fila++);
            HSSFRichTextString text = new HSSFRichTextString(generalTitle[0]);
            HSSFCell cell = row.createCell((short)0);
            cell.setCellStyle(cellStyle);
            cell.setCellType(1);
            cell.setCellValue(text);
            CellRangeAddress region = new CellRangeAddress(fila - 1, fila - 1, 0, this.endRegion);
            sheet.addMergedRegion(region);        
            row = sheet.createRow(fila++);
            text = new HSSFRichTextString(clientName);
            cell = row.createCell((short)0);
            cell.setCellStyle(cellStyle);
            cell.setCellType(1);
            cell.setCellValue(text);
            region = new CellRangeAddress(fila - 1, fila - 1, 0, this.endRegion);      
            sheet.addMergedRegion(region);
            row = sheet.createRow(fila++);
            text = new HSSFRichTextString(city);
            cell = row.createCell((short)0);
            cell.setCellStyle(cellStyle);
            cell.setCellType(1);
            cell.setCellValue(text);
            region = new CellRangeAddress(fila - 1, fila - 1, 0, this.endRegion);
            sheet.addMergedRegion(region);
            row = sheet.createRow(fila++);
            text = new HSSFRichTextString(clientNIT);
            cell = row.createCell((short)0);
            cell.setCellStyle(cellStyle);
            cell.setCellType(1);
            cell.setCellValue(text);
            region = new CellRangeAddress(fila - 1, fila - 1, 0, this.endRegion);
            sheet.addMergedRegion(region);
            String pn = "";
            if (generalTitle[1] != null && generalTitle[1].length() > 0) {
                pn = generalTitle[1] + " " + periodName;
            }
            else {
                pn = periodName;
            }
            if (generalTitle[2] != null && generalTitle[2].length() > 0) {
                pn = pn + " " + generalTitle[2];
            }
            row = sheet.createRow(fila++);
            text = new HSSFRichTextString(pn);
            cell = row.createCell((short)0);
            cell.setCellStyle(cellStyle);
            cell.setCellType(1);
            cell.setCellValue(text);
            region = new CellRangeAddress(fila - 1, fila - 1, 0, this.endRegion);
            sheet.addMergedRegion(region);
            row = sheet.createRow(fila++);
            text = new HSSFRichTextString(currencyName);
            cell = row.createCell((short)0);
            cell.setCellStyle(cellStyle);
            cell.setCellType(1);
            cell.setCellValue(text);
            region = new CellRangeAddress(fila - 1, fila - 1, 0, this.endRegion);
            sheet.addMergedRegion(region);
            row = sheet.createRow(fila++);
            this.titleTable(book, sheet, fila++, m_columns);
            this.reportTable(book, data, sheet, fila);
            return book;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private void titleTable(final HSSFWorkbook book, final HSSFSheet sheet, final int fila, final MReportColumn[] m_columns) {
        short col = 0;
        final HSSFFont font = book.createFont();
        //Encabezados de Columnas
        font.setFontHeightInPoints((short)10);
        font.setFontName("Arial");
        font.setBoldweight((short)10);
        final HSSFCellStyle cellStyle = book.createCellStyle();
        cellStyle.setWrapText(true);
        cellStyle.setAlignment((short)5);
        cellStyle.setVerticalAlignment((short)0);
        cellStyle.setFont(font);
        final HSSFRow row = sheet.createRow(fila);
        HSSFRichTextString text = new HSSFRichTextString(Msg.translate(Env.getCtx(), "name").toUpperCase());
        final HSSFRow hssfRow = row;
        final short n = col;
        ++col;
        HSSFCell cell = hssfRow.createCell(n);
        cell.setCellStyle(cellStyle);
        cell.setCellType(1);
        cell.setCellValue(text);
        text = new HSSFRichTextString(Msg.translate(Env.getCtx(), "description").toUpperCase());
        final HSSFRow hssfRow2 = row;
        final short n2 = col;
        ++col;
        cell = hssfRow2.createCell(n2);
        cell.setCellStyle(cellStyle);
        cell.setCellType(1);
        cell.setCellValue(text);
        for (final MReportColumn mcol : m_columns) {
            final String colName = mcol.getName();
            text = new HSSFRichTextString(colName.toUpperCase());
            final HSSFRow hssfRow3 = row;
            final short n3 = col;
            ++col;
            cell = hssfRow3.createCell(n3);
            cell.setCellStyle(cellStyle);
            cell.setCellType(1);
            cell.setCellValue(text);
        }
    }
    
    public void reportTable(final HSSFWorkbook book, final LinkedList<ReportTO> data, final HSSFSheet sheet, int fila) {
        final HSSFFont font = book.createFont();
        font.setFontHeightInPoints((short)10);
        font.setFontName("Arial");
        final Iterator<ReportTO> itRep = data.iterator();
        Boolean newRow = false;
        sheet.setColumnWidth((short)0, (short)3328);
        sheet.setColumnWidth((short)1, (short)15360);
        for (int i = 2; i < this.cols; ++i) {
            sheet.setColumnWidth((short)i, (short)3840);
        }
        HSSFCellStyle cellStyle = book.createCellStyle();
        HSSFCellStyle cellStyleD = book.createCellStyle();
        HSSFCellStyle cellStyleN = book.createCellStyle();
        while (itRep.hasNext()) {
            final short col = 0;
            final ReportTO rpt = itRep.next();
            if (!newRow) {
                cellStyle = book.createCellStyle();
                cellStyleD = book.createCellStyle();
                cellStyleN = book.createCellStyle();
            }
            newRow = false;
            if (rpt.getSmj_reportline() != null && rpt.getSmj_reportline().equals("T")) {
                final HSSFRow row = sheet.createRow(fila++);
                final HSSFFont fontT = book.createFont();
                //Titulo Centrado
                fontT.setFontHeightInPoints((short)10);
                fontT.setFontName("Arial");
                fontT.setBoldweight((short)700);
                final HSSFCellStyle cellStyleT = book.createCellStyle();
                cellStyleT.setWrapText(true);
                cellStyleT.setAlignment((short)2);
                cellStyleT.setVerticalAlignment((short)0);
                cellStyleT.setFont(fontT);
                final CellRangeAddress region = new CellRangeAddress(fila - 1, fila - 1, 0, this.endRegion);
                sheet.addMergedRegion(region);
                final HSSFRichTextString text = new HSSFRichTextString(rpt.getDescription());
                final HSSFCell cellT = row.createCell(col);
                cellT.setCellStyle(cellStyleT);
                cellT.setCellValue(text);
                newRow = true;
            }
            else if (rpt.getSmj_reportline() != null && rpt.getSmj_reportline().equals("L")) {
                cellStyle.setWrapText(true);
                cellStyle.setBorderTop((short)2);
                cellStyle.setBottomBorderColor((short)8);
                cellStyleD.setWrapText(true);
                cellStyleD.setBorderTop((short)2);
                cellStyleD.setBottomBorderColor((short)8);
                cellStyleN.setWrapText(true);
                cellStyleN.setBorderTop((short)2);
                cellStyleN.setBottomBorderColor((short)8);
                newRow = true;
            }
            else if (rpt.getSmj_reportline() != null && rpt.getSmj_reportline().equals("X")) {
                cellStyle.setWrapText(true);
                cellStyle.setVerticalAlignment((short)0);
                cellStyle.setBorderTop((short)2);
                cellStyle.setBottomBorderColor((short)8);
                newRow = true;
            }
            else if (rpt.getSmj_reportline() != null && rpt.getSmj_reportline().equals("Z")) {
                cellStyle.setWrapText(true);
                cellStyle.setVerticalAlignment((short)0);
                cellStyle.setBorderTop((short)6);
                cellStyle.setBottomBorderColor((short)8);
                final HSSFRow row = sheet.createRow(fila++);
                final ReportTO rptD = new ReportTO();
                this.putRow(cellStyle, cellStyleD, cellStyleN, sheet, row, fila, rptD);
                cellStyle = book.createCellStyle();
                newRow = true;
            }
            else if (rpt.getSmj_reportline() != null && rpt.getSmj_reportline().equals("D")) {
                cellStyleD.setWrapText(true);
                cellStyleD.setVerticalAlignment((short)0);
                cellStyleD.setBorderTop((short)2);
                cellStyleD.setBottomBorderColor((short)8);
                newRow = true;
            }
            else if (rpt.getSmj_reportline() != null && rpt.getSmj_reportline().equals("S")) {
                final HSSFRow row = sheet.createRow(fila++);
                newRow = true;
            }
            else if (rpt.getSmj_hierarchylevel() != null && rpt.getSmj_hierarchylevel() > 0) {
                final HSSFRow row = sheet.createRow(fila++);
                String jerarchy = "";
                for (int j = 1; j <= rpt.getSmj_hierarchylevel(); ++j) {
                    jerarchy += "   ";
                }
                final CellRangeAddress region2 = new CellRangeAddress(fila - 1, fila - 1, 0, this.endRegion);
                sheet.addMergedRegion(region2);
                final HSSFRichTextString text = new HSSFRichTextString(jerarchy + rpt.getDescription());
                final HSSFCell cellJ = row.createCell(col);
                cellJ.setCellValue(text);
                newRow = true;
            }
            else {
                final HSSFRow row = sheet.createRow(fila++);
                this.putRow(cellStyle, cellStyleD, cellStyleN, sheet, row, fila, rpt);
            }
        }
    }
    
    private void putRow(final HSSFCellStyle cellStyle, final HSSFCellStyle cellStyleD, final HSSFCellStyle cellStyleN, final HSSFSheet sheet, final HSSFRow row, final int fila, final ReportTO rpt) {
        short col = 0;
        cellStyle.setAlignment((short)3);
        HSSFRichTextString text = new HSSFRichTextString(rpt.getName());
        final short n = col;
        ++col;
        HSSFCell cell = row.createCell(n);
        cell.setCellStyle(cellStyleN);
        cell.setCellValue(text);
        text = new HSSFRichTextString(rpt.getDescription());
        cell.setCellStyle(cellStyleD);
        final short n2 = col;
        ++col;
        cell = row.createCell(n2);
        cell.setCellValue(text);
        if (this.cols >= 3) {
            text = new HSSFRichTextString(this.formatValue(rpt.getCol_0()));
            final short n3 = col;
            ++col;
            cell = row.createCell(n3);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(text);
        }
        if (this.cols >= 4) {
            text = new HSSFRichTextString(this.formatValue(rpt.getCol_1()));
            final short n4 = col;
            ++col;
            cell = row.createCell(n4);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(text);
        }
        if (this.cols >= 5) {
            text = new HSSFRichTextString(this.formatValue(rpt.getCol_2()));
            final short n5 = col;
            ++col;
            cell = row.createCell(n5);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(text);
        }
        if (this.cols >= 6) {
            text = new HSSFRichTextString(this.formatValue(rpt.getCol_3()));
            final short n6 = col;
            ++col;
            cell = row.createCell(n6);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(text);
        }
        if (this.cols >= 7) {
            text = new HSSFRichTextString(this.formatValue(rpt.getCol_4()));
            final short n7 = col;
            ++col;
            cell = row.createCell(n7);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(text);
        }
        if (this.cols >= 8) {
            text = new HSSFRichTextString(this.formatValue(rpt.getCol_5()));
            final short n8 = col;
            ++col;
            cell = row.createCell(n8);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(text);
        }
        if (this.cols >= 9) {
            text = new HSSFRichTextString(this.formatValue(rpt.getCol_6()));
            final short n9 = col;
            ++col;
            cell = row.createCell(n9);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(text);
        }
        if (this.cols >= 10) {
            text = new HSSFRichTextString(this.formatValue(rpt.getCol_7()));
            final short n10 = col;
            ++col;
            cell = row.createCell(n10);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(text);
        }
        if (this.cols >= 11) {
            text = new HSSFRichTextString(this.formatValue(rpt.getCol_8()));
            final short n11 = col;
            ++col;
            cell = row.createCell(n11);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(text);
        }
        if (this.cols >= 12) {
            text = new HSSFRichTextString(this.formatValue(rpt.getCol_9()));
            final short n12 = col;
            ++col;
            cell = row.createCell(n12);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(text);
        }
        if (this.cols >= 13) {
            text = new HSSFRichTextString(this.formatValue(rpt.getCol_10()));
            final short n13 = col;
            ++col;
            cell = row.createCell(n13);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(text);
        }
        if (this.cols >= 14) {
            text = new HSSFRichTextString(this.formatValue(rpt.getCol_11()));
            final short n14 = col;
            ++col;
            cell = row.createCell(n14);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(text);
        }
        if (this.cols >= 15) {
            text = new HSSFRichTextString(this.formatValue(rpt.getCol_12()));
            final short n15 = col;
            ++col;
            cell = row.createCell(n15);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(text);
        }
        if (this.cols >= 16) {
            text = new HSSFRichTextString(this.formatValue(rpt.getCol_13()));
            final short n16 = col;
            ++col;
            cell = row.createCell(n16);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(text);
        }
        if (this.cols >= 17) {
            text = new HSSFRichTextString(this.formatValue(rpt.getCol_14()));
            final short n17 = col;
            ++col;
            cell = row.createCell(n17);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(text);
        }
        if (this.cols >= 18) {
            text = new HSSFRichTextString(this.formatValue(rpt.getCol_15()));
            final short n18 = col;
            ++col;
            cell = row.createCell(n18);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(text);
        }
        if (this.cols >= 19) {
            text = new HSSFRichTextString(this.formatValue(rpt.getCol_16()));
            final short n19 = col;
            ++col;
            cell = row.createCell(n19);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(text);
        }
        if (this.cols >= 20) {
            text = new HSSFRichTextString(this.formatValue(rpt.getCol_17()));
            final short n20 = col;
            ++col;
            cell = row.createCell(n20);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(text);
        }
        if (this.cols >= 21) {
            text = new HSSFRichTextString(this.formatValue(rpt.getCol_18()));
            final short n21 = col;
            ++col;
            cell = row.createCell(n21);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(text);
        }
        if (this.cols >= 22) {
            text = new HSSFRichTextString(this.formatValue(rpt.getCol_19()));
            final short n22 = col;
            ++col;
            cell = row.createCell(n22);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(text);
        }
        if (this.cols >= 23) {
            text = new HSSFRichTextString(this.formatValue(rpt.getCol_20()));
            final short n23 = col;
            ++col;
            cell = row.createCell(n23);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(text);
        }
    }
    
    public File tofile(final HSSFWorkbook wb, final String[] generalTitle) {
        final File file = new File(generalTitle[0] + ".xls");
        try {
            final FileOutputStream fos = new FileOutputStream(file);
            wb.write(fos);
            fos.flush();
            fos.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e2) {
            e2.printStackTrace();
        }
        return file;
    }
    
    private String formatValue(final BigDecimal data) {
        if (data == null) {
            return "";
        }
        final DecimalFormat frm = new DecimalFormat("###,###,###,##0.00");
        return frm.format(data.setScale(2));
    }
 
}

