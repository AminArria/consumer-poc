# Consumer PoC

This is a PoC for an unexpected behavior we are seeing in spring-kafka consumer

Consumer is configured with `asyncAcks` and `AckMode.MANUAL` and as such we expect that when a record from the batch of a poll is not acked the offset is committed until the most recent in the sequence and no further batches are polled. The first part of this assumption is true, we see the offset unchanged, but the second part is not true

Our assumption comes from this part in the docs ["The consumer will be paused (no new records delivered) until all the offsets for the previous poll have been committed."](https://docs.spring.io/spring-kafka/docs/current/reference/html/#ooo-commits), but is this assumption correct?

## The (unexpected) behavior
We are seeing that when a poll batch has been processed, but not been fully acknowledge and there is a rebalance event for some reason the instance re-consumes everything and more (beyond the 500 batch size)

This is our setup for the PoCtests:
- Single topic with a single partition and 1200 messages in it, 
- App does not ack records with offsets divisible by 10, offset strategy is set to earliest, and we start with 0 instances

### Test 1
1. We bring 1st consumer instance up and it consumes 500 messages (1 to 500), and then nothing, consumer seems paused (as expected), and the committed offset is 10
2. Next we bring a 2nd instance up, this causes a rebalance and the topic partition gets assigned to the 2nd instance, thus it polls and consumes 500 messages (from 10 to 510), but committed offset remains at 10
3. Finally the 1st instance goes down causing a rebalance and this causes 2nd instance to process all records (10 to 1200)

### Test 2
1. We bring 1st consumer instance up and it consumes 500 messages (1 to 500), and then nothing, consumer seems paused (as expected), and the committed offset is 10
2. Next we bring a 2nd instance up, this causes a rebalance and the topic partition gets assigned to the 1st instance and it processes all records (10 to 1200)
