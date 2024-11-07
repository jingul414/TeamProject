package com.donghaeng.withme.screen.guide;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.donghaeng.withme.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ExpandableAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_LOADING = 2;
    private List<ListItem> items;
    private Set<String> loadingItems; // 현재 로딩 중인 아이템들의 ID를 추적

    public ExpandableAdapter(List<ListItem> items) {
        this.items = items;
        this.loadingItems = new HashSet<>();
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView titleText;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.titleText);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    ListItem item = items.get(position);
                    // 현재 로딩 중인 아이템이 있는지 확인
                    if (!isAnyItemLoading() || item.isExpanded()) {
                        if (item.isExpanded()) {
                            collapseItem(position);
                        } else {
                            expandItem(position);
                        }
                    }
                }
            });
        }
    }

    // 현재 로딩 중인 아이템이 있는지 확인
    private boolean isAnyItemLoading() {
        return !loadingItems.isEmpty();
    }

    // 특정 아이템이 로딩 중인지 확인
    private boolean isItemLoading(String itemId) {
        return loadingItems.contains(itemId);
    }

    // 로딩 시작
    private void startLoading(String itemId) {
        loadingItems.add(itemId);
    }

    // 로딩 종료
    private void finishLoading(String itemId) {
        loadingItems.remove(itemId);
    }

    private void expandItem(int position) {
        ListItem item = items.get(position);
        if (!item.isExpanded() && !isItemLoading(item.getId())) {
            item.setExpanded(true);
            startLoading(item.getId());

            // 로딩 표시 추가
            int loadingPosition = position + 1;
            ListItem loadingItem = new ListItem("loading_" + item.getId(), TYPE_LOADING, "");
            items.add(loadingPosition, loadingItem);
            notifyItemInserted(loadingPosition);

            // 데이터 로드
            DataRepository.getInstance().loadSubItems(item.getId(), new DataCallback() {
                @Override
                public void onDataLoaded(List<String> subItems) {
                    // UI 업데이트는 메인 스레드에서 실행
                    if (item.isExpanded()) { // 여전히 확장 상태인지 확인
                        // 로딩 항목 제거
                        int loadingIndex = findLoadingPosition(item.getId());
                        if (loadingIndex != -1) {
                            items.remove(loadingIndex);
                            notifyItemRemoved(loadingIndex);

                            // 새로운 항목 추가
                            List<ListItem> newItems = new ArrayList<>();
                            for (String subItem : subItems) {
                                newItems.add(new ListItem(
                                        UUID.randomUUID().toString(),
                                        ListItem.TYPE_ITEM,
                                        subItem
                                ));
                            }

                            items.addAll(loadingIndex, newItems);
                            notifyItemRangeInserted(loadingIndex, newItems.size());
                        }
                    }
                    finishLoading(item.getId());
                }

                @Override
                public void onError(String error) {
                    // 로딩 항목 제거
                    int loadingIndex = findLoadingPosition(item.getId());
                    if (loadingIndex != -1) {
                        items.remove(loadingIndex);
                        notifyItemRemoved(loadingIndex);
                    }
                    item.setExpanded(false);
                    finishLoading(item.getId());
                }
            });
        }
    }

    private int findLoadingPosition(String headerId) {
        for (int i = 0; i < items.size(); i++) {
            ListItem item = items.get(i);
            if (item.getType() == TYPE_LOADING &&
                    item.getId().equals("loading_" + headerId)) {
                return i;
            }
        }
        return -1;
    }

    private void collapseItem(int position) {
        ListItem item = items.get(position);
        if (item.isExpanded()) {
            item.setExpanded(false);

            // 로딩 중인 경우 로딩 상태 제거
            if (isItemLoading(item.getId())) {
                finishLoading(item.getId());
            }

            int nextPosition = position + 1;
            List<ListItem> removedItems = new ArrayList<>();

            while (nextPosition < items.size() &&
                    items.get(nextPosition).getType() != ListItem.TYPE_HEADER) {
                removedItems.add(items.get(nextPosition));
                nextPosition++;
            }

            for (int i = 0; i < removedItems.size(); i++) {
                items.remove(position + 1);
            }

            notifyItemRangeRemoved(position + 1, removedItems.size());
        }
    }

    // 나머지 메서드들은 그대로 유지...
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case ListItem.TYPE_HEADER:
                View headerView = inflater.inflate(R.layout.item_header, parent, false);
                return new HeaderViewHolder(headerView);
            case ListItem.TYPE_ITEM:
                View itemView = inflater.inflate(R.layout.item_content, parent, false);
                return new ItemViewHolder(itemView);
            case TYPE_LOADING:
                View loadingView = inflater.inflate(R.layout.item_loading, parent, false);
                return new LoadingViewHolder(loadingView);
            default:
                throw new IllegalArgumentException("Unknown view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ListItem item = items.get(position);
        if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            headerHolder.titleText.setText(item.getTitle());
        } else if (holder instanceof ItemViewHolder) {
            ItemViewHolder itemHolder = (ItemViewHolder) holder;
            itemHolder.contentText.setText(item.getTitle());
        }
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView contentText;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            contentText = itemView.findViewById(R.id.contentText);
        }
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
}