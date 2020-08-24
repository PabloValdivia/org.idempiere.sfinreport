package sfr.idempiere.component;

import org.adempiere.base.IProcessFactory;
import org.compiere.process.ProcessCall;

import com.smj.process.SmjReport;


public class SFRProcess implements IProcessFactory {

	public SFRProcess(){
		
	}
	
	public ProcessCall newProcessInstance(String className) {
		if (className.equals(SmjReport.class.getName()))
			return new SmjReport();
		return null;
	}

}
