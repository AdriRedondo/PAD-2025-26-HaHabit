package es.ucm.fdi.pad.hahabit.ui.home;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import es.ucm.fdi.pad.hahabit.R;
import java.util.ArrayList;
import java.util.List;

public class ListItemAdapter extends RecyclerView.Adapter<ListItemAdapter.ListItemViewHolder> {

    public static class ListItem {
        public String text;
        public boolean completed;

        public ListItem(String text, boolean completed) {
            this.text = text;
            this.completed = completed;
        }
    }

    public interface OnItemChangeListener {
        void onItemsChanged(List<ListItem> items);
    }

    private List<ListItem> items;
    private OnItemChangeListener listener;

    public ListItemAdapter() {
        this.items = new ArrayList<>();
    }

    public void setItems(List<ListItem> items) {
        this.items = items != null ? items : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setOnItemChangeListener(OnItemChangeListener listener) {
        this.listener = listener;
    }

    public void addItem() {
        items.add(new ListItem("", false));
        notifyItemInserted(items.size() - 1);
        notifyChangeToListener();
    }

    @NonNull
    @Override
    public ListItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_checkbox, parent, false);
        return new ListItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListItemViewHolder holder, int position) {
        ListItem item = items.get(position);

        // Remover listeners previos para evitar loops
        holder.editText.removeTextChangedListener(holder.textWatcher);
        holder.checkBox.setOnCheckedChangeListener(null);

        // Configurar valores
        holder.editText.setText(item.text);
        holder.checkBox.setChecked(item.completed);

        // TextWatcher para actualizar el texto en tiempo real
        holder.textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                int pos = holder.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && pos < items.size()) {
                    items.get(pos).text = s.toString();
                    notifyChangeToListener();
                }
            }
        };
        holder.editText.addTextChangedListener(holder.textWatcher);

        // CheckBox listener
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION && pos < items.size()) {
                items.get(pos).completed = isChecked;
                notifyChangeToListener();
            }
        });

        // Botón de eliminar
        holder.btnDelete.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION && pos < items.size()) {
                items.remove(pos);
                notifyItemRemoved(pos);
                notifyItemRangeChanged(pos, items.size());
                notifyChangeToListener();
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private void notifyChangeToListener() {
        if (listener != null) {
            listener.onItemsChanged(new ArrayList<>(items));
        }
    }

    static class ListItemViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        EditText editText;
        ImageButton btnDelete;
        TextWatcher textWatcher;

        ListItemViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkboxListItem);
            editText = itemView.findViewById(R.id.etListItemText);
            btnDelete = itemView.findViewById(R.id.btnDeleteListItem);
        }
    }
}
