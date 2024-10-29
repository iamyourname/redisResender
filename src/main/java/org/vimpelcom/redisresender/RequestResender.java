package org.vimpelcom.redisresender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

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
                    //Thread.sleep(5000);
                    Runnable task = () -> {
                        logger.info("Thread"+Thread.currentThread().toString()+ "for"+ host+":"+body);
                        sendRequest(host,body);

                         try{
                            int secToWait = 1000 * 1;
                            Thread.currentThread().sleep(secToWait);
                        }catch (InterruptedException e) {
                            e.printStackTrace();
                        }



                        //System.out.println("New thread");
                        /*
                        try {
                            int secToWait = 1000 * 10;
                            Thread.currentThread().sleep(secToWait);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        */
                    };
                    Thread thread = new Thread(task);
                    thread.start();
                    thread.join();

                }
            }
        }catch (JedisConnectionException e){
            logger.info(e.toString());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    public static void sendRequest(String host, String rbody){
        try {
            logger.info("Sending POST request....");
            URL url = new URL(host);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            String body = "host=http://localhost:8080/api&body="+rbody;
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(body);
            writer.flush();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            logger.info("RESPONSE " + response.toString());
            logger.info("---------------------");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
