package com.smartechbraintechnologies.medillah.Models;

public class ModelMyAppointments {
    private String consultationID;
    private String consultationDoctorImage;
    private String consultationDoctorName;
    private String consultationDoctorSpeciality;
    private String consultationTiming;
    private String consultationStatus;

    public ModelMyAppointments() {

    }

    public ModelMyAppointments(String consultationID, String consultationDoctorImage, String consultationDoctorName, String consultationDoctorSpeciality, String consultationTiming, String consultationStatus) {
        this.consultationID = consultationID;
        this.consultationDoctorImage = consultationDoctorImage;
        this.consultationDoctorName = consultationDoctorName;
        this.consultationDoctorSpeciality = consultationDoctorSpeciality;
        this.consultationTiming = consultationTiming;
        this.consultationStatus = consultationStatus;
    }

    public String getConsultationID() {
        return consultationID;
    }

    public String getConsultationDoctorImage() {
        return consultationDoctorImage;
    }

    public String getConsultationDoctorName() {
        return consultationDoctorName;
    }

    public String getConsultationDoctorSpeciality() {
        return consultationDoctorSpeciality;
    }

    public String getConsultationTiming() {
        return consultationTiming;
    }

    public String getConsultationStatus() {
        return consultationStatus;
    }
}
