package server;

public class Test {

	
	public static void main(String[] args) throws InterruptedException {
		A a = new A();
		a.start();
		System.out.println(a.flag);
		Thread.sleep(10);
		System.out.println(a.flag);
		a.flag=true;
	}
}
	class A extends Thread{
	boolean flag;
	public A(){
		flag = false;
	}
	public boolean isFlag() {
		return flag;
	}
	@Override
	public void run(){
//		while(!isFlag());
//		while(!flag);
//		while(!isFlag())System.out.println("pass");;
//		while(!flag)System.out.println("pass");
		System.out.println("finish");
	}
}
