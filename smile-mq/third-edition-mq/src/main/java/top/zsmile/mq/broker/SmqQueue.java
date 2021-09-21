package top.zsmile.mq.broker;

import top.zsmile.mq.SmqMessage;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SmqQueue {
    private List<SmqMessage> messages;

    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private int wIndex;
    private int rIndex;

    public SmqQueue() {
        messages = new ArrayList<>();
        wIndex = 0;
        rIndex = 0;
    }

    public boolean write(SmqMessage smqMessage) {
        try {
            lock.writeLock().lock();
            boolean add = messages.add(smqMessage);
            if (add) {
                wIndex++;
            }
            return add;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public SmqMessage read(int readIndex) {
        try {
            if (readIndex == wIndex) {
                return null;
            }
            lock.readLock().tryLock(2000, TimeUnit.MILLISECONDS);
            SmqMessage smqMessage = messages.get(readIndex);
            lock.readLock().unlock();
            return smqMessage;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public SmqMessage readAndOffset() {

        if (this.rIndex == wIndex) {
            return null;
        }
        lock.readLock().lock();
        SmqMessage smqMessage = messages.get(rIndex);
        this.rIndex++;
        lock.readLock().unlock();
        return smqMessage;

    }

}
