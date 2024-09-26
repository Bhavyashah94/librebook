/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.apache.librebook;

/**
 *
 * @author bhavy
 */
public class vMember {
    private String id;
    private String name;
    private String mail;
    private String phone;
    private String address;

    public vMember(String id, String name, String mail, String phone, String address) {
        this.id = id;
        this.name = name;
        this.mail = mail;
        this.phone = phone;
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getMail() {
        return mail;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }
    
    
}
