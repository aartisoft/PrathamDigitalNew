package com.pratham.prathamdigital.socket.entity;

public class ChatEntity {
    private int id;
    private Message.CONTENT_TYPE type;
    private String content;
    private long time;
    private WFile file;
    private int percent;

    private boolean isSend;

    public Message.CONTENT_TYPE getType() {
        return type;
    }

    public void setType(Message.CONTENT_TYPE type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public WFile getFile() {
        return file;
    }

    public void setFile(WFile file) {
        this.file = file;
    }

    public boolean isSend() {
        return isSend;
    }

    public void setIsSend(boolean isSend) {
        this.isSend = isSend;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
