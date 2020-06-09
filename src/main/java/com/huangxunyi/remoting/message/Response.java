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
public class Response extends KubboMessage {

    private byte type = Constants.TYPE_RESPONSE;
    /**
     * 服务端返回状态码
     */
    private int code;
    /**
     * 错误信息
     */
    private String errorMsg;
    /**
     * 服务端返回信息
     */
    private Object data;

}
