package com.example.donary.notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers({
            "Content_Type:application/json",
            "Authorization:key=AAAAIFpHaF0:APA91bGVfDfLy8UfhcuWLf5DgCQWEJHaa240-FXtXK2NebUewYGPioLbUpn4kZbyjW2v9x8lNEt7Q4-C1LeRDTHohNZf6ODoxy9eRne4fsxuVs98LqKA7OzaM4w39hxS22vR_u0JZyc6"

    })

    @POST("fcm/send")
    Call<Response> sendNotification(@Body Sender body);
}
