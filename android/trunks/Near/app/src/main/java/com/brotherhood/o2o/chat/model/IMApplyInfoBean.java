package com.brotherhood.o2o.chat.model;

/**
 * Created by Administrator on 2015/12/24 0024.
 */
public class IMApplyInfoBean {

    public long taUid;
    public long sendId;

    public boolean isAck;
    public String msgContents;
    public long time;

    public boolean hasRead;

    public static IMApplyInfoBean getBean(IMChatBean bean) {
        IMApplyInfoBean b = new IMApplyInfoBean();
        b.taUid = bean.receiverId;
        b.sendId = bean.sender;
        b.time = bean.time;
        b.hasRead = false;
        b.isAck = false;
        b.msgContents = bean.content;
        return b;
    }


}
