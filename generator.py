import random
from tqdm import tqdm
from confluent_kafka import Producer

kafka_brokers = "localhost:19092"

def produce_random():
    producer_conf = {'bootstrap.servers': kafka_brokers}
    producer = Producer(producer_conf)
    for i in tqdm(range(1200)):
        key = bytes([random.randint(0, 255)])
        value = bytes([random.randint(0, 255)])
        producer.produce(topic="destination",
                        partition=0,
                        key=key,
                        value=value)
        if i % 1000 == 0:
            producer.flush()
    producer.flush()

produce_random()
