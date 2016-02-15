package net.viperfish.journal.dbProvider;

import java.util.List;
import java.util.logging.Level;

import org.hibernate.Session;

import net.viperfish.journal.framework.EntryDatabase;
import net.viperfish.journal.framework.Journal;

/**
 * an EntryDatabase that uses Hibernate ORM for database
 * 
 * @author sdai
 *
 */
public abstract class HibernateEntryDatabase implements EntryDatabase {

	/**
	 * get a session for persistent operations, should be implemented to support
	 * different type of hibernate dialect and options
	 * 
	 * @return a usable session
	 */
	protected abstract Session getSession();

	{
		java.util.logging.Logger.getLogger("org.hibernate").setLevel(Level.OFF);
		java.util.logging.Logger.getLogger("com.mchange").setLevel(Level.OFF);
	}

	@Override
	public Journal addEntry(Journal j) {
		this.getSession().getTransaction().begin();
		this.getSession().persist(j);
		this.getSession().getTransaction().commit();
		this.getSession().flush();
		return j;
	}

	@Override
	public Journal removeEntry(Long id) {
		this.getSession().getTransaction().begin();
		Journal deleted = getEntry(id);
		this.getSession().delete(getEntry(id));
		this.getSession().getTransaction().commit();
		return deleted;
	}

	@Override
	public Journal getEntry(Long id) {
		Journal result = this.getSession().get(Journal.class, id);
		return result;
	}

	@Override
	public Journal updateEntry(Long id, Journal j) {
		this.getSession().getTransaction().begin();
		j.setId(id);
		this.getSession().merge(j);
		this.getSession().getTransaction().commit();
		return j;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Journal> getAll() {
		List<Journal> result = this.getSession().createQuery("FROM Journal J ORDER BY J.date DESC").list();
		return result;
	}

	@Override
	public void clear() {
		this.getSession().getTransaction().begin();
		this.getSession().createQuery("DELETE FROM Journal").executeUpdate();
		this.getSession().getTransaction().commit();
		return;
	}

}
