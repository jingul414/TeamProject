package com.donghaeng.withme.screen.guide;

import android.os.AsyncTask;
import android.util.Log;

import com.donghaeng.withme.data.guide.GuideBook;
import com.donghaeng.withme.data.guide.GuideBookType;
import com.donghaeng.withme.data.database.room.guide.GuideBookRepository;
import com.donghaeng.withme.data.user.Target;
import com.donghaeng.withme.data.user.User;
import com.donghaeng.withme.data.user.UserType;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataRepository {
    private static DataRepository instance;
    private Map<String, List<String>> cache;
    private GuideActivity guideActivity;
    private FirebaseFirestore firestore;

    private DataRepository() {
        cache = new HashMap<>();
        firestore = FirebaseFirestore.getInstance();
    }

    public static DataRepository getInstance() {
        if (instance == null) {
            instance = new DataRepository();
        }
        return instance;
    }

    public void loadSubItems(String headerId, GuideActivity guideActivity, final DataCallback callback) {
        this.guideActivity = guideActivity;
        if (cache.containsKey(headerId)) {
            callback.onDataLoaded(cache.get(headerId));
            return;
        }

        new LoadDataTask(headerId, callback, cache, guideActivity.getUser()).execute();
    }

    public GuideActivity getGuideActivity() {
        return guideActivity;
    }

    private static class LoadDataTask extends AsyncTask<Void, Void, List<String>> {
        private final String headerId;
        private final DataCallback callback;
        private final Map<String, List<String>> cache;
        private String error = null;
        private GuideActivity guideActivity;
        private GuideBookRepository guideBookRepository;
        private FirebaseFirestore firestore;
        private User user;

        LoadDataTask(String headerId, DataCallback callback, Map<String, List<String>> cache, User user) {
            this.headerId = headerId;
            this.callback = callback;
            this.cache = cache;
            this.firestore = FirebaseFirestore.getInstance();
            this.user = user;
        }

        @Override
        protected List<String> doInBackground(Void... voids) {
            List<String> subItems = new ArrayList<>();
            try {
                guideActivity = getInstance().getGuideActivity();
                guideBookRepository = new GuideBookRepository(guideActivity);

                String collectionName;
                switch (headerId) {
                    case "guide":
                        collectionName = "app_guide_book";
                        break;
                    case "smartphone":
                        collectionName = "smartphone_guide_book";
                        break;
                    case "guardian":
                        collectionName = "controller_instruction";
                        break;
                    default:
                        error = "Unknown header ID: " + headerId;
                        return null;
                }

                try {
                    if (headerId.equals("guardian")) {
                        String targetUid;
                        if (user.getUserType() == UserType.TARGET) {
                            // Target인 경우 Controller의 가이드를 불러옴
                            targetUid = ((Target)user).getController().getId();
                        } else {
                            // Controller인 경우 자신의 가이드를 불러옴
                            targetUid = user.getId();
                        }

                        com.google.firebase.firestore.QuerySnapshot querySnapshot =
                                Tasks.await(firestore.collection(collectionName)
                                        .whereEqualTo("controllerUid", targetUid)
                                        .get());

                        for (QueryDocumentSnapshot document : querySnapshot) {
                            GuideBook guideBook = document.toObject(GuideBook.class);
                            if (guideBook != null) {
                                guideBookRepository.insert(guideBook);
                                subItems.add(guideBook.getTitle());
                                Log.d("DataRepository", "Added guide: " + guideBook.getTitle());
                            }
                        }
                    } else {
                        com.google.firebase.firestore.QuerySnapshot querySnapshot =
                                Tasks.await(firestore.collection(collectionName).get());

                        for (QueryDocumentSnapshot document : querySnapshot) {
                            GuideBook guideBook = document.toObject(GuideBook.class);
                            if (guideBook != null) {
                                guideBookRepository.insert(guideBook);
                                subItems.add(guideBook.getTitle());
                                Log.d("DataRepository", "Added guide: " + guideBook.getTitle());
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e("DataRepository", "Firebase error: " + e.getMessage());
                    return new ArrayList<>();
                }

                Thread.sleep(1000); // 로딩 표시를 위한 지연

            } catch (Exception e) {
                error = "데이터 로딩 중 오류가 발생했습니다: " + e.getMessage();
                Log.e("DataRepository", "Error loading data", e);
                return null;
            }

            return subItems;
        }

        @Override
        protected void onPostExecute(List<String> result) {
            if (error != null) {
                callback.onError(error);
            } else {
                cache.put(headerId, result);
                callback.onDataLoaded(result);
            }
        }
    }

    public void clearCache() {
        cache.clear();
    }
}