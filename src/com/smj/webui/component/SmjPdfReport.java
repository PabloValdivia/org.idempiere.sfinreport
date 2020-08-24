package com.smj.webui.component;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.smj.entity.*;
import org.compiere.apps.form.*;
import org.compiere.report.*;
import org.compiere.model.*;
import org.compiere.*;
import java.awt.*;
import org.compiere.util.*;
import java.util.*;


import java.io.*;
import java.math.*;
import java.text.*;


public class SmjPdfReport extends PdfPageEventHelper
{
    public CLogger log;
    private ByteArrayOutputStream baosPDF;
    private Font titleFont;
    private Font titleTableFont;
    private Font catFont;
    private Font subFont;
    private int cols;
    private LinkedList<ReportTO> data;
    private Document document;
    private PdfWriter writer;
    private PdfPTable table;
    private BaseFont helv;
    private PdfTemplate total;
    
    public SmjPdfReport() {
        this.log = CLogger.getCLogger((Class)Allocation.class);

        this.titleFont = new Font(Font.FontFamily.COURIER, 12, Font.NORMAL);
        this.titleTableFont = new Font(Font.FontFamily.COURIER, 10, Font.NORMAL);
        this.catFont = new Font(Font.FontFamily.COURIER, 10, Font.BOLD);
        this.subFont = new Font(Font.FontFamily.COURIER, 8, Font.NORMAL);
        
//       this.titleFont = new com.lowagie.text.Font(1, 15.0f, 3);
//       this.titleTableFont = new com.lowagie.text.Font(1, 12.0f, 3);
//       this.catFont = new com.lowagie.text.Font(1, 12.0f, 1);
//       this.subFont = new com.lowagie.text.Font(1, 10.0f, 0);
        
        this.cols = 0;
        this.data = null;
        this.document = null;
        this.writer = null;
        this.table = null;
    }
    
