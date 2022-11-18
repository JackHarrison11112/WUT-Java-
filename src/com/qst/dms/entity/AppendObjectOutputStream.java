package com.qst.dms.entity;
import java.io.*;
import java.io.File;
public class AppendObjectOutputStream extends ObjectOutputStream {
	public  static File file=null;
	public AppendObjectOutputStream (File file)throws IOException{
		super(new FileOutputStream(file,true));
	}
	public void writeStreamHeader() throws IOException{
		if(file==null)super.writeStreamHeader();//�ļ�������
		else {
			if(file.length()==0)super.writeStreamHeader();//�ļ�Ϊ�գ�д��ͷ��Ϣ
			else this.reset();//�ļ���Ϊ�գ���Ҫ��дͷ��Ϣ
		}
	}

}
