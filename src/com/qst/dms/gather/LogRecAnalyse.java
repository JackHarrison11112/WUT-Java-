package com.qst.dms.gather;

import java.util.ArrayList;

import com.qst.dms.entity.DataBase;
import com.qst.dms.entity.LogRec;
import com.qst.dms.entity.MatchedLogRec;
import com.qst.dms.exception.DataAnalyseException;

//��־�����࣬�̳�DataFilter�����࣬ʵ�����ݷ����ӿ�
public class LogRecAnalyse extends DataFilter implements IDataAnalyse {
	// ����¼������
	private ArrayList<LogRec> logIns = new ArrayList<>();
	// ���ǳ�������
	private ArrayList<LogRec> logOuts = new ArrayList<>();

	// ���췽��
	public LogRecAnalyse() {
	}

	public LogRecAnalyse(ArrayList<LogRec> logRecs) {
		super(logRecs);
	}

	// ʵ��DataFilter�������еĹ��˳��󷽷�
	public void doFilter() {
		// ��ȡ���ݼ���
		ArrayList<LogRec> logs = (ArrayList<LogRec>) this.getDatas();

		// ����������־���ݽ��й��ˣ�������־��¼״̬�ֱ���ڲ�ͬ��������
		for(LogRec l:logs){
			if(l.getLogType()==LogRec.LOG_IN)logIns.add(l);// ��ӵ�����¼����־������
			else if(l.getLogType()==LogRec.LOG_OUT)logOuts.add(l);// ��ӵ����ǳ�����־������
		}

	}
	// ʵ��IDataAnalyse�ӿ������ݷ�������
	public ArrayList<MatchedLogRec> matchData() {
		// ������־ƥ�伯��
		ArrayList<MatchedLogRec> matchLogs = new ArrayList<>();
		// ����ƥ�����
			for(LogRec in:logIns){
				for(LogRec out:logOuts){
					if(in.getUser().equals(out.getUser())&&in.getIp().equals(out.getIp()))
						matchLogs.add(new MatchedLogRec(in,out));
				}
			}
		try {
			if(matchLogs.size()==0){
				throw new DataAnalyseException("�]��ƥ�����־���ݣ�");
			}
			// û�ҵ�ƥ�������,�׳�DataAnalyseException�쳣
		}catch (Exception e) {
			e.printStackTrace();}
		return matchLogs;
	}
}