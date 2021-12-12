package com.trab4;

public class HeartbeatArrayTask implements Runnable{
    private long heartBeatArray[];
    private Message msgHeartbeat = new Message();
    private String mode;
    private Integer timeLimit;
    private Integer serverCount;

    public HeartbeatArrayTask(Integer pServerCount, Message pMsgHeartbeat){
        this.serverCount = pServerCount;
        this.msgHeartbeat = pMsgHeartbeat;
        this.mode = "insert";
    }

    public HeartbeatArrayTask(Integer pServerCount, Integer pTimeLimit) {
        this.serverCount = pServerCount;
        this.timeLimit = pTimeLimit;
        this.mode = "timer";
    }

    @Override
    public void run() {
        
        if(this.mode.equals("insert")) {
            this.heartBeatArray[this.msgHeartbeat.getIdServ()] = System.currentTimeMillis();
        }
        else if (this.mode.equals("timer")) {
            long now = System.currentTimeMillis();

            for(int index = 0 ; index < this.serverCount ; index++)
            {
                if(heartBeatArray[index] == -1L) { continue; }
                
                if((now - heartBeatArray[index]) >= timeLimit) {
                    //SERVIDOR FALHOU
                    TimeoutHandler timeoutHandler = new TimeoutHandler(index, heartBeatArray[index]);
                    Thread timeoutHandlerThread = new Thread(timeoutHandler);
                    timeoutHandlerThread.start();
                    heartBeatArray[index] = -1L;
                }
                else {
                    heartBeatArray[index] = System.currentTimeMillis();
                }
            }
        }
    }
}
