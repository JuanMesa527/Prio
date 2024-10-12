package unipiloto.edu.co.prio;

import android.os.Parcel;
import android.os.Parcelable;

public class Project implements Parcelable {

    private int logoResId;
    private int id;
    private String title;
    private String description;
    private double budget;
    private String startDate;
    private String endDate;
    private int categoryId;
    private int localityId;
    private String address;

    public Project(int id, String title, String description, double budget, String startDate, String endDate, int categoryId, int localityId, String address) {
        switch(categoryId){
            case 1:
                this.logoResId = R.drawable.ambiente_bienestar_animal;
                break;
            case 2:
                this.logoResId = R.drawable.cultura_recreacion_deportes;
                break;
            case 3:
                this.logoResId = R.drawable.derechos_mujeres;
                break;
        }
        this.id = id;
        this.title = title;
        this.address = address;
        this.budget = budget;
        this.categoryId = categoryId;
        this.description = description;
        this.endDate = endDate;
        this.localityId = localityId;
        this.startDate = startDate;

    }

    protected Project(Parcel in) {
        logoResId = in.readInt();
        id = in.readInt();
        title = in.readString();
        description = in.readString();
        budget = in.readDouble();
        startDate = in.readString();
        endDate = in.readString();
        categoryId = in.readInt();
        localityId = in.readInt();
        address = in.readString();

    }

    public static final Creator<Project> CREATOR = new Creator<Project>() {
        @Override
        public Project createFromParcel(Parcel in) {
            return new Project(in);
        }

        @Override
        public Project[] newArray(int size) {
            return new Project[size];
        }
    };

    public int getLogoResId() {
        return logoResId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getLocalityId() {
        return localityId;
    }

    public void setLocalityId(int localityId) {
        this.localityId = localityId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setLogoResId(int logoResId) {
        this.logoResId = logoResId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(logoResId);
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeDouble(budget);
        dest.writeString(startDate);
        dest.writeString(endDate);
        dest.writeInt(categoryId);
        dest.writeInt(localityId);
        dest.writeString(address);
    }
}