    public ByteArrayOutputStream generate(final LinkedList<ReportTO> dataReport, final String nameTrx, final String[] generalTitle, final String clientName, final String clientNIT, final String periodName, final String currencyName, final MReportColumn[] m_columns, final String codeFont, final String city, final Integer logoId) {
        this.baosPDF = new ByteArrayOutputStream();
        this.data = dataReport;
        final String[] fontPar = codeFont.split("-");
        final Integer lFont = Integer.parseInt(fontPar[2]);
        this.titleFont = FontFactory.getFont(fontPar[0], (float)(lFont + 5), 3);
        this.titleTableFont = FontFactory.getFont(fontPar[0], (float)(lFont + 2), 3);
        this.catFont = FontFactory.getFont(fontPar[0], (float)(lFont + 2), 1);
        this.subFont = FontFactory.getFont(fontPar[0], (float)lFont, 0);
        try {
            this.document = new Document(PageSize.LETTER, 20.0f, 20.0f, 20.0f, 40.0f);
            this.writer = PdfWriter.getInstance(this.document, (OutputStream)this.baosPDF);
            this.document.open();
            this.document.addTitle(generalTitle[0]);
            this.document.addAuthor("SmartJSP S.A.S.");
            this.document.addCreator("SmartJSP S.A.S.");
            this.onOpenDocument(this.writer, this.document);
            this.onEndPage(this.writer, this.document);
            java.awt.Image img;
            if (logoId > 0) {
                final MImage mimage = MImage.get(Env.getCtx(), (int)logoId);
                final byte[] imageData = mimage.getData();
                if (imageData !=null)
                	img = Toolkit.getDefaultToolkit().createImage(imageData);
                else
                	img = Adempiere.getImageLogoSmall(true);
            }
            else {
                img = Adempiere.getImageLogoSmall(true);
            }
            final com.itextpdf.text.Image logo = com.itextpdf.text.Image.getInstance(img, (Color)null);
            logo.scaleToFit(100.0f, 30.0f);
            this.document.add((Element)logo);
            final Paragraph genTitle = new Paragraph(this.dataNull(generalTitle[0]).toUpperCase(), this.titleFont);
            genTitle.setAlignment(1);
            this.document.add((Element)genTitle);
            final Paragraph clitName = new Paragraph(this.dataNull(clientName).toUpperCase(), this.titleFont);
            clitName.setAlignment(1);
            this.document.add((Element)clitName);
            final Paragraph cliCity = new Paragraph(this.dataNull(city).toUpperCase(), this.titleFont);
            cliCity.setAlignment(1);
            this.document.add((Element)cliCity);
            final Paragraph cliNIT = new Paragraph(this.dataNull(clientNIT).toUpperCase(), this.titleFont);
            cliNIT.setAlignment(1);
            this.document.add((Element)cliNIT);
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
            final Paragraph perName = new Paragraph(this.dataNull(pn).toUpperCase(), this.titleTableFont);
            perName.setAlignment(1);
            this.document.add((Element)perName);
            final Paragraph currency = new Paragraph(this.dataNull(currencyName), this.titleTableFont);
            currency.setAlignment(1);
            this.addEmptyLine(currency, 2);
            this.document.add((Element)currency);
            this.cols = m_columns.length + 2;
            final float[] columnWidths = new float[this.cols];
            columnWidths[0] = 1.0f;
            columnWidths[1] = 3.0f;
            for (int i = 2; i < this.cols; ++i) {
                columnWidths[i] = 1.0f;
            }
            this.table = new PdfPTable(columnWidths);
            PdfPCell cellTitle = new PdfPCell((Phrase)new Paragraph(Msg.translate(Env.getCtx(), "name").toUpperCase(), this.catFont));
            cellTitle.setHorizontalAlignment(2);
             cellTitle.setBackgroundColor(BaseColor.LIGHT_GRAY);
            this.table.addCell(cellTitle);
            cellTitle = new PdfPCell((Phrase)new Paragraph(Msg.translate(Env.getCtx(), "description").toUpperCase(), this.catFont));
            cellTitle.setHorizontalAlignment(0);
             cellTitle.setBackgroundColor(BaseColor.LIGHT_GRAY);
            this.table.addCell(cellTitle);
            for (final MReportColumn mcol : m_columns) {
                final String colName = mcol.getName();
                cellTitle = new PdfPCell((Phrase)new Paragraph(colName.toUpperCase(), this.catFont));
                cellTitle.setHorizontalAlignment(2);
                cellTitle.setBackgroundColor(BaseColor.LIGHT_GRAY);
                this.table.addCell(cellTitle);
            }
            this.reportTable();
            this.document.add((Element)this.table);
            this.onEndPage(this.writer, this.document);
            this.onCloseDocument(this.writer, this.document);
            this.document.close();
        }
        catch (Exception e) {
            System.out.println("SMpdfReport(generar)ERROR:: al crear el documento PDF");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return this.baosPDF;
    }
    
    public void reportTable() {
        for (final ReportTO rpt : this.data) {
            if (rpt.getSmj_reportline() != null && rpt.getSmj_reportline().equals("T")) {
                final PdfPCell title = new PdfPCell((Phrase)new Paragraph(this.dataNull(rpt.getDescription()), this.titleTableFont));
                title.setColspan(this.cols);
                title.setHorizontalAlignment(1);
                title.setBorder(0);
                this.table.addCell(title);
            }
            else if (rpt.getSmj_reportline() != null && rpt.getSmj_reportline().equals("L")) {
                final PdfPCell line = new PdfPCell((Phrase)new Paragraph("", this.subFont));
                line.setColspan(this.cols);
                line.setBorderWidthLeft(0.0f);
                line.setBorderWidthRight(0.0f);
                line.setBorderWidthTop(0.0f);
                line.setBorderColorBottom(BaseColor.BLACK);
                this.table.addCell(line);
            }
            else if (rpt.getSmj_reportline() != null && rpt.getSmj_reportline().equals("X")) {
                this.simpleLine();
            }
            else if (rpt.getSmj_reportline() != null && rpt.getSmj_reportline().equals("Z")) {
                for (int j = 0; j < 2; ++j) {
                    this.simpleLine();
                }
            }
            else if (rpt.getSmj_reportline() != null && rpt.getSmj_reportline().equals("D")) {
                PdfPCell tableCell = new PdfPCell(new Phrase(""));
                tableCell.setBorder(0);
                this.table.addCell(tableCell);
                tableCell = new PdfPCell(new Phrase(""));
                tableCell.setBorderWidthLeft(0.0f);
                tableCell.setBorderWidthRight(0.0f);
                tableCell.setBorderWidthTop(0.0f);
                tableCell.setBorderColorBottom(BaseColor.BLACK);
                this.table.addCell(tableCell);
                for (int i = 0; i < this.cols - 2; ++i) {
                    tableCell = new PdfPCell(new Phrase(""));
                    tableCell.setBorder(0);
                    this.table.addCell(tableCell);
                }
            }
            else if (rpt.getSmj_reportline() != null && rpt.getSmj_reportline().equals("S")) {
                final PdfPCell line = new PdfPCell((Phrase)new Paragraph("         "));
                line.setColspan(this.cols);
                line.setBorder(0);
                this.table.addCell(line);
            }
            else if (rpt.getSmj_hierarchylevel() != null && rpt.getSmj_hierarchylevel() > 0) {
                String jerarchy = "";
                for (int k = 1; k <= rpt.getSmj_hierarchylevel(); ++k) {
                    jerarchy += "   ";
                }
                final PdfPCell line2 = new PdfPCell((Phrase)new Paragraph(jerarchy + this.dataNull(rpt.getDescription()), this.catFont));
                line2.setColspan(this.cols);
                line2.setHorizontalAlignment(0);
                line2.setBorder(0);
                this.table.addCell(line2);
            }
            else {
                if (rpt.getDescription() == null) {
                    continue;
                }
                PdfPCell tableCell = new PdfPCell(new Phrase(this.dataNull(rpt.getName()), this.subFont));
                tableCell.setBorder(0);
                tableCell.setHorizontalAlignment(0);
                this.table.addCell(tableCell);
                tableCell = new PdfPCell(new Phrase(this.dataNull(rpt.getDescription()), this.subFont));
                tableCell.setBorder(0);
                this.table.addCell(tableCell);
                if (this.cols >= 3) {
                    tableCell = new PdfPCell(new Phrase(this.formatValue(rpt.getCol_0()), this.subFont));
                    tableCell.setBorder(0);
                    tableCell.setHorizontalAlignment(2);
                    this.table.addCell(tableCell);
                }
                if (this.cols >= 4) {
                    tableCell = new PdfPCell(new Phrase(this.formatValue(rpt.getCol_1()), this.subFont));
                    tableCell.setBorder(0);
                    tableCell.setHorizontalAlignment(2);
                    this.table.addCell(tableCell);
                }
                if (this.cols >= 5) {
                    tableCell = new PdfPCell(new Phrase(this.formatValue(rpt.getCol_2()), this.subFont));
                    tableCell.setBorder(0);
                    tableCell.setHorizontalAlignment(2);
                    this.table.addCell(tableCell);
                }
                if (this.cols >= 6) {
                    tableCell = new PdfPCell(new Phrase(this.formatValue(rpt.getCol_3()), this.subFont));
                    tableCell.setBorder(0);
                    tableCell.setHorizontalAlignment(2);
                    this.table.addCell(tableCell);
                }
                if (this.cols >= 7) {
                    tableCell = new PdfPCell(new Phrase(this.formatValue(rpt.getCol_4()), this.subFont));
                    tableCell.setBorder(0);
                    tableCell.setHorizontalAlignment(2);
                    this.table.addCell(tableCell);
                }
                if (this.cols >= 8) {
                    tableCell = new PdfPCell(new Phrase(this.formatValue(rpt.getCol_5()), this.subFont));
                    tableCell.setBorder(0);
                    tableCell.setHorizontalAlignment(2);
                    this.table.addCell(tableCell);
                }
                if (this.cols >= 9) {
                    tableCell = new PdfPCell(new Phrase(this.formatValue(rpt.getCol_6()), this.subFont));
                    tableCell.setBorder(0);
                    tableCell.setHorizontalAlignment(2);
                    this.table.addCell(tableCell);
                }
                if (this.cols >= 10) {
                    tableCell = new PdfPCell(new Phrase(this.formatValue(rpt.getCol_7()), this.subFont));
                    tableCell.setBorder(0);
                    tableCell.setHorizontalAlignment(2);
                    this.table.addCell(tableCell);
                }
                if (this.cols >= 11) {
                    tableCell = new PdfPCell(new Phrase(this.formatValue(rpt.getCol_8()), this.subFont));
                    tableCell.setBorder(0);
                    tableCell.setHorizontalAlignment(2);
                    this.table.addCell(tableCell);
                }
                if (this.cols >= 12) {
                    tableCell = new PdfPCell(new Phrase(this.formatValue(rpt.getCol_9()), this.subFont));
                    tableCell.setBorder(0);
                    tableCell.setHorizontalAlignment(2);
                    this.table.addCell(tableCell);
                }
                if (this.cols >= 13) {
                    tableCell = new PdfPCell(new Phrase(this.formatValue(rpt.getCol_10()), this.subFont));
                    tableCell.setBorder(0);
                    tableCell.setHorizontalAlignment(2);
                    this.table.addCell(tableCell);
                }
                if (this.cols >= 14) {
                    tableCell = new PdfPCell(new Phrase(this.formatValue(rpt.getCol_11()), this.subFont));
                    tableCell.setBorder(0);
                    tableCell.setHorizontalAlignment(2);
                    this.table.addCell(tableCell);
                }
                if (this.cols >= 15) {
                    tableCell = new PdfPCell(new Phrase(this.formatValue(rpt.getCol_12()), this.subFont));
                    tableCell.setBorder(0);
                    tableCell.setHorizontalAlignment(2);
                    this.table.addCell(tableCell);
                }
                if (this.cols >= 16) {
                    tableCell = new PdfPCell(new Phrase(this.formatValue(rpt.getCol_13()), this.subFont));
                    tableCell.setBorder(0);
                    tableCell.setHorizontalAlignment(2);
                    this.table.addCell(tableCell);
                }
                if (this.cols >= 17) {
                    tableCell = new PdfPCell(new Phrase(this.formatValue(rpt.getCol_14()), this.subFont));
                    tableCell.setBorder(0);
                    tableCell.setHorizontalAlignment(2);
                    this.table.addCell(tableCell);
                }
                if (this.cols >= 18) {
                    tableCell = new PdfPCell(new Phrase(this.formatValue(rpt.getCol_15()), this.subFont));
                    tableCell.setBorder(0);
                    tableCell.setHorizontalAlignment(2);
                    this.table.addCell(tableCell);
                }
                if (this.cols >= 19) {
                    tableCell = new PdfPCell(new Phrase(this.formatValue(rpt.getCol_16()), this.subFont));
                    tableCell.setBorder(0);
                    tableCell.setHorizontalAlignment(2);
                    this.table.addCell(tableCell);
                }
                if (this.cols >= 20) {
                    tableCell = new PdfPCell(new Phrase(this.formatValue(rpt.getCol_17()), this.subFont));
                    tableCell.setBorder(0);
                    tableCell.setHorizontalAlignment(2);
                    this.table.addCell(tableCell);
                }
                if (this.cols >= 21) {
                    tableCell = new PdfPCell(new Phrase(this.formatValue(rpt.getCol_18()), this.subFont));
                    tableCell.setBorder(0);
                    tableCell.setHorizontalAlignment(2);
                    this.table.addCell(tableCell);
                }
                if (this.cols >= 22) {
                    tableCell = new PdfPCell(new Phrase(this.formatValue(rpt.getCol_19()), this.subFont));
                    tableCell.setBorder(0);
                    tableCell.setHorizontalAlignment(2);
                    this.table.addCell(tableCell);
                }
                if (this.cols < 23) {
                    continue;
                }
                tableCell = new PdfPCell(new Phrase(this.formatValue(rpt.getCol_20()), this.subFont));
                tableCell.setBorder(0);
                tableCell.setHorizontalAlignment(2);
                this.table.addCell(tableCell);
            }
        }
    }
    
    private void simpleLine() {
        PdfPCell tableCell = new PdfPCell(new Phrase(""));
        tableCell.setBorder(0);
        this.table.addCell(tableCell);
        tableCell = new PdfPCell(new Phrase(""));
        tableCell.setBorder(0);
        this.table.addCell(tableCell);
        for (int i = 0; i < this.cols - 2; ++i) {
            tableCell = new PdfPCell(new Phrase(""));
            tableCell.setBorderWidthLeft(0.0f);
            tableCell.setBorderWidthRight(0.0f);
            tableCell.setBorderWidthTop(0.0f);
            tableCell.setBorderColorBottom(BaseColor.BLACK);
            this.table.addCell(tableCell);
        }
    }
    
    public void onOpenDocument(final PdfWriter writer, final Document document) {
        (this.total = writer.getDirectContent().createTemplate(100.0f, 100.0f)).setBoundingBox(new com.itextpdf.text.Rectangle(-20.0f, -20.0f, 100.0f, 100.0f));
        try {
            this.helv = BaseFont.createFont("Helvetica", "Cp1252", false);
        }
        catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }
    
    public void onEndPage(final PdfWriter writer, final Document document) {
        final PdfContentByte cb = writer.getDirectContent();
        cb.saveState();
        final Date date = new Date();
        final String textLeft = "Pagina " + writer.getPageNumber() + " de ";
        final String textRight = date + "         " + "Pagina " + writer.getPageNumber() + " de ";
        final float textBase = document.bottom() - 20.0f;
        final float textSizeLeft = this.helv.getWidthPoint(textLeft, 12.0f);
        final float textSizeRigth = this.helv.getWidthPoint(textRight, 12.0f);
        cb.beginText();
        cb.setFontAndSize(this.helv, 12.0f);
        if (writer.getPageNumber() % 2 == 1) {
            cb.setTextMatrix(document.left(), textBase);
            cb.showText(textLeft + "         " + date);
            cb.endText();
            cb.addTemplate(this.total, document.left() + textSizeLeft, textBase);
        }
        else {
            final float adjust = this.helv.getWidthPoint("", 12.0f);
            cb.setTextMatrix(document.right() - textSizeRigth - adjust, textBase);
            cb.showText(textRight);
            cb.endText();
            cb.addTemplate(this.total, document.right() - adjust, textBase);
        }
        cb.restoreState();
    }
    
    public void onCloseDocument(final PdfWriter writer, final Document document) {
        this.total.beginText();
        this.total.setFontAndSize(this.helv, 12.0f);
        this.total.setTextMatrix(0.0f, 0.0f);
        this.total.showText(String.valueOf(writer.getPageNumber()));
        this.total.endText();
    }
    
    public File tofile(final byte[] buf, final String[] generalTitle) {
        final File file = new File(generalTitle[0] + ".pdf");
        try {
            final FileOutputStream fos = new FileOutputStream(file);
            fos.write(buf);
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
    
    private void addEmptyLine(final Paragraph paragraph, final int number) {
        for (int i = 0; i < number; ++i) {
            paragraph.add((Element)new Paragraph(" "));
        }
    }
    
    private String dataNull(final String data) {
        if (data == null) {
            return "";
        }
        return data;
    }
    
    private String formatValue(final BigDecimal data) {
        if (data == null) {
            return "";
        }
        final DecimalFormat frm = new DecimalFormat("###,###,###,##0.00");
        return frm.format(data.setScale(2));
    }
}
