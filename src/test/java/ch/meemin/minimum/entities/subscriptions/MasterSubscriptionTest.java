package ch.meemin.minimum.entities.subscriptions;

import junit.framework.Assert;

import org.junit.Test;

import ch.meemin.minimum.entities.Customer;

public class MasterSubscriptionTest {

	@Test
	public void testValid() {
		MasterSubscription ms = new MasterSubscription(new Customer());
		Assert.assertTrue(ms.valid());
	}
}
