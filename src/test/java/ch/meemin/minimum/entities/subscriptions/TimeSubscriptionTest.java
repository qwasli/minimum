package ch.meemin.minimum.entities.subscriptions;

import java.util.Date;

import junit.framework.Assert;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.meemin.minimum.entities.Customer;
import ch.meemin.minimum.entities.Visit;
import ch.meemin.minimum.entities.settings.Duration;
import ch.meemin.minimum.entities.settings.Duration.TimeSpan;
import ch.meemin.minimum.entities.settings.TimeSubscriptions;

public class TimeSubscriptionTest {
	private static TimeSubscriptions tss;

	@BeforeClass
	public static void prepare() {
		tss = new TimeSubscriptions();
		tss.setName("test-tss");
		tss.setDuration(new Duration(1, TimeSpan.YEARS));
	}

	@Test
	public void construct() {
		TimeSubscriptions ytss = new TimeSubscriptions();
		ytss.setName("test-tss");
		ytss.setDuration(new Duration(1, TimeSpan.YEARS));
		TimeSubscriptions mtss = new TimeSubscriptions();
		mtss.setName("test-tss");
		mtss.setDuration(new Duration(6, TimeSpan.MONTHS));

		TimeSubscription yts = new TimeSubscription(new Customer(), ytss);
		Date yfn = DateUtils.addYears(new Date(), 1);
		Assert.assertTrue(DateUtils.isSameDay(yfn, yts.getExpiry()));

		TimeSubscription mts = new TimeSubscription(new Customer(), mtss);
		Date mfn = DateUtils.addMonths(new Date(), 6);
		Assert.assertTrue(DateUtils.isSameDay(mfn, mts.getExpiry()));
	}

	@Test
	public void testValid() {
		TimeSubscription ts = new TimeSubscription(new Customer(), tss);
		Assert.assertTrue(ts.valid());
	}

	@Test
	public void testReplace() {
		TimeSubscription ts = new TimeSubscription(new Customer(), tss);
		ts.replace();
		Assert.assertFalse(ts.valid());
		TimeSubscription nts = (TimeSubscription) ts.getReplacedBy();
		Assert.assertNotNull(nts);
		Assert.assertTrue(nts.valid());
		Assert.assertEquals(ts.getExpiry(), nts.getExpiry());
	}

	@Test
	public void testSuspend() {
		TimeSubscription ts = new TimeSubscription(new Customer(), tss);
		ts.suspend();
		Assert.assertFalse(ts.valid());
		ts.reactivate();
		Assert.assertTrue(ts.valid());
	}

	@Test
	public void testSuspendDuration() {
		TimeSubscription ts = new TimeSubscription(new Customer(), tss);
		Date ex1 = ts.getExpiry();
		ts.suspend();
		ts.reactivate();
		Date ex2 = ts.getExpiry();
		Assert.assertTrue(DateUtils.isSameDay(ex1, ex2));
	}

	@Test
	public void checkIn() {
		TimeSubscription ts = new TimeSubscription(new Customer(), tss);
		Visit v = ts.checkIn();
		Assert.assertNotNull(v);
		Assert.assertEquals(ts, v.getSubscription());
		Assert.assertEquals(v, ts.getLastVisit());
	}

}
