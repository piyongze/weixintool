package com.pyz.tool.weixintool.util;

import java.io.*;
import java.util.Hashtable;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * �����ļ�
 * @author Administrator
 *
 */
public class LockFile extends File{
	

	private static Hashtable<String,ReentrantReadWriteLock> locks=new Hashtable<String,ReentrantReadWriteLock>();
	private ReentrantReadWriteLock lock=null;
	/**
	 * 
	 */
	public LockFile(String str) {
		super(str);
		lock=initLock(str);
		// TODO Auto-generated constructor stub
	}

	private static synchronized ReentrantReadWriteLock initLock(String path){
		ReentrantReadWriteLock lock=locks.get(path);
		if(lock==null){
			lock=new ReentrantReadWriteLock();
			locks.put(path, lock);
		}
		return lock;
		
	}
	
	public ReentrantReadWriteLock getLock(){
		return lock;
	}
}
