package com.banking.logservice;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.banking.dao.LogServiceDao;
import com.banking.dao.implementation.LogServiceDaoImplementation;
import com.banking.model.AuditLog;

public class AuditLogHandler {

	private ExecutorService executor;
	private LogServiceDao logServiceDao;

	public AuditLogHandler() {
		this.executor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS,
				new LinkedBlockingQueue<Runnable>());
		this.logServiceDao = new LogServiceDaoImplementation();
	}

	public void addAuditData(AuditLog auditLog) {
		executor.execute(auditRunnable(auditLog));
	}

	private Runnable auditRunnable(AuditLog auditLog) {
		return new Runnable() {
			@Override
			public void run() {
				try {
					//System.out.println(Thread.currentThread().getName());
					logServiceDao.logAuditTable(auditLog);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
	}
}
