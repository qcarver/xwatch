package me.qcarver.xwatch.pubsub;

/**
 * original source: http://www.journaldev.com/1739/observer-design-pattern-in-java-example-tutorial
 * @author qcarver
 */
public interface Subject {
 
    //methods to register and unregister observers
    public void register(Observer obj);
    public void unregister(Observer obj);
     
    //method to notify observers of change
    public void notifyObservers();
     
    //method to get updates from subject
    public Object getUpdate(Observer obj);
     
}
