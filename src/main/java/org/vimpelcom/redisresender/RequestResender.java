package org.vimpelcom.redisresender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class RequestResender {
    final static Logger logger = LoggerFactory.getLogger(RequestResender.class);

    public static void reqResender(){

        try{
            JedisPool pool = new JedisPool("localhost", 6379);
            Jedis jedis = pool.getResource();

            boolean empty = false;
            int i=1;

            while (!empty) {
                String host = jedis.hget("request:"+i,"host");
                String body = jedis.hget("request:"+i,"body");

                if(body==null){
                    empty=true;
                    logger.info("Successfully resend "+(i-1)+" requests");
                    return;
                }else{
                    i++;
                    logger.info(host+":"+body);
                }
            }




            //logger.info(""+jedis.hlen("request:19"));
        }catch (JedisConnectionException e){
            logger.info(e.toString());
        }

    }

}
