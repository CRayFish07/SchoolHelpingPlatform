package dao;

import java.util.List;

import org.hibernate.HibernateException;

import exception.HibernateSessionNotInitializedException;
import exception.MissingParameterException;
import pojo.Qualificationtype;
import bean.TransferDbData;

public interface QualificationtypeDao {
	public static final int insert = 1;
	public static final int update = 2;
	public static final int select = 3;
	public static final int delete = 4;
	public static final int uid = 0;
	public static final int name = 1;
	public static final int problemTypeUid = 2;
	public static final int createIp = 3;
	public static final int createTime = 4;
	public static final int createUserUid = 5;
	public abstract void init() throws HibernateException;
	public abstract void close() throws HibernateException;
	public abstract void insert(TransferDbData transferDbData) throws HibernateException, HibernateSessionNotInitializedException;
	public abstract void update(TransferDbData transferDbData) throws HibernateException, HibernateSessionNotInitializedException, MissingParameterException;
	public abstract void delete(TransferDbData transferDbData) throws HibernateException, HibernateSessionNotInitializedException, MissingParameterException;
	public abstract List<Qualificationtype> select(TransferDbData transferDbData) throws HibernateException, HibernateSessionNotInitializedException;
}
