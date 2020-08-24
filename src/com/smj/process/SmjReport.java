package com.smj.process;

import org.compiere.process.ProcessInfo;
import org.compiere.process.SvrProcess;

import java.math.*;
import java.util.logging.*;

import org.compiere.report.*;
import org.compiere.process.*;

import java.util.*;
import java.sql.*;

import org.compiere.model.*;

import com.smj.webui.component.*;

import org.adempiere.report.jasper.ReportStarter;
import org.adempiere.webui.part.WindowContainer;
import org.adempiere.webui.session.*;
import org.adempiere.webui.apps.AEnv;
import org.adempiere.webui.component.*;
import org.compiere.util.*;
import org.compiere.print.*;

public class SmjReport extends SvrProcess {

	private int p_C_Period_ID;
    private int p_Org_ID;
    private int p_C_BPartner_ID;
    private int p_M_Product_ID;
    private int p_C_Project_ID;
    private int p_C_Activity_ID;
    private int p_C_SalesRegion_ID;
    private int p_C_Campaign_ID;
    private int p_User1_ID;
    private int p_User2_ID;
    private int p_UserElement1_ID;
    private int p_UserElement2_ID;
    private boolean p_DetailsSourceFirst;
    private int p_PA_Hierarchy_ID;
    private int p_PA_ReportCube_ID;
    private long m_start;
    private MReport m_report;
    private FinReportPeriod[] m_periods;
    private int m_reportPeriod;
    private StringBuffer m_parameterWhere;
    private MReportColumn[] m_columns;
    private MReportLine[] m_lines;
    
    private String result="";
    
    public SmjReport() {
        this.p_C_Period_ID = 0;
        this.p_Org_ID = 0;
        this.p_C_BPartner_ID = 0;
        this.p_M_Product_ID = 0;
        this.p_C_Project_ID = 0;
        this.p_C_Activity_ID = 0;
        this.p_C_SalesRegion_ID = 0;
        this.p_C_Campaign_ID = 0;
        this.p_User1_ID = 0;
        this.p_User2_ID = 0;
        this.p_UserElement1_ID = 0;
        this.p_UserElement2_ID = 0;
        this.p_DetailsSourceFirst = false;
        this.p_PA_Hierarchy_ID = 0;
        this.p_PA_ReportCube_ID = 0;
        this.m_start = System.currentTimeMillis();
        this.m_report = null;
        this.m_periods = null;
        this.m_reportPeriod = -1;
        this.m_parameterWhere = new StringBuffer();
    }
	
	protected void prepare() {
        final StringBuffer sb = new StringBuffer("Record_ID=").append(this.getRecord_ID());
        final ProcessInfoParameter[] para = this.getParameter();
        for (int i = 0; i < para.length; ++i) {
            final String name = para[i].getParameterName();
            if (para[i].getParameter() != null) {
                if (name.equals("C_Period_ID")) {
                    this.p_C_Period_ID = para[i].getParameterAsInt();
                }
                else if (name.equals("PA_Hierarchy_ID")) {
                    this.p_PA_Hierarchy_ID = para[i].getParameterAsInt();
                }
                else if (name.equals("Org_ID")) {
                    this.p_Org_ID = ((BigDecimal)para[i].getParameter()).intValue();
                }
                else if (name.equals("C_BPartner_ID")) {
                    this.p_C_BPartner_ID = ((BigDecimal)para[i].getParameter()).intValue();
                }
                else if (name.equals("M_Product_ID")) {
                    this.p_M_Product_ID = ((BigDecimal)para[i].getParameter()).intValue();
                }
                else if (name.equals("C_Project_ID")) {
                    this.p_C_Project_ID = ((BigDecimal)para[i].getParameter()).intValue();
                }
                else if (name.equals("C_Activity_ID")) {
                    this.p_C_Activity_ID = ((BigDecimal)para[i].getParameter()).intValue();
                }
                else if (name.equals("C_SalesRegion_ID")) {
                    this.p_C_SalesRegion_ID = ((BigDecimal)para[i].getParameter()).intValue();
                }
                else if (name.equals("C_Campaign_ID")) {
                    this.p_C_Campaign_ID = ((BigDecimal)para[i].getParameter()).intValue();
                }
                else if (name.equals("User1_ID")) {
                    this.p_User1_ID = ((BigDecimal)para[i].getParameter()).intValue();
                }
                else if (name.equals("User2_ID")) {
                    this.p_User2_ID = ((BigDecimal)para[i].getParameter()).intValue();
                }
                else if (name.equals("UserElement1_ID")) {
                    this.p_UserElement1_ID = ((BigDecimal)para[i].getParameter()).intValue();
                }
                else if (name.equals("UserElement2_ID")) {
                    this.p_UserElement2_ID = ((BigDecimal)para[i].getParameter()).intValue();
                }
                else if (name.equals("DetailsSourceFirst")) {
                    this.p_DetailsSourceFirst = "Y".equals(para[i].getParameter());
                }
                else if (name.equals("PA_ReportCube_ID")) {
                    this.p_PA_ReportCube_ID = para[i].getParameterAsInt();
                }
                else {
                    this.log.log(Level.SEVERE, "Unknown Parameter: " + name);
                }
            }
        }
        if (this.p_Org_ID != 0) {
            this.m_parameterWhere.append(" AND ").append(MReportTree.getWhereClause(this.getCtx(), this.p_PA_Hierarchy_ID, "OO", this.p_Org_ID));
        }
        if (this.p_C_BPartner_ID != 0) {
            this.m_parameterWhere.append(" AND ").append(MReportTree.getWhereClause(this.getCtx(), this.p_PA_Hierarchy_ID, "BP", this.p_C_BPartner_ID));
        }
        if (this.p_M_Product_ID != 0) {
            this.m_parameterWhere.append(" AND ").append(MReportTree.getWhereClause(this.getCtx(), this.p_PA_Hierarchy_ID, "PR", this.p_M_Product_ID));
        }
        if (this.p_C_Project_ID != 0) {
            this.m_parameterWhere.append(" AND ").append(MReportTree.getWhereClause(this.getCtx(), this.p_PA_Hierarchy_ID, "PJ", this.p_C_Project_ID));
        }
        if (this.p_C_Activity_ID != 0) {
            this.m_parameterWhere.append(" AND ").append(MReportTree.getWhereClause(this.getCtx(), this.p_PA_Hierarchy_ID, "AY", this.p_C_Activity_ID));
        }
        if (this.p_C_Campaign_ID != 0) {
            this.m_parameterWhere.append(" AND C_Campaign_ID=").append(this.p_C_Campaign_ID);
        }
        if (this.p_C_SalesRegion_ID != 0) {
            this.m_parameterWhere.append(" AND ").append(MReportTree.getWhereClause(this.getCtx(), this.p_PA_Hierarchy_ID, "SR", this.p_C_SalesRegion_ID));
        }
        if (this.p_User1_ID != 0) {
            this.m_parameterWhere.append(" AND ").append(MReportTree.getWhereClause(this.getCtx(), this.p_PA_Hierarchy_ID, "U1", this.p_User1_ID));
        }
        if (this.p_User2_ID != 0) {
            this.m_parameterWhere.append(" AND ").append(MReportTree.getWhereClause(this.getCtx(), this.p_PA_Hierarchy_ID, "U2", this.p_User2_ID));
        }
        if (this.p_UserElement1_ID != 0) {
            this.m_parameterWhere.append(" AND UserElement1_ID=").append(this.p_UserElement1_ID);
        }
        if (this.p_UserElement2_ID != 0) {
            this.m_parameterWhere.append(" AND UserElement2_ID=").append(this.p_UserElement2_ID);
        }
        this.m_report = new MReport(this.getCtx(), this.getRecord_ID(), (String)null);
        sb.append(" - ").append(this.m_report);
        this.setPeriods();
        sb.append(" - C_Period_ID=").append(this.p_C_Period_ID).append(" - ").append(this.m_parameterWhere);
        if (this.p_PA_ReportCube_ID > 0) {
            this.m_parameterWhere.append(" AND PA_ReportCube_ID=").append(this.p_PA_ReportCube_ID);
        }
        this.log.info(sb.toString());
  
        addPeriodContext(p_C_Period_ID);    //maximea
	}


