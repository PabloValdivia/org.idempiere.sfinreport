package com.smj.webui.component;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.LinkedList;
import java.util.Properties;
import java.util.logging.Level;

import org.adempiere.exceptions.AdempiereException;
import org.adempiere.webui.panel.StatusBarPanel;
import org.adempiere.webui.window.FDialog;
import org.adempiere.webui.window.ZkReportViewer;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.compiere.model.MClientInfo;
import org.compiere.model.MOrgInfo;
import org.compiere.model.MRole;
import org.compiere.report.MReportColumn;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.zkoss.zul.Iframe;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Center;
import org.zkoss.zul.North;
import org.zkoss.zul.Separator;
import org.zkoss.zul.Toolbar;

import com.smj.entity.ReportTO;


public class SMJReportViewer extends org.adempiere.webui.component.Window implements org.zkoss.zk.ui.event.EventListener
{
    private static final long serialVersionUID = 4640088641140012438L;
    private int m_WindowNo;
    private Properties m_ctx;
    private int m_AD_Table_ID;
    private static CLogger log;
    private StatusBarPanel statusBar;
    private Iframe iframe;
    private Integer reportId;
    private File filePdf;
    private File fileXls;
    private ByteArrayOutputStream baosPDF;
    private String trxName;
    private Integer reportLineSetId;
    private Integer p_C_Period_ID;
    private Integer p_AD_PrintFont_ID;
    private LinkedList<ReportTO> data;
    private String[] generalTitle;
    private String clientName;
    private String clientNIT;
    private String periodName;
    private String currencyName;
    private String codeFont;
    private Toolbar toolBar;
    private org.adempiere.webui.component.Listbox previewType;
    private MReportColumn[] m_columns;
    private String city;
    private Integer logoId;
    
    public SMJReportViewer(final Integer idReport, final String nameTrx, final Integer idReportLineSet, final Integer C_Period_ID, final Integer AD_PrintFont_ID, final MReportColumn[] columns) {
    	super();
        this.m_AD_Table_ID = 0;
        
        this.trxName = "";
        this.data = null;
        this.toolBar = new Toolbar();
        this.statusBar = new StatusBarPanel();
        this.previewType = new org.adempiere.webui.component.Listbox();
        this.reportId = idReport;
        this.trxName = nameTrx;
        this.reportLineSetId = idReportLineSet;
        this.p_C_Period_ID = C_Period_ID;
        this.p_AD_PrintFont_ID = AD_PrintFont_ID;
        this.m_columns = columns;
        if (!MRole.getDefault().isCanReport(this.m_AD_Table_ID)) {
            this.onClose();
        }
        try {
            this.m_ctx = Env.getCtx();
            this.dynInit();
            this.init();
        }
        catch (Exception e) {
            SMJReportViewer.log.log(Level.SEVERE, "", (Throwable)e);
            FDialog.error(this.m_WindowNo, (Component)this, "LoadError", e.getLocalizedMessage());
            this.onClose();
        }
    }
    
    private void init() {
        final org.zkoss.zul.Borderlayout layout = new org.zkoss.zul.Borderlayout();
        layout.setStyle("position: absolute; height: 99%; width: 99%");
        this.appendChild((Component)layout);
        this.setStyle("width: 100%; height: 100%; position: absolute");
        this.toolBar.setHeight("26px");
        this.previewType.setMold("select");
        this.previewType.appendItem("PDF", "PDF");
        this.previewType.appendItem("Excel", "XLS");
        this.toolBar.appendChild((Component)this.previewType);
        this.previewType.addEventListener("onSelect", (org.zkoss.zk.ui.event.EventListener)this);
        this.toolBar.appendChild((Component)new Separator("vertical"));
        this.previewType.setSelectedIndex(0);
        final North north = new North();
        layout.appendChild((Component)north);
        north.appendChild((Component)this.toolBar);
        final Center center = new Center();
        center.setFlex(true);
        layout.appendChild((Component)center);
        (this.iframe = new Iframe()).setId("reportFrame");
        this.iframe.setHeight("100%");
        this.iframe.setWidth("100%");
        center.appendChild((Component)this.iframe);
        try {
            this.renderReport("PDF");
        }
        catch (Exception e) {
            throw new AdempiereException("Failed to render report", (Throwable)e);
        }
        this.iframe.setAutohide(true);
        this.setBorder("normal");
    }
    
