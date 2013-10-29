package ch.meemin.minimum;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.jpa.JpaHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This Bean is needed because create-or-extend-tables does not seem to work with glassfish when its defined in
 * persistece.xml This Bean forces the Database to be extended;
 */
@Startup
@Singleton
@LocalBean
public class EclipseLinkBean {
	Logger LOG = LoggerFactory.getLogger(EclipseLinkBean.class);

	@PersistenceContext
	private EntityManager em;

	@Resource(name = "minimum/basePath")
	private String basePath;

	@Schedule(minute = "1", hour = "1,13")
	public void backup() {
		LOG.info("Starting Backup");
		if (basePath == null) {
			LOG.warn("BasePath ist not set, will backup to application directory");
			basePath = ".";
		}
		File base = new File(basePath);
		File f = new File(base, "dbbackup3.zip");
		if (f.exists())
			f.delete();
		for (int i = 3; i >= 1; i--) {
			f = new File(base, "dbbackup" + i + ".zip");
			if (f.exists())
				f.renameTo(new File(base, "dbbackup" + (i + 1) + ".zip"));
		}
		try {
			this.em.createNativeQuery("BACKUP TO '" + basePath + "/dbbackup1.zip'").executeUpdate();
		} catch (Exception e) {
			LOG.warn("Backup failed", e);
		}

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@PostConstruct
	public void initDatabase() {
		backup();
		LOG.info("Forcing DDL Generation -- Database UPDATE");
		Map properties = new HashMap();
		properties.put(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.CREATE_OR_EXTEND);
		// create-or-extend only works on the database
		properties.put(PersistenceUnitProperties.DDL_GENERATION_MODE, PersistenceUnitProperties.DDL_DATABASE_GENERATION);
		// this causes DDL generation to occur on refreshMetadata rather than wait until an em is obtained
		properties.put(PersistenceUnitProperties.DEPLOY_ON_STARTUP, "true");
		JpaHelper.getEntityManagerFactory(em).refreshMetadata(properties);
	}
}
