package com.example.staffprofile.Model;

public class Staff {
    private String Name;
    private String Post;
    private String Degree;
    private int Phone;
    private String Email;
    private String Image;

    public Staff() {
    }

    public Staff(String name, String post, String degree, int phone, String email, String image) {
        Name = name;
        Post = post;
        Degree = degree;
        Phone = phone;
        Email = email;
        Image = image;
    }


    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPost() {
        return Post;
    }

    public void setPost(String post) {
        Post = post;
    }

    public String getDegree() {
        return Degree;
    }

    public void setDegree(String degree) {
        Degree = degree;
    }

    public int getPhone() {
        return Phone;
    }

    public void setPhone(int phone) {
        Phone = phone;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }
}
