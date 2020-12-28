package com.smartechbraintechnologies.medillah.Models;

public class ModelDoctor {

    private String doctorImage;
    private String doctorName;
    private String doctorSpeciality;
    private Double doctorExperience;
    private Double doctorMRP;
    private Double doctorConsultationPrice;

    public ModelDoctor(String doctorImage, String doctorName, String doctorSpeciality, Double doctorExperience, Double doctorMRP, Double doctorConsultationPrice) {
        this.doctorImage = doctorImage;
        this.doctorName = doctorName;
        this.doctorSpeciality = doctorSpeciality;
        this.doctorExperience = doctorExperience;
        this.doctorMRP = doctorMRP;
        this.doctorConsultationPrice = doctorConsultationPrice;
    }

    public String getDoctorImage() {
        return doctorImage;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public String getDoctorSpeciality() {
        return doctorSpeciality;
    }

    public Double getDoctorExperience() {
        return doctorExperience;
    }

    public Double getDoctorMRP() {
        return doctorMRP;
    }

    public Double getDoctorConsultationPrice() {
        return doctorConsultationPrice;
    }
}
