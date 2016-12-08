package com.kento.UserInterface;

public class Project {


	private int id;
	private String admin;
	private String projectName;
	private String city;
	private String country;
	private String description;
	private String tools;
	private String languages;
	private String messages;
	
	public Project(String admin, String projectName, String city, String country, 
			String description, String tools, String languages){
		this.admin = admin;
		this.projectName = projectName;
		this.city = city;
		this.country = country;
		this.description = description;
		this.tools = tools;
		this.languages = languages;
		messages = "";
	}

	public String getProjectName(){
        return projectName;
    }

    public String getAdmin(){
        return admin;
    }
}
