package com.qst.dms.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.rmi.server.ServerNotActiveException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.io.File;
import java.io.EOFException;

import com.qst.dms.db.DBUtil;
import com.qst.dms.entity.AppendObjectOutputStream;
import com.qst.dms.entity.DataBase;
import com.qst.dms.entity.MatchedLogRec;
import com.qst.dms.entity.MatchedTransport;
import com.qst.dms.entity.Transport;

public class TransportService {
	// �������ݲɼ�
	DBUtil db=new DBUtil();
	public Transport inputTransport() {
		Transport trans = null;

		// ����һ���Ӽ��̽������ݵ�ɨ����
		Scanner scanner = new Scanner(System.in);
		try {
			// ��ʾ�û�����ID��ʶ
			System.out.println("������ID��ʶ��");
			// ���ռ������������
			int id = scanner.nextInt();
			// ��ȡ��ǰϵͳʱ��
			Date nowDate = new Date();
			// ��ʾ�û������ַ
			System.out.println("�������ַ��");
			// ���ռ���������ַ�����Ϣ
			String address = scanner.next();
			// ����״̬�ǡ��ɼ���
			int type = DataBase.GATHER;

			// ��ʾ�û������¼�û���
			System.out.println("��������ﾭ���ˣ�");
			// ���ռ���������ַ�����Ϣ
			String handler = scanner.next();
			// ��ʾ�û���������IP
			System.out.println("������ �ջ���:");
			// ���ռ���������ַ�����Ϣ
			String reciver = scanner.next();
			// ��ʾ������������״̬
			System.out.println("����������״̬��1�����У�2�ͻ��У�3��ǩ��");
			// ��������״̬
			int transportType = scanner.nextInt();
			// ����������Ϣ����
			trans = new Transport(id, nowDate, address, type, handler, reciver,
					transportType);
		} catch (Exception e) {
			System.out.println("�ɼ�����־��Ϣ���Ϸ�");
		}
		// ������������
		return trans;
	}

	// ������Ϣ���
	public void showTransport(Transport... transports) {
		for (Transport e : transports) {
			if (e != null) {
				System.out.println(e.toString());
			}
		}
	}

	// ƥ���������Ϣ������ɱ����
	public void showMatchTransport(MatchedTransport... matchTrans) {
		for (MatchedTransport e : matchTrans) {
			if (e != null) {
				System.out.println(e.toString());
			}
		}
	}

	// ƥ���������Ϣ����������Ǽ���
	public void showMatchTransport(ArrayList<MatchedTransport> matchTrans) {
		for (MatchedTransport e : matchTrans) {
			if (e != null) {
				System.out.println(e.toString());
			}
		}
	}

