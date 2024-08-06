package com.ems.application.exception;

import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class MsgInfo implements Serializable {

    private String msgId;

    private List<String> msgArgs = new ArrayList<>();

    /**
     * @param msgId   String
     * @param msgArgs List-String
     * @return MsgInfo
     */
    public static MsgInfo by(String msgId, List<String> msgArgs) {
        MsgInfo msgInfo = new MsgInfo();
        msgInfo.setMsgId(msgId);
        msgInfo.setMsgArgs(msgArgs);
        return msgInfo;
    }

    /**
     * @param msgId String
     * @return MsgInfo
     */
    public static MsgInfo by(String msgId) {
        MsgInfo msgInfo = new MsgInfo();
        msgInfo.setMsgId(msgId);
        return msgInfo;
    }
}
