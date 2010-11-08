package es.igosoftware.concurrent;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;


public class GConcurrentTest {


   public static void main(final String[] args) throws InterruptedException, ExecutionException {
      final ExecutorService executor = GConcurrent.getDefaultExecutor();


      final long started = System.currentTimeMillis();
      for (int i = 0; i < 500000; i++) {
         executor.execute(new Runnable() {
            @Override
            public void run() {
               final Random random = new Random();
               for (int j = 0; j < 2000; j++) {
                  random.nextGaussian();
               }
            }
         });
      }

      final Future<Integer> result = executor.submit(new Callable<Integer>() {
         @Override
         public Integer call() throws Exception {
            return new Random().nextInt(10);
         }
      });

      //System.out.println(result.isDone());

      System.out.println(result.get());

      final long elapsed = System.currentTimeMillis() - started;
      System.out.println("Run in " + elapsed + "ms");

      //executor.shutdown();
   }
}
