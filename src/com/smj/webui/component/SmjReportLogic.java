package com.smj.webui.component;

import org.compiere.apps.form.*;
import java.util.*;
import com.smj.entity.*;
import java.util.logging.*;
import java.sql.*;
import org.compiere.util.*;

public class SmjReportLogic
{
    private CLogger log;
    
    public SmjReportLogic() {
        this.log = CLogger.getCLogger((Class)Allocation.class);
    }
    
    protected LinkedList<ReportTO> getDataReport(final Integer idReport, final String nameTrx) {
        final StringBuffer sql = new StringBuffer();
        sql.append("select seqno, name, description, col_0, col_1, col_2, col_3, col_4, col_5, col_6, col_7, col_8, ");
        sql.append("col_9, col_10, col_11, col_12, col_13, col_14, col_15, col_16, col_17, col_18, col_19, col_20, ");
        sql.append("smj_hierarchylevel, smj_reportline, smj_fixedPercentage from T_Report ");
        sql.append("where ad_pinstance_id = " + idReport + " ");
        sql.append(" order by seqno, name asc ");
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final LinkedList<ReportTO> data = new LinkedList<ReportTO>();
        try {
            pstmt = (PreparedStatement)DB.prepareStatement(sql.toString(), nameTrx);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                final ReportTO rpt = new ReportTO();
                rpt.setSeqno(rs.getInt("seqno"));
                rpt.setName(rs.getString("name"));
                rpt.setDescription(rs.getString("description"));
                rpt.setCol_0(rs.getBigDecimal("col_0"));
                rpt.setCol_1(rs.getBigDecimal("col_1"));
                rpt.setCol_2(rs.getBigDecimal("col_2"));
                rpt.setCol_3(rs.getBigDecimal("col_3"));
                rpt.setCol_4(rs.getBigDecimal("col_4"));
                rpt.setCol_5(rs.getBigDecimal("col_5"));
                rpt.setCol_6(rs.getBigDecimal("col_6"));
                rpt.setCol_7(rs.getBigDecimal("col_7"));
                rpt.setCol_8(rs.getBigDecimal("col_8"));
                rpt.setCol_9(rs.getBigDecimal("col_9"));
                rpt.setCol_10(rs.getBigDecimal("col_10"));
                rpt.setCol_11(rs.getBigDecimal("col_11"));
                rpt.setCol_12(rs.getBigDecimal("col_12"));
                rpt.setCol_13(rs.getBigDecimal("col_13"));
                rpt.setCol_14(rs.getBigDecimal("col_14"));
                rpt.setCol_15(rs.getBigDecimal("col_15"));
                rpt.setCol_16(rs.getBigDecimal("col_16"));
                rpt.setCol_17(rs.getBigDecimal("col_17"));
                rpt.setCol_18(rs.getBigDecimal("col_18"));
                rpt.setCol_19(rs.getBigDecimal("col_19"));
                rpt.setCol_20(rs.getBigDecimal("col_20"));
                rpt.setSmj_hierarchylevel(rs.getInt("smj_hierarchylevel"));
                rpt.setSmj_reportline(rs.getString("smj_reportline"));
                rpt.setSmj_fixedPercentage(rs.getInt("smj_fixedPercentage"));
                data.add(rpt);
            }
        }
        catch (Exception e) {
            this.log.log(Level.SEVERE, sql.toString(), (Throwable)e);
        }
        finally {
            DB.close(rs, (Statement)pstmt);
            rs = null;
            pstmt = null;
        }
        return data;
    }
    
    protected String[] getGeneralTitle(final Integer idReportLineSet, final String nameTrx) {
        final StringBuffer sql = new StringBuffer();
        sql.append("select name, smj_prePeriodName, smj_posPeriodName from PA_Report ");
        sql.append("where pa_reportlineset_id = " + idReportLineSet + " ");
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final String[] data = new String[3];
        try {
            pstmt = (PreparedStatement)DB.prepareStatement(sql.toString(), nameTrx);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                data[0] = rs.getString("name");
                data[1] = rs.getString("smj_prePeriodName");
                data[2] = rs.getString("smj_posPeriodName");
            }
        }
        catch (Exception e) {
            this.log.log(Level.SEVERE, sql.toString(), (Throwable)e);
        }
        finally {
            DB.close(rs, (Statement)pstmt);
            rs = null;
            pstmt = null;
        }
        return data;
    }
    
    protected String getClientName(final String nameTrx) {
        final StringBuffer sql = new StringBuffer();
        sql.append("select name from AD_Client ");
        sql.append("where AD_Client_ID = " + Env.getAD_Client_ID(Env.getCtx()) + " ");
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String name = "";
        try {
            pstmt = (PreparedStatement)DB.prepareStatement(sql.toString(), nameTrx);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                name = rs.getString("name");
            }
        }
        catch (Exception e) {
            this.log.log(Level.SEVERE, sql.toString(), (Throwable)e);
        }
        finally {
            DB.close(rs, (Statement)pstmt);
            rs = null;
            pstmt = null;
        }
        return name;
    }
    
    protected String getOrgName(final String nameTrx) {
        final StringBuffer sql = new StringBuffer();
        sql.append("select name from AD_Org where AD_Client_ID = ");
        sql.append(Env.getAD_Client_ID(Env.getCtx()) + " and ad_org_id = " + Env.getAD_Org_ID(Env.getCtx()) + " ");
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String name = "";
        try {
            pstmt = (PreparedStatement)DB.prepareStatement(sql.toString(), nameTrx);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                name = rs.getString("name");
            }
        }
        catch (Exception e) {
            this.log.log(Level.SEVERE, sql.toString(), (Throwable)e);
        }
        finally {
            DB.close(rs, (Statement)pstmt);
            rs = null;
            pstmt = null;
        }
        return name;
    }
    
    protected String getOrgNIT(final String nameTrx) {
        final StringBuffer sql = new StringBuffer();
        sql.append("select taxid from AD_OrgInfo where ad_client_id = ");
        sql.append(Env.getAD_Client_ID(Env.getCtx()) + " and ad_org_id = " + Env.getAD_Org_ID(Env.getCtx()) + " ");
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String name = "";
        try {
            pstmt = (PreparedStatement)DB.prepareStatement(sql.toString(), nameTrx);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                name = rs.getString("taxid");
            }
        }
        catch (Exception e) {
            this.log.log(Level.SEVERE, sql.toString(), (Throwable)e);
        }
        finally {
            DB.close(rs, (Statement)pstmt);
            rs = null;
            pstmt = null;
        }
        return name;
    }
    
    protected String getPeriodName(final Integer idPeriod, final String nameTrx) {
        final StringBuffer sql = new StringBuffer();
        sql.append("select name from C_Period ");
        sql.append("where C_Period_ID = " + idPeriod + " ");
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String name = "";
        try {
            pstmt = (PreparedStatement)DB.prepareStatement(sql.toString(), nameTrx);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                name = rs.getString("name");
            }
        }
        catch (Exception e) {
            this.log.log(Level.SEVERE, sql.toString(), (Throwable)e);
        }
        finally {
            DB.close(rs, (Statement)pstmt);
            rs = null;
            pstmt = null;
        }
        return name;
    }
    
    protected String getCurrency(final String nameTrx) {
        final StringBuffer sql = new StringBuffer();
        sql.append("select description from c_currency ");
        sql.append("where c_currency_id = (select c_currency_id from C_AcctSchema ");
        sql.append("where AD_Client_ID = " + Env.getAD_Client_ID(Env.getCtx()) + ") ");
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String name = "";
        try {
            pstmt = (PreparedStatement)DB.prepareStatement(sql.toString(), nameTrx);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                name = rs.getString("description");
            }
        }
        catch (Exception e) {
            this.log.log(Level.SEVERE, sql.toString(), (Throwable)e);
        }
        finally {
            DB.close(rs, (Statement)pstmt);
            rs = null;
            pstmt = null;
        }
        return name;
    }
    
    protected String getCodeFont(final String nameTrx, final Integer codeFont) {
        final StringBuffer sql = new StringBuffer();
        sql.append("SELECT code FROM ad_printfont WHERE ad_printfont_id = " + codeFont + " ");
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String name = "";
        try {
            pstmt = (PreparedStatement)DB.prepareStatement(sql.toString(), nameTrx);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                name = rs.getString("code");
            }
        }
        catch (Exception e) {
            this.log.log(Level.SEVERE, sql.toString(), (Throwable)e);
        }
        finally {
            DB.close(rs, (Statement)pstmt);
            rs = null;
            pstmt = null;
        }
        return name;
    }
    
    protected String getClientCity(final String nameTrx) {
        final StringBuffer sql = new StringBuffer();
        sql.append(" select city from c_location where c_location_id = ( ");
        sql.append(" select c_location_id from AD_OrgInfo where ad_client_id = ");
        sql.append(Env.getAD_Client_ID(Env.getCtx()) + " and ad_org_id = " + Env.getAD_Org_ID(Env.getCtx()) + ") ");
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String name = "";
        try {
            pstmt = (PreparedStatement)DB.prepareStatement(sql.toString(), nameTrx);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                name = rs.getString("city");
            }
        }
        catch (Exception e) {
            this.log.log(Level.SEVERE, sql.toString(), (Throwable)e);
        }
        finally {
            DB.close(rs, (Statement)pstmt);
            rs = null;
            pstmt = null;
        }
        return name;
    }
}
