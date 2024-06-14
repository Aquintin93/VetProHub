package paquete.app.vetprohub.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import paquete.app.vetprohub.R;
import paquete.app.vetprohub.model.Citation;
import android.util.Log;

public class EmployeeCitationAdapter extends RecyclerView.Adapter<EmployeeCitationAdapter.CitationViewHolder> {

    private List<Citation> citationList;
    private OnCitationClickListener listener;
    private static final String TAG = "EmployeeCitationAdapter";

    public interface OnCitationClickListener {
        void onEditClick(Citation citation);
        void onDeleteClick(Citation citation);
    }

    public EmployeeCitationAdapter(List<Citation> citationList, OnCitationClickListener listener) {
        this.citationList = citationList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CitationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_employee_citation, parent, false);
        return new CitationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CitationViewHolder holder, int position) {
        Citation citation = citationList.get(position);
        holder.bind(citation, listener);
    }

    @Override
    public int getItemCount() {
        return citationList.size();
    }

    public static class CitationViewHolder extends RecyclerView.ViewHolder {
        private TextView tvOwnerName, tvPetName, tvDate, tvTime, tvReason, tvEmployeeName, tvEmployeeSpecialty;
        private Button btnEdit;
        private ImageButton btnDelete;

        public CitationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOwnerName = itemView.findViewById(R.id.tvOwnerName);
            tvPetName = itemView.findViewById(R.id.tvPetName);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvReason = itemView.findViewById(R.id.tvReason);
            tvEmployeeName = itemView.findViewById(R.id.tvEmployeeName);
            tvEmployeeSpecialty = itemView.findViewById(R.id.tvEmployeeSpecialty);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        public void bind(Citation citation, OnCitationClickListener listener) {
            Log.d(TAG, "Binding citation: " + citation.toString());
            tvOwnerName.setText(citation.getOwnerName());
            tvPetName.setText(citation.getPetName());
            tvDate.setText(citation.getDate());
            tvTime.setText(citation.getTime());
            tvReason.setText(citation.getReason());
            tvEmployeeName.setText(citation.getEmployeeName());
            tvEmployeeSpecialty.setText(citation.getEmployeeSpecialty());
            btnEdit.setOnClickListener(v -> listener.onEditClick(citation));
            btnDelete.setOnClickListener(v -> listener.onDeleteClick(citation));
        }
    }
}
