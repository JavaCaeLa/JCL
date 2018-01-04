/*
 * Copyright 2012, Google Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following disclaimer
 * in the documentation and/or other materials provided with the
 * distribution.
 *     * Neither the name of Google Inc. nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.jf.dexlib2.immutable.debug;

import org.jf.dexlib2.DebugItemType;
import org.jf.dexlib2.iface.debug.RestartLocal;




public class ImmutableRestartLocal extends ImmutableDebugItem implements RestartLocal {
    private static final long serialVersionUID = 8153197851684700371L;
    protected final int register;
     protected final String name;
     protected final String type;
     protected final String signature;

    public ImmutableRestartLocal(int codeAddress,
                                 int register) {
        super(codeAddress);
        this.register = register;
        this.name = null;
        this.type = null;
        this.signature = null;
    }

    public ImmutableRestartLocal(int codeAddress,
                                 int register,
                                  String name,
                                  String type,
                                  String signature) {
        super(codeAddress);
        this.register = register;
        this.name = name;
        this.type = type;
        this.signature = signature;
    }


    public static ImmutableRestartLocal of( RestartLocal restartLocal) {
        if (restartLocal instanceof  ImmutableRestartLocal) {
            return (ImmutableRestartLocal)restartLocal;
        }
        return new ImmutableRestartLocal(
                restartLocal.getCodeAddress(),
                restartLocal.getRegister(),
                restartLocal.getType(),
                restartLocal.getName(),
                restartLocal.getSignature());
    }

    @Override public int getRegister() { return register; }
     @Override public String getName() { return name; }
     @Override public String getType() { return type; }
     @Override public String getSignature() { return signature; }

    @Override public int getDebugItemType() { return DebugItemType.RESTART_LOCAL; }
}
