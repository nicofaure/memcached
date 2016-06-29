package utils;

import net.spy.memcached.MemcachedClient;

import java.net.InetSocketAddress;

/**
 * Created by cgrappa on 6/29/16.
 */
public class LockManager {

    private static MemcachedClient client;
    private static final int MEMCACHED_TTL = 15; //15 seconds

    static {
        try {
            client = new MemcachedClient(new InetSocketAddress("localhost",11211));
        } catch (Exception e){
            throw  new RuntimeException("Cannot initialize MemcachedClient");
        }
    }


    public static Boolean acquireLock(String key){
        Boolean result = false;
        try {
            result = client.add(key, MEMCACHED_TTL, "lock").get();
        } catch (Exception e){
            throw new RuntimeException("Memcached Error");
        }
        return result;
    }

    public static void releaseLock(String key){
        try {
            client.delete(key);
        } catch (Exception e){
            throw new RuntimeException("Memcached Error on delete");
        }
    }
}
