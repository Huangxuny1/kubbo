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
public class Request extends KubboMessage {

    private String className;

    private String methodName;

    private Object[] args;

    private Class<?>[] argTypes;

    private byte type = Constants.TYPE_REQUEST;

}
