package com.chang;

import java.util.concurrent.TimeUnit;

public class Processor {


  public static void main(String[] args) throws InterruptedException {
//    args = new String[4];
//    args[0] = "100";
//    args[1] = "100";
//    args[2] = "ec2-18-217-82-41.us-east-2.compute.amazonaws.com";
//    args[3] = "8080";

    int numOfThread = Integer.valueOf(args[0]);
    int numOfIteration = Integer.valueOf(args[1]);
    String ip = args[2];
    String port = args[3];
    String url = "http://" + ip + ":" + port + "/simple-service-webapp/webapi/myresource";
    MyClient client = new MyClient(url);

    long globalStartTime = System.currentTimeMillis();



    // Warm up phase
    System.out.println("Client starting ..... Time: 0");
    System.out.println("Warmup phase: All threads running ...");
    System.out.println("Warmup phase complete: Time " + client.runPhase(numOfThread / 10, numOfIteration) + "seconds");



    // Loading phase
    System.out.println("Loading phase: All threads running ...");
    System.out.println("Loading phase complete: Time " + client.runPhase(numOfThread / 2, numOfIteration) + "seconds");



    // Peak phase
    System.out.println("Peak phase: All threads running ...");
    System.out.println("Peak phase complete: Time " + client.runPhase(numOfThread, numOfIteration) + "seconds");



    // Cooldown phase
    System.out.println("Cool down phase: All threads running ...");
    System.out.println("Cool down phase complete: Time " + client.runPhase(numOfThread / 4, numOfIteration) + "seconds");
    System.out.println("================================");


    long globalEndTime = System.currentTimeMillis();
    long wallTime = TimeUnit.MILLISECONDS.toSeconds(globalEndTime - globalStartTime);

    System.out.println("Total num of requests sent: " + client.getNumOfReqSent());
    System.out.println("Total num of requests received: " + client.getNumOfRes());
    System.out.println("Total running time in seconds: " + wallTime);
    System.out.println("Overall throughput is: " + client.getNumOfReqSent() / wallTime);
    client.sortLatencies();
//    client.printAllLatencies();
    System.out.println("The median latency in milliseconds is: " + client.getLatenciesMedian());
    System.out.println("The 90th percentile in milliseconds latency is: " + client.getKthPercentileLatency(90));
    System.out.println("The 95th percentile in milliseconds latency is: " + client.getKthPercentileLatency(95));
  }
}
