package top.zsmile.mq;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SmqQueue {
    private List<SmqMessage> messages;
    private int wIndex;

    public SmqQueue() {
        messages = new ArrayList<>();
        wIndex = 0;
    }

    public boolean write(SmqMessage smqMessage) {
        boolean add = messages.add(smqMessage);
        if (add) {
            wIndex++;
        }
        return add;
    }

    public SmqMessage read(int readIndex) {
        if (readIndex == wIndex) {
            return null;
        }
        SmqMessage smqMessage = messages.get(readIndex);
        return smqMessage;
    }

}
