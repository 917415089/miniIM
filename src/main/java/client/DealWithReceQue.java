package client;

import java.util.concurrent.BlockingQueue;

import json.util.JSONNameandString;

public class DealWithReceQue implements Runnable{

    private BlockingQueue<JSONNameandString> receque;
    
	public DealWithReceQue(BlockingQueue<JSONNameandString> receque) {
		super();
		this.receque = receque;
	}

	@Override
	public void run() {
		while(true){
			try {
				JSONNameandString take = receque.take();
				switch(take.getJSONName()){
				case "json.server.login.WrongNameorPassword":
					dealwithWrongNameorPassword(take.getJSONStr());
					break;
				case "json.server.lgoin.RegisiterResult":
					dealwithRegisterResult(take.getJSONStr());
					break;
				default:
					System.err.println("can't deal "+take.getJSONName());
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void dealwithRegisterResult(String jsonStr) {
		System.out.println("user's name has been registered");
		
	}

	private void dealwithWrongNameorPassword(String jsonStr) {
		System.out.println("send wrong name or password message to gui");
		//this is not a good idea cause this method handle similar work as ClientSeesion, but I can't find a better idea;
		
	}
	
}
