/*
Name: Joshua Khooba
Course: CNT 4714 Spring 2026
Assignment title: Project 2 – Multi-threaded programming in Java
Date: February 15, 2026
Class: Switch
Description: Represents a lockable switch in the train yard. Each switch can be locked
by only one train at a time to ensure safe passage through the yard.
*/

import java.util.concurrent.locks.ReentrantLock;

public class Switch {
    private int switchNumber;
    private ReentrantLock lock;
    
    public Switch(int switchNumber) {
        this.switchNumber = switchNumber;
        this.lock = new ReentrantLock();
    }
    
    public int getSwitchNumber() {
        return switchNumber;
    }
    
    public ReentrantLock getLock() {
        return lock;
    }
    
    public boolean tryLock() {
        return lock.tryLock();
    }
    
    public void unlock() {
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }
}
