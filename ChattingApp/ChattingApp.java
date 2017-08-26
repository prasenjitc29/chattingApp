import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ChattingApp {

	public static  String CHANNEL_NAME = "commonChannel";


    public static void main(String[] args) throws Exception {

	final JedisPoolConfig poolConfig = new JedisPoolConfig();
    	final JedisPool jedisPool = new JedisPool(poolConfig, "localhost", 6379, 0);
	final Jedis subscriberJedis = jedisPool.getResource();
	final Subscriber subscriber = new Subscriber();
	
	

    new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                subscriberJedis.subscribe(subscriber, CHANNEL_NAME);
            } catch (Exception e) {
            }
        }
    }).start();

    final Jedis publisherJedis = jedisPool.getResource();

    new Publisher(publisherJedis, CHANNEL_NAME).start();

    subscriber.unsubscribe();
    jedisPool.returnResource(subscriberJedis);
    jedisPool.returnResource(publisherJedis);
} }



class Publisher {


    	private final Jedis publisherJedis;

	private final String channel;

	public Publisher(Jedis publisherJedis, String channel) {
    	this.publisherJedis = publisherJedis;
        this.channel = channel;
	}

    public void start() {
	System.out.println("typing....(quit for terminate)");
    	try {
        	BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        	while (true) {
            	String line = reader.readLine();

                if (!"quit".equals(line)) {
                	publisherJedis.publish(channel, line);
                } else {
	                break;
    	        }
        	}

        } catch (IOException e) {
		System.out.println("Exception :"+e);
    	}
    }
}


class Subscriber extends JedisPubSub {


	@Override
    public void onMessage(String channel, String message) {
	           System.out.println(message+" message received "+ channel );
    }

	@Override
    public void onPMessage(String pattern, String channel, String message) {

	}

	@Override
    public void onSubscribe(String channel, int subscribedChannels) {

	}

	@Override
    public void onUnsubscribe(String channel, int subscribedChannels) {

	}

	@Override
    public void onPUnsubscribe(String pattern, int subscribedChannels) {

	}

    @Override
	public void onPSubscribe(String pattern, int subscribedChannels) {

	}
}