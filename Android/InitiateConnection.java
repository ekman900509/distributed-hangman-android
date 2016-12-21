package kth.hangman_3;


public class InitiateConnection extends Thread {

    String ip;
    int port;
    public InitiateConnection(String ip, int port){
        this.ip = ip;
        this.port = port;
    }

    @Override
    public void run(){

    }
}
