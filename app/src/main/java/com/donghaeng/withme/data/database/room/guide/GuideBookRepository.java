package com.donghaeng.withme.data.database.room.guide;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.donghaeng.withme.data.guide.GuideBook;
import com.donghaeng.withme.data.guide.GuideBookType;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GuideBookRepository {
    private GuideBookDao guideBookDao;
    private ExecutorService executorService;

    public GuideBookRepository(Context context) {
        GuideBookDatabase db = GuideBookDatabase.getInstance(context);
        guideBookDao = db.guideBookDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void insert(GuideBook guideBook) {
        executorService.execute(() -> guideBookDao.insert(guideBook));
    }

    public void update(GuideBook guideBook) {
        executorService.execute(() -> guideBookDao.update(guideBook));
    }

    public void delete(GuideBook guideBook) {
        executorService.execute(() -> guideBookDao.delete(guideBook));
    }

    public List<GuideBook> getAllGuides() {
        return guideBookDao.getAllGuides();
    }

    public GuideBook getGuideById(int guideIndex) {
        return guideBookDao.getGuideById(guideIndex);
    }

    public List<GuideBook> getAppGuides() {
        return guideBookDao.getGuidesByType(GuideBookType.APP_GUIDE_BOOK);
    }

    public List<GuideBook> getSmartphoneGuides() {
        return guideBookDao.getGuidesByType(GuideBookType.SMARTPHONE_GUIDE_BOOK);
    }

    public List<GuideBook> getControllerInstructions() {
        return guideBookDao.getGuidesByType(GuideBookType.CONTROLLER_INSTRUCTION);
    }

    // 비동기로 데이터 불러오기
    public interface OnGuideLoadedListener {
        void onGuideLoaded(List<GuideBook> guides);
    }

    public void getAppGuidesAsync(String GuideType, OnGuideLoadedListener listener) {
        executorService.execute(() -> {
            List<GuideBook> guides = guideBookDao.getGuidesByType(GuideType);
            new Handler(Looper.getMainLooper()).post(() -> {
                listener.onGuideLoaded(guides);
            });
        });
    }

}