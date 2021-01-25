package firebase.kunasainath.doyourthing.viewpager_fragments;

import firebase.kunasainath.doyourthing.notification.MyResponse;
import firebase.kunasainath.doyourthing.notification.Sender;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAjLe_L4Q:APA91bEEXjDz0qqI3e9rz2hVwkTA3jCbEgVejXVC_FfisTB316nPpOB_cngR6z-GJoQc69dX8F6BG0QqJIx5Uj9El_osrOsJJtyLVWqCjbFZogi3p85WTPkTP1L6Apaz7nHeLFF2JsE5"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
