package paquete.app.vetprohub.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import paquete.app.vetprohub.R;
import paquete.app.vetprohub.model.Citation;
import java.util.List;

public class CitationAdapter extends RecyclerView.Adapter<CitationAdapter.CitationViewHolder> {

    private List<Citation> citationList;

    public CitationAdapter(List<Citation> citationList) {
        this.citationList = citationList;
    }

    @NonNull
    @Override
    public CitationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_citation, parent, false);
        return new CitationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CitationViewHolder holder, int position) {
        Citation citation = citationList.get(position);
        holder.tvOwnerName.setText(citation.getOwnerName());
        holder.tvPetName.setText(citation.getPetName());
        holder.tvDate.setText(citation.getDate());
        holder.tvTime.setText(citation.getTime());
        holder.tvReason.setText(citation.getReason());
        holder.tvEmployeeName.setText(citation.getEmployeeName());
        holder.tvEmployeeSpecialty.setText(citation.getEmployeeSpecialty());
    }

    @Override
    public int getItemCount() {
        return citationList.size();
    }

    public static class CitationViewHolder extends RecyclerView.ViewHolder {
        TextView tvOwnerName, tvPetName, tvDate, tvTime, tvReason, tvEmployeeName, tvEmployeeSpecialty;

        public CitationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOwnerName = itemView.findViewById(R.id.tvOwnerName);
            tvPetName = itemView.findViewById(R.id.tvPetName);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvReason = itemView.findViewById(R.id.tvReason);
            tvEmployeeName = itemView.findViewById(R.id.tvEmployeeName);
            tvEmployeeSpecialty = itemView.findViewById(R.id.tvEmployeeSpecialty);
        }
    }
}
