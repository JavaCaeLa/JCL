package interfaces.kernel;

import implementations.dm_kernel.MessageBoolImpl;
import implementations.dm_kernel.MessageClassImpl;
import implementations.dm_kernel.MessageCommonsImpl;
import implementations.dm_kernel.MessageControlImpl;
import implementations.dm_kernel.MessageGenericImpl;
import implementations.dm_kernel.MessageGetHostImpl;
import implementations.dm_kernel.MessageGlobalVarImpl;
import implementations.dm_kernel.MessageGlobalVarObjImpl;
import implementations.dm_kernel.MessageImpl;
import implementations.dm_kernel.MessageListGlobalVarImpl;
import implementations.dm_kernel.MessageListTaskImpl;
import implementations.dm_kernel.MessageLongImpl;
import implementations.dm_kernel.MessageMetadataImpl;
import implementations.dm_kernel.MessageRegisterImpl;
import implementations.dm_kernel.MessageResultImpl;
import implementations.dm_kernel.MessageSensorImpl;
import implementations.dm_kernel.MessageTaskImpl;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

public interface Constant {
	
	public static final int MSG = 0;
	public static final int MSG_COMMONS = 1;
	public static final int MSG_CONTROL = 2;
	public static final int MSG_GETHOST = 3;
	public static final int MSG_GLOBALVARS = 4;
	public static final int MSG_REGISTER = 5;
	public static final int MSG_RESULT = 6;
	public static final int MSG_TASK = 7;
	public static final int MSG_LISTTASK = 8;
	public static final int MSG_GENERIC = 9;
	public static final int MSG_LONG = 10;
	public static final int MSG_BOOL = 11;
	public static final int MSG_CLASS = 12;
	public static final int MSG_LISTGLOBALVARS = 13;
	public static final int MSG_GLOBALVARSOBJ = 14;
	public static final int MSG_SENSOR = 15;
	public static final int MSG_METADATA = 16;

	@SuppressWarnings("rawtypes")
	public static final Schema[] schema = 
		{
		RuntimeSchema.getSchema(MessageImpl.class),
		RuntimeSchema.getSchema(MessageCommonsImpl.class),
		RuntimeSchema.getSchema(MessageControlImpl.class),
		RuntimeSchema.getSchema(MessageGetHostImpl.class),
		RuntimeSchema.getSchema(MessageGlobalVarImpl.class),
		RuntimeSchema.getSchema(MessageRegisterImpl.class),
		RuntimeSchema.getSchema(MessageResultImpl.class),
		RuntimeSchema.getSchema(MessageTaskImpl.class),
		RuntimeSchema.getSchema(MessageListTaskImpl.class),
		RuntimeSchema.getSchema(MessageGenericImpl.class),
		RuntimeSchema.getSchema(MessageLongImpl.class),
		RuntimeSchema.getSchema(MessageBoolImpl.class),
		RuntimeSchema.getSchema(MessageClassImpl.class),
		RuntimeSchema.getSchema(MessageListGlobalVarImpl.class),
		RuntimeSchema.getSchema(MessageGlobalVarObjImpl.class),
		RuntimeSchema.getSchema(MessageSensorImpl.class),
		RuntimeSchema.getSchema(MessageMetadataImpl.class)
		};
}
