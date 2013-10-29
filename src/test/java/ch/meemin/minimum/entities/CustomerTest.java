package ch.meemin.minimum.entities;

import junit.framework.Assert;

import org.junit.Test;

import ch.meemin.minimum.entities.subscriptions.BasicSubscription;

public class CustomerTest {

	@Test
	public void testConstruct() {
		Customer c = new Customer();
		Assert.assertNotNull(c.getCurrentSubscription());
		Assert.assertEquals(BasicSubscription.class, c.getCurrentSubscription().getClass());
	}

	@Test
	public void testCheckSubscriptions() {
		Customer c = new Customer();
		Assert.assertFalse(c.getSubscriptions().contains(c.getCurrentSubscription()));
		c.checkSubscriptions();
		Assert.assertTrue(c.getSubscriptions().contains(c.getCurrentSubscription()));

	}

	@Test
	public void testCheckIn() {
		Customer c = new Customer();
		Assert.assertNull(c.getLastVisit());
		c.checkIn(true);
		Assert.assertNotNull(c.getLastVisit());
	}
}