    private void renderReport(final String type) throws Exception {
        AMedia media = null;
        final String path = System.getProperty("java.io.tmpdir");
        final String prefix = this.makePrefix("financial");
        if (SMJReportViewer.log.isLoggable(Level.FINE)) {
            SMJReportViewer.log.log(Level.FINE, "Path=" + path + " Prefix=" + prefix);
        }
        if (type.equals("PDF")) {
            media = new AMedia(this.generalTitle[0], "pdf", "application/pdf", this.filePdf, true);
        }
        else if (type.equals("XLS")) {
            media = new AMedia(this.generalTitle[0], "xls", "application/xls", this.fileXls, true);
        }
        else {
            media = new AMedia(this.generalTitle[0], "pdf", "application/pdf", this.filePdf, true);
        }
        this.iframe.setContent((Media)media);
    }
    
    private String makePrefix(final String name) {
        final StringBuffer prefix = new StringBuffer();
        final char[] arr$;
        final char[] nameArray = arr$ = name.toCharArray();
        for (final char ch : arr$) {
            if (Character.isLetterOrDigit(ch)) {
                prefix.append(ch);
            }
            else {
                prefix.append("_");
            }
        }
        return prefix.toString();
    }
    
    private void dynInit() {
        final SmjReportLogic logic = new SmjReportLogic();
        this.data = (LinkedList<ReportTO>)logic.getDataReport(this.reportId, this.trxName);
        this.generalTitle = logic.getGeneralTitle(this.reportLineSetId, this.trxName);
        this.clientName = logic.getOrgName(this.trxName);
        this.clientNIT = logic.getOrgNIT(this.trxName);
        this.periodName = logic.getPeriodName(this.p_C_Period_ID, this.trxName);
        this.currencyName = logic.getCurrency(this.trxName);
        this.codeFont = logic.getCodeFont(this.trxName, this.p_AD_PrintFont_ID);
        this.city = logic.getClientCity(this.trxName);
        final Properties prop = Env.getCtx();
        final MOrgInfo oi = MOrgInfo.get(prop, Env.getAD_Org_ID(prop), (String)null);
        this.logoId = oi.getLogo_ID();
        if (this.logoId <= 0) {
            final MClientInfo ci = MClientInfo.get(prop);
            this.logoId = ci.getLogoReport_ID();
        }
        final SmjPdfReport pdf = new SmjPdfReport();
        this.baosPDF = pdf.generate((LinkedList)this.data, this.trxName, this.generalTitle, this.clientName, this.clientNIT, this.periodName, this.currencyName, this.m_columns, this.codeFont, this.city, this.logoId);
        this.filePdf = pdf.tofile(this.baosPDF.toByteArray(), this.generalTitle);
        this.revalidate();
    }
    
    private void createXlsReport() {
       final SmjXlsReport xls = new SmjXlsReport();
        final HSSFWorkbook book = xls.generate((LinkedList)this.data, this.generalTitle, this.clientName, this.clientNIT, this.periodName, this.currencyName, this.m_columns, this.city, this.logoId);
        this.fileXls = xls.tofile(book, this.generalTitle);
        this.revalidate();
    }
    
    private void revalidate() {
        this.setTitle(Msg.translate(Env.getCtx(), "PA_Report_ID"));
        final StringBuffer sb = new StringBuffer();
        sb.append(Msg.getMsg(this.m_ctx, "DataCols")).append("=").append(", ").append(Msg.getMsg(this.m_ctx, "DataRows")).append("=");
        this.statusBar.setStatusLine(sb.toString());
    }
    
    public void onClose() {
        Env.clearWinContext(this.m_WindowNo);
        this.m_ctx = null;
        super.onClose();
    }
    
    public void onEvent(final Event ev) throws Exception {
        if (ev.getTarget().equals(this.previewType) && this.previewType != null) {
            final int index = this.previewType.getSelectedIndex();
            if (index == 1) {
                this.createXlsReport();
                try {
                    this.renderReport("XLS");
                    return;
                }
                catch (Exception e) {
                    throw new AdempiereException("Failed to render report", (Throwable)e);
                }
            }
            if (index == 0) {
                try {
                    this.renderReport("PDF");
                    return;
                }
                catch (Exception e) {
                    throw new AdempiereException("Failed to render report", (Throwable)e);
                }
            }
            try {
                this.renderReport("PDF");
            }
            catch (Exception e) {
                throw new AdempiereException("Failed to render report", (Throwable)e);
            }
        }
    }
    
    static {
        SMJReportViewer.log = CLogger.getCLogger((Class)ZkReportViewer.class);
    }
}
