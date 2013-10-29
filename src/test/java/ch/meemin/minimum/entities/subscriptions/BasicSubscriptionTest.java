package ch.meemin.minimum.entities.subscriptions;

import junit.framework.Assert;

import org.junit.Test;

import ch.meemin.minimum.entities.Customer;

public class BasicSubscriptionTest {

	@Test
	public void testValid() {
		BasicSubscription bs = new BasicSubscription(new Customer());
		Assert.assertFalse(bs.valid());
	}
}
