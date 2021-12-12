package com.trab4;

public class HeartbeatArrayThread extends Thread{
    private long heartBeatArray[];
    private Message msg = new Message();
    private Integer timeLimit;
    private Integer serverCount;

    public HeartbeatArrayThread(Integer pServerCount, Integer pTimeLimit) {
        this.serverCount = pServerCount;
        this.timeLimit = pTimeLimit;
    }

    @Override
    public void run() {}

    public synchronized void operate(Message pMsg)
    {
        if(pMsg.getTipoMsg().equals("insert"))
        {
            heartBeatArray[msg.getIdServ()] = System.currentTimeMillis();
        }
        else if (pMsg.getTipoMsg().equals("timer"))
        {
            long now = System.currentTimeMillis();

            for(int index = 0 ; index < serverCount ; index++)
            {
                if(heartBeatArray[index] == -1L) { continue; }
                
                if((now - heartBeatArray[index]) >= timeLimit) {
                    //SERVIDOR FALHOU
                    TimeoutHandlerThread timeoutHandlerThread = new TimeoutHandlerThread(index, heartBeatArray[index]);
                    timeoutHandlerThread.start();
                    heartBeatArray[index] = -1L;
                }
                else {
                    heartBeatArray[index] = System.currentTimeMillis();
                }
            }
        }
    }

    class TimerThread extends Thread{

        private HeartbeatArrayThread heartbeatArrayThread;
        private Integer timeLimit;
        private Message timerMsg;
    
        public TimerThread(Integer pTimeLimit, HeartbeatArrayThread pHeartbeatArrayThread){
            this.timeLimit = pTimeLimit;
            this.heartbeatArrayThread = pHeartbeatArrayThread;
            timerMsg = new Message();
            timerMsg.setTipoMsg("timer");
        }
    
        @Override
        public void run() {
            while(true){
                try {
                    Thread.sleep(timeLimit * 60 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                heartbeatArrayThread.operate(timerMsg);
            }
        }
    }
}
