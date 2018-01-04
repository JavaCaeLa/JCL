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

package org.jf.dexlib2.immutable.instruction;

import com.google.common.collect.ImmutableList;
import org.jf.dexlib2.iface.instruction.SwitchElement;
import org.jf.util.ImmutableConverter;


import java.io.Serializable;
import java.util.List;

public class ImmutableSwitchElement implements SwitchElement, Serializable {
    private static final long serialVersionUID = 4210244104236207114L;
    protected final int key;
    protected final int offset;

    public ImmutableSwitchElement(int key,
                                  int offset) {
        this.key = key;
        this.offset = offset;
    }


    public static ImmutableSwitchElement of(SwitchElement switchElement) {
        if (switchElement instanceof  ImmutableSwitchElement) {
            return (ImmutableSwitchElement)switchElement;
        }
        return new ImmutableSwitchElement(
                switchElement.getKey(),
                switchElement.getOffset());
    }

    @Override public int getKey() { return key; }
    @Override public int getOffset() { return offset; }


    public static ImmutableList<ImmutableSwitchElement> immutableListOf( List<? extends SwitchElement> list) {
        return CONVERTER.toList(list);
    }

    private static final ImmutableConverter<ImmutableSwitchElement, SwitchElement> CONVERTER =
            new ImmutableConverter<ImmutableSwitchElement, SwitchElement>() {
                @Override
                protected boolean isImmutable( SwitchElement item) {
                    return item instanceof ImmutableSwitchElement;
                }


                @Override
                protected ImmutableSwitchElement makeImmutable( SwitchElement item) {
                    return ImmutableSwitchElement.of(item);
                }
            };
}
