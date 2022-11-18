package com.qst.dms.service;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.io.EOFException;

import com.qst.dms.db.DBUtil;
import com.qst.dms.entity.AppendObjectOutputStream;
import com.qst.dms.entity.DataBase;
import com.qst.dms.entity.LogRec;
import com.qst.dms.entity.MatchedLogRec;
import com.qst.dms.entity.MatchedTransport;
//��־ҵ����
public class LogRecService {
	//ArrayList<MatchedLogRec> MatchedLogs;
	// ��־���ݲɼ�
	//DataBase db;
	//int recnum;
	DBUtil du=new DBUtil();
	public LogRec inputLog() {
		LogRec log = null;
		// ����һ���Ӽ��̽������ݵ�ɨ����
		Scanner scanner = new Scanner(System.in);
		try {
			// ��ʾ�û�����ID��ʶ
			System.out.println("������ID��ʶ��");
			// ���ռ������������
			int id=scanner.nextInt();
			// ��ȡ��ǰϵͳʱ��
			Date nowDate=new Date();
			// ��ʾ�û������ַ
			System.out.println("�������ַ��");
			// ���ռ���������ַ�����Ϣ
			String address=scanner.next();
			// ����״̬�ǡ��ɼ���
			int type = DataBase.GATHER;
			// ��ʾ�û������¼�û���
			System.out.println("�������û�����");
			// ���ռ���������ַ�����Ϣ
			String user=scanner.next()
;			// ��ʾ�û���������IP
			System.out.println("������IP��");
			// ���ռ���������ַ�����Ϣ
			String ip=scanner.next();
			// ��ʾ�û������¼״̬���ǳ�״̬
			System.out.println("�������¼״̬:1�ǵ�¼��0�ǵǳ�");
			int logType = scanner.nextInt();
			// ������־����
			log = new LogRec(id, nowDate, address, type, user, ip, logType);
		} catch (Exception e) {
			System.out.println("�ɼ�����־��Ϣ���Ϸ�");
		}
		// ������־����
		return log;
	}

	// ��־��Ϣ���
	public void showLog(LogRec... logRecs) {
		for (LogRec e : logRecs) {
			if (e != null) {
				System.out.println(e.toString());
			}
		}
	}

	// ƥ����־��Ϣ������ɱ����
	public void showMatchLog(MatchedLogRec... matchLogs) {
		for (MatchedLogRec e : matchLogs) {
			if (e != null) {
				System.out.println(e.toString());
			}
		}
	}

