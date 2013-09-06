/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/seetha/Desktop/practicum/zense/mobisens_android/android/src/edu/cmu/sv/mobisens/ISystemLog.aidl
 */
package edu.cmu.sv.mobisens;
public interface ISystemLog extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements edu.cmu.sv.mobisens.ISystemLog
{
private static final java.lang.String DESCRIPTOR = "edu.cmu.sv.mobisens.ISystemLog";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an edu.cmu.sv.mobisens.ISystemLog interface,
 * generating a proxy if needed.
 */
public static edu.cmu.sv.mobisens.ISystemLog asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof edu.cmu.sv.mobisens.ISystemLog))) {
return ((edu.cmu.sv.mobisens.ISystemLog)iin);
}
return new edu.cmu.sv.mobisens.ISystemLog.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_log:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
this.log(_arg0, _arg1);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements edu.cmu.sv.mobisens.ISystemLog
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public void log(java.lang.String tag, java.lang.String msg) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(tag);
_data.writeString(msg);
mRemote.transact(Stub.TRANSACTION_log, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_log = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
}
public void log(java.lang.String tag, java.lang.String msg) throws android.os.RemoteException;
}
