package client.state;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.alibaba.fastjson.JSON;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import client.ClientManage;
import client.DealWithReceQue;
import json.util.JSONNameandString;
import server.session.state.State;
import util.EnDeCryProcess;

public class ClientDealwithJSON implements State {

	static final int  DealWithJSONThread = 2;
	
	private final ClientStatemanagement management;
	private ExecutorService sendJSON = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("SendThread-%d").build());
	private ExecutorService dealwithJSON = Executors.newFixedThreadPool(DealWithJSONThread,new ThreadFactoryBuilder().setNameFormat("DeaiwithJSONThread-%d").build());
	private volatile boolean init = false;
	
	public ClientDealwithJSON(ClientStatemanagement clientStatemanagement) {
		management = clientStatemanagement;
	}

	@Override
	public void handle(String s) throws Exception {
		init();
		
		String str = EnDeCryProcess.SysKeyDecryWithBase64(s, management.getSecretKey());
		JSONNameandString parseObject = JSON.parseObject(str, JSONNameandString.class);
		ClientManage.putJSONintoRecequeue(parseObject);
	}

	void init() {
		if(!init){
			synchronized (this) {
				if(!init){
					sendJSON.submit(new Runnable() {	

						@Override
						public void run() {

							while(true){
								JSONNameandString msg = ClientManage.getSendqueueJSON();
								if("json.client.access.ClosingChannel".equals(msg.getJSONName())) {
									management.CloseandSyn();
				                    break;
				                } else {
				                	String send = JSON.toJSONString(msg);
				                	if(send.length()<100)
				                		System.out.println("Send:"+send+" ——in BaseClient 129 line");
				                	else
				                		System.out.println("Send:"+msg.getJSONName()+" ——in BaseClient 129 line");
				            		send = EnDeCryProcess.SysKeyEncryWithBase64(send, management.getSecretKey());
				                    management.WriteWebSocketChannel(send);
				                }
							}
						}
					},"SendJSON");

					for(int i = 0 ; i < DealWithJSONThread;i++)
						dealwithJSON.submit(new DealWithReceQue(ClientManage.getReceque()),"DealwithReceiveJSONthread-"+1);
					init = true;
				}
			}
		}
	}

}
