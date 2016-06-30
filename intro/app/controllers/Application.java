package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import net.spy.memcached.ConnectionFactory;
import net.spy.memcached.ConnectionFactoryBuilder;
import net.spy.memcached.DefaultHashAlgorithm;
import net.spy.memcached.MemcachedClient;
import play.*;
import play.mvc.*;

import utils.LockManager;
import views.html.*;

import java.io.IOException;
import java.net.InetSocketAddress;

import static net.spy.memcached.AddrUtil.getAddresses;

public class Application extends Controller {

    private static MemcachedClient memcachedClient = createClient();

    private static MemcachedClient createClient() {
        try {

            ConnectionFactory connectionFactory = new ConnectionFactoryBuilder().setHashAlg(DefaultHashAlgorithm.KETAMA_HASH)
                    .setProtocol(ConnectionFactoryBuilder.Protocol.BINARY).setOpQueueMaxBlockTime(10000L).build();
            return new MemcachedClient(connectionFactory, getAddresses("localhost:11211"));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Result simpleGet(String key) {
        Object value = memcachedClient.get(key);
        if(value==null){
            return notFound();
        }
        return ok(value.toString());
    }

    public static Result create() {
        System.out.println(request().body().asJson());
        JsonNode json = request().body().asJson();
        String key = json.get("key").asText();
        String value = json.get("value").asText();

        try {
            memcachedClient.add(key, 10000, value);
            return created();
        } catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }

    public static Result delete(String key) {
        try{
            memcachedClient.delete(key);
            return ok();
        }catch (Exception e){
            return internalServerError(e.getMessage());
        }
    }

    public static Result trylock(String key) {
        try {
            Boolean result = LockManager.acquireLock(key);
            if (result)
                return ok("Lock acquired.");
            else
                return status(409,"Lock is already taken");
        } catch (Exception e){
            return internalServerError("Internal error: "+e.toString());
        }
    }

    public static Result tryAndRelease(String key) {
        try {
            LockManager.releaseLock(key);
            return ok(index.render("Lock acquired."));
        } catch (Exception e){
            return internalServerError("Internal error: "+e.toString());
        }
    }

}
