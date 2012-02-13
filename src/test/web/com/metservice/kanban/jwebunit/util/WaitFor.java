package com.metservice.kanban.jwebunit.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WaitFor {
    
    private final static Logger logger = LoggerFactory.getLogger(WaitFor.class);

    public static void me(UntilTrue untilTrue, int orMaxMills, int millsBetweenChecks) {
        
        // we time out at maxTime
        long maxTime = System.currentTimeMillis() + orMaxMills;
        
        // while time hasn't exceeded maxTime
        while (maxTime > System.currentTimeMillis()) {
            
            // check condition
            try {
            if (untilTrue.condition())
                return;
            } catch (Exception ex) {
            } catch (Error ex) {
            }
            
            // wait for next check
            try {
                logger.info("waiting");
                Thread.sleep(millsBetweenChecks);
            } catch (Exception e) {
            }
        }
        
        logger.error("WaitFor timed out");
    }

}
