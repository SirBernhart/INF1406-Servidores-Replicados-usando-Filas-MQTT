package com.trab4;

public class HeartbeatArrayThread extends Thread{
    private long heartBeatArray[];
    private Integer timeLimit;
    private Integer serverCount;

    public HeartbeatArrayThread(Integer pServerCount, Integer pTimeLimit) {
        this.serverCount = pServerCount;
        this.timeLimit = pTimeLimit;
        heartBeatArray = new long[pServerCount];
        for(int i = 0 ; i < pServerCount ; i++){
            heartBeatArray[i] = -1;
        }
    }

    @Override
    public void run() {
        new TimerThread(timeLimit, this).start();;
    }

    public synchronized void operate(Message pMsg)
    {
        if(pMsg.getTipoMsg().equals("insert"))
        {
            heartBeatArray[pMsg.getIdServ()] = System.currentTimeMillis();
            System.out.println("New Heartbeat: " + Message.serialize(pMsg) + "at " + heartBeatArray[pMsg.getIdServ()]+"\n");
        }
        else if (pMsg.getTipoMsg().equals("timer"))
        {
            System.out.println("!!! CHECKING FOR TIMED OUT HEARTBEATS !!!"+"\n");
            long now = System.currentTimeMillis();

            for(int index = 0 ; index < serverCount ; index++)
            {
                if(heartBeatArray[index] == -1L) { continue; }
                
                if((now - heartBeatArray[index]) >= timeLimit) {
                    //SERVIDOR FALHOU
                    System.out.println("Server "+index+" has timed out!!\n");

                    TimeoutHandlerThread timeoutHandlerThread = new TimeoutHandlerThread(index, heartBeatArray[index]);
                    timeoutHandlerThread.start();
                    heartBeatArray[index] = -1L;
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
                    Thread.sleep(timeLimit);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                heartbeatArrayThread.operate(timerMsg);
            }
        }
    }
}
