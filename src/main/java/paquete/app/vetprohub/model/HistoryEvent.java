package paquete.app.vetprohub.model;

public class HistoryEvent {
    private String date;
    private String description;

    public HistoryEvent() {
        // Constructor vacío requerido por Firestore
    }

    public HistoryEvent(String date, String description) {
        this.date = date;
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
