package util;

import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
@Deprecated
public class ActionListenerInstaller {

	public static void processAnnotations(Object obj){
		try{
			Class<?> cl = obj.getClass();
			for(Method m : cl.getDeclaredMethods()){
				ActionListenerFor a = m.getAnnotation(ActionListenerFor.class);
				if(a!=null){
					Field f = cl.getDeclaredField(a.source());
					f.setAccessible(true);
					addListener(f.get(obj),obj,m);
				}
			}	
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

	private static void addListener(Object source, final Object param, final Method m) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		InvocationHandler handler = new InvocationHandler() {
			
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				return m.invoke(param);
			}
		};
		
		Object listener = Proxy.newProxyInstance(null, new Class[]{java.awt.event.ActionListener.class}, handler);
		Method adder = source.getClass().getMethod("addActionListener", ActionListener.class);
		adder.invoke(source, listener);
	}
}
