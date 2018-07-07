package it.polimi.polishare.server.network.DHT.chord;

import it.polimi.polishare.common.DHT.chord.Node;

/**
 * Represents a {@link it.polimi.polishare.common.DHT.chord.Node Node}'s background tasks.
 * This tasks call periodically the methods {@link Node#stabilize() stabilize}, {@link Node#checkPred()} () checkPred},
 * {@link Node#fixFingers() fixFingers} and {@link Node#replicate() replicate} to maintain the correct state of the node.
 */
public class NodeWorkers {
    private static final int PERIODIC_STABILIZATION_DELAY = 1000;
    private static final int PERIODIC_PREDECESSOR_CHECK_DELAY = 1000;
    private static final int PERIODIC_FINGERS_FIX_DELAY = 2000;
    private static final int PERIODIC_REPLICATION_DELAY = 2000;

    private Node node;
    private Thread t1, t2, t3, t4;
    private volatile boolean running = true;

    /**
     * Create the tasks for the given Node.
     *
     * @param Node
     */
    public NodeWorkers(Node Node){
        this.node = Node;
    }

    /**
     * Starts the background tasks.
     */
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

    /**
     * Stops the background tasks.
     */
    public void stop(){
        this.running = false;
    }

    private void stabilizer(){
        while(running) {
            try{
                Thread.sleep(PERIODIC_STABILIZATION_DELAY);
                node.stabilize();
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }
    }

    private void predChecker(){
        while(running) {
            try{
                Thread.sleep(PERIODIC_PREDECESSOR_CHECK_DELAY);
                node.checkPred();
            } catch (Exception e){
                //e.printStackTrace();
            }
        }
    }

    private void fingersFixer(){
        while(running) {
            try{
                Thread.sleep(PERIODIC_FINGERS_FIX_DELAY);
                node.fixFingers();
            } catch (Exception e){
                //e.printStackTrace();
            }
        }
    }

    private void replicator() {
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
