package org.vimpelcom.redisresender;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.vimpelcom.redisresender.RequestResender.reqResender;

@RestController
public class ResenderController {
    final static Logger logger = LoggerFactory.getLogger(ResenderController.class);


    @RequestMapping("/start_resender")
    public String startResender(
            @RequestParam(value="start", required=false, defaultValue = "0") String start) {
        if(start.equals("1")){
            logger.info("command has been received to start resender");
            reqResender();
            return "STARTED";
        }else{
            return "request fail";
        }

    }
}
