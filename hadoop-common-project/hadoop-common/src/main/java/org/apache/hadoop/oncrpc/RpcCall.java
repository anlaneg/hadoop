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

import org.apache.hadoop.oncrpc.security.Credentials;
import org.apache.hadoop.oncrpc.security.Verifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents an RPC message of type RPC call as defined in RFC 1831
 * 表示一个call类型的rpc消息
 */
public class RpcCall extends RpcMessage {
  public static final int RPC_VERSION = 2;
  private static final Logger LOG = LoggerFactory.getLogger(RpcCall.class);

  public static RpcCall read(XDR xdr) {
	  /*通过XDR来构造rpc call类型消息*/
    return new RpcCall(xdr.readInt(), RpcMessage.Type.fromValue(xdr.readInt()),
        xdr.readInt(), xdr.readInt(), xdr.readInt(), xdr.readInt(),
        Credentials.readFlavorAndCredentials(xdr),
        Verifier.readFlavorAndVerifier(xdr));
  }

  public static RpcCall getInstance(int xid, int program, int version,
      int procedure, Credentials cred, Verifier verifier) {
    return new RpcCall(xid, RpcMessage.Type.RPC_CALL, 2, program, version,
        procedure, cred, verifier);
  }

  private final int rpcVersion;
  private final int program;
  private final int version;
  private final int procedure;/*消息要调用的过程编号*/
  private final Credentials credentials;
  private final Verifier verifier;

  protected RpcCall(int xid, RpcMessage.Type messageType, int rpcVersion,
      int program, int version, int procedure, Credentials credential,
      Verifier verifier) {
	  /*初始化rpc消息*/
    super(xid, messageType);
    this.rpcVersion = rpcVersion;
    this.program = program;
    this.version = version;
    this.procedure = procedure;
    this.credentials = credential;
    this.verifier = verifier;
    if (LOG.isTraceEnabled()) {
      LOG.trace(this.toString());
    }
    validate();
  }

  private void validateRpcVersion() {
	  /*如果rpc version不一致，则异常*/
    if (rpcVersion != RPC_VERSION) {
      throw new IllegalArgumentException("RPC version is expected to be "
          + RPC_VERSION + " but got " + rpcVersion);
    }
  }

  public void validate() {
	  /*检查自身是否为call类型*/
    validateMessageType(RpcMessage.Type.RPC_CALL);
    /*校验rpc版本*/
    validateRpcVersion();
    // Validate other members
    // Throw exception if validation fails
  }


  public int getRpcVersion() {
	  /*返回rpc version*/
    return rpcVersion;
  }

  public int getProgram() {
    return program;
  }

  public int getVersion() {
    return version;
  }

  public int getProcedure() {
    return procedure;
  }

  public Credentials getCredential() {
    return credentials;
  }

  public Verifier getVerifier() {
    return verifier;
  }

  @Override
  public XDR write(XDR xdr) {
	  /*写xid*/
    xdr.writeInt(xid);
    /*写message type*/
    xdr.writeInt(RpcMessage.Type.RPC_CALL.getValue());
    /*使用2做为rpc version*/
    xdr.writeInt(2);
    xdr.writeInt(program);
    xdr.writeInt(version);
    /*写要调用的过程*/
    xdr.writeInt(procedure);
    /*写凭证类型及凭证值*/
    Credentials.writeFlavorAndCredentials(credentials, xdr);
    /*写verifier类型及verifier值*/
    Verifier.writeFlavorAndVerifier(verifier, xdr);
    return xdr;
  }

  @Override
  public String toString() {
	  /*将此message转为字符串*/
    return String.format("Xid:%d, messageType:%s, rpcVersion:%d, program:%d,"
        + " version:%d, procedure:%d, credential:%s, verifier:%s", xid,
        messageType, rpcVersion, program, version, procedure,
        credentials.toString(), verifier.toString());
  }
}
