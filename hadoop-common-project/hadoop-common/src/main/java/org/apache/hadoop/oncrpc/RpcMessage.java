/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.oncrpc;

/**
 * Represent an RPC message as defined in RFC 1831.
 */
public abstract class RpcMessage {
  /** Message type */
	/*定义sunrpc消息类型*/
  public enum Type {
    // the order of the values below are significant.
    RPC_CALL,
    RPC_REPLY;

    public int getValue() {
    	/*返回当前type的源始值*/
      return ordinal();
    }

    public static Type fromValue(int value) {
    	/*由value转为type*/
      if (value < 0 || value >= values().length) {
        return null;
      }
      /*返回此枚举对应的type*/
      return values()[value];
    }
  }

  protected final int xid;
  protected final Type messageType;

  RpcMessage(int xid, Type messageType) {
    if (messageType != Type.RPC_CALL && messageType != Type.RPC_REPLY) {
    	/*无效的rpc 消息类型*/
      throw new IllegalArgumentException("Invalid message type " + messageType);
    }
    
    /*设置xid,message type*/
    this.xid = xid;
    this.messageType = messageType;
  }

  public abstract XDR write(XDR xdr);

  public int getXid() {
    return xid;
  }

  public Type getMessageType() {
	  /*取消息类型*/
    return messageType;
  }

  protected void validateMessageType(Type expected) {
    if (expected != messageType) {
    	/*检查expected对应的type是否与此message类型一致，如不一致扔异常*/
      throw new IllegalArgumentException("Message type is expected to be "
          + expected + " but got " + messageType);
    }
  }
}
