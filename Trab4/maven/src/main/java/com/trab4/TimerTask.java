package com.trab4;

public class TimerTask implements Runnable{

    private Integer timeLimit = 5; // Minutos
    private Integer serverCount;

    public TimerTask(Integer pServerCount){
        this.serverCount = pServerCount;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(timeLimit * 60 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        HeartbeatArrayTask heartbeatArrayTask = new HeartbeatArrayTask(serverCount, timeLimit);
        Thread threadHeartbeatArray = new Thread(heartbeatArrayTask);
        threadHeartbeatArray.start();
    }
    
}
