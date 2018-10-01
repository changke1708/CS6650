package com.chang;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
;import static java.util.Collections.swap;

public class MyClient {

  private Client client;
  private WebTarget myResource;
  private Invocation.Builder builder;
  private String url;
  private int numOfReqSent;
  private int numOfRes;
  private CopyOnWriteArrayList<Long> latencies;

  public MyClient(String url) {
    this.url = url;
    client = ClientBuilder.newClient();
    myResource = client.target(url);
    builder = myResource.request(MediaType.TEXT_PLAIN);
    numOfReqSent = 0;
    numOfRes = 0;
    latencies = new CopyOnWriteArrayList<>();
  }

  public void getStatus() throws
      ClientErrorException{
      long startTime = System.currentTimeMillis();
      numOfReqSent++;
      if (myResource.request(MediaType.TEXT_PLAIN).get(String.class).equals("Got it!")) {
        numOfRes++;
        latencies.add(System.currentTimeMillis() - startTime);
      }
  }

  public void postText(String requestEntity) throws
      ClientErrorException {
      long startTime = System.currentTimeMillis();
      numOfReqSent++;
      if (myResource.request(MediaType.TEXT_PLAIN).post(Entity.entity(requestEntity, MediaType.TEXT_PLAIN),String.class).equals("Post it!")) {
        numOfRes++;
        latencies.add(System.currentTimeMillis() - startTime);
      }
  }

  public int getNumOfReqSent() {
    return numOfReqSent;
  }

  public int getNumOfRes() {
    return numOfRes;
  }


  public void sortLatencies() {
    Object[] arr = latencies.toArray();
    Arrays.sort(arr);
    for(int i = 0; i < latencies.size(); ++i) {
      latencies.set(i, (Long) arr[i]);
    }
  }

  public long getLatenciesMedian() {
    return latencies.get(latencies.size() / 2);
  }

  public long getKthPercentileLatency(int k) {
    int index = (int) ((double) k / 100 * latencies.size());
    return latencies.get(index);
  }

  public long runPhase(int numOfThreads, final int numOfIterations) throws InterruptedException {
    long startTime = System.currentTimeMillis();
    ExecutorService executorService = Executors.newFixedThreadPool(numOfThreads);
    for (int j = 0; j < numOfThreads; j++) {
      executorService.execute(new Runnable() {
        public void run() {
          for (int i = 0; i < numOfIterations; i++) {
            getStatus();
            postText(" ");
          }
        }
      });
    }
    executorService.shutdown();
    executorService.awaitTermination(10, TimeUnit.MINUTES);
    long endTime1 = System.currentTimeMillis();
    return TimeUnit.MILLISECONDS.toSeconds(endTime1 - startTime);
  }
}
