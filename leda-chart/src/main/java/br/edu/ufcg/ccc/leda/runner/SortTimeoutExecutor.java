package br.edu.ufcg.ccc.leda.runner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SortTimeoutExecutor {
	//tempo de timeout em milisegundos para executar o sort
	public static final int TIMEOUT = 2000;

	public static void invoke(Object sortImplementation, Method sortMethod,
			List<Integer> arguments) throws InterruptedException, ExecutionException, TimeoutException  {
		
		ExecutorService executor = Executors.newFixedThreadPool(1);
        Future<Integer[]> control = null;
        try {
        	System.out.println("Invoking " +sortImplementation.getClass().getSimpleName() + " in array of size " + arguments.size());
        	SortCaller caller = new SortCaller(sortImplementation,sortMethod,arguments);
        	control = executor.submit(caller);
            control.get(TIMEOUT, TimeUnit.MILLISECONDS);
            
        } catch (TimeoutException ex) {
        	//ex.printStackTrace();
        	if(control != null){
        		control.cancel(true);
        	}
        	throw ex;
        } catch (InterruptedException ex) {
        	//ex.printStackTrace();
        	throw ex;
        	//TODO no futuro as excecoes podem sumir
        } catch (ExecutionException ex) {
        	//ex.printStackTrace();
        	throw ex;
        }
        executor.shutdown();
	}
	
	
	public static class SortCaller implements Callable<Integer[]>{

		private Object sortImplementation;
		private Method sortMethod;
		private List<Integer> arguments;
		
		
		public SortCaller(Object sortImplementation, Method sortMethod,
				List<Integer> arguments) {
			super();
			this.sortImplementation = sortImplementation;
			this.sortMethod = sortMethod;
			this.arguments = arguments;
		}


		@Override
	    public Integer[] call() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
			sortMethod.invoke(sortImplementation, new Object[] { arguments.toArray(new Integer[0]) });
			return arguments.toArray(new Integer[0]);
	    }

	}
}
