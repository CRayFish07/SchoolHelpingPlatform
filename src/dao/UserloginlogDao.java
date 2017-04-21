package dao;

import java.util.List;

import org.hibernate.HibernateException;

import pojo.Userloginlog;
import exception.HibernateSessionNotInitializedException;
import exception.MissingParameterException;
import bean.TransferDbData;

public interface UserloginlogDao {
	public static final int insert = 1;
	public static final int update = 2;
	public static final int select = 3;
	public static final int uid = 0;
	public static final int userUid = 1;
	public static final int ip = 2;
	public static final int loginTime = 3;
	public abstract void init() throws HibernateException;
	public abstract void close() throws HibernateException;
	public abstract void insert(TransferDbData transferDbData) throws HibernateException, HibernateSessionNotInitializedException;
	public abstract void update(TransferDbData transferDbData) throws HibernateException, HibernateSessionNotInitializedException, MissingParameterException;
	public abstract List<Userloginlog> select(TransferDbData transferDbData) throws HibernateException, HibernateSessionNotInitializedException;
}
