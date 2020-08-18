package com.img.mysure11.Firebase;

//import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by HP on 21-03-2017.
 */
public class FirebaseIDService /*extends FirebaseInstanceIdService*/ {
    private static final String TAG = "FirebaseIDService";

/*
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.i(TAG, "Refreshed token: " + refreshedToken);

        System.out.println("Refreshed token: " + refreshedToken);

        session=new UserSessionManager(this);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);



    }
*/

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // Add custom implementation, as needed.


    }









}