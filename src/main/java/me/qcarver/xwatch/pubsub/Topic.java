package me.qcarver.xwatch.pubsub;

/**
 *
 * @author qcarver
 */
 
import java.util.ArrayList;
import java.util.List;
import me.qcarver.xwatch.configuration.Configuration;
 
public class Topic implements Subject {
 
    private List<Observer> observers;
    private Configuration message;
    private boolean changed;
    private final Object MUTEX= new Object();
     
    public Topic(){
        this.observers=new ArrayList<>();
    }
    @Override
    public void register(Observer obj) {
        if(obj == null) throw new NullPointerException("Null Observer");
        synchronized (MUTEX) {
        if(!observers.contains(obj)) observers.add(obj);
        }
    }
 
    @Override
    public void unregister(Observer obj) {
        synchronized (MUTEX) {
        observers.remove(obj);
        }
    }
 
    @Override
    public void notifyObservers() {
        List<Observer> observersLocal = null;
        //synchronization is used to make sure any observer registered after message is received is not notified
        synchronized (MUTEX) {
            if (!changed)
                return;
            observersLocal = new ArrayList<>(this.observers);
            this.changed=false;
        }
        for (Observer obj : observersLocal) {
            obj.update();
        }
 
    }
 
    @Override
    public Object getUpdate(Observer obj) {
        return this.message;
    }
     
    //method to post message to the topic
    public void postMessage(Configuration msg){
        System.out.println("Message Posted to Topic:"+msg);
        this.message=msg;
        this.changed=true;
        notifyObservers();
    }
 
}
