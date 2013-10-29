package ch.meemin.minimum.entities.subscriptions;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import ch.meemin.minimum.entities.Customer;
import ch.meemin.minimum.entities.Visit;
import ch.meemin.minimum.entities.settings.PrepaidSubscriptions;

public class PrepaisSubscriptionTest {
	private static PrepaidSubscriptions pss;

	@BeforeClass
	public static void prepare() {
		pss = new PrepaidSubscriptions();
		pss.setName("test-pss");
		pss.setCredit(11);
	}

	@Test
	public void construct() {
		PrepaidSubscription ps = new PrepaidSubscription(new Customer(), pss);
		Assert.assertEquals(pss.getCredit().intValue(), ps.getCredit());
		Assert.assertEquals(11, ps.getCredit());
	}

	@Test
	public void testValid() {
		PrepaidSubscription ps = new PrepaidSubscription(new Customer(), pss);
		Assert.assertTrue(ps.valid());
	}

	@Test
	public void testReplace() {
		PrepaidSubscription ps = new PrepaidSubscription(new Customer(), pss);
		ps.replace();
		Assert.assertFalse(ps.valid());
		PrepaidSubscription nps = (PrepaidSubscription) ps.getReplacedBy();
		Assert.assertNotNull(nps);
		Assert.assertTrue(nps.valid());
		Assert.assertEquals(ps.getCredit(), nps.getCredit());
	}

	@Test
	public void testSuspend() {
		PrepaidSubscription ps = new PrepaidSubscription(new Customer(), pss);
		ps.suspend();
		Assert.assertTrue(ps.valid());// Prepaid can not be suspended
		ps.reactivate();
		Assert.assertTrue(ps.valid());
	}

	@Test
	public void checkIn() {
		PrepaidSubscription ps = new PrepaidSubscription(new Customer(), pss);
		Visit v = ps.checkIn();
		Assert.assertNotNull(v);
		Assert.assertEquals(ps, v.getSubscription());
		Assert.assertEquals(v, ps.getLastVisit());
		for (int i = 10; i > 0; i--) {
			Assert.assertEquals(i, ps.getCredit());
			ps.checkIn();
		}
		Assert.assertFalse(ps.valid());

	}

}
