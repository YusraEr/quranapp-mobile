package com.example.quranapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quranapp.R;
import com.example.quranapp.data.remote.model.AyatSearchResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {

    private List<AyatSearchResult> results = new ArrayList<>();
    private String currentQuery = "";
    private final Context context;

    public SearchResultAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<AyatSearchResult> newResults, String query) {
        this.results = newResults;
        this.currentQuery = query;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(results.get(position), currentQuery);
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView header, textArab, textTranslation;
        int highlightColor;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            header = itemView.findViewById(R.id.textViewSearchResultHeader);
            textArab = itemView.findViewById(R.id.textViewSearchResultArab);
            textTranslation = itemView.findViewById(R.id.textViewSearchResultTranslation);
            highlightColor = Color.YELLOW; // Atau ambil dari colors.xml
        }

        void bind(AyatSearchResult result, String query) {
            header.setText(String.format(Locale.getDefault(), "%s: %d", result.getSurahNameLatin(), result.getAyatNumber()));
            textArab.setText(result.getAyatTextArab());

            String translation = result.getAyatTranslation();
            SpannableString spannableString = new SpannableString(translation);

            if (query != null && !query.isEmpty()) {
                String lowerTranslation = translation.toLowerCase();
                String lowerQuery = query.toLowerCase();
                int startIndex = 0;
                while ((startIndex = lowerTranslation.indexOf(lowerQuery, startIndex)) != -1) {
                    int endIndex = startIndex + lowerQuery.length();
                    spannableString.setSpan(new BackgroundColorSpan(highlightColor), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    startIndex = endIndex;
                }
            }
            textTranslation.setText(spannableString);
        }
    }
}