	// ƥ����־��Ϣ���,�����Ǽ���
	public void showMatchLog(ArrayList<MatchedLogRec> matchLogs) {
		for (MatchedLogRec e : matchLogs) {
			if (e != null) {
				System.out.println(e.toString());
			}
		}
	}
	public void saveMatchLog(ArrayList<MatchedLogRec> matchLogrecs) {
		// ����һ��ObjectOutputStream������������������ļ������
		// �Կ�׷�ӵķ�ʽ�����ļ�����������ݱ��浽MatchedTransports.txt�ļ���
		File file=new File("./MatchedLogRecs.txt");
		try (AppendObjectOutputStream obs = new AppendObjectOutputStream(file)) {
			//����AppendStream����
			// ѭ�������������
			for (MatchedLogRec e : matchLogrecs) {
				if (e != null) {
					// �Ѷ���д�뵽�ļ���
					obs.writeObject(e);
					obs.flush();
				}
			}
			// �ļ�ĩβ����һ��null���󣬴����ļ�����
			obs.writeObject(null);
			obs.flush();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	public void saveAppendMatchLog(ArrayList<MatchedLogRec> matchLogrecs) {
		// ����һ��ObjectOutputStream������������������ļ������
		// �Կ�׷�ӵķ�ʽ�����ļ�����������ݱ��浽MatchedTransports.txt�ļ���
		AppendObjectOutputStream obs=null;
		File file=new File("./MatchedLogRecs.txt");
		try {
			//����AppendStream����
			AppendObjectOutputStream.file=file;//��һ���������ʲô�أ��ɲ�����ʡ��
			obs=new AppendObjectOutputStream(file);
			//obs.setfile(file);
			// ѭ�������������
			for (MatchedLogRec e : matchLogrecs) {
				if (e != null) {
					// �Ѷ���д�뵽�ļ���
					obs.writeObject(e);
					obs.flush();
				}
			}
		} catch (Exception ex) {			
		}finally {
			if(obs!=null) {
				try {obs.close();}catch(IOException e) {e.printStackTrace();}
			}
		}
	}
	public ArrayList<MatchedLogRec> readMatchLog(){
		ArrayList<MatchedLogRec> matchLogs = new ArrayList<>();
		// ����һ��ObjectInputStream�������������������ļ�����������MatchedTransports.txt�ļ���
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
				"./MatchedLogRecs.txt"))) {
			MatchedLogRec matchLog;
			// ѭ�����ļ��еĶ���
			/*
			while((matchLog = (MatchedLogRec) ois.readObject())!=null) {
				matchLogs.add(matchLog);
			}//����EOF�����޷����*/
			//ѭ������ȡ��EOF�������˳�ѭ��
			while (true) {
				try {
					// ��������ӵ����ͼ�����
					matchLog = (MatchedLogRec) ois.readObject();
					matchLogs.add(matchLog);
				}catch(EOFException ef) {break;}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return matchLogs;
	}
	public void saveMatchLogToDB(ArrayList<MatchedLogRec> matchLogs) {
		try{
		du.getConnection();
			//int[] mlogid=new int[]{matchLogs.get(i).getLogin().getId(),matchLogs.get(i).getLogout().getId()}
			String sql1 = "insert into gather_logrec(id,time,address,type,username,ip,logtype) values(?,?,?,?,?,?,?),(?,?,?,?,?,?,?);";
			String sql2 = "insert into matched_logrec(loginid,logoutid) values(?,?);";
		for(MatchedLogRec r:matchLogs){
			//String sql1 = "insert into gather_logrec values(?,?,?,?,?,?,?),(?,?,?,?,?,?,?);";
			LogRec in=r.getLogin();
			LogRec out=r.getLogout();
			du.executeUpdate(sql1,new Object[]{in.getId(),in.getTime(),in.getAddress(),in.getType(),in.getUser(),in.getIp(),
					in.getLogType(),out.getId(),out.getTime(),out.getAddress(),out.getType(),out.getUser(),out.getIp(),out.getLogType()});
			//du.executeUpdate(sql1,new Object[]{});
			du.executeUpdate(sql2,new Object[]{in.getId(),out.getId()});
		}
			du.closeAll();
		}catch(Exception e){e.printStackTrace();}
	}
	public ArrayList<MatchedLogRec> readMatchedLogFromDB(){
		ArrayList<MatchedLogRec> logList=new ArrayList<MatchedLogRec>();
		String sql2="select i.id,i.time,i.address,i.type,i.username,i.ip,i.logtype,o.id,o.time,o.address,o.type,o.username,o.ip,o.logtype from matched_logrec m,gather_logrec i, gather_logrec o where i.id=m.loginid and o.id=m.logoutid;";
		try{
			du.getConnection();
			ResultSet rs=du.executeQuery(sql2,null);
			while(rs.next()){
				LogRec login=new LogRec(rs.getInt(1),rs.getDate(2),rs.getString(3),rs.getInt(4),rs.getString(5),rs.getString(6),rs.getInt(7));
				LogRec logout=new LogRec(rs.getInt(8),rs.getDate(9),
						rs.getString(10),rs.getInt(11),rs.getString(12),
						rs.getString(13),rs.getInt(14));
				logList.add(new MatchedLogRec(login,logout));
			}
			du.closeAll();
		}catch (Exception e){e.printStackTrace();};
		return logList;
	}
}
	