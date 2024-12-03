package com.donghaeng.withme.screen.guide;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.donghaeng.withme.R;
import com.donghaeng.withme.databinding.FragmentGuideInputBinding;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GuideInputFragment extends Fragment {
    private FragmentGuideInputBinding binding;
    private ArrayList<GuideContent> contentList;
    private ActivityResultLauncher<String> getContent;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private PreviewAdapter previewAdapter;
    private FirebaseFirestore db;

    // JSON 구조를 위한 클래스들
    private static class GuideContent {
        String type;
        String value;

        GuideContent(String type, String value) {
            this.type = type;
            this.value = value;
        }
    }

    private static class GuideData {
        List<GuideContent> content;

        GuideData(List<GuideContent> content) {
            this.content = content;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contentList = new ArrayList<>();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference().child("guide_images");
        db = FirebaseFirestore.getInstance();  // Firestore 초기화

        getContent = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                result -> {
                    if (result != null) {
                        addContentItem("image", result.toString()); // 미리보기용으로 URI 추가
                    }
                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGuideInputBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // RecyclerView 설정
        previewAdapter = new PreviewAdapter(requireContext());
        binding.recyclerViewPreview.setAdapter(previewAdapter);
        binding.recyclerViewPreview.setLayoutManager(new LinearLayoutManager(requireContext()));

        // 삭제 리스너 설정
        previewAdapter.setOnItemDeleteListener(position -> {
            contentList.remove(position);
            // 어댑터의 removeItem 메서드가 이미 notifyItemRemoved를 호출하므로
            // 여기서 따로 어댑터를 갱신할 필요 없음
        });

        // 뒤로가기 버튼
        binding.back.setOnClickListener(v -> requireActivity().onBackPressed());

        // 텍스트 추가 버튼
        binding.buttonAddText.setOnClickListener(v -> {
            String text = binding.editTextContent.getText().toString();
            if (!text.isEmpty()) {
                addContentItem("text", text);
                binding.editTextContent.setText("");
            }
        });

        // 이미지 추가 버튼
        binding.buttonAddImage.setOnClickListener(v -> {
            getContent.launch("image/*");
        });

        // JSON 생성 버튼
        binding.buttonGenerateJson.setOnClickListener(v -> {
            uploadImagesAndGenerateJson();
        });
    }

    private void generateJsonAndSaveToFirestore() {
        String title = binding.editTextTitle.getText().toString();
        if (title.isEmpty()) {
            Toast.makeText(getContext(), "제목을 입력해주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // JSON 생성
            GuideData guideData = new GuideData(contentList);
            Gson gson = new Gson();
            String jsonString = gson.toJson(guideData);

            // Firestore에 저장할 데이터 생성
            Map<String, Object> document = new HashMap<>();
            document.put("contentJson", jsonString);
            document.put("date", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            document.put("title", title);
            document.put("type", "ControllerInstruction");

            // TODO 문서 이름을 제어자 ID로 사용? 혹은 다른 방법 이용해서 문서 이름으로 구분?
            // 문서 이름 지정할 때 add 대신 .document().set 사용
            //  String documentId = "document1";  // 원하는 문서 이름
            //  db.collection("controller_instruction")
            //       .document(documentId)  // 문서 ID 직접 지정
            //       .set(document)

            // Firestore에 저장
            db.collection("controller_instruction")
                    .add(document)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(getContext(), "저장되었습니다", Toast.LENGTH_SHORT).show();
                        // 클립보드에 복사
                        ClipboardManager clipboard = (ClipboardManager)
                                requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("JSON", jsonString);
                        clipboard.setPrimaryClip(clip);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "저장 실패: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    });

        } catch (Exception e) {
            Toast.makeText(getContext(), "오류가 발생했습니다", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    // uploadImagesAndGenerateJson 메소드 수정
    private void uploadImagesAndGenerateJson() {
        String title = binding.editTextTitle.getText().toString();
        if (title.isEmpty()) {
            Toast.makeText(getContext(), "제목을 입력해주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading(true);
        List<Task<Uri>> uploadTasks = new ArrayList<>();

        // 이미지 업로드 태스크 생성
        for (int i = 0; i < contentList.size(); i++) {
            GuideContent item = contentList.get(i);
            if (item.type.equals("image")) {
                final int index = i;
                String fileName = "image_" + System.currentTimeMillis() + "_" +
                        UUID.randomUUID().toString() + ".jpg";
                StorageReference imageRef = storageRef.child(fileName);

                Task<Uri> uploadTask = imageRef.putFile(Uri.parse(item.value))
                        .continueWithTask(task -> imageRef.getDownloadUrl())
                        .addOnSuccessListener(uri -> {
                            contentList.get(index).value = uri.toString();
                        });
                uploadTasks.add(uploadTask);
            }
        }

        // 모든 업로드가 완료되면 Firestore에 저장
        Tasks.whenAllComplete(uploadTasks)
                .addOnSuccessListener(tasks -> {
                    generateJsonAndSaveToFirestore();
                    showLoading(false);
                })
                .addOnFailureListener(e -> {
                    showLoading(false);
                    Toast.makeText(getContext(), "이미지 업로드 실패", Toast.LENGTH_SHORT).show();
                });
    }

    private void addContentItem(String type, String value) {
        contentList.add(new GuideContent(type, value));
        previewAdapter.updateItems(contentList);
    }


    private void showLoading(boolean show) {
        if (show) {
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.buttonAddImage.setEnabled(false);
            binding.buttonAddText.setEnabled(false);
            binding.buttonGenerateJson.setEnabled(false);
        } else {
            binding.progressBar.setVisibility(View.GONE);
            binding.buttonAddImage.setEnabled(true);
            binding.buttonAddText.setEnabled(true);
            binding.buttonGenerateJson.setEnabled(true);
        }
    }

    public void removeContent(int position) {
        if (position >= 0 && position < contentList.size()) {
            contentList.remove(position);
        }
    }

    // PreviewAdapter 클래스
    private static class PreviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static final int TYPE_TEXT = 0;
        private static final int TYPE_IMAGE = 1;
        private List<GuideContent> items = new ArrayList<>();
        private Context context;

        PreviewAdapter(Context context) {
            this.context = context;
        }

        void updateItems(List<GuideContent> newItems) {
            this.items = new ArrayList<>(newItems);
            notifyDataSetChanged();
        }

        void removeItem(int position) {
            if (position >= 0 && position < items.size()) {
                items.remove(position);
                notifyItemRemoved(position);
            }
        }

        @Override
        public int getItemViewType(int position) {
            return items.get(position).type.equals("text") ? TYPE_TEXT : TYPE_IMAGE;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == TYPE_TEXT) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_guide_text, parent, false);
                view.findViewById(R.id.delete).setClickable(true);
                view.findViewById(R.id.delete).setVisibility(View.VISIBLE);
                return new TextViewHolder(view);
            } else {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_guide_image, parent, false);
                view.findViewById(R.id.delete).setClickable(true);
                view.findViewById(R.id.delete).setVisibility(View.VISIBLE);
                return new ImageViewHolder(view);
            }
        }

        public interface OnItemDeleteListener {
            void onItemDelete(int position);
        }

        private OnItemDeleteListener deleteListener;

        public void setOnItemDeleteListener(OnItemDeleteListener listener) {
            this.deleteListener = listener;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            GuideContent item = items.get(position);

            // Delete button click listener
            View.OnClickListener deleteClickListener = v -> {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    removeItem(adapterPosition);
                    if (deleteListener != null) {
                        deleteListener.onItemDelete(adapterPosition);
                    }
                }
            };

            if (holder instanceof TextViewHolder) {
                TextViewHolder textHolder = (TextViewHolder) holder;
                textHolder.textView.setText(item.value);
                textHolder.deleteButton.setOnClickListener(deleteClickListener);
            } else if (holder instanceof ImageViewHolder) {
                ImageViewHolder imageHolder = (ImageViewHolder) holder;
                Picasso.get()
                        .load(Uri.parse(item.value))
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.error_image)
                        .into(imageHolder.imageView);
                imageHolder.deleteButton.setOnClickListener(deleteClickListener);
            }
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class TextViewHolder extends RecyclerView.ViewHolder {
            TextView textView;
            ImageButton deleteButton;

            TextViewHolder(View view) {
                super(view);
                textView = view.findViewById(R.id.guideText);
                deleteButton = view.findViewById(R.id.deleteButton);
            }
        }

        static class ImageViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            ImageButton deleteButton;

            ImageViewHolder(View view) {
                super(view);
                imageView = view.findViewById(R.id.guideImage);
                deleteButton = view.findViewById(R.id.deleteButton);
            }
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}