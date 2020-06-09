package com.huangxunyi.remoting.message;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
@EqualsAndHashCode
public abstract class KubboMessage implements Serializable {
    private transient byte cmd;
    private transient int messageId;
    private transient byte type;
}
