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

package org.jf.dexlib2.immutable;

import com.google.common.collect.*;
import org.jf.dexlib2.base.reference.BaseTypeReference;
import org.jf.dexlib2.iface.Annotation;
import org.jf.dexlib2.iface.ClassDef;
import org.jf.dexlib2.iface.Field;
import org.jf.dexlib2.iface.Method;
import org.jf.dexlib2.util.FieldUtil;
import org.jf.dexlib2.util.MethodUtil;
import org.jf.util.ImmutableConverter;
import org.jf.util.ImmutableUtils;


import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class ImmutableClassDef extends BaseTypeReference implements ClassDef, Serializable {
    private static final long serialVersionUID = 5554827583155529429L;
    protected final String type;
    protected final int accessFlags;
     protected final String superclass;
     protected final ImmutableList<String> interfaces;
     protected final String sourceFile;
     protected final ImmutableSet<? extends ImmutableAnnotation> annotations;
     protected final ImmutableSortedSet<? extends ImmutableField> staticFields;
     protected final ImmutableSortedSet<? extends ImmutableField> instanceFields;
     protected final ImmutableSortedSet<? extends ImmutableMethod> directMethods;
     protected final ImmutableSortedSet<? extends ImmutableMethod> virtualMethods;

    public ImmutableClassDef( String type,
                             int accessFlags,
                              String superclass,
                              Collection<String> interfaces,
                              String sourceFile,
                              Collection<? extends Annotation> annotations,
                              Iterable<? extends Field> fields,
                              Iterable<? extends Method> methods) {
        if (fields == null) {
            fields = ImmutableList.of();
        }
        if (methods == null) {
            methods = ImmutableList.of();
        }

        this.type = type;
        this.accessFlags = accessFlags;
        this.superclass = superclass;
        this.interfaces = interfaces==null ? ImmutableList.<String>of() : ImmutableList.copyOf(interfaces);
        this.sourceFile = sourceFile;
        this.annotations = ImmutableAnnotation.immutableSetOf(annotations);
        this.staticFields = ImmutableField.immutableSetOf(Iterables.filter(fields, FieldUtil.FIELD_IS_STATIC));
        this.instanceFields = ImmutableField.immutableSetOf(Iterables.filter(fields, FieldUtil.FIELD_IS_INSTANCE));
        this.directMethods = ImmutableMethod.immutableSetOf(Iterables.filter(methods, MethodUtil.METHOD_IS_DIRECT));
        this.virtualMethods = ImmutableMethod.immutableSetOf(Iterables.filter(methods, MethodUtil.METHOD_IS_VIRTUAL));
    }

    public ImmutableClassDef( String type,
                             int accessFlags,
                              String superclass,
                              Collection<String> interfaces,
                              String sourceFile,
                              Collection<? extends Annotation> annotations,
                              Iterable<? extends Field> staticFields,
                              Iterable<? extends Field> instanceFields,
                              Iterable<? extends Method> directMethods,
                              Iterable<? extends Method> virtualMethods) {
        this.type = type;
        this.accessFlags = accessFlags;
        this.superclass = superclass;
        this.interfaces = interfaces==null ? ImmutableList.<String>of() : ImmutableList.copyOf(interfaces);
        this.sourceFile = sourceFile;
        this.annotations = ImmutableAnnotation.immutableSetOf(annotations);
        this.staticFields = ImmutableField.immutableSetOf(staticFields);
        this.instanceFields = ImmutableField.immutableSetOf(instanceFields);
        this.directMethods = ImmutableMethod.immutableSetOf(directMethods);
        this.virtualMethods = ImmutableMethod.immutableSetOf(virtualMethods);
    }

    public ImmutableClassDef( String type,
                             int accessFlags,
                              String superclass,
                              ImmutableList<String> interfaces,
                              String sourceFile,
                              ImmutableSet<? extends ImmutableAnnotation> annotations,
                              ImmutableSortedSet<? extends ImmutableField> staticFields,
                              ImmutableSortedSet<? extends ImmutableField> instanceFields,
                              ImmutableSortedSet<? extends ImmutableMethod> directMethods,
                              ImmutableSortedSet<? extends ImmutableMethod> virtualMethods) {
        this.type = type;
        this.accessFlags = accessFlags;
        this.superclass = superclass;
        this.interfaces = ImmutableUtils.nullToEmptyList(interfaces);
        this.sourceFile = sourceFile;
        this.annotations = ImmutableUtils.nullToEmptySet(annotations);
        this.staticFields = ImmutableUtils.nullToEmptySortedSet(staticFields);
        this.instanceFields = ImmutableUtils.nullToEmptySortedSet(instanceFields);
        this.directMethods = ImmutableUtils.nullToEmptySortedSet(directMethods);
        this.virtualMethods = ImmutableUtils.nullToEmptySortedSet(virtualMethods);
    }

    public static ImmutableClassDef of(ClassDef classDef) {
        if (classDef instanceof ImmutableClassDef) {
            return (ImmutableClassDef)classDef;
        }
        return new ImmutableClassDef(
                classDef.getType(),
                classDef.getAccessFlags(),
                classDef.getSuperclass(),
                classDef.getInterfaces(),
                classDef.getSourceFile(),
                classDef.getAnnotations(),
                classDef.getStaticFields(),
                classDef.getInstanceFields(),
                classDef.getDirectMethods(),
                classDef.getVirtualMethods());
    }

     @Override public String getType() { return type; }
    @Override public int getAccessFlags() { return accessFlags; }
     @Override public String getSuperclass() { return superclass; }
     @Override public ImmutableList<String> getInterfaces() { return interfaces; }
     @Override public String getSourceFile() { return sourceFile; }
     @Override public ImmutableSet<? extends ImmutableAnnotation> getAnnotations() { return annotations; }
     @Override public ImmutableSet<? extends ImmutableField> getStaticFields() { return staticFields; }
     @Override public ImmutableSet<? extends ImmutableField> getInstanceFields() { return instanceFields; }
     @Override public ImmutableSet<? extends ImmutableMethod> getDirectMethods() { return directMethods; }
     @Override public ImmutableSet<? extends ImmutableMethod> getVirtualMethods() { return virtualMethods; }


    @Override
    public Collection<? extends ImmutableField> getFields() {
        return new AbstractCollection<ImmutableField>() {

            @Override
            public Iterator<ImmutableField> iterator() {
                return Iterators.concat(staticFields.iterator(), instanceFields.iterator());
            }

            @Override public int size() {
                return staticFields.size() + instanceFields.size();
            }
        };
    }


    @Override
    public Collection<? extends ImmutableMethod> getMethods() {
        return new AbstractCollection<ImmutableMethod>() {

            @Override
            public Iterator<ImmutableMethod> iterator() {
                return Iterators.concat(directMethods.iterator(), virtualMethods.iterator());
            }

            @Override public int size() {
                return directMethods.size() + virtualMethods.size();
            }
        };
    }


    public static ImmutableSet<ImmutableClassDef> immutableSetOf( Iterable<? extends ClassDef> iterable) {
        return CONVERTER.toSet(iterable);
    }

    private static final ImmutableConverter<ImmutableClassDef, ClassDef> CONVERTER =
            new ImmutableConverter<ImmutableClassDef, ClassDef>() {
                @Override
                protected boolean isImmutable( ClassDef item) {
                    return item instanceof ImmutableClassDef;
                }


                @Override
                protected ImmutableClassDef makeImmutable( ClassDef item) {
                    return ImmutableClassDef.of(item);
                }
            };
}
