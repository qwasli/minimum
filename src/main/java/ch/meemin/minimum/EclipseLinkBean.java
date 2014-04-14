package ch.meemin.minimum;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang3.StringUtils;
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

	private Properties props;
	private String backupPath;
	private Integer numberOfBackups;

	private void checkProps() {
		try {
			props = InitialContext.doLookup("minimum/Properties");
		} catch (Exception e1) {
			LOG.warn("Problem geting properties", e1);
		}

		if (props != null) {
			backupPath = props.getProperty("backupPath");
			String nob = props.getProperty("numberOfBackups");
			if (nob != null)
				try {
					numberOfBackups = Integer.parseInt(nob);
				} catch (NumberFormatException e) {
					numberOfBackups = null;
				}
			else
				numberOfBackups = null;
		} else
			LOG.info("Props are not set");

		if (StringUtils.isBlank(backupPath))
			backupPath = ".";
		try {
			File bd = new File(backupPath);
			if (!bd.exists())
				bd.mkdirs();
			File sample = new File(bd, "empty");
			/*
			 * Create and delete a dummy file in order to check file permissions. Maybe there is a safer way for this check.
			 */
			sample.createNewFile();
			sample.delete();
		} catch (IOException e) {
			backupPath = ".";
		}

		if (numberOfBackups == null)
			numberOfBackups = 60;
	}

	@Schedule(minute = "1", hour = "1,13")
	public void backup() {
		checkProps();
		LOG.info("Starting Backup to: " + backupPath);
		File base = new File(backupPath);
		File f = new File(base, "dbbackup" + numberOfBackups + ".zip");
		if (f.exists())
			f.delete();
		for (int i = numberOfBackups; i >= 1; i--) {
			f = new File(base, "dbbackup" + i + ".zip");
			if (f.exists())
				f.renameTo(new File(base, "dbbackup" + (i + 1) + ".zip"));
		}
		try {
			String path = base.getAbsolutePath() + "/dbbackup1.zip";
			backup(path);
		} catch (Exception e) {
			LOG.warn("Backup failed", e);
		}

	}

	public void backup(String path) {
		this.em.createNativeQuery("BACKUP TO " + "'" + path + "'").executeUpdate();
	}

	public FileInputStream createBackupStream() {
		File base = new File(backupPath);
		File f = new File(base, "dbbackup-tmp.zip");
		if (f.exists())
			f.delete();
		backup(f.getAbsolutePath());
		try {
			return new FileInputStream(f);
		} catch (FileNotFoundException e) {
			LOG.warn("Problem with tmp backup", e);
			return null;
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
