package com.kento.UserInterface;

public class Requests {

	private int id;
	private String admin;
	private String userName;
	
	public Requests(){
		
	}
	
	public Requests(int id, String admin, String userName){
		this.id = id;
		this.admin = admin;
		this.userName = userName;
	}

	public int getId() {
		return id;
	}

	public String getAdmin() {
		return admin;
	}

	public String getUserName() {
		return userName;
	}
	
}
