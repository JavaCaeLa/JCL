package broadcasts;




import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hpc.jcl_android.JCL_ANDROID_Facade;

import java.io.IOException;

import implementations.dm_kernel.host.MainHost;
import services.JCL_HostService;


public class ReceiverActionNotification extends BroadcastReceiver {
	

	@Override
	public void onReceive(Context context, Intent arg1) {
		//int action = arg1.getIntExtra("action", -1); (how to get a argument)
		//Log.e("Msg", action+"");
		//MusicoollaFacade.sendLikeOrUnlike(action+"");
		//FacadeNewMusicolla.getInstance().changeCollorNotification();
		try{
			JCL_ANDROID_Facade jcl = JCL_ANDROID_Facade.getInstance();
			String[] ipPort = new String[0];
			try {
				ipPort = jcl.getIpPort(context);
			} catch (IOException e) {
				e.printStackTrace();
			}
			String[] inf = {jcl.getMyIp(context), ipPort[0], ipPort[1]};
			MainHost.unRegister(inf);

			JCL_HostService.isWorking = false;
			context.stopService(new Intent(context, JCL_HostService.class));
		}catch (Exception e){
			e.printStackTrace();
			JCL_HostService.isWorking = false;
			context.stopService(new Intent(context, JCL_HostService.class));
		}

	}

	
}
