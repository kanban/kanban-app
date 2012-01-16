package com.metservice.kanban.jwebunit.util;


public class WaitFor {
    
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
                System.out.println("waiting");
                Thread.sleep(millsBetweenChecks);
            } catch (Exception e) {
            }
        }
        
        System.err.println("WaitFor timed out");
    }

}
