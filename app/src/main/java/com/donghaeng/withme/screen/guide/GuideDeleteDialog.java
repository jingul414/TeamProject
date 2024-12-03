package com.donghaeng.withme.screen.guide;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.donghaeng.withme.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class GuideDeleteDialog extends Dialog {
    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private GuideListAdapter adapter;
    private Context context;
    private String userUid; // 추가

    public GuideDeleteDialog(@NonNull Context context, String userUid) { // 생성자 수정
        super(context);
        this.context = context;
        this.userUid = userUid;
    }

    public GuideDeleteDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_guide_delete);

        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recyclerViewGuides);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new GuideListAdapter();
        recyclerView.setAdapter(adapter);

        loadGuides();

        // 닫기 버튼 설정
        findViewById(R.id.buttonClose).setOnClickListener(v -> dismiss());
    }


    private void loadGuides() {
        db.collection("controller_instruction")
                .whereEqualTo("controllerUid", userUid) // 사용자의 Uid로 필터링
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<GuideItem> guideItems = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        GuideItem item = new GuideItem(
                                document.getId(),
                                document.getString("title"),
                                document.getString("date")
                        );
                        guideItems.add(item);
                    }
                    adapter.setItems(guideItems);

                    // 가이드가 없을 경우 메시지 표시
                    if (guideItems.isEmpty()) {
                        Toast.makeText(context, "삭제할 가이드가 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(context, "가이드 목록을 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
                );
    }
    private static class GuideItem {
        String id;
        String title;
        String date;

        GuideItem(String id, String title, String date) {
            this.id = id;
            this.title = title;
            this.date = date;
        }
    }

    private class GuideListAdapter extends RecyclerView.Adapter<GuideListAdapter.ViewHolder> {
        private List<GuideItem> items = new ArrayList<>();

        void setItems(List<GuideItem> items) {
            this.items = items;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_guide_delete, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            GuideItem item = items.get(position);
            holder.textTitle.setText(item.title);
            holder.textDate.setText(item.date);
            holder.buttonDelete.setOnClickListener(v -> deleteGuide(item, position));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        private void deleteGuide(GuideItem item, int position) {
            db.collection("controller_instruction")
                    .document(item.id)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        items.remove(position);
                        notifyItemRemoved(position);
                        Toast.makeText(context, "가이드가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(context, "삭제에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    );
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView textTitle;
            TextView textDate;
            ImageButton buttonDelete;

            ViewHolder(View view) {
                super(view);
                textTitle = view.findViewById(R.id.textTitle);
                textDate = view.findViewById(R.id.textDate);
                buttonDelete = view.findViewById(R.id.buttonDelete);
            }
        }
    }
}