	// ƥ��������Ϣ���棬�����Ǽ���
	public void saveMatchedTransport(ArrayList<MatchedTransport> matchTrans) {
		// ����һ��ObjectOutputStream������������������ļ������
		// �Կ�׷�ӵķ�ʽ�����ļ�����������ݱ��浽MatchedTransports.txt�ļ���
		File file=new File("./MatchedTransports.txt");
		try (ObjectOutputStream obs = new ObjectOutputStream(
				new FileOutputStream(file))) {
			// ѭ�������������
			for (MatchedTransport e : matchTrans) {
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
	public void saveAppendedMatchedTransport(ArrayList<MatchedTransport> matchTrans) {
		// ����һ��ObjectOutputStream������������������ļ������
		// �Կ�׷�ӵķ�ʽ�����ļ�����������ݱ��浽MatchedTransports.txt�ļ���
		File file=new File("./MatchedTransports.txt");
		AppendObjectOutputStream ops=null;
		//(ObjectOutputStream obs = new ObjectOutputStream(new FileOutputStream(file)))			
		try  {
			AppendObjectOutputStream.file=file;
			ops=new AppendObjectOutputStream(file);
			// ѭ�������������
			for (MatchedTransport e : matchTrans) {
				if (e != null) {
					// �Ѷ���д�뵽�ļ���
					ops.writeObject(e);
					ops.flush();
				}
			}
			// �ļ�ĩβ����һ��null���󣬴����ļ�����
			ops.writeObject(null);
			ops.flush();
		} catch (Exception ex) {ex.printStackTrace();}
		/*finally {
			if(ops!=null) {
				try{ops.close();}catch(IOException e) {e.printStackTrace();}
			}
		}*/
			
	}
	

	// ��ƥ��������Ϣ���棬�����Ǽ���
	public ArrayList<MatchedTransport> readMatchedTransport() {
		ArrayList<MatchedTransport> matchTrans = new ArrayList<>();
		// ����һ��ObjectInputStream�������������������ļ�����������MatchedTransports.txt�ļ���
			MatchedTransport matchTran;
			try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream("./MatchedTransports.txt"))){
			// ѭ�����ļ��еĶ���
			while (true) {
				try {
				matchTran = (MatchedTransport) ois.readObject();
				// ��������ӵ����ͼ�����
				matchTrans.add(matchTran);
				} catch (EOFException ex) {break;}
			}
			}catch(Exception e) {e.printStackTrace();}
		
		return matchTrans;
	}
	public void saveMatchTransportToDB(ArrayList<MatchedTransport> matchTrans){

		String sql1="insert into gather_transport (id,time,address,type,handler,reciver,transporttype) values(?,?,?,?,?,?,?)," +
				"(?,?,?,?,?,?,?),(?,?,?,?,?,?,?);";//�������������ݿ����
		String sql2="insert into matched_transport(sendid,transid,receiveid) values(?,?,?);";
		try {
			db.getConnection();
			for(MatchedTransport match:matchTrans){
				Transport send=match.getSend();
				Transport trans=match.getTrans();
				Transport receive=match.getReceive();
				db.executeUpdate(sql1,new Object[]{send.getId(),send.getTime(),send.getAddress(),send.getType(),send.getHandler(),
						send.getReciver(),send.getTransportType(),trans.getId(),trans.getTime(),trans.getAddress(),trans.getType(),trans.getHandler(),
						trans.getReciver(),trans.getTransportType(),receive.getId(),receive.getTime(),receive.getAddress(),receive.getType(),receive.getHandler(),
						receive.getReciver(),receive.getTransportType()
				});
				db.executeUpdate(sql2,new Object[]{send.getId(),trans.getId(),receive.getId()});
			}
			db.closeAll();
		}catch (Exception e){
			e.printStackTrace();
		}

	}
	public ArrayList<MatchedTransport> readMatchedTransportFromDB(){
		ArrayList<MatchedTransport> transportList=new ArrayList<MatchedTransport>();
		String sql="select gt.id,gt.`time` ,gt.address ,gt.`type` ,gt.handler ,gt.reciver ,gt.transporttype ,gt1.id,gt1.`time` ,gt1.address ,gt1.`type`,gt1.handler ,gt1.reciver ,gt1.transporttype" +
				",gt2.id,gt2.`time` ,gt2.address ,gt2.`type` ,gt2.handler ,gt2.reciver ,gt2.transporttype " +
				"from gather_transport gt,gather_transport gt1,gather_transport gt2,matched_transport m where gt.id=m.sendid and gt1.id=m.transid and gt2.id=m.receiveid ;";
		try{
			db.getConnection();
			ResultSet rs=db.executeQuery(sql,null);
			while(rs.next()){
				Transport send=new Transport(rs.getInt(1),rs.getTime(2),rs.getString(3),
						rs.getInt(4),rs.getString(5),rs.getString(6),rs.getInt(7));
				Transport trans=new Transport(rs.getInt(8),rs.getTime(9),rs.getString(10),
						rs.getInt(11),rs.getString(12),rs.getString(13),rs.getInt(14));
				Transport receive=new Transport(rs.getInt(15),rs.getTime(16),rs.getString(17),
						rs.getInt(18),rs.getString(19),rs.getString(20),rs.getInt(21));
				transportList.add(new MatchedTransport(send,trans,receive));
			}
			db.closeAll();
		}catch (Exception e){e.printStackTrace();}

		return transportList;
	}
}