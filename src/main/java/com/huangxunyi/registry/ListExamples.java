package com.huangxunyi.registry;

import io.fabric8.kubernetes.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.CountDownLatch;

public class ListExamples {

//    private static final Logger logger = LoggerFactory.getLogger(ListExamples.class);
//
//    public static void main(String[] args) {
//        CountDownLatch countDownLatch = new CountDownLatch(1);
//        Config config = new ConfigBuilder().build();
//        try (final KubernetesClient client = new DefaultKubernetesClient(config)) {
////
////            client.pods().inNamespace("default").watch(new Watcher<>() {
////
////                @Override
////                public void eventReceived(Action action, Pod resource) {
////                    System.out.println(action);
////                    System.out.println(" --- ");
////                    System.out.println(resource);
////                    System.out.println(" ==== ");
////                }
////
////                @Override
////                public void onClose(KubernetesClientException cause) {
////                    cause.printStackTrace();
////                }
////
////            });
//
//
//            try (Socket socket = new Socket()) {
//                SocketAddress addr = new InetSocketAddress(client.getMasterUrl().getHost(), client.getMasterUrl().getPort());
//                socket.connect(addr);
//                String hostAddress = socket.getLocalAddress().getHostAddress();
//                System.out.println(hostAddress);
//                ServerSocket ss = new ServerSocket();
//                ss.bind(new InetSocketAddress("0.0.0.0", 9376));
//                while (true) {
//                    Socket accept = ss.accept();
//                    new Thread(() -> {
//                        try {
//                            System.out.println(" accept " + accept);
//                            accept.getOutputStream().write("HTTP/1.0 200 OK\r\nContent-Type: text/plain;charset=UTF-8\r\n\r\nHello,World\n\n".getBytes());
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }finally {
//                            try {
//                                accept.close();
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }).start();
//                }
//            }
////            countDownLatch.await();
//
////            System.out.println(client.namespaces().list());
//
//        } catch (KubernetesClientException | IOException e) {
//            logger.error(e.getMessage(), e);
//        }
//    }

}