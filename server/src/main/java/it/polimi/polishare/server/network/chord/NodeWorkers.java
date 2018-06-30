package it.polimi.polishare.server.network.chord;

import it.polimi.polishare.common.chord.Node;

public class NodeWorkers {
    private static final int PERIODIC_STABILIZATION_DELAY = 1000;
    private static final int PERIODIC_PREDECESSOR_CHECK_DELAY = 1000;
    private static final int PERIODIC_FINGERS_FIX_DELAY = 2000;
    private static final int PERIODIC_REPLICATION_DELAY = 2000;

    private Node node;
    private Thread t1, t2, t3, t4;
    private volatile boolean running = true;

    public NodeWorkers(Node Node){
        this.node = Node;
    }

    public void start(){
        running = true;
        t1 = new Thread(this::stabilizer);
        t2 = new Thread(this::predChecker);
        t3 = new Thread(this::fingersFixer);
        t4 = new Thread(this::replicator);
        t1.start();
        t2.start();
        t3.start();
        t4.start();
    }

    public void stop(){
        this.running = false;
    }

    public void stabilizer(){
        while(running) {
            try{
                Thread.sleep(PERIODIC_STABILIZATION_DELAY);
                node.stabilize();
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }
    }

    public void predChecker(){
        while(running) {
            try{
                Thread.sleep(PERIODIC_PREDECESSOR_CHECK_DELAY);
                node.checkPred();
            } catch (Exception e){
                //e.printStackTrace();
            }
        }
    }

    public void fingersFixer(){
        while(running) {
            try{
                Thread.sleep(PERIODIC_FINGERS_FIX_DELAY);
                node.fixFingers();
            } catch (Exception e){
                //e.printStackTrace();
            }
        }
    }

    public void replicator() {
        while(running) {
            try {
                Thread.sleep(PERIODIC_REPLICATION_DELAY);
                node.replicate();
            } catch (Exception e){
                //e.printStackTrace();
            }
        }
    }
}
