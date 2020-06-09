package com.huangxunyi.remoting.message;

import com.huangxunyi.remoting.codec.Constants;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
@NoArgsConstructor
public class Heartbeat extends KubboMessage {
    private byte type = Constants.TYPE_HEARTBEAT;
    private long timestamp;
    private long count;
}
