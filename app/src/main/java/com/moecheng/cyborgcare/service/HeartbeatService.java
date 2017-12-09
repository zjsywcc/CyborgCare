package com.moecheng.cyborgcare.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;

import com.moecheng.cyborgcare.MainActivity;
import com.moecheng.cyborgcare.R;
import com.moecheng.cyborgcare.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.SSLException;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;


/**
 * Created by wangchengcheng on 2017/12/9.
 */

public class HeartbeatService extends Service {

    private IBinder mBinder;

    /**
     * 单一线程池
     */
    private ExecutorService executorService= Executors.newSingleThreadExecutor();

    /**
     * 是否需要继续运行
     */
    private boolean running = true;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            create();
        } catch (SSLException e) {
        }

        executorService.execute(new Mythread());

        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        running = false;
        executorService.shutdown();
        if(f != null){
            f.channel().close();
        }
        stopSelf();
        super.onDestroy();
    }

    /***********************************************************************************
     * start Netty长连接服务器保持数据通讯
     ***********************************************************************************/
    private static final boolean SSL = System.getProperty("ssl") != null;
    SslContext sslCtx;

    private void create() throws SSLException {
        if (SSL) {
            sslCtx = SslContext.newClientContext(InsecureTrustManagerFactory.INSTANCE);
        } else {
            sslCtx = null;
        }
    }
    ChannelFuture f;
    public void createBootStrap() {

        Log.v("HeartbeatService", "重连......");
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            if (sslCtx != null) {
                                p.addLast(sslCtx.newHandler(ch.alloc(), "192.168.0.103", 9800));
                            }
                            p.addLast("idle", new IdleStateHandler(300, 300, 300));
                            p.addLast(new ObjectEncoder(), new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                    new CarmgrClientHandler());
                        }
                    });

            //设置TCP协议的属性
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.option(ChannelOption.TCP_NODELAY, true);
            bootstrap.option(ChannelOption.SO_TIMEOUT, 5000);
            // Start the client. IP和Port是服务器运行的IP和自己设置的端口
            f = bootstrap.connect("192.168.0.103", 9800).sync();
            f.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if (channelFuture.isSuccess()) {

                    } else {

                    }
                }
            });
            f.channel().closeFuture().sync();
        } catch (Exception e) {
        } finally {
            // Shut down the event loop to terminate all threads.
            group.shutdownGracefully();
        }

    }

    private class Mythread extends Thread{
        @Override
        public void run() {
            while(running) {
                createBootStrap();
                try {
                    //休息30s重连
                    Thread.sleep(30 * 1000);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    /**
     * 开启推送消息展示
     */
    private int notificaionId = 100;

    public void startNotification(String title, String context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        // 设置通知的基本信息：icon、标题、内容
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(title);
        builder.setContentText(context);
        builder.setAutoCancel(true);

        // 设置通知的优先级
        builder.setPriority(NotificationCompat.PRIORITY_MAX);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        // 设置通知的提示音
        builder.setSound(alarmSound);
        builder.setDefaults(Notification.DEFAULT_ALL);

        // 设置通知的点击行为：这里启动一个 Activity
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        if (notificaionId < 1000) {
            notificaionId++;
        } else {
            notificaionId = 100;
        }

        // 发送通知 id 需要在应用内唯一
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificaionId, builder.build());

    }

    /**
     * Netty消息接收内部类
     */
    private class CarmgrClientHandler extends ChannelInboundHandlerAdapter {

        /**
         * Creates a client-side handler.
         */
        public CarmgrClientHandler() {
            //TODO
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) {
            ctx.writeAndFlush("1");
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {

            if (msg.toString().equals("y")) {
                //心跳不用理会
            } else {
                //处理服务端推送的消息
            }
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) {
            ctx.flush();
        }


        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            // Close the connection when an exception is raised.
            ctx.close();
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            super.channelInactive(ctx);
        }


        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            super.userEventTriggered(ctx, evt);

            if (evt instanceof IdleStateEvent) {
                IdleStateEvent e = (IdleStateEvent) evt;
                switch (e.state()) {
                    case WRITER_IDLE:
                        ctx.writeAndFlush("1");
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /***********************************************************************************
     * end Netty长连接服务器保持数据通讯
     ***********************************************************************************/
}
