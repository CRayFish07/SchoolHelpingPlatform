package dao.impl;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import pojo.Creditvaluelog;
import pojo.User;
import cache.Configurations;
import bean.TransferDbData;
import dao.CreditvaluelogDao;
import exception.HibernateSessionNotInitializedException;
import exception.MissingParameterException;

public class CreditvaluelogDaoImpl extends HibernateDaoSupport implements CreditvaluelogDao {
	Session session;
	
	private static String getHql(TransferDbData transferDbData) {
		String preHql;
		if (transferDbData.getValues().size() <= 0)
			preHql = "SELECT c FROM Creditvaluelog AS c";
		else
			preHql = "SELECT c FROM Creditvaluelog AS c WHERE";
		for (Integer key : transferDbData.getValues().keySet()) {
			switch(key) {
			case uid:
				preHql += " AND c.uid = ?";
				break;
			case userUid:
				preHql += " AND c.user.uid = ?";
				break;
			case changeValue:
				preHql += " AND c.changevalue >= ? AND c.changevalue <= ?";
				break;
			case finalValue:
				preHql += " AND c.finalvalue >= ? AND c.finalvalue <= ?";
				break;
			case reason:
				preHql += " AND c.value = ?";
				break;
			case createTime:
				preHql += " AND c.createtime >= ? AND c.createtime <= ?";
				break;
			}
		}
		return preHql;
	}
	
	@SuppressWarnings("unchecked")
	private List<Creditvaluelog> hibernateListProcess(TransferDbData transferDbData)
			throws HibernateException, HibernateSessionNotInitializedException {
		String hql = getHql(transferDbData);
		if (session == null)
			throw new HibernateSessionNotInitializedException("Hibernate Session 未被初始化!");
		Query query = session.createQuery(hql);
		int currentSet = 0;
		for (Integer key : transferDbData.getValues().keySet()) {
			String value = transferDbData.getValues().get(key);
			switch(key) {
			case uid:
			case userUid:
			case reason:
				query.setString(currentSet, value);
				currentSet++;
				break;
			case changeValue:
			case finalValue:
			case createTime:
				String[] splitStrings = value.split(Configurations.split_string);
				if (splitStrings.length > 1) {
					for (int i = 0; i < splitStrings.length; i++)
						query.setString(currentSet + i, splitStrings[i]);
				} else {
					query.setString(currentSet++, value);
					query.setString(currentSet++, value);
				}
			}
		}
		List<Creditvaluelog> list = query.list();
		return list;
	}
	
	private void hibernateSaveProcess(TransferDbData transferDbData)
			throws NumberFormatException, HibernateSessionNotInitializedException, NumberFormatException {
		if (session == null)
			throw new HibernateSessionNotInitializedException("Hibernate Session 未被初始化!");
		Creditvaluelog creditValueLog = new Creditvaluelog();
		for (Integer key : transferDbData.getValues().keySet()) {
			String value = transferDbData.getValues().get(key);
			switch(key) {
			case uid:
				creditValueLog.setUid(value);
				break;
			case userUid:
				User user = new User();
				user.setUid(value);
				creditValueLog.setUser(user);
				break;
			case reason:
				creditValueLog.setReason(value);
				break;
			case changeValue:
				creditValueLog.setChangevalue(Integer.valueOf(value));
				break;
			case finalValue:
				creditValueLog.setFinalvalue(Integer.valueOf(value));
				break;
			case createTime:
				creditValueLog.setCreatetime(Integer.valueOf(value));
				break;
			}
		}
		Transaction tx = session.beginTransaction();
		session.save(creditValueLog);
		tx.commit();
	}
	
	private void hibernateUpdateProcess(TransferDbData transferDbData)
			throws HibernateSessionNotInitializedException, MissingParameterException, NumberFormatException {
		if (session == null)
			throw new HibernateSessionNotInitializedException("Hibernate Session 未被初始化!");
		String s_uid = transferDbData.getValues().get(uid);
		if (s_uid == null)
			throw new MissingParameterException("uid 不能为空");
		session.beginTransaction();
		Creditvaluelog creditValueLog = (Creditvaluelog) session.get(Creditvaluelog.class, s_uid);
		for (Integer key : transferDbData.getValues().keySet()) {
			String value = transferDbData.getValues().get(key);
			switch(key) {
			case userUid:
				creditValueLog.setUid(value);
				break;
			case reason:
				creditValueLog.setReason(value);
				break;
			case changeValue:
				creditValueLog.setChangevalue(Integer.valueOf(value));
				break;
			case finalValue:
				creditValueLog.setFinalvalue(Integer.valueOf(value));
				break;
			case createTime:
				creditValueLog.setCreatetime(Integer.valueOf(value));
				break;
			}
		}
		Transaction tx = session.beginTransaction();
		session.update(creditValueLog);
		tx.commit();
	}
	
	@Override
	public void init() throws HibernateException {
		// TODO Auto-generated method stub
		session = getHibernateTemplate().getSessionFactory().openSession();
	}

	@Override
	public void close() throws HibernateException {
		// TODO Auto-generated method stub
		if (session != null)
			releaseSession(session);
	}

	@Override
	public void insert(TransferDbData transferDbData)
			throws HibernateException, HibernateSessionNotInitializedException {
		// TODO Auto-generated method stub
		hibernateSaveProcess(transferDbData);
	}

	@Override
	public void update(TransferDbData transferDbData)
			throws HibernateException, HibernateSessionNotInitializedException,
			MissingParameterException {
		// TODO Auto-generated method stub
		hibernateUpdateProcess(transferDbData);
	}

	@Override
	public List<Creditvaluelog> select(TransferDbData transferDbData)
			throws HibernateException, HibernateSessionNotInitializedException {
		// TODO Auto-generated method stub
		return hibernateListProcess(transferDbData);
	}
}
