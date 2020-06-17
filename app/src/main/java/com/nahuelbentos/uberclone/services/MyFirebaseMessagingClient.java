package com.nahuelbentos.uberclone.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.nahuelbentos.uberclone.R;
import com.nahuelbentos.uberclone.channel.NotificationHelper;
import com.nahuelbentos.uberclone.receivers.AcceptReceiver;
import com.nahuelbentos.uberclone.receivers.CancelReceiver;

import java.util.Map;

public class MyFirebaseMessagingClient extends FirebaseMessagingService {

    private static final int NOTIFICATION_CODE = 100;

    // Genera un token para enviar notificaciones de dispositivo a dispositivo
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

    }

    // Este metodo recibe las notificaciones Push que llegaan del servidor
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        Map<String, String> data = remoteMessage.getData();
        String title = data.get("title");
        String body = data.get("body");

        if(title != null) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                if(title.contains("SOLICITUD DE SERVICIO")){
                    String idClient = data.get("idClient");
                    showNotificationApiOreoActions(title, body,idClient);
                }else{

                showNotificationApiOreo(title, body);
                }
            } else {

                if(title.contains("SOLICITUD DE SERVICIO")){
                    String idClient = data.get("idClient");
                    showNotificationActions(title, body, idClient);
                } else{
                    showNotification(title, body);

                }

            }
        }
    }

    /*
        Con los dos metodos siguientes queda implementado la forma de mostrar notificaciones
        que se envian desde un servidor (Firebase en este caso)

     */

    private void showNotification(String title, String body) {
         Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationHelper notificationHelper = new NotificationHelper((getBaseContext()));
        NotificationCompat.Builder builder = notificationHelper.getNotificationOldAPI(title, body, sound );

        notificationHelper.getManager().notify(1, builder.build());
    }
    private void showNotificationActions(String title, String body, String idClient) {

        // Accion aceptar
        Intent acceptIntent = new Intent(this, AcceptReceiver.class);
        acceptIntent.putExtra("idClient", idClient);

        PendingIntent acceptPendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_CODE, acceptIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action acceptAction = new NotificationCompat.Action.Builder(
                R.mipmap.ic_launcher,
                "Aceptar",
                acceptPendingIntent
        ).build();

        // Accion cancelar
        Intent cancelIntent = new Intent(this, CancelReceiver.class);
        cancelIntent.putExtra("idClient", idClient);

        PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_CODE, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action cancelAction = new NotificationCompat.Action.Builder(
                R.mipmap.ic_launcher,
                "Cancelar",
                cancelPendingIntent
        ).build();


        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationHelper notificationHelper = new NotificationHelper((getBaseContext()));
        NotificationCompat.Builder builder = notificationHelper.getNotificationOldAPIActions(title, body, sound, acceptAction, cancelAction );

        notificationHelper.getManager().notify(2, builder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showNotificationApiOreo(String title, String body) {
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationHelper notificationHelper = new NotificationHelper((getBaseContext()));
        Notification.Builder builder = notificationHelper.getNotification(title, body, sound );

        notificationHelper.getManager().notify(1, builder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showNotificationApiOreoActions(String title, String body, String idClient ) {

        // Accion aceptar
        Intent acceptIntent = new Intent(this, AcceptReceiver.class);
        acceptIntent.putExtra("idClient", idClient);

        PendingIntent acceptPendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_CODE, acceptIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Action acceptAction = new Notification.Action.Builder(
                R.mipmap.ic_launcher,
                "Aceptar",
                acceptPendingIntent
        ).build();

        // Accion cancelar
        Intent cancelIntent = new Intent(this, CancelReceiver.class);
        cancelIntent.putExtra("idClient", idClient);

        PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_CODE, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Action cancelAction = new Notification.Action.Builder(
                R.mipmap.ic_launcher,
                "Cancelar",
                cancelPendingIntent
        ).build();


        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationHelper notificationHelper = new NotificationHelper((getBaseContext()));
        Notification.Builder builder = notificationHelper.getNotificationActions(title, body, sound, acceptAction, cancelAction );

        notificationHelper.getManager().notify(2, builder.build());
    }

}
