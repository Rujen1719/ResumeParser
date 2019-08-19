package com.example.resumeparser2.Models;

public class ResumeListModel {
    public String name;
    public String resume_id;
    public String user_id;
    public String created_at;
    public String skills;
    public String education;
    public String experience;

    public ResumeListModel(String name, String resume_id, String user_id, String created_at, String skills, String education, String experience) {
        this.name = name;
        this.resume_id = resume_id;
        this.user_id = user_id;
        this.created_at = created_at;
        this.skills = skills;
        this.education = education;
        this.experience = experience;
    }

    public ResumeListModel(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getResume_id() {
        return resume_id;
    }

    public void setResume_id(String resume_id) {
        this.resume_id = resume_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }
}
