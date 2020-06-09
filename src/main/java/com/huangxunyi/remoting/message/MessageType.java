package com.huangxunyi.remoting.message;

import com.huangxunyi.remoting.codec.Constants;
import lombok.Getter;

import java.util.Arrays;

public enum MessageType {
    REQUEST(Constants.TYPE_REQUEST) {
        @Override
        public Class<? extends KubboMessage> getClazz() {
            return Request.class;
        }
    },

    RESPONSE(Constants.TYPE_RESPONSE) {
        @Override
        public Class<? extends KubboMessage> getClazz() {
            return Response.class;
        }
    },

    HEARTBEAT(Constants.TYPE_HEARTBEAT) {
        @Override
        public Class<? extends KubboMessage> getClazz() {
            return Heartbeat.class;
        }
    };


    @Getter
    private int type;

    MessageType(int i) {
        this.type = i;
    }

    public abstract Class<? extends KubboMessage> getClazz();

}
