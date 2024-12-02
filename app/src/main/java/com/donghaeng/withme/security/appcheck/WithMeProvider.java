package com.donghaeng.withme.security.appcheck;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.AppCheckProvider;
import com.google.firebase.appcheck.AppCheckProviderFactory;
import com.google.firebase.appcheck.AppCheckToken;
import com.google.firebase.appcheck.FirebaseAppCheck;

public class WithMeProvider {
    public class WithMeAppCheckToken extends AppCheckToken {
        private final String token;
        private final long expiration;

        WithMeAppCheckToken(String token, long expiration) {
            this.token = token;
            this.expiration = expiration;
        }

        @NonNull
        @Override
        public String getToken() {
            return token;
        }

        @Override
        public long getExpireTimeMillis() {
            return expiration;
        }
    }

    public class WithMeAppCheckProvider implements AppCheckProvider {
        public WithMeAppCheckProvider(FirebaseApp firebaseApp) {
            // ...
        }

        @NonNull
        @Override
        public Task<AppCheckToken> getToken() {
            // Logic to exchange proof of authenticity for an App Check token and
            //   expiration time.
            // [START_EXCLUDE]
            // TODO: 아래 데이터 바꾸기
            long expirationFromServer = 6L;
            String tokenFromServer = "FA0189BF-AD5B-42CD-8142-9E68174A6605";
            // [END_EXCLUDE]

            // Refresh the token early to handle clock skew.
            long expMillis = expirationFromServer * 1000L - 60000L;

            // Create AppCheckToken object.
            AppCheckToken appCheckToken =
                    new WithMeAppCheckToken(tokenFromServer, expMillis);

            return Tasks.forResult(appCheckToken);
        }
    }

    public class WithMeAppCheckProviderFactory implements AppCheckProviderFactory {
        @NonNull
        @Override
        public AppCheckProvider create(@NonNull FirebaseApp firebaseApp) {
            // Create and return an AppCheckProvider object.
            return new WithMeAppCheckProvider(firebaseApp);
        }
    }

    public void init(Context context) {
        // [START appcheck_initialize_custom_provider]
        FirebaseApp.initializeApp(/*context=*/ context);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                new WithMeAppCheckProviderFactory());
        // [END appcheck_initialize_custom_provider]
    }
}