    private void setPeriods() {
        this.log.info("C_Calendar_ID=" + this.m_report.getC_Calendar_ID());
        final Timestamp today = TimeUtil.getDay(System.currentTimeMillis());
        final ArrayList<FinReportPeriod> list = new ArrayList<FinReportPeriod>();
        final String sql = "SELECT p.C_Period_ID, p.Name, p.StartDate, p.EndDate, MIN(p1.StartDate) FROM C_Period p  INNER JOIN C_Year y ON (p.C_Year_ID=y.C_Year_ID), C_Period p1 WHERE y.C_Calendar_ID=? AND p.IsActive='Y' AND p.PeriodType='S'  AND p1.C_Year_ID=y.C_Year_ID AND p1.PeriodType='S' GROUP BY p.C_Period_ID, p.Name, p.StartDate, p.EndDate ORDER BY p.StartDate";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = (PreparedStatement)DB.prepareStatement(sql, (String)null);
            pstmt.setInt(1, this.m_report.getC_Calendar_ID());
            rs = pstmt.executeQuery();
            while (rs.next()) {
                final FinReportPeriod frp = new FinReportPeriod(rs.getInt(1), rs.getString(2), rs.getTimestamp(3), rs.getTimestamp(4), rs.getTimestamp(5));
                list.add(frp);
                if (this.p_C_Period_ID == 0 && frp.inPeriod(today)) {
                    this.p_C_Period_ID = frp.getC_Period_ID();
                }
            }
        }
        catch (Exception e) {
            this.log.log(Level.SEVERE, sql, (Throwable)e);
        }
        finally {
            DB.close(rs, (Statement)pstmt);
            rs = null;
            pstmt = null;
        }
        list.toArray(this.m_periods = new FinReportPeriod[list.size()]);
        if (this.p_C_Period_ID == 0) {
            this.m_reportPeriod = this.m_periods.length - 1;
            this.p_C_Period_ID = this.m_periods[this.m_reportPeriod].getC_Period_ID();
        }
    }
    
    protected String doIt() throws Exception {
        this.log.info("AD_PInstance_ID=" + this.getAD_PInstance_ID());
        if (this.p_PA_ReportCube_ID > 0) {
            final MReportCube cube = new MReportCube(this.getCtx(), this.p_PA_ReportCube_ID, this.get_TrxName());
            final String result = cube.update(false, false);
            this.log.log(Level.FINE, result);
        }
        final int PA_ReportLineSet_ID = this.m_report.getLineSet().getPA_ReportLineSet_ID();
        final StringBuffer sql = new StringBuffer("INSERT INTO T_Report (AD_PInstance_ID, PA_ReportLine_ID, Record_ID,Fact_Acct_ID, SeqNo,LevelNo, Name,Description, smj_HierarchyLevel, smj_ReportLine, smj_fixedPercentage ) SELECT ").append(this.getAD_PInstance_ID()).append(", PA_ReportLine_ID, 0,0, SeqNo,0, Name,Description, smj_HierarchyLevel, smj_ReportLine, smj_fixedPercentage FROM PA_ReportLine WHERE IsActive='Y' AND PA_ReportLineSet_ID=").append(PA_ReportLineSet_ID);
        final int no = DB.executeUpdate(sql.toString(), this.get_TrxName());
        this.log.fine("Report Lines = " + no);
        this.m_columns = this.m_report.getColumnSet().getColumns();
        if (this.m_columns.length == 0) {
            throw new AdempiereUserError("@No@ @PA_ReportColumn_ID@");
        }
        this.m_lines = this.m_report.getLineSet().getLiness();
        if (this.m_lines.length == 0) {
            throw new AdempiereUserError("@No@ @PA_ReportLine_ID@");
        }
        for (int line = 0; line < this.m_lines.length; ++line) {
            if (this.m_lines[line].isLineTypeSegmentValue()) {
                this.insertLine(line);
            }
        }
        this.insertLineDetail();
        this.doCalculations();
        this.deleteUnprintedLines();
        this.scaleResults();
       
        //final Window viewer = (Window)new SfrReportViewer(this.getAD_PInstance_ID(), this.get_TrxName(), PA_ReportLineSet_ID, this.p_C_Period_ID, pf.getAD_PrintFont_ID(), this.m_columns);
		 final MPrintFormat pf = getPrintFormat();
        AEnv.executeAsyncDesktopTask(new Runnable() {
        	@Override
			public void run() {
        		 //Window viewer = new SfrReportViewer(ren,"");

                 ReportEngine re = createReportEngine(getProcessInfo(),Env.WINDOW_MAIN,pf);
                 if (p_Org_ID==0)
                	 p_Org_ID = Env.getAD_Org_ID(getCtx());
        		 Window viewer = (Window)new SfrReportViewer(getAD_PInstance_ID(), get_TrxName(), PA_ReportLineSet_ID, p_C_Period_ID, pf.getAD_PrintFont_ID(), m_columns,re,p_Org_ID);
        		 
        		 viewer.setAttribute(Window.MODE_KEY, Window.MODE_EMBEDDED);
        	     viewer.setAttribute(Window.INSERT_POSITION_KEY, Window.INSERT_NEXT);
        	     viewer.setAttribute(WindowContainer.DEFER_SET_SELECTED_TAB, Boolean.TRUE);
        	     SessionManager.getAppDesktop().showWindow(viewer);
        	}
        });
       
        
// Create Report
//        	if (Ini.isClient())
//    			getProcessInfo().setTransientObject (pf);
//    		else
//    			getProcessInfo().setSerializableObject(pf);

//    		if (log.isLoggable(Level.FINE)) log.fine((System.currentTimeMillis() - m_start) + " ms");
//        this.log.fine(System.currentTimeMillis() - this.m_start + " ms");
        return ""+result;
    }
    
    private void insertLine(final int line) {
    	this.log.info("" + this.m_lines[line]);
        if (this.m_lines[line] == null || this.m_lines[line].getSources().length == 0) {
            return;
        }

        final StringBuffer update = new StringBuffer();
        for (int col = 0; col < this.m_columns.length; ++col) {
System.out.println("---line="+line+" col="+col);
            if (!this.m_columns[col].isColumnTypeCalculation()) {
                final StringBuffer info = new StringBuffer();
                info.append("Line=").append(line).append(",Col=").append(col);
                final StringBuffer select = new StringBuffer("SELECT ");
                if (this.m_lines[line].getPAAmountType() != null) {
                    final String sql = this.m_lines[line].getSelectClause(true);
                    select.append(sql);
                    info.append(": LineAmtType=").append(this.m_lines[line].getPAAmountType());
                }
                else {
                    if (this.m_columns[col].getPAAmountType() == null) {
                        this.log.warning("No Amount Type in line: " + this.m_lines[line] + " or column: " + this.m_columns[col]);
                        continue;
                    }
                    final String sql = this.m_columns[col].getSelectClause(true);
                    select.append(sql);
                    info.append(": ColumnAmtType=").append(this.m_columns[col].getPAAmountType());
                }
                if (this.p_PA_ReportCube_ID > 0) {
                    select.append(" FROM Fact_Acct_Summary fa WHERE DateAcct ");
                }
                else {
                    select.append(" FROM Fact_Acct fa WHERE TRUNC(DateAcct) ");
                }
                BigDecimal relativeOffset = null;
                if (this.m_columns[col].isColumnTypeRelativePeriod()) {
                    relativeOffset = this.m_columns[col].getRelativePeriod();
                }
                final FinReportPeriod frp = this.getPeriod(relativeOffset);
                if (this.m_lines[line].getPAPeriodType() != null) {
                    info.append(" - LineDateAcct=");
                    if (this.m_lines[line].isPeriod()) {
                        final String sql2 = frp.getPeriodWhere();
                        info.append("Period");
                        select.append(sql2);
                    }
                    else if (this.m_lines[line].isYear()) {
                        final String sql2 = frp.getYearWhere();
                        info.append("Year");
                        select.append(sql2);
                    }
                    else if (this.m_lines[line].isTotal()) {
                        final String sql2 = frp.getTotalWhere();
                        info.append("Total");
                        select.append(sql2);
                    }
                    else if (this.m_lines[line].isNatural()) {
                        select.append(frp.getNaturalWhere("fa"));
                    }
                    else {
                        this.log.log(Level.SEVERE, "No valid Line PAPeriodType");
                        select.append("=0");
                    }
                }
                else if (this.m_columns[col].getPAPeriodType() != null) {
                    info.append(" - ColumnDateAcct=");
                    if (this.m_columns[col].isPeriod()) {
                        final String sql2 = frp.getPeriodWhere();
                        info.append("Period");
                        select.append(sql2);
                    }
                    else if (this.m_columns[col].isYear()) {
                        final String sql2 = frp.getYearWhere();
                        info.append("Year");
                        select.append(sql2);                  
                    }
                    else if (this.m_columns[col].isTotal()) {
                        final String sql2 = frp.getTotalWhere();
                        info.append("Total");
                        select.append(sql2);
                    }
                    else if (this.m_columns[col].isNatural()) {
                        select.append(frp.getNaturalWhere("fa"));
                    }
                    else {
                        this.log.log(Level.SEVERE, "No valid Column PAPeriodType");
                        select.append("=0");
                    }
                }
                String s = this.m_lines[line].getWhereClause(this.p_PA_Hierarchy_ID);
                if (s != null && s.length() > 0) {
                    select.append(" AND ").append(s);
                }
 /*               
        		//maximea
        		if (s != null && s.length() > 0)
        		{
        			String sqlstatement = DB.getSQLValueString  (get_TrxName(), "SELECT sqlstatement FROM PA_ReportLine WHERE PA_ReportLine_ID="+m_lines[line].getPA_ReportLine_ID());	
        			if (sqlstatement != null && sqlstatement.length()>0 )
        			{
        				if( sqlstatement.startsWith("@SQL="))	
        				{
        					sqlstatement = sqlstatement.replace("@SQL=","");
        					sqlstatement = sqlstatement.replace("@C_ElementValue_ID@",s);
        					select.append(" AND ").append(sqlstatement);
        				}
        			}
        		}
                //maximea
*/        		
                s = this.m_report.getWhereClause();
                if (s != null && s.length() > 0) {
                    select.append(" AND ").append(s);
                }
                if (!this.m_lines[line].isPostingType()) {
                    final String PostingType = this.m_columns[col].getPostingType();
                    if (PostingType != null && PostingType.length() > 0) {
                        select.append(" AND PostingType='").append(PostingType).append("'");
                    }
                    if (PostingType.equals("B") && this.m_columns[col].getGL_Budget_ID() > 0) {
                        select.append(" AND GL_Budget_ID=" + this.m_columns[col].getGL_Budget_ID());
                    }
                }
                if (this.m_columns[col].isColumnTypeSegmentValue()) {
                    select.append(this.m_columns[col].getWhereClause(this.p_PA_Hierarchy_ID));
                }
                select.append(this.m_parameterWhere);
                this.log.finest("Line=" + line + ",Col=" + line + ": " + (Object)select);
                if (update.length() > 0) {
                    update.append(", ");
                }
System.out.println("select4 #="+select.toString());
                update.append("Col_").append(col).append(" = (").append(select).append(")");
                this.log.finest(info.toString());
            }
        }
        if (update.length() > 0) {
            update.insert(0, "UPDATE T_Report SET ");
            update.append(" WHERE AD_PInstance_ID=").append(this.getAD_PInstance_ID()).append(" AND PA_ReportLine_ID=").append(this.m_lines[line].getPA_ReportLine_ID()).append(" AND ABS(LevelNo)<2");
            final int no = DB.executeUpdate(update.toString(), this.get_TrxName());
            if (no != 1) {
                this.log.log(Level.SEVERE, "#=" + no + " for " + (Object)update);
            }
            this.log.finest(update.toString());
        }
    }
    
    private void doCalculations() {
        for (int line = 0; line < this.m_lines.length; ++line) {
            if (this.m_lines[line].isLineTypeCalculation()) {
                int oper_1 = this.m_lines[line].getOper_1_ID();
                int oper_2 = this.m_lines[line].getOper_2_ID();
                this.log.fine("Line " + line + " = #" + oper_1 + " " + this.m_lines[line].getCalculationType() + " #" + oper_2);
                if (this.m_lines[line].isCalculationTypeAdd() || this.m_lines[line].isCalculationTypeRange()) {
                    if (oper_1 > oper_2) {
                        final int temp = oper_1;
                        oper_1 = oper_2;
                        oper_2 = temp;
                    }
                    final StringBuffer sb = new StringBuffer("UPDATE T_Report SET (");
                    for (int col = 0; col < this.m_columns.length; ++col) {
                        if (col > 0) {
                            sb.append(",");
                        }
                        sb.append("Col_").append(col);
                    }
                    sb.append(") = (SELECT ");
                    for (int col = 0; col < this.m_columns.length; ++col) {
                        if (col > 0) {
                            sb.append(",");
                        }
                        sb.append("COALESCE(SUM(r2.Col_").append(col).append("),0)");
                    }
                    sb.append(" FROM T_Report r2 WHERE r2.AD_PInstance_ID=").append(this.getAD_PInstance_ID()).append(" AND r2.PA_ReportLine_ID IN (");
                    if (this.m_lines[line].isCalculationTypeAdd()) {
                        sb.append(oper_1).append(",").append(oper_2);
                    }
                    else {
                        sb.append(this.getLineIDs(oper_1, oper_2));
                    }
                    sb.append(") AND ABS(r2.LevelNo)<1) WHERE AD_PInstance_ID=").append(this.getAD_PInstance_ID()).append(" AND PA_ReportLine_ID=").append(this.m_lines[line].getPA_ReportLine_ID()).append(" AND ABS(LevelNo)<1");
                    final int no = DB.executeUpdate(sb.toString(), this.get_TrxName());
                    if (no != 1) {
                        this.log.log(Level.SEVERE, "(+) #=" + no + " for " + this.m_lines[line] + " - " + sb.toString());
                    }
                    else {
                        this.log.fine("(+) Line=" + line + " - " + this.m_lines[line]);
                        this.log.finest("(+) " + sb.toString());
                    }
                }
                else {
                    StringBuffer sb = new StringBuffer("UPDATE T_Report SET (");
                    for (int col = 0; col < this.m_columns.length; ++col) {
                        if (col > 0) {
                            sb.append(",");
                        }
                        sb.append("Col_").append(col);
                    }
                    sb.append(") = (SELECT ");
                    for (int col = 0; col < this.m_columns.length; ++col) {
                        if (col > 0) {
                            sb.append(",");
                        }
                        sb.append("COALESCE(r2.Col_").append(col).append(",0)");
                    }
                    sb.append(" FROM T_Report r2 WHERE r2.AD_PInstance_ID=").append(this.getAD_PInstance_ID()).append(" AND r2.PA_ReportLine_ID=").append(oper_1).append(" AND r2.Record_ID=0 AND r2.Fact_Acct_ID=0) WHERE AD_PInstance_ID=").append(this.getAD_PInstance_ID()).append(" AND PA_ReportLine_ID=").append(this.m_lines[line].getPA_ReportLine_ID()).append(" AND ABS(LevelNo)<1");
                    int no = DB.executeUpdate(sb.toString(), this.get_TrxName());
                    if (no != 1) {
                        this.log.severe("(x) #=" + no + " for " + this.m_lines[line] + " - " + sb.toString());
                    }
                    else {
                        sb = new StringBuffer("UPDATE T_Report r1 SET (");
                        final StringBuffer fp = new StringBuffer(" UPDATE T_Report SET ");
                        Boolean fixPerc = false;
                        for (int col2 = 0; col2 < this.m_columns.length; ++col2) {
                            if (col2 > 0) {
                                sb.append(",");
                            }
                            sb.append("Col_").append(col2);
                        }
                        sb.append(") = (SELECT ");
                        for (int col2 = 0; col2 < this.m_columns.length; ++col2) {
                            if (col2 > 0) {
                                sb.append(",");
                            }
                            sb.append("COALESCE(r1.Col_").append(col2).append(",0)");
                            if (this.m_lines[line].isCalculationTypeSubtract()) {
                                sb.append("-");
                                sb.append("COALESCE(r2.Col_").append(col2).append(",0)");
                            }
                            //else {
                            //    sb.append("/");
                            //    sb.append("DECODE (r2.Col_").append(col2).append(", 0, NULL, r2.Col_").append(col2).append(")");
                            //}
                            
                            //ps<
                            else if (this.m_lines[line].isCalculationTypePercent()){
                                sb.append("/");
                                sb.append("DECODE (r2.Col_").append(col2).append(", 0, NULL, r2.Col_").append(col2).append(")");
                            }
                            else if (this.m_lines[line].getCalculationType().equals("M")){
                                sb.append("*");
                                sb.append("DECODE (r2.Col_").append(col2).append(", 0, NULL, r2.Col_").append(col2).append(")");
                            }
                            else if (this.m_lines[line].getCalculationType().equals("D")){
                                sb.append("/");
                                sb.append("DECODE (r2.Col_").append(col2).append(", 0, NULL, r2.Col_").append(col2).append(")");
                            }
                            
                            if (this.m_lines[line].getCalculationType().equals("D")) {
                            	// sb.append(" *100");
                                final BigDecimal fixedNumber = this.getFixedNumber(this.get_TrxName(), this.getAD_PInstance_ID(), this.m_lines[line].getPA_ReportLine_ID(), "Col_" + col2);
                                if (fixedNumber.compareTo(Env.ZERO)>0) {
                                    fixPerc = true;
                                }
                                if (col2 > 0) {
                                    fp.append(",");
                                }
                                fp.append("Col_" + col2 + " = " + fixedNumber);

                            }
                            
                            //ps>
                            if (this.m_lines[line].isCalculationTypePercent()) {
                                sb.append(" *100");
                                final Float fixedPercentage = this.getFixedPercentage(this.get_TrxName(), this.getAD_PInstance_ID(), this.m_lines[line].getPA_ReportLine_ID(), "Col_" + col2);
                                //fixed by Nicolas
                                if (fixedPercentage != 0.0f) {
                                    fixPerc = true;
                                }
                                if (col2 > 0) {
                                    fp.append(",");
                                }
                                fp.append("Col_" + col2 + " = " + fixedPercentage);
                            }
                        }
                        sb.append(" FROM T_Report r2 WHERE r2.AD_PInstance_ID=").append(this.getAD_PInstance_ID()).append(" AND r2.PA_ReportLine_ID=").append(oper_2).append(" AND r2.Record_ID=0 AND r2.Fact_Acct_ID=0) WHERE AD_PInstance_ID=").append(this.getAD_PInstance_ID()).append(" AND PA_ReportLine_ID=").append(this.m_lines[line].getPA_ReportLine_ID()).append(" AND ABS(LevelNo)<1 ");
                        fp.append(" WHERE AD_PInstance_ID = " + this.getAD_PInstance_ID()).append(" AND PA_ReportLine_ID= " + this.m_lines[line].getPA_ReportLine_ID()).append(" AND ABS(LevelNo) < 1   ");
                        if (fixPerc) {
                            try {
                                no = DB.executeUpdate(fp.toString(), this.get_TrxName());
                            }
                            catch (Exception e) {
                                this.log.log(Level.SEVERE, fp.toString(), (Throwable)e);
                            }
                            if (no != 1) {
                                this.log.severe("fixedPercentage #=" + fp.toString());
                            }
                            else {
                                this.log.fine("fixedPercentage Line=" + line + " - " + this.m_lines[line]);
                                this.log.finest(fp.toString());
                            }
                        }
                        else {
                            no = DB.executeUpdate(sb.toString(), this.get_TrxName());
                            if (no != 1) {
                                this.log.severe("(x) #=" + no + " for " + this.m_lines[line] + " - " + sb.toString());
                            }
                            else {
                                this.log.fine("(x) Line=" + line + " - " + this.m_lines[line]);
                                this.log.finest(sb.toString());
                            }
                        }
                    }
                }
            }
        }
        for (int col3 = 0; col3 < this.m_columns.length; ++col3) {
            if (this.m_columns[col3].isColumnTypeCalculation()) {
                final StringBuffer sb2 = new StringBuffer("UPDATE T_Report SET ");
                sb2.append("Col_").append(col3).append("=");
                int ii_1 = this.getColumnIndex(this.m_columns[col3].getOper_1_ID());
                if (ii_1 < 0) {
                    this.log.log(Level.SEVERE, "Column Index for Operator 1 not found - " + this.m_columns[col3]);
                }
                else {
                    int ii_2 = this.getColumnIndex(this.m_columns[col3].getOper_2_ID());
                    if (ii_2 < 0) {
                        this.log.log(Level.SEVERE, "Column Index for Operator 2 not found - " + this.m_columns[col3]);
                    }
                    else {
                        this.log.fine("Column " + col3 + " = #" + ii_1 + " " + this.m_columns[col3].getCalculationType() + " #" + ii_2);
                        if (ii_1 > ii_2 && this.m_columns[col3].isCalculationTypeRange()) {
                            this.log.fine("Swap operands from " + ii_1 + " op " + ii_2);
                            final int temp2 = ii_1;
                            ii_1 = ii_2;
                            ii_2 = temp2;
                        }
                        if (this.m_columns[col3].isCalculationTypeAdd()) {
                            sb2.append("COALESCE(Col_").append(ii_1).append(",0)").append("+").append("COALESCE(Col_").append(ii_2).append(",0)");
                        }
                        else if (this.m_columns[col3].isCalculationTypeSubtract()) {
                            sb2.append("COALESCE(Col_").append(ii_1).append(",0)").append("-").append("COALESCE(Col_").append(ii_2).append(",0)");
                        }
                        //ps<
                        else if (this.m_columns[col3].getCalculationType().equals("M")) {
                            sb2.append("COALESCE(Col_").append(ii_1).append(",0)").append("*").append("COALESCE(Col_").append(ii_2).append(",0)");
                        }
                        //ps>
                        if (this.m_columns[col3].isCalculationTypePercent()) {
                            sb2.append("CASE WHEN COALESCE(Col_").append(ii_2).append(",0)=0 THEN NULL ELSE ").append("COALESCE(Col_").append(ii_1).append(",0)").append("/").append("Col_").append(ii_2).append("*100 END");
                        }
                        else if (this.m_columns[col3].isCalculationTypeRange()) {
                            sb2.append("COALESCE(Col_").append(ii_1).append(",0)");
                            for (int ii = ii_1 + 1; ii <= ii_2; ++ii) {
                                sb2.append("+COALESCE(Col_").append(ii).append(",0)");
                            }
                        }
                        sb2.append(" WHERE AD_PInstance_ID=").append(this.getAD_PInstance_ID()).append(" AND ABS(LevelNo)<2");
                        final int no = DB.executeUpdate(sb2.toString(), this.get_TrxName());
                        if (no < 1) {
                            this.log.severe("#=" + no + " for " + this.m_columns[col3] + " - " + sb2.toString());
                        }
                        else {
                            this.log.fine("Col=" + col3 + " - " + this.m_columns[col3]);
                            this.log.finest(sb2.toString());
                        }
                    }
                }
            }
        }
    }
    
    private Float getFixedPercentage(final String trxName, final Integer AD_PInstance_ID, final Integer PA_ReportLine_ID, final String colName) {
        final StringBuffer sql = new StringBuffer();
        sql.append("SELECT smj_FixedPercentage, " + colName + " FROM T_Report WHERE AD_PInstance_ID= " + AD_PInstance_ID + " ");
        sql.append("AND PA_ReportLine_ID= " + PA_ReportLine_ID + " ");
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Float percent = new Float(0.0f);
        Float col = new Float(0.0f);
        Float percentaje = new Float(0.0f);
        try {
            pstmt = (PreparedStatement)DB.prepareStatement(sql.toString(), trxName);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                percent = rs.getFloat("smj_FixedPercentage");
                col = rs.getFloat(colName);
                percentaje = percent / 100.0f;
            }
        }
        catch (Exception e) {
            this.log.log(Level.SEVERE, sql.toString(), (Throwable)e);
        }
        finally {
            DB.close((Statement)pstmt);
            rs = null;
            pstmt = null;
        }
        
      //fixed by Nicolas
        if (col != 0.0f) {
            return col * percentaje;
        }
        return new Float(0.0f);
    }
    
    private BigDecimal getFixedNumber(final String trxName, final Integer AD_PInstance_ID, final Integer PA_ReportLine_ID, final String colName) {
        final StringBuffer sql = new StringBuffer();
        sql.append("SELECT smj_FixedPercentage, " + colName + " FROM T_Report WHERE AD_PInstance_ID= " + AD_PInstance_ID + " ");
        sql.append("AND PA_ReportLine_ID= " + PA_ReportLine_ID + " ");
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        BigDecimal fixNumber = Env.ZERO;
        BigDecimal col = Env.ZERO;
        try {
            pstmt = (PreparedStatement)DB.prepareStatement(sql.toString(), trxName);
            rs = pstmt.executeQuery();
            if (rs.next()) {
            	fixNumber = rs.getBigDecimal("smj_FixedPercentage");
                col = rs.getBigDecimal(colName);
            }
        }
        catch (Exception e) {
            this.log.log(Level.SEVERE, sql.toString(), (Throwable)e);
        }
        finally {
            DB.close((Statement)pstmt);
            rs = null;
            pstmt = null;
        }
        if ((col.compareTo(Env.ZERO) > 0) && (fixNumber.compareTo(Env.ZERO) > 0)) 
        		 return col.divide((fixNumber),2, BigDecimal.ROUND_UP);
         
        return Env.ZERO;
    }
    
    private String getLineIDs(final int fromID, final int toID) {
        this.log.finest("From=" + fromID + " To=" + toID);
        int firstPA_ReportLine_ID = 0;
        int lastPA_ReportLine_ID = 0;
        for (int line = 0; line < this.m_lines.length; ++line) {
            final int PA_ReportLine_ID = this.m_lines[line].getPA_ReportLine_ID();
            if (PA_ReportLine_ID == fromID || PA_ReportLine_ID == toID) {
                if (firstPA_ReportLine_ID != 0) {
                    lastPA_ReportLine_ID = PA_ReportLine_ID;
                    break;
                }
                firstPA_ReportLine_ID = PA_ReportLine_ID;
            }
        }
        final StringBuffer sb = new StringBuffer();
        sb.append(firstPA_ReportLine_ID);
        boolean addToList = false;
        for (int line2 = 0; line2 < this.m_lines.length; ++line2) {
            final int PA_ReportLine_ID2 = this.m_lines[line2].getPA_ReportLine_ID();
            this.log.finest("Add=" + addToList + " ID=" + PA_ReportLine_ID2 + " - " + this.m_lines[line2]);
            if (addToList) {
                sb.append(",").append(PA_ReportLine_ID2);
                if (PA_ReportLine_ID2 == lastPA_ReportLine_ID) {
                    break;
                }
            }
            else if (PA_ReportLine_ID2 == firstPA_ReportLine_ID) {
                addToList = true;
            }
        }
        return sb.toString();
    }
    
    private int getColumnIndex(final int PA_ReportColumn_ID) {
        for (int i = 0; i < this.m_columns.length; ++i) {
            if (this.m_columns[i].getPA_ReportColumn_ID() == PA_ReportColumn_ID) {
                return i;
            }
        }
        return -1;
    }
    
    private FinReportPeriod getPeriod(final BigDecimal relativeOffset) {
        if (relativeOffset == null) {
            return this.getPeriod(0);
        }
        return this.getPeriod(relativeOffset.intValue());
    }
    
    private FinReportPeriod getPeriod(final int relativeOffset) {
        if (this.m_reportPeriod < 0) {
            for (int i = 0; i < this.m_periods.length; ++i) {
                if (this.p_C_Period_ID == this.m_periods[i].getC_Period_ID()) {
                    this.m_reportPeriod = i;
                    break;
                }
            }
        }
        if (this.m_reportPeriod < 0 || this.m_reportPeriod >= this.m_periods.length) {
            throw new UnsupportedOperationException("Period index not found - ReportPeriod=" + this.m_reportPeriod + ", C_Period_ID=" + this.p_C_Period_ID);
        }
        int index = this.m_reportPeriod + relativeOffset;
        if (index < 0) {
            this.log.log(Level.SEVERE, "Relative Offset(" + relativeOffset + ") not valid for selected Period(" + this.m_reportPeriod + ")");
            index = 0;
        }
        else if (index >= this.m_periods.length) {
            this.log.log(Level.SEVERE, "Relative Offset(" + relativeOffset + ") not valid for selected Period(" + this.m_reportPeriod + ")");
            index = this.m_periods.length - 1;
        }
        return this.m_periods[index];
    }
    
    private void insertLineDetail() {
        if (!this.m_report.isListSources()) {
            return;
        }
        this.log.info("");
        for (int line = 0; line < this.m_lines.length; ++line) {
            if (this.m_lines[line].isLineTypeSegmentValue()) {
                this.insertLineSource(line);
            }
        }
        StringBuffer sql = new StringBuffer("DELETE FROM T_Report WHERE ABS(LevelNo)<>0").append(" AND Col_0 IS NULL AND Col_1 IS NULL AND Col_2 IS NULL AND Col_3 IS NULL AND Col_4 IS NULL AND Col_5 IS NULL AND Col_6 IS NULL AND Col_7 IS NULL AND Col_8 IS NULL AND Col_9 IS NULL").append(" AND Col_10 IS NULL AND Col_11 IS NULL AND Col_12 IS NULL AND Col_13 IS NULL AND Col_14 IS NULL AND Col_15 IS NULL AND Col_16 IS NULL AND Col_17 IS NULL AND Col_18 IS NULL AND Col_19 IS NULL AND Col_20 IS NULL");
        int no = DB.executeUpdate(sql.toString(), this.get_TrxName());
        this.log.fine("Deleted empty #=" + no);
        sql = new StringBuffer("UPDATE T_Report r1 SET SeqNo = (SELECT SeqNo FROM T_Report r2 WHERE r1.AD_PInstance_ID=r2.AD_PInstance_ID AND r1.PA_ReportLine_ID=r2.PA_ReportLine_ID AND r2.Record_ID=0 AND r2.Fact_Acct_ID=0)WHERE SeqNo IS NULL");
        no = DB.executeUpdate(sql.toString(), this.get_TrxName());
        this.log.fine("SeqNo #=" + no);
        if (!this.m_report.isListTrx()) {
            return;
        }
        final String sql_select = "SELECT e.Name, fa.Description FROM Fact_Acct fa INNER JOIN AD_Table t ON (fa.AD_Table_ID=t.AD_Table_ID) INNER JOIN AD_Element e ON (t.TableName||'_ID'=e.ColumnName) WHERE r.Fact_Acct_ID=fa.Fact_Acct_ID";
        sql = new StringBuffer("UPDATE T_Report r SET (Name,Description)=(").append(sql_select).append(") WHERE Fact_Acct_ID <> 0 AND AD_PInstance_ID=").append(this.getAD_PInstance_ID());
        no = DB.executeUpdate(sql.toString(), this.get_TrxName());
        if (CLogMgt.isLevelFinest()) {
            this.log.fine("Trx Name #=" + no + " - " + sql.toString());
        }
    }
    
    private void insertLineSource(final int line) {
        this.log.info("Line=" + line + " - " + this.m_lines[line]);
        if (this.m_lines[line] == null || this.m_lines[line].getSources().length == 0) {
            return;
        }
        final String variable = this.m_lines[line].getSourceColumnName();
        if (variable == null) {
            return;
        }
        this.log.fine("Variable=" + variable);
        final StringBuffer insert = new StringBuffer("INSERT INTO T_Report (AD_PInstance_ID, PA_ReportLine_ID, Record_ID,Fact_Acct_ID,LevelNo ");
        for (int col = 0; col < this.m_columns.length; ++col) {
            insert.append(",Col_").append(col);
        }
        insert.append(") SELECT ").append(this.getAD_PInstance_ID()).append(",").append(this.m_lines[line].getPA_ReportLine_ID()).append(",").append(variable).append(",0,");
        if (this.p_DetailsSourceFirst) {
            insert.append("-1 ");
        }
        else {
            insert.append("1 ");
        }
        for (int col = 0; col < this.m_columns.length; ++col) {
            insert.append(", ");
            if (this.m_columns[col].isColumnTypeCalculation()) {
                insert.append("NULL");
            }
            else {
                final StringBuffer select = new StringBuffer("SELECT ");
                if (this.m_lines[line].getPAAmountType() != null) {
                    select.append(this.m_lines[line].getSelectClause(true));
                }
                else {
                    if (this.m_columns[col].getPAAmountType() == null) {
                        insert.append("NULL");
                        continue;
                    }
                    select.append(this.m_columns[col].getSelectClause(true));
                }
                if (this.p_PA_ReportCube_ID > 0) {
                    select.append(" FROM Fact_Acct_Summary fb WHERE DateAcct ");
                }
                else {
                    select.append(" FROM Fact_Acct fb WHERE TRUNC(DateAcct) ");
                }
                final FinReportPeriod frp = this.getPeriod(this.m_columns[col].getRelativePeriod());
                if (this.m_lines[line].getPAPeriodType() != null) {
                    if (this.m_lines[line].isPeriod()) {
                        select.append(frp.getPeriodWhere());
                    }
                    else if (this.m_lines[line].isYear()) {
                        select.append(frp.getYearWhere());
                    }
                    else if (this.m_lines[line].isNatural()) {
                        select.append(frp.getNaturalWhere("fb"));
                    }
                    else {
                        select.append(frp.getTotalWhere());
                    }
                }
                else if (this.m_columns[col].getPAPeriodType() != null) {
                    if (this.m_columns[col].isPeriod()) {
                        select.append(frp.getPeriodWhere());
                    }
                    else if (this.m_columns[col].isYear()) {

                        select.append(frp.getYearWhere());
                    }
                    else if (this.m_columns[col].isNatural()) {
                        select.append(frp.getNaturalWhere("fb"));
                    }
                    else {
                        select.append(frp.getTotalWhere());
                    }
                }
                
                select.append(" AND fb.").append(variable).append("=x.").append(variable);
                if (!this.m_lines[line].isPostingType()) {
                    final String PostingType = this.m_columns[col].getPostingType();
                    if (PostingType != null && PostingType.length() > 0) {
                        select.append(" AND fb.PostingType='").append(PostingType).append("'");
                    }
                    if (PostingType.equals("B") && this.m_columns[col].getGL_Budget_ID() > 0) {
                        select.append(" AND GL_Budget_ID=" + this.m_columns[col].getGL_Budget_ID());
                    }
                }
                final String s = this.m_report.getWhereClause();
                if (s != null && s.length() > 0) {
                    select.append(" AND ").append(s);
                }
                if (this.m_columns[col].isColumnTypeSegmentValue()) {
                    select.append(this.m_columns[col].getWhereClause(this.p_PA_Hierarchy_ID));
                }
                select.append(this.m_parameterWhere);
                insert.append("(").append(select).append(")");
            }
        }
        final StringBuffer where = new StringBuffer(this.m_lines[line].getWhereClause(this.p_PA_Hierarchy_ID));
        final String s2 = this.m_report.getWhereClause();
        if (s2 != null && s2.length() > 0) {
            if (where.length() > 0) {
                where.append(" AND ");
            }
            where.append(s2);
        }
        if (where.length() > 0) {
            where.append(" AND ");
        }
        where.append(variable).append(" IS NOT NULL");
        if (this.p_PA_ReportCube_ID > 0) {
            insert.append(" FROM Fact_Acct_Summary x WHERE ").append(where);
        }
        else {
            insert.append(" FROM Fact_Acct x WHERE ").append(where);
        }
        insert.append(this.m_parameterWhere).append(" GROUP BY ").append(variable);

        int no = DB.executeUpdate(insert.toString(), this.get_TrxName());
        
        if (CLogMgt.isLevelFinest()) {
            this.log.fine("Source #=" + no + " - " + (Object)insert);
        }
        if (no == 0) {
            return;
        }
        final StringBuffer sql = new StringBuffer("UPDATE T_Report SET (Name,Description)=(").append(this.m_lines[line].getSourceValueQuery()).append("T_Report.Record_ID) WHERE Record_ID <> 0 AND AD_PInstance_ID=").append(this.getAD_PInstance_ID()).append(" AND PA_ReportLine_ID=").append(this.m_lines[line].getPA_ReportLine_ID()).append(" AND Fact_Acct_ID=0");
        no = DB.executeUpdate(sql.toString(), this.get_TrxName());
        if (CLogMgt.isLevelFinest()) {
            this.log.fine("Name #=" + no + " - " + sql.toString());
        }
        if (this.m_report.isListTrx()) {
            this.insertLineTrx(line, variable);
        }
    }
    
    private void insertLineTrx(final int line, final String variable) {
        this.log.info("Line=" + line + " - Variable=" + variable);
        final StringBuffer insert = new StringBuffer("INSERT INTO T_Report (AD_PInstance_ID, PA_ReportLine_ID, Record_ID,Fact_Acct_ID,LevelNo ");
        for (int col = 0; col < this.m_columns.length; ++col) {
            insert.append(",Col_").append(col);
        }
        insert.append(") SELECT ").append(this.getAD_PInstance_ID()).append(",").append(this.m_lines[line].getPA_ReportLine_ID()).append(",").append(variable).append(",Fact_Acct_ID, ");
        if (this.p_DetailsSourceFirst) {
            insert.append("-2 ");
        }
        else {
            insert.append("2 ");
        }
        for (int col = 0; col < this.m_columns.length; ++col) {
            insert.append(", ");
            if (!this.m_columns[col].isColumnTypeRelativePeriod() || this.m_columns[col].getRelativePeriodAsInt() != 0) {
                insert.append("NULL");
            }
            else if (this.m_lines[line].getPAAmountType() != null) {
                insert.append(this.m_lines[line].getSelectClause(false));
            }
            else if (this.m_columns[col].getPAAmountType() != null) {
                insert.append(this.m_columns[col].getSelectClause(false));
            }
            else {
                insert.append("NULL");
            }
        }
        insert.append(" FROM Fact_Acct WHERE ").append(this.m_lines[line].getWhereClause(this.p_PA_Hierarchy_ID));
        final String s = this.m_report.getWhereClause();
        if (s != null && s.length() > 0) {
            insert.append(" AND ").append(s);
        }
        final FinReportPeriod frp = this.getPeriod(0);
        insert.append(" AND TRUNC(DateAcct) ").append(frp.getPeriodWhere());
        final int no = DB.executeUpdate(insert.toString(), this.get_TrxName());
        this.log.finest("Trx #=" + no + " - " + (Object)insert);
        if (no == 0) {
            return;
        }
    }
    
    private void deleteUnprintedLines() {
        for (int line = 0; line < this.m_lines.length; ++line) {
            if (!this.m_lines[line].isPrinted()) {
                final String sql = "DELETE FROM T_Report WHERE AD_PInstance_ID=" + this.getAD_PInstance_ID() + " AND PA_ReportLine_ID=" + this.m_lines[line].getPA_ReportLine_ID();
                final int no = DB.executeUpdate(sql, this.get_TrxName());
                if (no > 0) {
                    this.log.fine(this.m_lines[line].getName() + " - #" + no);
                }
            }
        }
    }
    
    private void scaleResults() {
        for (int column = 0; column < this.m_columns.length; ++column) {
            final String factor = this.m_columns[column].getFactor();
            if (factor != null) {
                int divisor = 1;
                if (factor.equals("k")) {
                    divisor = 1000;
                }
                else {
                    if (!factor.equals("M")) {
                        break;
                    }
                    divisor = 1000000;
                }
                final String sql = "UPDATE T_Report SET Col_" + column + "=Col_" + column + "/" + divisor + " WHERE AD_PInstance_ID=" + this.getAD_PInstance_ID();
                final int no = DB.executeUpdate(sql, this.get_TrxName());
                if (no > 0) {
                    this.log.fine(this.m_columns[column].getName() + " - #" + no);
                }
            }
        }
    }
    
    private MPrintFormat getPrintFormat() {
        int AD_PrintFormat_ID = this.m_report.getAD_PrintFormat_ID();
        this.log.info("AD_PrintFormat_ID=" + AD_PrintFormat_ID);
        MPrintFormat pf = null;
        final boolean createNew = AD_PrintFormat_ID == 0;
        if (createNew) {
            final int AD_Table_ID = 544;
            pf = MPrintFormat.createFromTable(Env.getCtx(), AD_Table_ID);
            AD_PrintFormat_ID = pf.getAD_PrintFormat_ID();
            this.m_report.setAD_PrintFormat_ID(AD_PrintFormat_ID);
            this.m_report.save();
        }
        else {
            pf = MPrintFormat.get(this.getCtx(), AD_PrintFormat_ID, false);
        }
        if (!this.m_report.getName().equals(pf.getName())) {
            pf.setName(this.m_report.getName());
        }
        if (this.m_report.getDescription() == null) {
            if (pf.getDescription() != null) {
                pf.setDescription((String)null);
            }
        }
        else if (!this.m_report.getDescription().equals(pf.getDescription())) {
            pf.setDescription(this.m_report.getDescription());
        }
        pf.save();
        this.log.fine(pf + " - #" + pf.getItemCount());
        for (int count = pf.getItemCount(), i = 0; i < count; ++i) {
            final MPrintFormatItem pfi = pf.getItem(i);
            final String ColumnName = pfi.getColumnName();
            if (ColumnName == null) {
                this.log.log(Level.SEVERE, "No ColumnName for #" + i + " - " + pfi);
                if (pfi.isPrinted()) {
                    pfi.setIsPrinted(false);
                }
                if (pfi.isOrderBy()) {
                    pfi.setIsOrderBy(false);
                }
                if (pfi.getSortNo() != 0) {
                    pfi.setSortNo(0);
                }
            }
            else if (ColumnName.startsWith("Col")) {
                final int index = Integer.parseInt(ColumnName.substring(4));
                if (index < this.m_columns.length) {
                    pfi.setIsPrinted(this.m_columns[index].isPrinted());
                    String s = this.m_columns[index].getName();
                    if (this.m_columns[index].isColumnTypeRelativePeriod()) {
                        final BigDecimal relativeOffset = this.m_columns[index].getRelativePeriod();
                        final FinReportPeriod frp = this.getPeriod(relativeOffset);
                        if (s.contains("@Period@")) {
                            s = s.replace("@Period@", frp.getName());
                        }
                    }
                    if (!pfi.getName().equals(s)) {
                        pfi.setName(s);
                        pfi.setPrintName(s);
                    }
                    final int seq = 30 + index;
                    if (pfi.getSeqNo() != seq) {
                        pfi.setSeqNo(seq);
                    }
                    s = this.m_columns[index].getFormatPattern();
                    pfi.setFormatPattern(s);
                }
                else if (pfi.isPrinted()) {
                    pfi.setIsPrinted(false);
                }
                if (pfi.isOrderBy()) {
                    pfi.setIsOrderBy(false);
                }
                if (pfi.getSortNo() != 0) {
                    pfi.setSortNo(0);
                }
            }
            else if (ColumnName.equals("SeqNo")) {
                if (pfi.isPrinted()) {
                    pfi.setIsPrinted(false);
                }
                if (!pfi.isOrderBy()) {
                    pfi.setIsOrderBy(true);
                }
                if (pfi.getSortNo() != 10) {
                    pfi.setSortNo(10);
                }
            }
            else if (ColumnName.equals("LevelNo")) {
                if (pfi.isPrinted()) {
                    pfi.setIsPrinted(false);
                }
                if (!pfi.isOrderBy()) {
                    pfi.setIsOrderBy(true);
                }
                if (pfi.getSortNo() != 20) {
                    pfi.setSortNo(20);
                }
            }
            else if (ColumnName.equals("Name")) {
                if (pfi.getSeqNo() != 10) {
                    pfi.setSeqNo(10);
                }
                if (!pfi.isPrinted()) {
                    pfi.setIsPrinted(true);
                }
                if (!pfi.isOrderBy()) {
                    pfi.setIsOrderBy(true);
                }
                if (pfi.getSortNo() != 30) {
                    pfi.setSortNo(30);
                }
            }
            else if (ColumnName.equals("Description")) {
                if (pfi.getSeqNo() != 20) {
                    pfi.setSeqNo(20);
                }
                if (!pfi.isPrinted()) {
                    pfi.setIsPrinted(true);
                }
                if (pfi.isOrderBy()) {
                    pfi.setIsOrderBy(false);
                }
                if (pfi.getSortNo() != 0) {
                    pfi.setSortNo(0);
                }
            }
            else {
                if (pfi.isPrinted()) {
                    pfi.setIsPrinted(false);
                }
                if (pfi.isOrderBy()) {
                    pfi.setIsOrderBy(false);
                }
                if (pfi.getSortNo() != 0) {
                    pfi.setSortNo(0);
                }
            }
            pfi.save();
            this.log.fine(pfi.toString());
        }
        pf.setTranslation();
        pf = MPrintFormat.get(this.getCtx(), AD_PrintFormat_ID, true);
        return pf;
    }

    private ReportEngine createReportEngine (ProcessInfo pi, int WindowNo,MPrintFormat pf)
	{
		@SuppressWarnings("unused")
		int AD_Client_ID = Env.getAD_Client_ID(Env.getCtx());

		//  Create Query from Parameters
		String TableName = pi.getAD_Process_ID() == 202 ? "T_Report" : "T_ReportStatement";
		MQuery query = MQuery.get (Env.getCtx(), pi.getAD_PInstance_ID(), TableName);

		//	Get PrintFormat
		MPrintFormat format = pf;
		PrintInfo info = new PrintInfo(pi);

		ReportEngine re = new ReportEngine(Env.getCtx(), format, query, info);
		re.setWindowNo(WindowNo);
		
		return re;
	}	//	startFinReport

    //maximea
    private void addPeriodContext(int period)
    {
    	FinReportPeriod frpc = this.getPeriod(period);
        Env.setContext(Env.getCtx(), "Period1", frpc.getYearStartDate());  	
        Env.setContext(Env.getCtx(), "Period2", frpc.getEndDate());
    }
    
}
