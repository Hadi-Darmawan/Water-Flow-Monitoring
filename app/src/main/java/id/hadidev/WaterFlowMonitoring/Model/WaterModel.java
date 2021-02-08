package id.hadidev.WaterFlowMonitoring.Model;

public class WaterModel {

    Integer dateEpoch;
    String dateString;
    Integer value;

    public WaterModel(){

    }

    public WaterModel(Integer dateEpoch, String dateString, Integer value) {
        this.dateEpoch = dateEpoch;
        this.dateString = dateString;
        this.value = value;
    }

    public Integer getDateEpoch() {
        return dateEpoch;
    }

    public Integer getValue() {
        return value;
    }

    public String getDateString() {
        return dateString;
    }

    public void setDateEpoch(Integer dateEpoch) {
        this.dateEpoch = dateEpoch;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
